package com.czy.search.controller;

import com.czy.api.api.feature.DiseasesNeo4jService;
import com.czy.api.api.post.PostNerService;
import com.czy.api.api.post.PostSearchService;
import com.czy.api.api.user_relationship.UserHealthDataService;
import com.czy.api.api.user_relationship.UserService;
import com.czy.api.constant.post.DiseasesKnowledgeGraphEnum;
import com.czy.api.constant.search.FuzzySearchResponseEnum;
import com.czy.api.constant.search.NlpResultEnum;
import com.czy.api.constant.search.SearchConstant;
import com.czy.api.constant.search.SearchLevel;
import com.czy.api.constant.search.result.PersonalResultIntent;
import com.czy.api.constant.search.result.PostRecommendResult;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.Do.user.UserHealthDataDo;
import com.czy.api.domain.ao.post.PostInfoUrlAo;
import com.czy.api.domain.ao.post.PostNerResult;
import com.czy.api.domain.ao.search.AppFunctionAo;
import com.czy.api.domain.ao.search.DiseaseQuestionAo;
import com.czy.api.domain.ao.search.PersonalEvaluateAo;
import com.czy.api.domain.ao.search.PostRecommendAo;
import com.czy.api.domain.ao.search.PostSearchResultAo;
import com.czy.api.domain.ao.search.QuestionAo;
import com.czy.api.domain.dto.base.BaseResponse;
import com.czy.api.domain.dto.http.request.FuzzySearchRequest;
import com.czy.api.domain.dto.http.response.FuzzySearchResponse;
import com.czy.api.domain.dto.python.MedicalPredictionResponse;
import com.czy.api.domain.dto.python.NlpSearchResponse;
import com.czy.api.domain.entity.kafkaMessage.UserActionSearchPost;
import com.czy.api.domain.vo.post.PostPreviewVo;
import com.czy.api.exception.UserExceptions;
import com.czy.search.component.KafkaSender;
import com.czy.search.config.SearchTestConfig;
import com.czy.search.service.FuzzySearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author 13225
 * @date 2025/4/30 18:13
 * 暂时使用Http直接网络请求python
 * TODO 后续改为RPC
 * 职责分析：
 * Java有post，user环境
 * 最后意图要落实在具体的实现上，
 * Java负责开始提供输入，user向量
 * python负责数据处理，返回Java决策
 * Java负责最后将决策转为实体，返回前端。
 */
@Slf4j
@CrossOrigin(origins = "*") // 跨域
@RequiredArgsConstructor // 自动注入@Autowired
@RestController
@RequestMapping(SearchConstant.MainSearch_CONTROLLER)
public class SearchController {

    // mainSearch，包括全部的模糊搜索。依赖全部business模块
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private PostNerService postNerService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private UserService userService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private UserHealthDataService userHealthDataService;
    private final FuzzySearchService fuzzySearchService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private PostSearchService postSearchService;
    private final RestTemplate restTemplate;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private DiseasesNeo4jService diseasesNeo4jService;

