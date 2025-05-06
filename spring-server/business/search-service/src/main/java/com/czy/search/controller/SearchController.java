package com.czy.search.controller;

import com.czy.api.api.oss.OssService;
import com.czy.api.api.post.PostSearchService;
import com.czy.api.constant.search.FuzzySearchResponseEnum;
import com.czy.api.constant.search.SearchConstant;
import com.czy.api.domain.ao.post.PostAo;
import com.czy.api.domain.ao.search.PostSearchResultAo;
import com.czy.api.domain.dto.http.request.FuzzySearchRequest;
import com.czy.api.domain.dto.http.response.FuzzySearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


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
    private PostSearchService postSearchService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private OssService ossService;

    /**
     * 模糊搜索
     * @param request  模糊搜索的句子 + userId（用于userContext特征上下文）
     * @return  搜索结果
     */
    @PostMapping("/fuzzy")
    public FuzzySearchResponse fuzzySearch(@Valid FuzzySearchRequest request) {
        FuzzySearchResponse response = new FuzzySearchResponse();
        String sentence = request.getSentence();
        // 略过python搜索响应
        // 到此处了说明一定是post
        response.setType(FuzzySearchResponseEnum.SEARCH_POST_RESULT.getType());
        PostSearchResultAo postSearchResultAo = new PostSearchResultAo();

        // 0~1级搜索 到此处说明sentence本身就是title，所以likeTitle传递sentence
        List<Long> likePostIdList = likeSearch(sentence);

        return null;
    }

    /**
     * 0~1级 的like搜索
     * @param title 搜索的标题
     * @return      搜索结果
     */
    private List<Long> likeSearch(String title){
        return postSearchService.searchPostIdsByLikeTitle(title);
    }


}
