package com.ai.medicine.controller;

import com.ai.medicine.domain.constant.MedicalEntityEnum;
import com.ai.medicine.domain.constant.MedicalRelationEnum;
import com.ai.medicine.domain.dto.BaseResponse;
import com.ai.medicine.domain.dto.QuestionRequest;
import com.ai.medicine.service.mcp.MedicalQuestionToolService;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 13225
 * @date 2025/9/25 15:58
 */
@Slf4j
@CrossOrigin(origins = "*") // 跨域
@RestController
@Validated // 启用校验
@RequiredArgsConstructor // 自动注入@Autowired
@RequestMapping("/ai")
public class SearchKnowledgeController {

    private final MedicalQuestionToolService medicalQuestionToolService;
    private final DashScopeChatModel dashScopeChatModel;

    @PostMapping("/question")
    public BaseResponse<String> knowledgeSearch(@RequestBody QuestionRequest request){
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

        // 渲染提示词，替换模板中的 <text> 为用户输入
        Map<String, Object> params = new HashMap<>();
        params.put("text", request.question);
        params.put("rels", MedicalRelationEnum.getAiPrompt());
        params.put("entity", MedicalEntityEnum.getAiPrompt());
        String prompt = promptTemplateObj.render(params);

        log.info("提示词内容是:{}", prompt);

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

        return BaseResponse.getResponseEntitySuccess(response);
    }

}