    private final KafkaSender kafkaSender;
    private final SearchTestConfig searchTestConfig;
    /**
     * 模糊搜索
     * @param request  模糊搜索的句子 + userId（用于userContext特征上下文）
     * @return  搜索结果
     */
    @PostMapping("/fuzzy")
    public BaseResponse<FuzzySearchResponse> fuzzySearch(@Valid @RequestBody FuzzySearchRequest request) {
        // 搜索这种消耗资源的操作需要给用户上限流和分布式锁

        // 账号检查
        UserDo userDo = userService.getUserById(request.getUserId());
        if (userDo == null || userDo.getId() == null){
            return BaseResponse.LogBackError(UserExceptions.USER_NOT_EXIST);
        }

        // 提取搜素句子
        String sentence = request.getSentence();


        log.info("debug状态：{}", searchTestConfig.isDebug);
        if (searchTestConfig.isDebug){
            NlpSearchResponse nlpSearchResponse = new NlpSearchResponse();
            nlpSearchResponse.setCode(200);
            nlpSearchResponse.setMessage("");
            nlpSearchResponse.setType(NlpResultEnum.SEARCH.getCode());

            FuzzySearchResponse response = handleNlpResult(nlpSearchResponse, sentence, userDo.getId());
            return BaseResponse.getResponseEntitySuccess(response);
        }

        // python服务处理nlp搜索
        ResponseEntity<NlpSearchResponse> pythonResponseEntity = invokePythonNlpSearch(sentence);

        // 处理 Python 响应
        NlpSearchResponse nlpSearchResponse = Optional.ofNullable(pythonResponseEntity)
                .filter(response -> response.getStatusCode().is2xxSuccessful())
                .map(ResponseEntity::getBody)
                .orElse(null);

        FuzzySearchResponse response = handleNlpResult(nlpSearchResponse, sentence, userDo.getId());

        // kafka发送搜索行为
        searchActionKafkaSend(response, userDo.getId());

        return BaseResponse.getResponseEntitySuccess(response);
    }

    /**
     * kafka发送搜索行为
     * @param response  搜索结果
     * @param userId    用户id
     */
    private void searchActionKafkaSend(FuzzySearchResponse response, Long userId){
        Map<Integer, List<Long>> postIdListMap = new HashMap<>();
        if (response.getData() != null && response.getData() instanceof PostSearchResultAo){
            PostSearchResultAo postSearchResultAo = (PostSearchResultAo) response.getData();
            List<Long> likePostList = Optional.ofNullable(postSearchResultAo.getLikePostPreviewVoList())
                    .filter(l -> !CollectionUtils.isEmpty(l))
                    .map(l -> l.stream()
                            .map(PostPreviewVo::getPostId)
                            .collect(Collectors.toList()))
                    .orElse(new ArrayList<>());
            List<Long> tokenizedPostList = Optional.ofNullable(postSearchResultAo.getTokenizedPostPreviewVoList())
                    .filter(l -> !CollectionUtils.isEmpty(l))
                    .map(l -> l.stream()
                            .map(PostPreviewVo::getPostId)
                            .collect(Collectors.toList()))
                    .orElse(new ArrayList<>());
            List<Long> similarPostList = Optional.ofNullable(postSearchResultAo.getSimilarPostPreviewVoList())
                    .filter(l -> !CollectionUtils.isEmpty(l))
                    .map(l -> l.stream()
                            .map(PostPreviewVo::getPostId)
                            .collect(Collectors.toList()))
                    .orElse(new ArrayList<>());
            List<Long> recommendPostList = Optional.ofNullable(postSearchResultAo.getRecommendPostPreviewVoList())
                    .filter(l -> !CollectionUtils.isEmpty(l))
                    .map(l -> l.stream()
                            .map(PostPreviewVo::getPostId)
                            .collect(Collectors.toList()))
                    .orElse(new ArrayList<>());
            postIdListMap.put(SearchLevel.ONE.getCode(), likePostList);
            postIdListMap.put(SearchLevel.TWO.getCode(), tokenizedPostList);
            postIdListMap.put(SearchLevel.THREE.getCode(), similarPostList);
            postIdListMap.put(SearchLevel.FOUR.getCode(), recommendPostList);

            // kafka -> feature-service
            UserActionSearchPost userActionSearchPost = new UserActionSearchPost();
            userActionSearchPost.setId(userId);
            userActionSearchPost.setLevelsPostIdMap(postIdListMap);
            try {
                kafkaSender.sendSearchAction(userActionSearchPost);
            } catch (Exception e) {
                log.error("用户显性行为Kafka传输异常：[搜索] [userId:{}]", userId, e);
            }
        }
    }

