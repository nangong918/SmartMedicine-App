import com.czy.api.domain.Do.test.TestSearchDo;
import com.czy.search.SearchApplication;
import com.czy.search.mapper.TestSearchMapper;
import com.czy.search.mapper.TestSearchMongoMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/24 17:26
 */

@Slf4j
@SpringBootTest(classes = SearchApplication.class)
@TestPropertySource("classpath:application.yml")
public class SearchTests {

    // 零级搜索：通过like模糊匹配
    public static final String[] testContent = {
            "新冠病毒如何治疗",
            "新冠病毒最佳治疗方法",
            "普通感冒治疗",
            "新馆病毒致死率是多少",
            "管状病毒感染如何治疗",
            "季节性流感应该如何治疗？"
    };
    @Test
    public void test() {
        log.info("test");
    }


    /**
     * 关于二级模糊匹配：
     * 1. 句子分词；将句子进行分词；
     * 2. 由于限制句子长度为15个，所以关键词的长的数量一定小于7；
     * 3. 用elasticSearch匹配这15个词语，只要找到一个内容包含2个以上的关键词就返回。
     */


    // 零级匹配
    @Autowired
    private TestSearchMapper testSearchMapper;
    @Autowired TestSearchMongoMapper testSearchMongoMapper;
    @Test
    public void testMysqlInsert(){
        for (String s : testContent) {
            TestSearchDo testSearchDo = new TestSearchDo();
            testSearchDo.setSearchName(s);
            testSearchMapper.insert(testSearchDo);
        }
    }

    @Test
    public void testMongoInsert(){
        for (int i = 0; i < testContent.length; i++) {
            TestSearchDo testSearchDo = new TestSearchDo();
            testSearchDo.setSearchName(testContent[i]);
            testSearchDo.setId(i + 1L);
            testSearchMongoMapper.saveSearchTestDo(testSearchDo);
        }
    }

    @Test
    public void deleteMongoAll(){
        testSearchMongoMapper.deleteSearchTestDoAll();
    }

    @Test
    public void testMysqlLikeSearch(){
        List<TestSearchDo> testSearchDo = testSearchMapper.selectByLikeName("新冠病毒");
        testSearchDo.forEach(item -> System.out.println(item.toJsonString()));
    }

    @Test
    public void testMongoLikeSearch(){
        List<TestSearchDo> testSearchDo = testSearchMongoMapper.findSearchTestDoByLikeName("新冠病毒");
        testSearchDo.forEach(item -> System.out.println(item.toJsonString()));
    }

}
