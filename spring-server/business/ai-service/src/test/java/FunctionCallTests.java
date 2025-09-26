import com.ai.medicine.MedicineAiApplication;
import com.ai.medicine.domain.constant.IntentEnum;
import com.ai.medicine.domain.constant.MedicalEntityEnum;
import com.ai.medicine.service.mcp.IntentRecognitionService;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 13225
 * @date 2025/9/22 9:48
 */


@Slf4j
@SpringBootTest(classes = MedicineAiApplication.class)
@TestPropertySource("classpath:application.yml")
public class FunctionCallTests {

    @Test
    public void helloWorldTest() {
        log.info("FunctionCallTests");
    }

    @Autowired
    private DashScopeChatModel dashScopeChatModel;

    // WDF 每个Test都需要独立的环境变量
    @Test
    public void intentRecognitionTest() {
        System.out.println(recognize());
        System.out.println(recognize2());
    }

    public String recognize(){
        // 1. 定义提示词模板，约束大模型输出格式
        // 1. 定义提示词模板，约束大模型输出格式
        String promptTemplate = """
            请识别用户输入的句子的意图和实体，并严格按照以下格式输出JSON：
            {
              "intent": "意图类型（如治疗方法、病因咨询、药品推荐等）",
              "entities": [
                {"name": "实体名称", "type": "实体类型（如症状、药品、疾病等）"}
              ]
            }
            
            规则：
            1. 意图类型需简洁明确，符合医疗咨询场景（如"询问药品适用性"、"咨询症状原因"等）
            2. 实体需提取句子中提到的关键信息（如症状、药品、疾病名称等）
            3. 若无法识别，intent填"未知"，entities留空数组
            """;


        // 构建聊天客户端
        ChatClient chatClient = ChatClient.builder(dashScopeChatModel)
                .build();

        // 用户查询：询问取消航班的注意事项
        String question = "失眠心慌吃人参滴丸行吗，不知道这是怎么回事，求医生指导疑惑？";

        return chatClient.prompt(promptTemplate)
                .user(question)
                .call()
                .content();
    }

