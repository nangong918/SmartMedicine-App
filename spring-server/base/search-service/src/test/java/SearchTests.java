import com.czy.search.SearchApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * @author 13225
 * @date 2025/4/24 17:26
 */

@Slf4j
@SpringBootTest(classes = SearchApplication.class)
@TestPropertySource("classpath:application.yml")
public class SearchTests {

    @Test
    public void test() {
        log.info("test");
    }

    // 零级搜索：通过like模糊匹配

    /**
     * 关于二级模糊匹配：
     * 1. 句子分词；将句子进行分词；
     * 2. 由于限制句子长度为15个，所以关键词的长的数量一定小于7；
     * 3. 用elasticSearch匹配这15个词语，只要找到一个内容包含2个以上的关键词就返回。
     */

}