    private ResponseEntity<NlpSearchResponse> invokePythonNlpSearch(String sentence) {
        // 请求头构造
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> requestBody = new HashMap<>();
        // 请求体数据
        requestBody.put("text", sentence);
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<NlpSearchResponse> pythonResponseEntity = null;
        // python搜索响应
        try {
            pythonResponseEntity = restTemplate.postForEntity(
                    SearchConstant.PYTHON_NLP_SEARCH_URL,
                    requestEntity,
                    NlpSearchResponse.class
            );
        } catch (Exception e) {
            log.error("python nlp search error", e);
        }

        return pythonResponseEntity;
    }

    private FuzzySearchResponse handleNlpResult(NlpSearchResponse nlpSearchResponse, String sentence, Long userId) {
        // error
        if (nlpSearchResponse == null ||
                nlpSearchResponse.getCode() != 200 ||
                nlpSearchResponse.getType() == NlpResultEnum.NONE.getCode()){
            FuzzySearchResponse response = new FuzzySearchResponse();
            response.setType(FuzzySearchResponseEnum.ERROR_RESULT.getType());
            return response;
        }
        // 非自然语言
        else if (nlpSearchResponse.getType() == NlpResultEnum.NOT_NL.getCode()) {
            FuzzySearchResponse response = new FuzzySearchResponse();
            response.setType(FuzzySearchResponseEnum.NOT_NATURAL_LANGUAGE_RESULT.getType());
            response.setData("看不懂！请输入正确的搜索内容！");
            return response;
        }
        // 寒暄
        else if (nlpSearchResponse.getType() == NlpResultEnum.GREETING.getCode()) {
            FuzzySearchResponse response = new FuzzySearchResponse();
            response.setType(FuzzySearchResponseEnum.TALK_RESULT.getType());
            // 要求python那边识别意图之后用TF-IDF识别不同的寒暄类型然后返回语句放在message中返回给前端
            response.setData(nlpSearchResponse.getMessage());
            return response;
        }
        // 搜索意图
        else if (nlpSearchResponse.getType() == NlpResultEnum.SEARCH.getCode()){
            PostSearchResultAo ao = handleSearchIntent(sentence);
            FuzzySearchResponse response = new FuzzySearchResponse();
            response.setType(FuzzySearchResponseEnum.SEARCH_POST_RESULT.getType());
            response.setData(ao);
            return response;
        }
        // 搜索需求
        else if (nlpSearchResponse.getType() >= NlpResultEnum.SYMPTOM_SEARCH_QUESTION.getCode() &&
                nlpSearchResponse.getType() <= NlpResultEnum.DISEASE_TREATMENT_TIME.getCode()){
            FuzzySearchResponse response = new FuzzySearchResponse();
            // 推荐意图
            if (nlpSearchResponse.getType() == NlpResultEnum.RECOMMEND.getCode()){
                response.setType(FuzzySearchResponseEnum.RECOMMEND_QUESTION_RESULT.getType());
                PostRecommendAo ao = handleRecommendIntent(sentence);
                response.setData(ao);
                return response;
            }
            // 个人评价意图
            else if (nlpSearchResponse.getType() == NlpResultEnum.PERSONAL_EVALUATION.getCode()){
                response.setType(FuzzySearchResponseEnum.PERSONAL_QUESTION_RESULT.getType());
                PersonalEvaluateAo ao = handlePersonalEvaluateIntent(sentence, userId);
                response.setData(ao);
            }
            // app功能查询意图
            else if (nlpSearchResponse.getType() == NlpResultEnum.APP_QUESTION.getCode()){
                response.setType(FuzzySearchResponseEnum.APP_FUNCTION_RESULT.getType());
                AppFunctionAo ao = handleAppFunctionIntent(sentence);
                response.setData(ao);
            }
            // disease question问题意图
            else {
                response.setType(FuzzySearchResponseEnum.QUESTION_RESULT.getType());
                PostSearchResultAo postSearchResultAo = handleSearchIntent(sentence);
                DiseaseQuestionAo diseaseQuestionAo = handleQuestionIntent(sentence, nlpSearchResponse.getType());
                QuestionAo questionAo = new QuestionAo();
                questionAo.setDiseaseQuestionAo(diseaseQuestionAo);
                questionAo.setPostSearchResultAo(postSearchResultAo);
                response.setData(questionAo);
                return response;
            }
        }
        // 未知意图
        FuzzySearchResponse response = new FuzzySearchResponse();
        response.setType(FuzzySearchResponseEnum.ERROR_RESULT.getType());
        response.setData("未知意图！");
        return response;
    }