    public String recognize2(){
        // 1. 定义提示词模板，约束大模型输出格式
        String promptTemplate = """
            请识别用户输入的句子的意图和实体，并严格按照以下格式输出JSON：
            {
              "intent": "意图类型（如治疗方法、病因咨询、药品推荐等）",
              "entities": [
                {"name": "实体名称", "type": "实体类型（如症状、药品、疾病等）"}
              ]
            }
            
            规则：
            1. 意图类型需简洁明确，符合医疗咨询场景（如"询问药品适用性"、"咨询症状原因"等）
            2. 实体需提取句子中提到的关键信息（如症状、药品、疾病名称等）
            3. 若无法识别，intent填"未知"，entities留空数组
            
            用户输入：<text>
            """;

        // 2. 构建提示词（替换模板中的{text}为用户输入）
        // 2. 构建提示词（使用 PromptTemplate）
        PromptTemplate promptTemplateObj = PromptTemplate.builder()
                .renderer(StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build())
                .template(promptTemplate)
                .build();


        // 用户查询：询问取消航班的注意事项
        String question = "失眠心慌吃人参滴丸行吗，不知道这是怎么回事，求医生指导疑惑？";

        // 渲染提示词，替换模板中的 <text> 为用户输入
        String prompt = promptTemplateObj.render(Map.of("text", question));

        // 构建聊天客户端
        ChatClient chatClient = ChatClient.builder(dashScopeChatModel)
                .build();


        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    @Test
    public void limitIntentRecognitionTest() {
        System.out.println(recognize3());
    }

    public String recognize3(){
        // 1. 定义提示词模板，约束大模型输出格式
        String promptTemplate = """
            请识别用户输入的句子的意图和实体，并严格按照以下格式输出JSON：
            {
              "intent": "意图类型（如治疗方法、病因咨询、药品推荐等）",
              "entities": [
                {"name": "实体名称", "type": "实体类型（如症状、药品、疾病等）"}
              ]
            }
            
            规则：
            1. 意图类型需简洁明确，符合医疗咨询场景, 并且要使用我的意图枚举类型: <intent>
            2. 实体需提取句子中提到的关键信息（如症状、药品、疾病名称等）并且要使用我的实体枚举类型: <entity>
            3. 若无法识别，intent填"未知"，entities留空数组
            
            用户输入：<text>
            """;

        // 2. 构建提示词（替换模板中的{text}为用户输入）
        // 2. 构建提示词（使用 PromptTemplate）
        PromptTemplate promptTemplateObj = PromptTemplate.builder()
                .renderer(StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build())
                .template(promptTemplate)
                .build();


        // 用户查询：询问取消航班的注意事项
        String question = "失眠心慌吃人参滴丸行吗，不知道这是怎么回事，求医生指导疑惑？";

        // 渲染提示词，替换模板中的 <text> 为用户输入
        Map<String, Object> params = new HashMap<>();
        params.put("text", question);
        params.put("intent", IntentEnum.getAiPrompt());
        params.put("entity", MedicalEntityEnum.getAiPrompt());
        String prompt = promptTemplateObj.render(params);

        // 构建聊天客户端
        ChatClient chatClient = ChatClient.builder(dashScopeChatModel)
                .build();

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    @Test
    public void limitIntentRecognitionListTest() {
        System.out.println(recognize4());
    }

    public String recognize4(){
        // 1. 定义提示词模板，约束大模型输出格式
        String promptTemplate = """
        请识别用户输入的句子的意图和实体，并严格按照以下格式输出JSON数组：
        [
            {
              "intent": "意图类型（如治疗方法、病因咨询、药品推荐等）",
              "entities": [
                {"name": "实体名称", "type": "实体类型（如症状、药品、疾病等）"}
              ]
            }
        ]
        
        规则：
        1. 意图类型需简洁明确，符合医疗咨询场景, 并且要使用我的意图枚举类型: <intent>
        2. 实体需提取句子中提到的关键信息（如症状、药品、疾病名称等）并且要使用我的实体枚举类型: <entity>
        3. 若无法识别，intent填"未知"，entities留空数组
        4. 如果在连续的问题中未知名所提的对象, 你应从全文上下文理解用户所知名的对象是哪个病添加到此问题的entities中
        
        用户输入：<text>
        """;

        // 2. 构建提示词（替换模板中的{text}为用户输入）
        // 2. 构建提示词（使用 PromptTemplate）
        PromptTemplate promptTemplateObj = PromptTemplate.builder()
                .renderer(StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build())
                .template(promptTemplate)
                .build();

        // 用户查询：多个问题示例
        String questions = "失眠心慌吃人参滴丸行吗？ 它可能是什么疾病导致的呢？ 要吃什么药品治疗呢 ? 这个病应该挂那个科室?";

        // 渲染提示词，替换模板中的 <text> 为用户输入
        Map<String, Object> params = new HashMap<>();
        params.put("text", questions);
        params.put("intent", IntentEnum.getAiPrompt());
        params.put("entity", MedicalEntityEnum.getAiPrompt());
        String prompt = promptTemplateObj.render(params);

        System.out.println("提示词内容是:\n" + prompt);

        // 构建聊天客户端
        ChatClient chatClient = ChatClient.builder(dashScopeChatModel).build();

        // 获取模型响应

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }


    @Autowired
    private IntentRecognitionService intentRecognitionService;

    @Test
    public void intentRecognitionListToolOutputTest() {
        ToolCallback[] toolCallbacks = ToolCallbacks.from(intentRecognitionService);
        ChatOptions chatOptions = ToolCallingChatOptions.builder()
                .toolCallbacks(toolCallbacks)
                .build();

        // 1. 定义提示词模板，约束大模型输出格式
        String promptTemplate = """
        请识别用户输入的句子的意图和实体，如果跟医疗不相关你就回答你知识库没有相关答案的类似答案.
        如果是医疗相关的就请你调用intentRecognitionService的recognizeOutput方法来输出你识别的意图;
        你只需要调用我的内部的意图识别方法,如果识别成功就回复: 知识库已查到数据. 识别失败就知识库未查到数据.
        
        规则：
        1. 意图类型需简洁明确，符合医疗咨询场景, 并且要使用我的意图枚举类型: <intent>
        2. 实体需提取句子中提到的关键信息（如症状、药品、疾病名称等）并且要使用我的实体枚举类型: <entity>
        3. 若无法识别，intent填"未知"，entities留空数组
        4. 如果在连续的问题中未知名所提的对象, 你应从全文上下文理解用户所知名的对象是哪个病添加到此问题的entities中
        
        用户输入：<text>
        """;

        // 2. 构建提示词（使用 PromptTemplate）
        PromptTemplate promptTemplateObj = PromptTemplate.builder()
                .renderer(StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build())
                .template(promptTemplate)
                .build();

        // 用户查询：多个问题示例
        String questions = "失眠心慌吃人参滴丸行吗？ 它可能是什么疾病导致的呢？ 要吃什么药品治疗呢 ? 这个病应该挂那个科室?";

        // 渲染提示词，替换模板中的 <text> 为用户输入
        Map<String, Object> params = new HashMap<>();
        params.put("text", questions);
        params.put("intent", IntentEnum.getAiPrompt());
        params.put("entity", MedicalEntityEnum.getAiPrompt());
        String prompt = promptTemplateObj.render(params);

        System.out.println("提示词内容是:\n" + prompt);

        // 构建聊天客户端
        ChatClient chatClient = ChatClient.builder(dashScopeChatModel).build();

        Prompt prompt1 = new Prompt(questions, chatOptions);

        // 获取模型响应
        String response = chatClient.prompt()
                .user(prompt)
                .options(chatOptions)
                .call()
                .content();
        System.out.println("模型响应是:\n" + response);
    }
}
