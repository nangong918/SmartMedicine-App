package files;

import com.czy.imports.ImportsApplication;
import com.czy.imports.domain.ao.AuthorAo;
import com.czy.imports.manager.CrawlerDataManager;
import com.czy.imports.manager.FileReadManager;
import com.czy.imports.service.ImportAuthorService;
import domain.FileOptionResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.CollectionUtils;

import java.util.List;

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


    /**
     * 单个数据导入测试：
     * 1.上传file（头像 + 文章）到minIO的oss + mysql的oss_info
     * 2.创建user信息存储到login_user
     * 3.创建post信息，mysql存储到post_info和post_files；postDetail->mongodb;postTitle->es
     */
    @Test
    public void singleDataImportTest(){

    }

}