    private PostSearchResultAo handleSearchIntent(String sentence){
        PostSearchResultAo postSearchResultAo = new PostSearchResultAo();

        // 0~1级搜索 到此处说明sentence本身就是title，所以likeTitle传递sentence;
        long startTime1 = System.currentTimeMillis();
        List<Long> likePostIdList = fuzzySearchService.likeSearch(sentence);
        if (searchTestConfig.isDebug){
            log.info("0~1级搜索 likePostIdList:{}", likePostIdList);
            //0~1级搜索 耗时:16
            log.info("0~1级搜索 耗时:{}", System.currentTimeMillis() - startTime1);
        }

        // 2级搜索 搜索：AcTree匹配实体 + ElasticSearch搜索;
        // 缓存结结果，避免后续搜索调用两次
        long startTime2 = System.currentTimeMillis();
        List<PostNerResult> nerResults = postNerService.getPostNerResults(sentence);
        if (searchTestConfig.isDebug){
            log.info("句子的ner识别结果 nerResults:{}", nerResults);
        }
        List<Long> tokenizedPostIdList = fuzzySearchService.tokenizedSearch(sentence);
        if (searchTestConfig.isDebug){
            log.info("2级搜索 tokenizedPostIdList:{}", tokenizedPostIdList);
            // 2级搜索 耗时:17
            log.info("2级搜索 耗时:{}", System.currentTimeMillis() - startTime2);
        }

        // 3级搜索：neo4j规则集 + es查询 + user context vector排序;
        long startTime3 = System.currentTimeMillis();
        List<Long> neo4jRulePostIdList = fuzzySearchService.neo4jRuleSearch(nerResults);
        if (searchTestConfig.isDebug){
            log.info("3级搜索 neo4jRulePostIdList:{}", neo4jRulePostIdList);
            // 3级搜索 耗时:882
            log.info("3级搜索 耗时:{}", System.currentTimeMillis() - startTime3);
        }

        // 4级搜索：neo4j疾病相似度查询 + user context vector排序;
        long startTime4 = System.currentTimeMillis();
        List<Long> similarList = new ArrayList<>();
        if (likePostIdList.isEmpty() && tokenizedPostIdList.isEmpty() && neo4jRulePostIdList.isEmpty()){
            log.info("4级之前搜索 搜索结果为空，开始调用4级搜索......");
            similarList = fuzzySearchService.similaritySearch(nerResults);
            if (searchTestConfig.isDebug){
                log.info("4级搜索 similarList:{}", similarList);
                // 4级搜索 耗时:9078        能不使用四级搜索就不使用四级搜索
                log.info("4级搜索 耗时:{}", System.currentTimeMillis() - startTime4);
            }
        }

        // 过滤

        // 过滤4级中3级包含的结果
        similarList = filterResults(neo4jRulePostIdList, similarList);
        log.info("过滤4级中3级包含的结果 similarList:{}", similarList);
        // 过滤3级中2级包含的结果
        neo4jRulePostIdList = filterResults(tokenizedPostIdList, neo4jRulePostIdList);
        log.info("过滤3级中2级包含的结果 neo4jRulePostIdList:{}", neo4jRulePostIdList);
        // 过滤2级中1级包含的结果
        tokenizedPostIdList = filterResults(likePostIdList, tokenizedPostIdList);
        log.info("过滤2级中1级包含的结果 tokenizedPostIdList:{}", tokenizedPostIdList);

        // 转换
        long startTimeChange = System.currentTimeMillis();
        postSearchResultAo.setLikePostPreviewVoList(postSearchService.getPostPreviewVosByIds(likePostIdList));
        postSearchResultAo.setTokenizedPostPreviewVoList(postSearchService.getPostPreviewVosByIds(tokenizedPostIdList));
        postSearchResultAo.setSimilarPostPreviewVoList(postSearchService.getPostPreviewVosByIds(similarList));
        postSearchResultAo.setRecommendPostPreviewVoList(postSearchService.getPostPreviewVosByIds(neo4jRulePostIdList));
        if (searchTestConfig.isDebug){
            // 转换耗时:79
            log.info("转换耗时:{}", System.currentTimeMillis() - startTimeChange);
        }

        return postSearchResultAo;
    }

