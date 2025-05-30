package files;

import com.czy.imports.ImportsApplication;
import com.czy.imports.manager.FileReadManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * @author 13225
 * @date 2025/5/30 14:34
 */
@Slf4j
@SpringBootTest(classes = ImportsApplication.class)
@TestPropertySource("classpath:application.yml")
public class FIleReadTests {

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

}
