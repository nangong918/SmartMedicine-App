package algorithm;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import lombok.Data;
import org.apache.commons.csv.*;

public class UserCF {

    // 评估指标实现
    public static double recall(Map<Integer, Set<Integer>> recDict,
                                Map<Integer, Set<Integer>> valDict) {
        int hitItems = 0;
        int allItems = 0;
        for (Map.Entry<Integer, Set<Integer>> entry : valDict.entrySet()) {
            Integer user = entry.getKey();
            Set<Integer> relSet = entry.getValue();
            Set<Integer> recSet = recDict.getOrDefault(user, Collections.emptySet());

            for (Integer item : recSet) {
                if (relSet.contains(item)) {
                    hitItems++;
                }
            }
            allItems += relSet.size();
        }
        return Math.round(hitItems * 100.0 / allItems * 100) / 100.0;
    }

    public static double precision(Map<Integer, Set<Integer>> recDict,
                                   Map<Integer, Set<Integer>> valDict) {
        int hitItems = 0;
        int allItems = 0;
        for (Map.Entry<Integer, Set<Integer>> entry : recDict.entrySet()) {
            Integer user = entry.getKey();
            Set<Integer> recSet = entry.getValue();
            Set<Integer> relSet = valDict.getOrDefault(user, Collections.emptySet());

            for (Integer item : recSet) {
                if (relSet.contains(item)) {
                    hitItems++;
                }
            }
            allItems += recSet.size();
        }
        return Math.round(hitItems * 100.0 / allItems * 100) / 100.0;
    }

    public static double coverage(Map<Integer, Set<Integer>> recDict,
                                  Map<Integer, Set<Integer>> trnDict) {
        Set<Integer> recItems = new HashSet<>();
        Set<Integer> allItems = new HashSet<>();

        trnDict.values().forEach(allItems::addAll);
        recDict.values().forEach(recItems::addAll);

        return Math.round(recItems.size() * 100.0 / allItems.size() * 100) / 100.0;
    }

    public static double popularity(Map<Integer, Set<Integer>> recDict,
                                    Map<Integer, Set<Integer>> trnDict) {
        Map<Integer, Integer> itemPopularity = new HashMap<>();

        // 计算训练集中每个物品的流行度
        for (Set<Integer> items : trnDict.values()) {
            for (Integer item : items) {
                itemPopularity.put(item, itemPopularity.getOrDefault(item, 0) + 1);
            }
        }

        double total = 0;
        int count = 0;
        for (Set<Integer> items : recDict.values()) {
            for (Integer item : items) {
                total += Math.log(itemPopularity.getOrDefault(item, 0) + 1);
                count++;
            }
        }
        return Math.round(total / count * 1000) / 1000.0;
    }

    // 数据加载与处理
    public static Map<Integer, Set<Integer>>[] loadData(String path) throws Exception {
        List<Rating> ratings = new ArrayList<>();
        try (Reader reader = new FileReader(path)) {
            // 使用 BufferedReader 逐行读取并分割
            BufferedReader br = new BufferedReader(reader);
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split("::"); // 使用::分割

                // 检查字段是否为空
                if (values.length < 4 || values[0].isEmpty() || values[1].isEmpty()) {
                    System.out.println("发现空值，跳过该记录: " + line);
                    continue; // 跳过该条记录
                }

                int userId = Integer.parseInt(values[0]);
                int movieId = Integer.parseInt(values[1]);
                ratings.add(new Rating(userId, movieId));
            }
        }

        // 分割训练集和验证集
        Collections.shuffle(ratings);
        int splitPoint = (int) (ratings.size() * 0.8);
        List<Rating> trnRatings = ratings.subList(0, splitPoint);
        List<Rating> valRatings = ratings.subList(splitPoint, ratings.size());

        Map<Integer, Set<Integer>> trnUserItems = groupByUser(trnRatings);
        Map<Integer, Set<Integer>> valUserItems = groupByUser(valRatings);

        Map<Integer, Set<Integer>>[] res = new Map[2];
        res[0] = trnUserItems;
        res[1] = valUserItems;

