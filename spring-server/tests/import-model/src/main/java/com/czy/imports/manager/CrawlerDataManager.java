package com.czy.imports.manager;

import com.alibaba.fastjson.JSON;
import com.czy.imports.domain.Do.ArticleDo;
import com.czy.imports.domain.ao.AuthorAo;
import com.czy.imports.domain.ao.AuthorInfoAo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    public static final String authorImagePath = "0.png";
    public static final String articleDataString = "data.json";
    private final FileReadManager fileReadManager;

    // 去读路径下的二级文件夹，如果二级文件夹的名字的Author就打开，读取0.txt
    public List<AuthorAo> readCrawlerAuthorData() {
        List<AuthorAo> authorAos = new ArrayList<>();
        // 获取爬取数据的路径
        String crawlerArticlePath = fileReadManager.crawlerArticlePath;
        // 一级路径的文件夹
        File[] crawler1Folder = fileReadManager.getFiles(crawlerArticlePath);
        if (crawler1Folder == null){
            log.error("爬取数据路径1不存在, 路径：{}", crawlerArticlePath);
            return authorAos;
        }
        // author级别for循环
        for (File file1 : crawler1Folder) {
            if (file1.isDirectory()) {
                AuthorAo authorAo = new AuthorAo();
                AuthorInfoAo authorInfoAo = new AuthorInfoAo();
                // 二级路径的文件夹
                File[] crawler2Folder = fileReadManager.getFiles(file1.getPath());
                if (crawler2Folder == null){
                    continue;
                }
                // info级别for循环
                List<ArticleDo> articleDos = new ArrayList<>();
                for (File file2 : crawler2Folder){
                    boolean baseLegal = (file2.isDirectory() &&
                            file2.listFiles() != null &&
                            Objects.requireNonNull(file2.listFiles()).length > 0);
                    if (baseLegal &&
                            authorPath.equals(file2.getName())){
                        // 查看里面的authorFile
                        String fileContentPath = file2.getPath() + File.separator + authorFile;
                        try {
                            String fileContent = new String(
                                    Files.readAllBytes(
                                            Paths.get(fileContentPath)),
                                    StandardCharsets.UTF_8
                            );
                            authorInfoAo.setUserName(fileContent);
                        } catch (IOException e) {
                            log.error("读取文件失败: {}", e.getMessage());
                        }
                        String imagePath = file2.getPath() + File.separator + authorImagePath;
                        // 检查file是否存在，存在加入到list，不存在加入null，用为要保证数据数量相等，数据一一对应
                        if (new File(imagePath).exists()){
                            authorInfoAo.setUserImagePath(imagePath);
                        }
                        else {
                            authorInfoAo.setUserImagePath(null);
                        }
                        authorAo.setAuthorInfoAo(authorInfoAo);
                    }
                    else if (baseLegal &&
                            isNumeric(file2.getName())){
                        String imagePath = file2.getPath() + File.separator + authorImagePath;
                        String dataPath = file2.getPath() + File.separator + articleDataString;

                        try {
                            // 读取 JSON 文件内容
                            String jsonString = new String(Files.readAllBytes(Paths.get(dataPath)));
                            // 将 JSON 字符串转换为 ArticleDo 对象
                            ArticleDo article = JSON.parseObject(jsonString, ArticleDo.class);
                            article.setArticleImagePath(imagePath); // 设置图片路径

                            articleDos.add(article);

                        } catch (IOException e) {
                            log.warn("读取文件失败: {}", file2.getName(), e);
                            continue;
                        }
                    }
                }
                authorAo.setArticleDos(articleDos);
                authorAos.add(authorAo);
            }
        }
        return authorAos;
    }

    // 辅助方法：检查字符串是否为数字
    private static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        String path = "D:\\CodeLearning\\smart-medicine\\爬取数据\\内科李大夫\\0\\data.json";
        ArticleDo articleDo = new ArticleDo();
        try {
            // 读取 JSON 文件内容
            String jsonString = new String(Files.readAllBytes(Paths.get(path)));
            // 将 JSON 字符串转换为 ArticleDo 对象
            articleDo = JSON.parseObject(jsonString, ArticleDo.class);
            // 打印结果
            System.out.println(articleDo);
        } catch (IOException e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
        }
    }
}
