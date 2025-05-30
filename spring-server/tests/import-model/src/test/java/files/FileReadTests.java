package files;

import com.czy.imports.ImportsApplication;
import com.czy.imports.manager.CrawlerDataManager;
import com.czy.imports.manager.FileReadManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
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
        List<String> userNames = new ArrayList<>();
        List<String> userImagePaths = new ArrayList<>();
        crawlerDataManager.readCrawlerAuthorData(userNames, userImagePaths);
        assert userNames.size() == userImagePaths.size();
        for (int i = 0; i < userNames.size(); i++){
            System.out.println(userNames.get(i) + "\n" + userImagePaths.get(i));
        }
    }

}