        return res;
    }

    private static Map<Integer, Set<Integer>> groupByUser(List<Rating> ratings) {
        return ratings.stream().collect(
                Collectors.groupingBy(Rating::getUserId,
                        Collectors.mapping(Rating::getMovieId, Collectors.toSet())));
    }

    // User-CF 核心算法
    public static Map<Integer, Set<Integer>> userCFRecommend(
            Map<Integer, Set<Integer>> trnUserItems,
            Map<Integer, Set<Integer>> valUserItems,
            int K, int N) {

        // 建立倒排表
        Map<Integer, Set<Integer>> itemUsers = new HashMap<>();
        for (Map.Entry<Integer, Set<Integer>> entry : trnUserItems.entrySet()) {
            Integer user = entry.getKey();
            for (Integer item : entry.getValue()) {
                itemUsers.computeIfAbsent(item, k -> new HashSet<>()).add(user);
            }
        }

        // 计算协同过滤矩阵
        Map<Integer, Map<Integer, Integer>> simMatrix = new HashMap<>();
        Map<Integer, Integer> itemCounts = new HashMap<>();

        for (Set<Integer> users : itemUsers.values()) {
            for (Integer u : users) {
                itemCounts.put(u, itemCounts.getOrDefault(u, 0) + 1);
                simMatrix.computeIfAbsent(u, k -> new HashMap<>());

                for (Integer v : users) {
                    if (!u.equals(v)) {
                        simMatrix.get(u).put(v,
                                simMatrix.get(u).getOrDefault(v, 0) + 1);
                    }
                }
            }
        }

        // 计算相似度
        Map<Integer, Map<Integer, Double>> similarity = new HashMap<>();
        for (Map.Entry<Integer, Map<Integer, Integer>> entry : simMatrix.entrySet()) {
            Integer u = entry.getKey();
            Map<Integer, Double> userSim = new HashMap<>();

            for (Map.Entry<Integer, Integer> inner : entry.getValue().entrySet()) {
                Integer v = inner.getKey();
                double score = inner.getValue() /
                        Math.sqrt(itemCounts.get(u) * itemCounts.get(v));
                userSim.put(v, score);
            }
            similarity.put(u, userSim);
        }

        // 生成推荐
        Map<Integer, Set<Integer>> recommendations = new HashMap<>();
        for (Integer user : valUserItems.keySet()) {
            PriorityQueue<Map.Entry<Integer, Double>> pq = new PriorityQueue<>(
                    (a, b) -> Double.compare(b.getValue(), a.getValue()));

            if (similarity.containsKey(user)) {
                similarity.get(user).entrySet().forEach(pq::add);
            }

            Set<Integer> candidates = new HashSet<>();
            for (int i = 0; i < K && !pq.isEmpty(); i++) {
                Integer similarUser = pq.poll().getKey();
                candidates.addAll(trnUserItems.getOrDefault(similarUser,
                        Collections.emptySet()));
            }

            // 过滤已交互物品并排序
            Set<Integer> interacted = trnUserItems.getOrDefault(user,
                    Collections.emptySet());
            Map<Integer, Double> itemScores = new HashMap<>();

            for (Integer item : candidates) {
                if (!interacted.contains(item)) {
                    double score = similarity.getOrDefault(user, Collections.emptyMap())
                            .entrySet().stream()
                            .filter(e -> trnUserItems.getOrDefault(e.getKey(),
                                    Collections.emptySet()).contains(item))
                            .mapToDouble(Map.Entry::getValue)
                            .sum();
                    itemScores.put(item, score);
                }
            }

            recommendations.put(user,
                    itemScores.entrySet().stream()
                            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                            .limit(N)
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toSet()));
        }

        return recommendations;
    }

    public static void main(String[] args) throws Exception {
        String relativePath = "spring-server/offline/recommend-offline-service/src/test/java/data/ml-1m/ratings.dat"; // 相对路径
        Path absolutePath = Paths.get(relativePath).toAbsolutePath(); // 转换为绝对路径

        Map<Integer, Set<Integer>>[] data = loadData(absolutePath.toString());
        System.out.println(data[0].size());
        Map<Integer, Set<Integer>> trnData = data[0];
        Map<Integer, Set<Integer>> valData = data[1];

        Map<Integer, Set<Integer>> recommendations = userCFRecommend(
                trnData, valData, 80, 10);

        System.out.printf("Recall: %.2f%%\n", recall(recommendations, valData));
        System.out.printf("Precision: %.2f%%\n", precision(recommendations, valData));
        System.out.printf("Coverage: %.2f%%\n", coverage(recommendations, trnData));
        System.out.printf("Popularity: %.3f\n", popularity(recommendations, trnData));
    }

    // 辅助类
    @Data
    static class Rating {
        private int userId;
        private int movieId;

        public Rating() {
        }

        public Rating(int userId, int movieId) {
            this.userId = userId;
            this.movieId = movieId;
        }

        // 构造函数、getters
    }

}
