package ac;

import com.alibaba.fastjson.JSONArray;
import com.czy.api.domain.ao.post.AcTreeInfo;
import com.hankcs.algorithm.AhoCorasickDoubleArrayTrie;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 13225
 * @date 2025/5/5 13:45
 */

public class AcTreeTest {
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
    @Test
    public void testPath() {
        // 获取当前项目的绝对路径
        String currentDir = System.getProperty("user.dir");
        String projectRoot = Paths.get(currentDir).getParent().getParent().toString(); // 获取根目录

        String relativePath = "/files/diseases.json";
        String absolutePath = Paths.get(projectRoot, relativePath).toString();

        System.out.println("absolutePath = " + absolutePath);

        for (String filePath : filePaths){
            String type = filePath.split("\\.")[0];
            System.out.println("type = " + type);
        }
    }

    @Test
    public void test() {
        // 获取当前项目的绝对路径
        String currentDir = System.getProperty("user.dir");
        String projectRoot = Paths.get(currentDir).getParent().getParent().toString(); // 获取根目录

        String relativePath = "/files/diseases.json";
        String absolutePath = Paths.get(projectRoot, relativePath).toString();

        Map<String, AcTreeInfo> diseasesMap = loadDiseases(absolutePath);
        System.out.println(diseasesMap.size());

        // 构建字典树
        AhoCorasickDoubleArrayTrie<AcTreeInfo> act = new AhoCorasickDoubleArrayTrie<>();
        act.build(diseasesMap);

        // 关键词提取
        List<AcTreeInfo> foundDiseases = new ArrayList<>();
        String textToSearch = "我得了感冒，怎么治疗？";
        act.parseText(textToSearch, (begin, end, value) -> {
            System.out.printf("[%d:%d]=%s\n", begin, end, value);
            // 将找到的词汇存入 Map
            foundDiseases.add(value);
        });

        if (!foundDiseases.isEmpty()) {
            foundDiseases.forEach(item -> {
                System.out.printf("Key: %s, Value: %s\n", item.getKey(), item.getValue());
            });
        }
    }

    private static Map<String, AcTreeInfo> loadDiseases(String path) {
        Map<String, AcTreeInfo> map = new HashMap<>();
        StringBuilder jsonString = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(Paths.get(path).toAbsolutePath().toString()))) {
            String line;
            while ((line = br.readLine()) != null) {
                jsonString.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 使用 fastjson 解析 JSON
        JSONArray jsonArray = JSONArray.parseArray(jsonString.toString());
        for (int i = 0; i < jsonArray.size(); i++) {
            String disease = jsonArray.getString(i);
            map.put(disease, new AcTreeInfo(disease, "diseases"));
        }
        return map;
    }

}