    public List<Long> filterResults(List<Long> previousResults, List<Long> currentResults) {
        Set<Long> previousResultSet = new HashSet<>(previousResults);
        return currentResults.stream()
                .filter(result -> !previousResultSet.contains(result))
                .collect(Collectors.toList());
    }

    // 推荐意图存在争议
    private PostRecommendAo handleRecommendIntent(String sentence){
        PostRecommendAo postRecommendAo = new PostRecommendAo();
        List<PostNerResult> nerResults = postNerService.getPostNerResults(sentence);
        PostRecommendResult postRecommendResult = PostRecommendResult.NO_RECOMMEND;
        if (nerResults.isEmpty()){
            postRecommendAo.setRecommendType(postRecommendResult.getCode());
            return postRecommendAo;
        }
        List<Long> tokenizedPostIdList = fuzzySearchService.tokenizedSearch(sentence);
        if (tokenizedPostIdList.isEmpty()){
            postRecommendAo.setRecommendType(PostRecommendResult.NO_DATA.getCode());
            return postRecommendAo;
        }
        List<PostInfoUrlAo> postInfoUrlAos = postSearchService.getPostInfoUrlAos(tokenizedPostIdList);
        postRecommendAo.setPostInfoUrlAos(postInfoUrlAos);
        postRecommendAo.setRecommendType(PostRecommendResult.HAS_DATA.getCode());
        return postRecommendAo;
    }

    private PersonalEvaluateAo handlePersonalEvaluateIntent(String sentence, Long userId){
        PersonalEvaluateAo personalEvaluateAo = new PersonalEvaluateAo();
        List<PostNerResult> nerResults = postNerService.getPostNerResults(sentence);
        PersonalResultIntent personalResultInt = PersonalResultIntent.UNRECOGNIZED;
        if (nerResults.isEmpty()){
            personalEvaluateAo.setIntent(personalEvaluateAo.getIntent());
            return personalEvaluateAo;
        }
        boolean[] haveIntent  = new boolean[]{false, false};
        for (PostNerResult nerResult : nerResults){
            ;
            if (SearchConstant.MEDICAL_IDENTIFY_ENTITIES[0].equals(nerResult.getKeyWord())){
                personalResultInt = PersonalResultIntent.RECOGNIZED;
                personalEvaluateAo.setIntent(personalResultInt.getType());
                haveIntent[0] = true;
            }
            if (SearchConstant.MEDICAL_IDENTIFY_ENTITIES[1].equals(nerResult.getKeyWord())){
                personalResultInt = PersonalResultIntent.RECOGNIZED;
                personalEvaluateAo.setIntent(personalResultInt.getType());
                haveIntent[1] = true;
            }
            if (haveIntent[0] && haveIntent[1]){
                break;
            }
        }
        // 没有意图
        if (!haveIntent[0] && !haveIntent[1]){
            personalEvaluateAo.setIntent(PersonalResultIntent.RECOGNIZED.getType());
            return personalEvaluateAo;
        }
        // 远程调用python的医疗预测
        UserHealthDataDo userHealthDataDo = userHealthDataService.findByUserId(userId);
        if (userHealthDataDo == null){
            personalResultInt = PersonalResultIntent.DATA_INCOMPLETE;
            personalEvaluateAo.setIntent(personalResultInt.getType());
            return personalEvaluateAo;
        }
        ResponseEntity<MedicalPredictionResponse> pythonResponse = invokePythonMedicalPredictionSearch(userHealthDataDo);

        // 处理 Python 响应
        MedicalPredictionResponse medicalPredictionResponse = Optional.ofNullable(pythonResponse)
                .filter(response -> response.getStatusCode().is2xxSuccessful())
                .map(ResponseEntity::getBody)
                .orElse(null);

        if (medicalPredictionResponse != null){
            personalEvaluateAo.setHeartDisease(medicalPredictionResponse.getHeartDisease());
            personalEvaluateAo.setDiabetes(medicalPredictionResponse.getDiabetes());
            personalEvaluateAo.setIntent(personalResultInt.getType());
        }

        return personalEvaluateAo;
    }

