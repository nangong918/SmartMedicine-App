package files;

import cn.hutool.core.util.IdUtil;
import com.czy.api.domain.Do.neo4j.TestNeo4jDo;
import com.czy.api.domain.Do.neo4j.UserFeatureNeo4jDo;
import com.czy.api.domain.Do.user.LoginUserDo;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.mapper.TestRepository;
import com.czy.api.mapper.UserFeatureRepository;
import com.czy.imports.ImportsApplication;
import com.czy.imports.domain.ao.AuthorAo;
import com.czy.imports.manager.CrawlerDataManager;
import com.czy.imports.manager.FileReadManager;
import com.czy.imports.mapper.LoginUserMapper;
import com.czy.imports.mapperEs.UserEsMapper;
import com.czy.imports.service.ImportAuthorService;
import domain.FileOptionResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

/**
 * @author 13225
 * @date 2025/5/30 14:34
 */
@Slf4j
@SpringBootTest(classes = ImportsApplication.class)
@TestPropertySource("classpath:application.yml")
public class FileReadTests {

    @Test
    public void helloWorld() {
        log.info("helloWorld");
    }

    @Autowired
    private FileReadManager fileReadManager;

    @Test
    public void checkPath(){
        System.out.println(fileReadManager.projectPath);
        System.out.println(fileReadManager.crawlerArticlePath);
    }

    @Test
    public void checkFolderCount(){
        int count = FileReadManager.getFolderCount(fileReadManager.crawlerArticlePath);
        System.out.println(count);
    }

    @Autowired
    private CrawlerDataManager crawlerDataManager;

    @Test
    public void readAuthorNames(){

        List<AuthorAo> userNames = crawlerDataManager.readCrawlerAuthorData();
        if (!CollectionUtils.isEmpty(userNames)){
            System.out.println(userNames.size());
            AuthorAo authorAo = userNames.get(0);
            System.out.println(authorAo.toJsonString());
        }
    }

    @Autowired
    private ImportAuthorService importAuthorService;

    private final static String testBucketName = "author-file-test";

    @Test
    public void storageFileTest(){
        String filePath = "D:\\CodeLearning\\smart-medicine\\爬取数据\\养生杂志\\5\\0.png";
        FileOptionResult result = importAuthorService.uploadFiles(
                filePath,
                testBucketName
        );
        System.out.println(result.toJsonString());
    }

    @Autowired
    private LoginUserMapper loginUserMapper;
    @Autowired
    private UserEsMapper userEsMapper;
    @Autowired
    private UserFeatureRepository userFeatureRepository;

    // TODO fix
    @Test
    public void storageUserTest(){
        importAuthorService.createUser(
                "test",
                "test",
                "test",
                102524534L
        );
        LoginUserDo loginUserDo = loginUserMapper.getLoginUserByAccount("test");
        System.out.println(loginUserDo.toJsonString());
        List<UserDo> userDos = userEsMapper.findByUserNameContaining("test");
        for (UserDo userDo : userDos) {
            System.out.println(userDo.toJsonString());
        }
        UserFeatureNeo4jDo userFeatureNeo4jDo = userFeatureRepository.findByAccount("test");
        System.out.println(userFeatureNeo4jDo.toJsonString());
    }

    // TODO fix
    @Test
    public void storageToNeo4jTest(){
        UserFeatureNeo4jDo userFeatureNeo4jDo = new UserFeatureNeo4jDo();
        userFeatureNeo4jDo.setId(123L);
        userFeatureNeo4jDo.setName("test");
        userFeatureNeo4jDo.setAccount("test");
        userFeatureRepository.save(userFeatureNeo4jDo);
        System.out.println("userFeatureNeo4jDo = " + userFeatureNeo4jDo.toJsonString());

        UserFeatureNeo4jDo userFeatureNeo4jDo1 = userFeatureRepository.findByName("test");
        System.out.println(userFeatureNeo4jDo1.toJsonString());
    }

    @Autowired
    private TestRepository testRepository;

    // TODO fix
    @Test
    public void neo4jTest(){
        TestNeo4jDo testNeo4jDo = new TestNeo4jDo();
        testNeo4jDo.setName("test");
        testNeo4jDo.setAccount("test");
        Long id = IdUtil.getSnowflakeNextId();
        testNeo4jDo.setId(id);
        testRepository.save(testNeo4jDo);
        System.out.println("testNeo4jDo = " + testNeo4jDo.toJsonString());

        Optional<TestNeo4jDo> testNeo4jDo1 = testRepository.findById(id);
        testNeo4jDo1.ifPresent(neo4jDo ->
                System.out.println("testNeo4jDo1 = " + neo4jDo.toJsonString())
        );
        Optional<TestNeo4jDo> testNeo4jDo2 = testRepository.findByAccount("test");
        testNeo4jDo2.ifPresent(neo4jDo ->
                System.out.println("testNeo4jDo2 = " + neo4jDo.toJsonString())
        );
    }

    /**
     * 单个数据导入测试：
     * 1.上传file（头像 + 文章）到minIO的oss + mysql的oss_info
     * 2.创建user信息存储到login_user;es;neo4j
     * 3.创建post信息，mysql存储到post_info和post_files；postDetail->mongodb;postTitle->es
     */
    @Test
    public void singleDataImportTest(){
        List<AuthorAo> userNames = crawlerDataManager.readCrawlerAuthorData();
        AuthorAo authorAo = userNames.get(10);
    }

}
