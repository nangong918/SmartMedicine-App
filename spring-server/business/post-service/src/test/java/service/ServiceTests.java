package service;


import com.alibaba.fastjson.JSONArray;
import com.czy.api.domain.ao.post.AcTreeInfo;
import com.czy.api.domain.ao.post.PostNerResult;
import com.czy.post.PostServiceApplication;
import com.hankcs.algorithm.AhoCorasickDoubleArrayTrie;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author 13225
 * @date 2025/1/13 15:32
 */

@Slf4j
@SpringBootTest(classes = PostServiceApplication.class)
@TestPropertySource("classpath:application.properties")
public class ServiceTests {

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
    private final static String[] filePaths = {
            "checks.json",
            "departments.json",
            "diseases.json",
            "drugs.json",
            "foods.json",
//            "medical.json",  // TODO 存在问题，注意检查
            "producers.json",
            "recipes.json",
            "symptoms.json",
    };
    // 上述json存在问题：数据不完全：TODO 待修复
    private static String getAbsolutePath(String relativePath){
        // 获取当前项目的绝对路径
        String currentDir = System.getProperty("user.dir");
        // D:\CodeLearning\smart-medicine
        String projectRoot = Paths.get(currentDir).toString(); // 获取根目录
        return Paths.get(projectRoot, relativePath).toString();
    }

    private static final String testStr1 = "风热感冒是怎么造成的？是由于月经不调吗？吃阿莫西林能治疗好吗？";

    @SneakyThrows
    public static void main(String[] args) {
        HashMap<String, String> map = new HashMap<>();
        for (String filePath : filePaths){
            String relativePath = "/files/" + filePath;
            String absolutePath = getAbsolutePath(relativePath);
            String type = filePath.split("\\.")[0];
            map = loadData(type, absolutePath);
        }
        HashMap<String, AcTreeInfo> acTreeMap = new HashMap<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            AcTreeInfo acTreeInfo = new AcTreeInfo(key, value);
            acTreeMap.put(key, acTreeInfo);
        }
        String testKey = acTreeMap.entrySet().iterator().next().getKey();
        AcTreeInfo testValue = acTreeMap.entrySet().iterator().next().getValue();
        log.info("testKey：{}，testValue.key:{},testValue.value:{}", testKey, testValue.getKey(), testValue.getValue());
        AhoCorasickDoubleArrayTrie<AcTreeInfo> acTree = new AhoCorasickDoubleArrayTrie<>();
        acTree.build(acTreeMap);

        long start = System.currentTimeMillis();
        List<PostNerResult> results = getPostNerResults(
                testStr1,
                acTree
        );
        long end = System.currentTimeMillis();
        log.info("耗时：{}ms", end - start);
        log.info("results = {}", results);
    }

    public static List<PostNerResult> getPostNerResults(String postTitle, AhoCorasickDoubleArrayTrie<AcTreeInfo> acTree){
        if (StringUtils.hasText(postTitle) && postTitle.length() >= 2){
            List<PostNerResult> results = new ArrayList<>();
            acTree.parseText(postTitle, (begin, end, valueInfo) -> {
                PostNerResult result = new PostNerResult();
                result.setKeyWord(valueInfo.getKey());
                result.setNerType(valueInfo.getValue());
                results.add(result);
            });
            return results;
        }
        return new ArrayList<>();
    }
}
