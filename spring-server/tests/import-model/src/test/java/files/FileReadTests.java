package files;

import com.czy.imports.ImportsApplication;
import com.czy.imports.domain.ao.AuthorAo;
import com.czy.imports.manager.CrawlerDataManager;
import com.czy.imports.manager.FileReadManager;
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

}
