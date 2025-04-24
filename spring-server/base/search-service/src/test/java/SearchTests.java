import com.czy.api.domain.Do.test.TestSearchDo;
import com.czy.api.domain.Do.test.TestSearchEsDo;
import com.czy.search.SearchApplication;
import com.czy.search.mapper.es.TestSearchEsMapper;
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
     * 零级匹配
     * 关键词级别匹配，使用左右相等去匹配
     */
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

    /**
     * 一级匹配
     * ElasticSearch倒排索引匹配
     */
    @Autowired
    private TestSearchEsMapper testSearchEsMapper;
    @Test
    public void testEsInsert(){
        for (int i = 0; i < testContent.length; i++) {
            TestSearchEsDo testSearchDo = new TestSearchEsDo();
            testSearchDo.setSearchName(testContent[i]);
            testSearchDo.setId(i + 1L);
            testSearchEsMapper.save(testSearchDo);
        }
    }

    @Test
    public void testEsDelete(){
        testSearchEsMapper.deleteAll();
    }

    @Test
    public void testEsPrintAll(){
        Iterable<TestSearchEsDo> testSearchDo = testSearchEsMapper.findAll();
        testSearchDo.forEach(item -> System.out.println(item.toJsonString()));
    }

    @Test
    public void testEsLikeSearch(){
        List<TestSearchEsDo> testSearchDo = testSearchEsMapper.findBySearchNameContaining("新冠病毒");
        List<TestSearchEsDo> testSearchDo1 = testSearchEsMapper.findBySearchNameContaining("病毒");
        List<TestSearchEsDo> testSearchDo2 = testSearchEsMapper.findBySearchNameContaining("新冠");
        testSearchDo.forEach(item -> System.out.println(item.toJsonString()));
        testSearchDo1.forEach(item -> System.out.println(item.toJsonString()));
        testSearchDo2.forEach(item -> System.out.println(item.toJsonString()));
    }

    // TODO 此处失败
    @Test
    public void testEsLikeMain(){
        testEsDelete();
        testEsInsert();
        testEsPrintAll();
        System.out.println("=========testEsLikeSearch=========");
        testEsLikeSearch();
    }

    /**
     * 关于二级模糊匹配：
     * 1. 句子分词；将句子进行分词；
     * 2. 由于限制句子长度为15个，所以关键词的长的数量一定小于7；
     * 3. 用elasticSearch匹配这15个词语，只要找到一个内容包含2个以上的关键词就返回。
     */

}
