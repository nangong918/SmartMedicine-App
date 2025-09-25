import com.ai.medicine.MedicineAiApplication;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 13225
 * @date 2025/9/22 13:16
 * 对话 -> 意图识别 -> 问诊意图 -> (意图 + 实体识别) -> 实体替换(向量数据库匹配替换)
 * -> Neo4j查询 -> 返回的文本交给大模型 -> 生成回复
 */
@Slf4j
@SpringBootTest(classes = MedicineAiApplication.class)
@TestPropertySource("classpath:application.yml")
public class EntitySimilarityTests {

    @Autowired
    DashScopeEmbeddingModel dashScopeEmbeddingModel;
    @Autowired
    DashScopeChatModel dashScopeChatModel;

    @Test
    public void helloWorldTest() {
        log.info("EntitySimilarityTests");
        // 检查两个模型的型号
        log.info("dashScopeEmbeddingModel: {}", dashScopeEmbeddingModel);
        log.info("dashScopeChatModel: {}", dashScopeChatModel);
    }

    @Test
    public void entitySimilarityTest() {
        String symptom = "着凉";
        List<String> entities = List.of("小儿麻痹","着凉","难受","感冒","发烧", "咳嗽", " 伤风", " 身体发冷", " 冷感", "体寒", "嗓子痛", "发烧", "流鼻涕", "打喷嚏", "流涕", "宫寒");

        var vectorStore = SimpleVectorStore.builder(dashScopeEmbeddingModel).build();
        List<Document> documents = getDocumentsByStrings(entities);
        vectorStore.add(documents);

        var searchRequest = SearchRequest.builder()
                .query(symptom)
                .similarityThreshold(0.3d)
                .topK(20)
                .build();

        List<Document> results = vectorStore.similaritySearch(searchRequest);

        if (CollectionUtils.isEmpty(results)){
            log.info("没有找到匹配的文档");
        }
        else {
            for (Document document : results) {
                log.info("text: {}", document.getText());
                log.info("metadata: {}", document.getMetadata());
                log.info("score: {}", document.getScore());
            }
        }
    }

    private List<Document> getDocumentsByStrings(@NonNull List<String> strings){
        List<Document> documents = new ArrayList<>();
        for (String string : strings) {
            Document document = Document.builder()
                    .text(string)
                    .build();
            documents.add(document);
        }
        return documents;
    }

}
