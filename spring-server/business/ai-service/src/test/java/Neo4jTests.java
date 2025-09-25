import com.ai.medicine.MedicineAiApplication;
import com.ai.medicine.domain.Do.neo4j.DiseaseDo;
import com.ai.medicine.domain.Do.neo4j.SymptomsDo;
import com.ai.medicine.domain.Do.neo4j.base.BaseNeo4jMatchDo;
import com.ai.medicine.domain.Do.neo4j.rels.HasSymptomRelsDo;
import com.ai.medicine.domain.MedicalRecognitionResult;
import com.ai.medicine.domain.constant.MedicalEntityEnum;
import com.ai.medicine.domain.constant.MedicalRelationEnum;
import com.ai.medicine.mapper.DiseaseRepository;
import com.ai.medicine.mapper.rels.DiseaseSymptomRelationRepository;
import com.ai.medicine.service.mcp.MedicalQuestionToolService;
import com.ai.medicine.service.mcp.QuestionToolService;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 13225
 * @date 2025/9/22 17:07
 */
@Slf4j
@SpringBootTest(classes = MedicineAiApplication.class)
@TestPropertySource("classpath:application.yml")
public class Neo4jTests {

    @Test
    public void helloWorldTest() {
        log.info("Neo4jTests");
    }

    @Autowired
    private DiseaseRepository diseaseRepository;

    // SpringBoot3大傻逼, 吧neo4j从spring.data.neo4j -> spring.neo4j
    @Test
    public void neo4jSearchTest(){
        DiseaseDo diseaseDo = diseaseRepository.findByName("感冒");
        log.info("diseaseDo: {}", diseaseDo);
        /*
            diseaseDo: DiseaseDo(desc=感冒，总体上分为普通感冒和流行性感冒，在这里先讨论普通感冒。普通感冒，祖国医学称"伤风"，是由多种病毒引起的一种呼吸道常见病，其中30%-50%是由某种血清型的鼻病毒引起，普通感冒虽多发于初冬，但任何季节，如春天，夏天也可发生，不同季节的感冒的致病病毒并非完全一样。流行性感冒，是由流感病毒引起的急性呼吸道传染病。病毒存在于病人的呼吸道中，在病人咳嗽，打喷嚏时经飞沫传染给别人。流感的传染性很强，由于这种病毒容易变异，即使是患过流感的人，当下次再遇上流感流行，他仍然会感染，所以流感容易引起暴发性流行。一般在冬春季流行的机会较多，每次可能有20～40%的人会传染上流感。, prevent=本病全年皆可发病，冬春季节多发，可通过含有病毒的飞沫或被污染的用具传播，多数为散发性，但常在气候突变时流行，由于病毒的类型较多，人体对各种病毒感染后产生的免疫力较弱且短暂，并无交叉免疫，同时在健康人群中有病毒携带者，故一个人一年内可有多次发病。日常预防四种简易预防感冒的方法1、冷水洗脸、热水泡足法：每日晨、晚养成用冷水浴面、热水泡足的习惯，这有助于提高身体抗病能力。2、盐水漱口：每日早晚、餐后用淡盐水漱口，以清除口腔病菌。在流感流行的。3、食醋熏蒸法：把陈醋加热，关上门窗，隔一段时间在房间里熏蒸一次，可有效杀除感冒等病毒。4、饮用姜茶法：晚上睡觉前，用萝卜加醋熬汤，或以生姜、红糖适量煮水代茶饮，对防止感冒有很好的效果。专业指导1、补充维生素E、维生素C。维生素E、维生素C都能有效提高人体免疫力。2、保证足够的睡眠。数据显示，只睡半宿的人，免疫力会下降大约三成。而在睡足8小时后，免疫力会立刻恢复。3、进行鼻部按摩。大部分感冒中，鼻咽部是最初感染的部位，因此鼻部按摩能有效预防感冒。, cause=感冒有70%-80%由病毒引起，主要有流感病毒(甲，乙，丙)，副流感病毒，呼吸道合胞病毒，腺病毒，鼻病毒，埃可病毒，柯萨奇病毒，麻疹病毒，风疹病毒。细菌感染可直接或继病毒感染之后发生，以溶血性链球菌为多见，其次为流感嗜血杆菌，肺炎球菌和葡萄球菌等，偶见革兰阴性杆菌，其感染的主要表现为鼻炎，咽喉炎或扁桃腺炎 全身酸痛等。当有受凉，淋雨，过度疲劳等诱发因素，使全身或呼吸道局部防御功能降低时，原已存在于上呼吸道或从外界侵入的病毒或细菌可迅速繁殖，引起发病，尤其是老幼体弱或有慢性呼吸道疾病如鼻旁窦炎，扁桃体炎者，更易罹病。鼻腔及咽粘膜充血，水肿，上皮细胞破坏，少量单核细胞浸润，有浆液性及粘液性炎性渗出，继发细菌感染后，有中性粒细胞浸润，大量脓性分泌物。, easyGet=无特定人群, cureDepartment=[内科, 呼吸内科], cureWay=[对症治疗, 中医治疗, 支持性治疗], cureLastTime=7-14天, symptom=, getProb=0.6%, curedProb=97%)
         */
    }

