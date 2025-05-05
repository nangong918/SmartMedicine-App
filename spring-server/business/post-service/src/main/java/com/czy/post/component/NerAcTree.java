package com.czy.post.component;

import com.alibaba.fastjson.JSONArray;
import com.utils.mvc.config.NerConstant;
import com.utils.mvc.domain.IsLoadNerFilesAo;
import com.utils.mvc.redisson.RedissonClusterLock;
import com.utils.mvc.redisson.RedissonService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 13225
 * @date 2025/5/5 13:46
 * 实体命名识别的AcTree自动机
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class NerAcTree {

    private final RedissonService redissonService;

    /**
     * 获取绝对路径，此文件的路径不能更换，否则此路径的逻辑需要改变
     * @param relativePath  相对路径
     * @return
     */
    private static String getAbsolutePath(String relativePath){
        // 获取当前项目的绝对路径
        String currentDir = System.getProperty("user.dir");
        // D:\CodeLearning\smart-medicine
        String projectRoot = Paths.get(currentDir).toString(); // 获取根目录
        System.out.println("projectRoot = " + projectRoot);
        return Paths.get(projectRoot, relativePath).toString();
    }

    public static void main(String[] args) {
        String relativePath = "/spring-server/files/diseases.json";
        String absolutePath = getAbsolutePath(relativePath);
        System.out.println(absolutePath);

        Map<String, String> diseasesMap = new HashMap<>();
        try {
            diseasesMap = loadData("diseases", absolutePath);
        } catch (Exception e) {
            log.error("Error loading json files: ", e);
        }
        System.out.println(diseasesMap.size());
    }

    private static HashMap<String, String> loadData(@NonNull String type, String path) throws Exception{
        HashMap<String, String> map = new HashMap<>();
        StringBuilder jsonString = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(Paths.get(path).toAbsolutePath().toString()))) {
            String line;
            while ((line = br.readLine()) != null) {
                jsonString.append(line);
            }
        }

        // 使用 fastjson 解析 JSON
        JSONArray jsonArray = JSONArray.parseArray(jsonString.toString());
        for (int i = 0; i < jsonArray.size(); i++) {
            String v = jsonArray.getString(i);
            map.put(v, type);
        }
        return map;
    }

    private void loadToRedis(@NonNull String type, String path){
        try {
            HashMap<String, String> map = loadData(type, path);
            String key = NerConstant.NER_REDIS_KEY;
            redissonService.saveHashMap(key, map, NerConstant.NER_REDIS_EXPIRE_TIME);
        } catch (Exception e) {
            log.error("Error loading json files: ", e);
        }
    }

    private final static String[] filePaths = {
            "checks.json",
            "departments.json",
            "diseases.json",
            "drugs.json",
            "foods.json",
            "medical.json",
            "producers.json",
            "recipes.json",
            "symptoms.json",
        };

    @PostConstruct
    public void init(){
        IsLoadNerFilesAo ao;
        try {
            ao = redissonService.getObjectFromJson(NerConstant.IS_LOAD_NER_FILES_KEY, IsLoadNerFilesAo.class);
        } catch (Exception e){
            log.error("初始化ner失败", e);
            return;
        }
        // 还没加载文件数据到redis
        if (ao != null && !ao.getIsLoadNerFiles()){
            RedissonClusterLock redissonClusterLock = new RedissonClusterLock(
                    "ner",
                    "ner"
            );
            if (redissonService.tryLock(redissonClusterLock)){
                try {
                    for (String filePath : filePaths){
                        String relativePath = "/spring-server/files/" + filePath;
                        String absolutePath = getAbsolutePath(relativePath);
                        String type = filePath.split("\\.")[0];
                        loadToRedis(type, absolutePath);
                    }
                    ao = new IsLoadNerFilesAo(true);
                    redissonService.setObjectByJson(
                            NerConstant.IS_LOAD_NER_FILES_KEY,
                            ao,
                            NerConstant.NER_REDIS_EXPIRE_TIME
                    );
                } catch (Exception e){
                    log.error("初始化ner失败", e);
                    redissonService.unlock(redissonClusterLock);
                }
            }
        }
        else {
            log.info("已加载ner文件数据到redis");
        }
    }

}
