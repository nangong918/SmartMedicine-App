package com.czy.imports.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author 13225
 * @date 2025/5/30 14:34
 */
@Slf4j
@Component
public class FileReadManager {

    // 项目绝对路径
    public static final String PROJECT_NAME = "smart-medicine";
    public final String projectPath = getProjectPath();
    public final String crawlerArticlePath = projectPath + File.separator + "爬取数据";

    private String getProjectPath() {
        String absolutePath = new File("").getAbsolutePath();

        // 查找项目名称的索引
        int index = absolutePath.indexOf(PROJECT_NAME);
        if (index != -1) {
            // 截取到项目名称的路径
            return absolutePath.substring(0, index + PROJECT_NAME.length());
        }
        return absolutePath; // 如果没有找到则返回原路径
    }

    public FileReadManager(){
        System.out.println(projectPath);
        System.out.println(crawlerArticlePath);
    }

    // 读取path下有多少个文件夹
    public static int getFolderCount(String path) {
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()){
            System.err.println("路径不存在");
            return 0;
        }
        File[] files = file.listFiles();
        if (files == null || files.length == 0){
            System.err.println("路径下没有文件夹");
            return 0;
        }
        return files.length;
    }

}
