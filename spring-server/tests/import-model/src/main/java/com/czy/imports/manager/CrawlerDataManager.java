package com.czy.imports.manager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author 13225
 * @date 2025/5/30 15:06
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class CrawlerDataManager {

    public static final String authorPath = "Author";
    public static final String authorFile = "0.txt";
    private final FileReadManager fileReadManager;

    // 去读路径下的二级文件夹，如果二级文件夹的名字的Author就打开，读取0.txt
    public void readCrawlerData(List<String> userNames) {
        // 获取爬取数据的路径
        String crawlerArticlePath = fileReadManager.crawlerArticlePath;
        // 一级路径的文件夹
        File[] crawler1Folder = fileReadManager.getFiles(crawlerArticlePath);
        if (crawler1Folder == null){
            log.error("爬取数据路径1不存在, 路径：{}", crawlerArticlePath);
            return;
        }
        for (File file1 : crawler1Folder) {
            if (file1.isDirectory()) {
                // 二级路径的文件夹
                File[] crawler2Folder = fileReadManager.getFiles(file1.getPath());
                if (crawler2Folder == null){
                    continue;
                }
                for (File file2 : crawler2Folder){
                    if (file2.isDirectory() && authorPath.equals(file2.getName())){
                        // 查看里面的authorFile
                        String fileContentPath = file2.getPath() + File.separator + authorFile;
                        try {
                            String fileContent = new String(
                                    Files.readAllBytes(
                                            Paths.get(fileContentPath)),
                                    StandardCharsets.UTF_8
                            );
                            userNames.add(fileContent);
                        } catch (IOException e) {
                            log.error("读取文件失败: {}", e.getMessage());
                        }
                    }
                }
            }
        }
    }
}