    private ResponseEntity<MedicalPredictionResponse> invokePythonMedicalPredictionSearch(UserHealthDataDo userHealthDataDo){

        // 请求头构造
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> requestBody = new HashMap<>();

        // 请求体数据
        requestBody.put("id", String.valueOf(userHealthDataDo.getId()));
        requestBody.put("userId", String.valueOf(userHealthDataDo.getUserId()));
        requestBody.put("time", String.valueOf(userHealthDataDo.getTime()));
        requestBody.put("hypertension", String.valueOf(userHealthDataDo.getHypertension()));
        requestBody.put("highCholesterol", String.valueOf(userHealthDataDo.getHighCholesterol()));
        requestBody.put("bmi", String.valueOf(userHealthDataDo.getBmi()));
        requestBody.put("smoking", String.valueOf(userHealthDataDo.getSmoking()));
        requestBody.put("stroke", String.valueOf(userHealthDataDo.getStroke()));
        requestBody.put("physicalActivity", String.valueOf(userHealthDataDo.getPhysicalActivity()));
        requestBody.put("fruitConsumption", String.valueOf(userHealthDataDo.getFruitConsumption()));
        requestBody.put("vegetableConsumption", String.valueOf(userHealthDataDo.getVegetableConsumption()));
        requestBody.put("heavyDrinking", String.valueOf(userHealthDataDo.getHeavyDrinking()));
        requestBody.put("anyHealthcare", String.valueOf(userHealthDataDo.getAnyHealthcare()));
        requestBody.put("noMedicalExpense", String.valueOf(userHealthDataDo.getNoMedicalExpense()));
        requestBody.put("generalHealthStatus", String.valueOf(userHealthDataDo.getGeneralHealthStatus()));
        requestBody.put("mentalHealth", String.valueOf(userHealthDataDo.getMentalHealth()));
        requestBody.put("physicalHealth", String.valueOf(userHealthDataDo.getPhysicalHealth()));
        requestBody.put("walkingDifficulty", String.valueOf(userHealthDataDo.getWalkingDifficulty()));
        requestBody.put("gender", String.valueOf(userHealthDataDo.getGender()));
        requestBody.put("age", String.valueOf(userHealthDataDo.getAge()));
        requestBody.put("educationLevel", String.valueOf(userHealthDataDo.getEducationLevel()));
        requestBody.put("income", String.valueOf(userHealthDataDo.getIncome()));

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<MedicalPredictionResponse> pythonResponseEntity = null;
        // python搜索响应
        try {
            pythonResponseEntity = restTemplate.postForEntity(
                    SearchConstant.PYTHON_MEDICAL_URL,
                    requestEntity,
                    MedicalPredictionResponse.class
            );
        } catch (Exception e) {
            log.error("python nlp search error", e);
        }

        return pythonResponseEntity;
    }