    @Autowired
    private DashScopeChatModel dashScopeChatModel;
    @Autowired
    private QuestionToolService questionToolService;

    @Test
    public void knowledgeSearchToolTest(){
        // 1. 注册工具回调
        ToolCallback[] toolCallbacks = ToolCallbacks.from(questionToolService);

        ChatOptions chatOptions = ToolCallingChatOptions.builder()
                .toolCallbacks(toolCallbacks)
                .build();

        // 2. 构建提示词模板
        String promptTemplate = """
            请分析用户的医疗问题，识别意图和实体，并调用知识查询工具获取相关信息。
            
            处理规则：
            1. 首先确认是不是跟医疗相关的, 如果不是久返回知识库中无数据
            2. 首先识别用户问题的意图和关键实体
            3. 调用 searchMedicalKnowledge 工具查询知识库
            4. 根据查询结果组织回答
            5. 如果searchMedicalKnowledge查询不到数据, 请回答: 抱歉, 知识库中未查询到数据.
            
            请确保：
            - 准确识别医疗意图（如：治疗方法、病因咨询、症状查询等）
            - 提取所有相关的医疗实体（症状、疾病、药品等）
            - 调用工具获取准确的知识信息
            
            用户输入：<text>
            """;

        // 2. 构建提示词（使用 PromptTemplate）
        PromptTemplate promptTemplateObj = PromptTemplate.builder()
                .renderer(StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build())
                .template(promptTemplate)
                .build();

        // 3. 用户问题示例
        String userQuestion = "感冒怎么治疗？";


        // 渲染提示词，替换模板中的 <text> 为用户输入
        Map<String, Object> params = new HashMap<>();
        params.put("text", userQuestion);
        String prompt = promptTemplateObj.render(params);

        System.out.println("提示词内容是1:\n" + prompt);

        // 4. 构建聊天客户端
        ChatClient chatClient = ChatClient.builder(dashScopeChatModel)
                .defaultOptions(chatOptions)
                .build();

        // 获取模型响应
        String response = chatClient.prompt()
                .user(prompt)
                .options(chatOptions)
                .call()
                .content();
        System.out.println("模型响应是1:\n" + response);
    }

    @Autowired
    private DiseaseSymptomRelationRepository diseaseSymptomRelationRepository;

