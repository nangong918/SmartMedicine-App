package files;

import cn.hutool.core.util.IdUtil;
import com.czy.api.domain.Do.neo4j.ProducersDo;
import com.czy.api.domain.Do.neo4j.TestNeo4jDo;
import com.czy.api.domain.Do.neo4j.UserFeatureNeo4jDo;
import com.czy.api.mapper.ProducersRepository;
import com.czy.api.mapper.TestRepository;
import com.czy.api.mapper.UserFeatureRepository;
import com.czy.imports.ImportsApplication;
import com.czy.imports.domain.Do.ArticleDo;
import com.czy.imports.domain.ao.AuthorAo;
import com.czy.imports.manager.CrawlerDataManager;
import com.czy.imports.manager.FileReadManager;
import com.czy.imports.service.ImportAuthorService;
import domain.FileOptionResult;
import domain.SuccessFile;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    private final static String testAuthorBucketName = "author-file-test";
    private final static String testPostBucketName = "post-file-test";

    @Test
    public void storageFileTest(){
        String filePath = "D:\\CodeLearning\\smart-medicine\\爬取数据\\养生杂志\\5\\0.png";
        FileOptionResult result = importAuthorService.uploadFiles(
                filePath,
                testAuthorBucketName
        );
        System.out.println(result.toJsonString());
    }

//    @Autowired
//    private LoginUserMapper loginUserMapper;
//    @Autowired
//    private UserEsMapper userEsMapper;
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
//        LoginUserDo loginUserDo = loginUserMapper.getLoginUserByAccount("test");
//        System.out.println(loginUserDo.toJsonString());
//        List<UserDo> userDos = userEsMapper.findByUserNameContaining("test");
//        for (UserDo userDo : userDos) {
//            System.out.println(userDo.toJsonString());
//        }
//        UserFeatureNeo4jDo userFeatureNeo4jDo = userFeatureRepository.findByAccount("test");
//        System.out.println(userFeatureNeo4jDo.toJsonString());
    }

    @Test
    public void storageToNeo4jTest(){
        UserFeatureNeo4jDo userFeatureNeo4jDo = new UserFeatureNeo4jDo();
        userFeatureNeo4jDo.setId(123L);
        userFeatureNeo4jDo.setName("test");
        userFeatureNeo4jDo.setAccount("test");
        userFeatureRepository.save(userFeatureNeo4jDo);
        System.out.println("userFeatureNeo4jDo = " + userFeatureNeo4jDo.toJsonString());

        Optional<UserFeatureNeo4jDo> result = userFeatureRepository.findByName("test");
        result.ifPresent(neo4jDo ->
                System.out.println("neo4jDo = " + neo4jDo.toJsonString())
        );
    }

    @Autowired
    private TestRepository testRepository;

    @Test
    public void neo4jTest(){
        TestNeo4jDo testNeo4jDo = new TestNeo4jDo();
        testNeo4jDo.setName("test1");
        testNeo4jDo.setAccount("test1");
        Long id = IdUtil.getSnowflakeNextId();
        testNeo4jDo.setTestId(id);
        testRepository.save(testNeo4jDo);
        System.out.println("testNeo4jDo = " + testNeo4jDo.toJsonString());

        Optional<TestNeo4jDo> testNeo4jDo1 = testRepository.findByTestId(id);
        testNeo4jDo1.ifPresent(neo4jDo ->
                System.out.println("testNeo4jDo1 = " + neo4jDo.toJsonString())
        );
        Optional<TestNeo4jDo> testNeo4jDo2 = testRepository.findByAccount("test1");
        testNeo4jDo2.ifPresent(neo4jDo ->
                System.out.println("testNeo4jDo2 = " + neo4jDo.toJsonString())
        );
    }

    private Long startTestPhone = 18911029182L;

    /**
     * 单个数据导入测试：
     * 1.上传file（头像 + 文章）到minIO的oss + mysql的oss_info
     * 2.创建user信息存储到login_user;es;neo4j
     * 3.创建post信息，mysql存储到post_info和post_files；postDetail->mongodb;postTitle->es
     * 用nacos + dubbo调用方法。
     * TODO user - post的发布关系
     */
    @Test
    public void singleDataImportTest(){
        List<AuthorAo> userNames = crawlerDataManager.readCrawlerAuthorData();
        AuthorAo minUser = null;
        int currentNum = Integer.MAX_VALUE;
        // 选取数量最小的用于测试
        for (AuthorAo user : userNames){
            if (user != null && user.getArticleDos().size() < currentNum){
                minUser = user;
                currentNum = user.getArticleDos().size();
            }
        }
        if (minUser == null){
            return;
        }
        Long authorImageId = null;
        // 1. 上传头像并获取id
        String authorImagePath = minUser.getAuthorInfoAo().getUserImagePath();
        if (StringUtils.hasText(authorImagePath)){
            FileOptionResult result = importAuthorService.uploadFiles(
                    authorImagePath,
                    testAuthorBucketName
            );
            if (!result.getSuccessFiles().isEmpty()){
                authorImageId = result.getSuccessFiles().get(0).getFileId();
            }
        }
        // 2. 上传postFileList
        // 2.1 获取postFileList
        List<ArticleDo> articleDos = minUser.getArticleDos();
        List<List<Long>> postFilesIds = new ArrayList<>(articleDos.size());
        for (ArticleDo articleDo : articleDos){
            String articleImagePath = articleDo.getArticleImagePath();
            if (StringUtils.hasText(articleImagePath)){
                FileOptionResult result = importAuthorService.uploadFiles(
                        articleImagePath,
                        testPostBucketName
                );
                if (!result.getSuccessFiles().isEmpty()){
                    List<Long> postFileIds = result.getSuccessFiles().stream()
                            .map(SuccessFile::getFileId)
                            .collect(Collectors.toList());
                    postFilesIds.add(postFileIds);
                }
                else {
                    postFilesIds.add(new ArrayList<>());
                }
            }
            else {
                postFilesIds.add(new ArrayList<>());
            }
        }

        // 3.创建user
        long userId = importAuthorService.createUser(
                minUser.getAuthorInfoAo().getUserName(),
                minUser.getUserAccount(),
                String.valueOf(startTestPhone),
                authorImageId
        );

        // 4.创建post
        for (int i = 0; i < articleDos.size(); i++){
            ArticleDo articleDo = articleDos.get(i);
            importAuthorService.createPost(
                    articleDo.getTitle(),
                    articleDo.getContent(),
                    getTimestamp(articleDo.getTime()),
                    postFilesIds.get(i),
                    userId
            );
        }

    }
    /*
        删除neo4j数据
        MATCH (u:user)
        DELETE u;
        MATCH (p:post)
        DELETE p;
     */

    // "2022-07-09 10:52" -> long timestamp
    private static long getTimestamp(String time){
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = simpleDateFormat.parse(time);
            return date.getTime();
        }
        catch (Exception e){
            return 0L;
        }
    }

    public static void main(String[] args) {
        System.out.println(getTimestamp("2022-07-09 10:52"));
    }

    @Test
    public void neo4jFindUserTest(){
        Long userId = 1930195272489353216L;
        Optional<UserFeatureNeo4jDo> userResult = userFeatureRepository.findByUserId(userId);
        userResult.ifPresent(userFeatureNeo4jDo -> System.out.println("userResult = " + userFeatureNeo4jDo.toJsonString()));
    }

    @Autowired
    private ProducersRepository producersRepository;

    @Test
    public void neo4jFindEntityTest(){
        ProducersDo existingProducers = producersRepository.findByName("新");
        System.out.println("existingProducers = " + existingProducers.toJsonString());
    }

}