    private AppFunctionAo handleAppFunctionIntent(String sentence){
        AppFunctionAo appFunctionAo = new AppFunctionAo();
        appFunctionAo.setMessage("此功能暂时未开发。。。");
        return appFunctionAo;
    }

    /**
     * 问题意图
     * 根据句子进行实体识别，然后根据不同的意图进行neo4j图数据库查找对应的疾病属性
     * @param sentence  句子，用于AcTree识别实体
     * @param type  意图分类
     * @see NlpResultEnum
     * @return
     */
    private DiseaseQuestionAo handleQuestionIntent(String sentence, int type){
        DiseaseQuestionAo diseaseQuestionAo = new DiseaseQuestionAo();
        List<PostNerResult> nerResults = postNerService.getPostNerResults(sentence);
        if (CollectionUtils.isEmpty(nerResults)){
            // 未找到实体
            diseaseQuestionAo.setAnswer("抱歉没有找到您问题的答案");
        }
        else {
            // 症状问诊
            if (type == NlpResultEnum.SYMPTOM_SEARCH_QUESTION.getCode()){
                List<String> symptoms = new ArrayList<>();
                for (PostNerResult nerResult : nerResults){
                    if (nerResult.getNerType().equals(DiseasesKnowledgeGraphEnum.SYMPTOMS.getName())){
                        symptoms.add(nerResult.getKeyWord());
                    }
                }
                if (symptoms.isEmpty()){
                    diseaseQuestionAo.setAnswer("不知道您的症状是什么呢");
                }
                else if (symptoms.size() == 1){
                    diseaseQuestionAo.setAnswer("请您多说几个症状，谢谢");
                }
                else {
                    List<String> diseaseNames = diseasesNeo4jService.findSymptomsFindDiseases(symptoms);
                    if (diseaseNames.isEmpty()){
                        StringBuilder sb = new StringBuilder();
                        sb.append("抱歉，关于：[");
                        for (String symptom : symptoms) {
                            sb.append(symptom).append("、");
                        }
                        sb.append("] 等症状没有找到相关的疾病");
                        diseaseQuestionAo.setAnswer(sb.toString());
                    }
                    else {
                        StringBuilder sb = new StringBuilder();
                        sb.append("关于：[");
                        for (String symptom : symptoms) {
                            sb.append(symptom).append("、");
                        }
                        sb.append("] 等症状，可能相关的疾病有：[");
                        for (String diseaseName : diseaseNames) {
                            sb.append(diseaseName).append("、");
                        }
                        sb.append("] 等疾病");
                        diseaseQuestionAo.setAnswer(sb.toString());
                    }
                }
            }
        }
        return diseaseQuestionAo;
    }

    /**
     * plan C
     * 句子 -> bert-nlj识别是否是自然语言（准确率几乎100%）
     * 句子 -> bert-nlu意图识别模型：（标题检索；询问问题；寒暄）
     * 检索分支：
     * <p>      精确
     *      0~1级：mysql的like
     *      2级：elasticsearch的tokenized
     * <p>      模糊 + user context vector
     *      3级：neo4j规则集 + es查询 + user context vector排序
     *      4级：neo4j疾病相似度查询 + user context vector排序
     * <p>
     * 问答分支：
     *      疾病属性问题集合：
     *          定义
     *          病因
     *          预防
     *          临床表现(病症表现)
     *          相关病症
     *          治疗方法
     *          所属科室
     *          传染性
     *          治愈率
     *          禁忌
     *          化验/体检方案
     *          治疗时间
     *      症状问诊意图
     *          多个症状进行共同疾病搜索
     *      推荐
     *          推荐内容检索 + post评分排序 + user context vector排序
     *      个人评价
     *          收集用户数据回答（帖子特征 + 用户健康数据 + 医疗预测结果）
     *      App问题
     *          识别出是App问题进入App规则集回答，如果规则集没有数据则回答不知道
     */
}