    @Test
    public void relationTest(){
        List<HasSymptomRelsDo> symptomsRelations = diseaseSymptomRelationRepository.findSymptomsRelsByDiseaseName("感冒");
        log.info("关系数量：{}", symptomsRelations.size());

        List<SymptomsDo> symptoms = diseaseSymptomRelationRepository.findSymptomsByDiseaseName("感冒");
        for (SymptomsDo symptom : symptoms) {
            log.info("症状名称：{}", symptom.getName());
        }

        List<BaseNeo4jMatchDo> diseaseMatchs = diseaseSymptomRelationRepository.findDiseaseMatchsBySymptomNames(
                List.of("头痛","发烧")
        );

        for (BaseNeo4jMatchDo diseaseMatch : diseaseMatchs) {
            log.info("疾病名称：{}", diseaseMatch.getName());
            log.info("匹配症状数量：{}", diseaseMatch.getMatchedSymptomCount());
            log.info("实体类型：{}", MedicalEntityEnum.getByCode(diseaseMatch.getEntityType()));
        }
    }

    @Autowired
    private MedicalQuestionToolService medicalQuestionToolService;

    @Test
    public void singleEntityTest(){
        var d = "咽炎";

        MedicalRecognitionResult.Entity entity = new MedicalRecognitionResult.Entity();
        entity.setName(d);
        entity.setType(MedicalEntityEnum.DISEASE.getDescription());
        List<String> knowledge = medicalQuestionToolService.entityKnowledge(
                entity
        );
        log.info("知识：{}", knowledge);
    }

    @Test
    public void knowledgeSearchToolTest2(){
        // 1. 注册工具回调
        ToolCallback[] toolCallbacks = ToolCallbacks.from(medicalQuestionToolService);

        ChatOptions chatOptions = ToolCallingChatOptions.builder()
                .toolCallbacks(toolCallbacks)
                .build();

        // 2. 构建提示词模板
        String promptTemplate = """
            请分析用户的医疗问题，识别关系和实体，并调用知识查询工具获取相关信息。
            
            处理规则：
            1. 首先确认是不是跟医疗相关的, 如果不是久返回知识库中无数据
            2. 首先识别用户问题的医疗关系和关键实体
            3. 调用 searchKnowledge 工具查询知识库
            4. 根据查询结果组织回答
            5. 如果searchKnowledge查询不到数据, 请回答: 抱歉, 知识库中未查询到数据.
            6. 只能返回我提供给你的关系类型和实体类型
            7. 如果用户询问的问题答案包括多个可能，选择最可能的一条。
            8. 如果你不知道实体相关的知识，你可以自行使用entityKnowledge方法去查询知识。
            
            关系类型集合：<rels>
            实体类型集合：<entity>
            
            请确保：
            - 准确识别医疗意图（如：治疗方法、病因咨询、症状查询等）
            - 提取所有相关的医疗实体（症状、疾病、药品等）
            - 调用工具获取准确的知识信息
            
            用户输入：<text>
            """;

        // 2. 构建提示词（使用 PromptTemplate）
        PromptTemplate promptTemplateObj = PromptTemplate.builder()
                .renderer(StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build())
                .template(promptTemplate)
                .build();

        // 3. 用户问题示例
        String userQuestion = "我感觉到吞咽痛，还有一些发烧，我应该是生了什么病？这种病要怎么治疗呢？";


        // 渲染提示词，替换模板中的 <text> 为用户输入
        Map<String, Object> params = new HashMap<>();
        params.put("text", userQuestion);
        params.put("rels", MedicalRelationEnum.getAiPrompt());
        params.put("entity", MedicalEntityEnum.getAiPrompt());
        String prompt = promptTemplateObj.render(params);

        System.out.println("提示词内容是1:\n" + prompt);

        // 4. 构建聊天客户端
        ChatClient chatClient = ChatClient.builder(dashScopeChatModel)
                .defaultOptions(chatOptions)
                .build();

        // 获取模型响应
        String response = chatClient.prompt()
                .user(prompt)
                .options(chatOptions)
                .call()
                .content();
        System.out.println("模型响应是1:\n" + response);
    }

}
