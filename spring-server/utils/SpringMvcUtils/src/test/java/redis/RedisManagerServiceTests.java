package redis;

import json.BaseBean;
import com.czy.springUtils.redis.SysCache;
import com.czy.springUtils.service.RedisManagerService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.DataType;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import com.utils.mvc.SpringMvcApplication;

/**
 * @author 13225
 * @date 2024/12/31 11:51
 */
@Slf4j
@SpringBootTest(classes = SpringMvcApplication.class)
@TestPropertySource("classpath:application.properties")
public class RedisManagerServiceTests {

    @Autowired
    private RedisManagerService redisManagerService;
    @Test
    public void test() {
        log.info("test");
        log.info("redisManagerService: {}", redisManagerService);
    }

    private final String testKey = "testKey";
//    @BeforeEach
//    public void setUp() {
//        // 清除 Redis 中的测试键，确保每次测试都是独立的
//        redisManagerService.deleteAny(testKey);
//    }

    /**
     * 测试添加对象到 Redis
     */
    @Test
    public void addRedisTest() {
        redisManagerService.setObjectAsString(testKey, new MyObject("Test Name", 123), 60L);
        try {
            MyObject myObject = redisManagerService.getObjectFromString(testKey, MyObject.class);
            log.info("Successfully retrieved object: {}", myObject);
        } catch (Exception e){
            log.error("Error getting object from string in Redis", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 测试 Redis 的基本操作
     *  添加
     *  获取
     *  移除
     *  设置过期时间
     *  获取剩余过期时间
     *  移除过期时间
     */
    @Test
    public void redisTest1() {
        // 添加一个对象到 Redis
        addRedisTest();

        // 测试设置过期时间
        boolean isExpiredSet = redisManagerService.expireKey(testKey, 30L);
        if (!isExpiredSet) {
            log.error("Failed to set expiration on key: {}", testKey);
        }
        else {
            log.info("Expiration time set successfully for key: {}", testKey);
        }

        // 检查键是否存在
        if (!redisManagerService.hasKey(testKey)) {
            log.error("Key should exist after setting it: {}", testKey);
        }
        else {
            log.info("Key: {} exists and has an expiration time.", testKey);
        }

        // 获取剩余过期时间
        long remainingTime = 0;
        try {
            remainingTime = redisManagerService.getExpireKey(testKey);
            log.info("Remaining expiration time: {} seconds", remainingTime);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (remainingTime <= 0) {
            log.error("Remaining expiration time should be greater than 0 for key: {}", testKey);
        }

        // 移除过期时间
        boolean isPersisted = false;
        try {
            isPersisted = redisManagerService.persist(testKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (!isPersisted) {
            log.error("Failed to remove expiration from key: {}", testKey);
        }

        // 再次检查键的存在性
        if (!redisManagerService.hasKey(testKey)) {
            log.error("Key should still exist after removing expiration: {}", testKey);
        }

        // 检查过期时间是否为 -1（表示没有过期时间）
        long newRemainingTime = 0;
        try {
            newRemainingTime = redisManagerService.getExpireKey(testKey);
        } catch (Exception e) {
            log.error("Failed to get remaining expiration time for key: {}", testKey);
        }
        if (newRemainingTime != -1) {
            log.error("Expiration time should be -1 after persistence for key: {}", testKey);
        }
    }

    private final String testKeyString = "testKeyString";
    private final String testKeyHash = "testKeyHash";

    /**
     * 测试将对象添加到 Redis 作为字符串
     */
    @Test
    public void testSetObjectAsString() {
        MyObject obj = new MyObject("Test Name", 123);
        boolean isSet = redisManagerService.setObjectAsString(testKeyString, obj, 60L);
        if (!isSet) {
            log.error("Failed to set object as string for key: {}", testKeyString);
        } else {
            MyObject retrievedObj = redisManagerService.getObjectFromString(testKeyString, MyObject.class);
            if (retrievedObj == null) {
                log.error("object == null; key: {}", testKeyString);
            } else {
                log.info("Successfully retrieved object: {} for key: {}", retrievedObj, testKeyString);
            }
        }
    }

    /**
     * 测试将对象添加到 Redis 作为哈希
     */
    @Test
    public void testSetObjectAsHash() {
        MyObject obj = new MyObject("Test Name", 456);
        boolean isSet = redisManagerService.setObjectAsHash(testKeyHash, obj, 60L);
        if (!isSet) {
            log.error("Failed to set object as hash for key: {}", testKeyHash);
        } else {
            MyObject retrievedObj = redisManagerService.getObjectFromHash(testKeyHash, MyObject.class);
            if (retrievedObj == null) {
                log.error("Retrieved object should not be null for key: {}", testKeyHash);
            } else {
                log.info("Successfully retrieved object: {} for key: {}", retrievedObj, testKeyHash);
            }
        }
    }

    @Test
    public void testGetObjectFromStringWithInvalidKey() {
        testSetObjectAsString();

        MyObject retrievedObj = redisManagerService.getObjectFromString(testKeyString, MyObject.class);
        if (retrievedObj != null) {
            log.error("Success get Object; key: {}, Object: {}", testKeyString, retrievedObj.toJsonString());
        } else {
            log.info("object == null, key: {}", testKeyString);
        }
    }

    @Test
    public void testGetObjectFromHashWithInvalidKey() {
        testSetObjectAsHash();

        MyObject retrievedObj = redisManagerService.getObjectFromHash(testKeyHash, MyObject.class);
        if (retrievedObj != null) {
            log.error("Success get Object; key: {}, Object: {}", testKeyString, retrievedObj.toJsonString());
        } else {
            log.info("object == null, key: {}", testKeyString);
        }
    }

    // 定义一个简单的测试对象
    @Data
    public static class MyObject implements BaseBean {
        private String name;
        private int value;

        // 无参构造函数
        public MyObject() {
        }

        public MyObject(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }

    @Test
    public void testSetObjectListAsString() {
        List<MyObject> list = Arrays.asList(new MyObject("Name1", 1), new MyObject("Name2", 2));
        String testKeyStringList = "testKeyStringList";
        boolean isSet = redisManagerService.setObjectListAsString(testKeyStringList, list, 60L);
        if (!isSet) {
            log.error("Failed to set object list as string for key: {}", testKeyStringList);
        } else {
            List<MyObject> retrievedList = redisManagerService.getObjectListFromString(testKeyStringList, MyObject.class);
            if (retrievedList == null || retrievedList.size() != list.size()) {
                log.error("Retrieved list should not be null and size should match for key: {}", testKeyStringList);
            } else {
                String all = retrievedList.stream().map(MyObject::toJsonString).collect(Collectors.joining(","));
                log.info("Successfully retrieved object list from string for key: {}, Objects = {}", testKeyStringList, all);
            }
        }
    }

    @Test
    public void testRange() {
        List<MyObject> list = Arrays.asList(new MyObject("Name1", 5), new MyObject("Name2", 6));
        String testKeyQueue = "testKeyQueue";
        redisManagerService.setObjectListAsString(testKeyQueue, list, 60L);

        List<MyObject> rangeList = redisManagerService.range(testKeyQueue, 0, 1, MyObject.class);
        if (rangeList == null || rangeList.size() != list.size()) {
            log.error("Range retrieved list should not be null and size should match");
        } else {
            // 使用调试信息输出
            String all = rangeList.stream().map(MyObject::toJsonString).collect(Collectors.joining(","));
            log.info("Successfully retrieved range from the queue for key: {}, Objects: {}", testKeyQueue, all);
        }
    }

    @Test
    public void testSetObjectSetAsString() {
        Set<MyObject> set = new HashSet<>(Arrays.asList(new MyObject("Name1", 1), new MyObject("Name2", 2)));
        String testKeyStringSet = "testKeyStringSet";
        boolean isSet = redisManagerService.setObjectSetAsString(testKeyStringSet, set, 60L);
        if (!isSet) {
            log.error("Failed to set object set as string for key: {}", testKeyStringSet);
        } else {
            Set<MyObject> retrievedSet = redisManagerService.getObjectSetFromString(testKeyStringSet, MyObject.class);
            if (retrievedSet == null || retrievedSet.isEmpty()) {
                log.error("Retrieved set should not be null and size should match for key: {}", testKeyStringSet);
            } else {
                String all = retrievedSet.stream().map(MyObject::toJsonString).collect(Collectors.joining(","));
                log.info("Successfully retrieved object set from string for key: {}, Objects: {}", testKeyStringSet, all);
            }
        }
    }

    @Test
    public void testSetObjectSetAsZSet() {
        Set<MyObject> set = new HashSet<>(Arrays.asList(new MyObject("Name1", 5), new MyObject("Name2", 6)));
        String testKeyZSet = "testKeyZSet";
        boolean isSet = redisManagerService.setObjectSetAsZSet(testKeyZSet, set, 10.0, 60L);
        if (!isSet) {
            log.error("Failed to set object set as ZSet for key: {}", testKeyZSet);
        } else {
            Set<MyObject> retrievedSet = redisManagerService.getObjectSetFromZSet(testKeyZSet, MyObject.class);
            if (retrievedSet == null || retrievedSet.isEmpty()) {
                log.error("Retrieved set should not be null and size should match for key: {}", testKeyZSet);
            } else {
                String all = retrievedSet.stream().map(MyObject::toJsonString).collect(Collectors.joining(","));
                log.info("Successfully retrieved object set from ZSet for key: {}, Objects: {}", testKeyZSet, all);
            }
        }
    }

    @Test
    public void testAddSetEle() {
        String key = "testAddSetEle";
        redisManagerService.addSetEle(key, "value1", "value2");
        Set<Object> retrievedObjectSet =redisManagerService.getObjectSetFromString(key, Object.class);
        Set<String> retrievedSet = retrievedObjectSet.stream()
                .map(Object::toString)
                .collect(Collectors.toSet());
//        Set<Object> retrievedSet =redisManagerService.getObjectSetFromString(key, String.class);
        if (retrievedSet.isEmpty()) {
            log.error("Set should not be null and size should be 2 for key: {}", key);
        } else {
            String all = String.join(",", retrievedSet);
            log.info("Successfully added elements to set for key: {}, Strings: {}", key, all);
        }
    }

    @Test
    public void testSetObjectMapAsHash() {
        Map<String, Integer> map = new HashMap<>();
        map.put("Key1", 12345);
        map.put("Key2", 67890);
        String testKeyHashMap = "testKeyHashMap";
        boolean isSet = redisManagerService.setObjectMapAsHash(testKeyHashMap, map, 60L);
        if (!isSet) {
            log.error("Failed to set object map as hash for key: {}", testKeyHashMap);
        } else {
            Map<String, Integer> retrievedMap = redisManagerService.getObjectMapFromHash(testKeyHashMap, Integer.class);
            if (retrievedMap == null || retrievedMap.size() != map.size()) {
                log.error("Retrieved map should not be null and size should match for key: {}", testKeyHashMap);
            } else {
                String all = retrievedMap.entrySet().stream()
                        .map(entry -> entry.getKey() + ":" + entry.getValue())
                        .collect(Collectors.joining(","));
                log.info("Successfully retrieved object map from hash for key: {}, Objects: {}", testKeyHashMap, all);
            }
        }
    }


    private final String testHashKey = "testHashKey";
    private final String testPrefixKey = "testPrefix:";
    private final String testPrefixValueKey = "testPrefix:value";

    @Test
    public void testDeleteAny() {
        // 设置键值以便测试删除
        redisManagerService.setObjectAsString(testKey, "value", 60L);
        boolean isDeleted = redisManagerService.deleteAny(testKey);
        if (!isDeleted) {
            log.error("Failed to delete key: {}", testKey);
        } else {
            log.info("Successfully deleted key: {}", testKey);
        }
    }

    @Test
    public void testHashDelete() {
        // 模拟哈希设置
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("field1", "value1");
        redisManagerService.setObjectAsHash(testHashKey, hashMap, 60L);
        Long result = redisManagerService.hashDelete(testHashKey, "field1");
        if (result == 0) {
            log.error("Failed to delete hash key: field1 from {}", testHashKey);
        } else {
            log.info("Successfully deleted hash key: field1 from {}", testHashKey);
        }
    }

    @Test
    public void testDeleteByPrefix() {
        // 设置多个以前缀开头的键
        redisManagerService.setObjectAsString(testPrefixValueKey, "value", 60L);
        boolean isDeleted = redisManagerService.deleteByPrefix(testPrefixKey);
        if (!isDeleted) {
            log.error("Failed to delete keys with prefix: {}", testPrefixKey);
        } else {
            log.info("Successfully deleted keys with prefix: {}", testPrefixKey);
        }
    }

    @Test
    public void testGetKeyByPrefix() {
        // 设置多个以指定前缀开头的键
        redisManagerService.setObjectAsString(testPrefixValueKey, "value", 60L);
        Set<String> keys = redisManagerService.getKeyByPrefix(testPrefixKey);
        if (keys == null || keys.isEmpty()) {
            log.error("No keys found with prefix: {}", testPrefixKey);
        } else {
            log.info("Successfully retrieved keys with prefix: {}: {}", testPrefixKey, keys);
        }
    }

    @Test
    public void testIncr() {
        Long incrementedValue = redisManagerService.incr(testKey);
        if (incrementedValue == null) {
            log.error("Failed to increment key: {}", testKey);
        } else {
            log.info("Successfully incremented key: {} to value: {}", testKey, incrementedValue);
        }
    }

    private final String testSecondKey = "testSecondKey";

    @Test
    public void testGetValue() {
        // 设置一个值用于测试获取
        redisManagerService.setCache(testKey, testSecondKey, DataType.STRING, "testValue");

        Object value = redisManagerService.getValue(testKey, DataType.STRING);
        if (!"testValue".equals(value)) {
            log.error("Failed to retrieve the correct value for key: {}", testKey);
        } else {
            log.info("Successfully retrieved value for key: {}: {}", testKey, value);
        }
    }

    @Test
    public void testSetCache() {
        // 设置缓存数据
        redisManagerService.setCache(testKey, testSecondKey, DataType.STRING, "cacheValue");

        Object value = redisManagerService.getValue(testKey, DataType.STRING);
        if (!"cacheValue".equals(value)) {
            log.error("Failed to set cache for key: {}", testKey);
        } else {
            log.info("Successfully set cache for key: {}: {}", testKey, value);
        }
    }

    @Test
    public void testScanRedis() {
        // 模拟添加多个缓存数据以便测试扫描
        for (int i = 0; i < 100; i++) {
            redisManagerService.setCache(testKey + ":" + i, testSecondKey, DataType.STRING, "value" + i);
        }

        List<SysCache> scannedData = redisManagerService.scanRedis(testKey + ":*", 1, 5);
        if (scannedData == null || scannedData.isEmpty()) {
            log.error("No data scanned for match key: {}", testKey + ":*");
        } else {
            log.info("Successfully scanned data: {}", scannedData);
        }
    }

    private final String testQueueKey = "testQueue";
    @Test
    public void testProduce() {
        // 生产消息
        String message = "Test Message";
        redisManagerService.produce(testQueueKey, message);

        Long length = redisManagerService.getListLength(testQueueKey);
        if (length == null || length != 1) {
            log.error("Failed to produce message. Expected length 1, but got: {}", length);
        } else {
            log.info("Successfully produced message. Current queue length: {}", length);
        }
    }

    @Test
    public void testConsume() {
        // 生产一条消息以便测试消费
        redisManagerService.produce(testQueueKey, "Test Message");

        String consumedMessage = redisManagerService.consume(testQueueKey);
        if (!"Test Message".equals(consumedMessage)) {
            log.error("Failed to consume message. Expected 'Test Message', but got: {}", consumedMessage);
        } else {
            log.info("Successfully consumed message: {}", consumedMessage);
        }
    }

    @Test
    public void testBlockingConsume() {
        // 使用阻塞消费测试
        redisManagerService.produce(testQueueKey, "Blocking Message");

        Object consumedMessage = redisManagerService.blockingConsume(testQueueKey);
        if (!"Blocking Message".equals(consumedMessage)) {
            log.error("Failed to blocking consume message. Expected 'Blocking Message', but got: {}", consumedMessage);
        } else {
            log.info("Successfully blocking consumed message: {}", consumedMessage);
        }
    }

    @Test
    public void testGetListLength() {
        // 测试获取队列长度
        redisManagerService.produce(testQueueKey, "Message 1");
        redisManagerService.produce(testQueueKey, "Message 2");

        Long length = redisManagerService.getListLength(testQueueKey);
        if (length == null || length != 2) {
            log.error("Failed to get correct list length. Expected 2, but got: {}", length);
        } else {
            log.info("Successfully retrieved list length: {}", length);
        }
    }

    private final String matchKey = "checkoutUrl:queue:*:*";

    // Error
    @Test
    public void testDiffSet() {
        // 设置测试集合
        String testSetKey1 = "testSet1";
        redisManagerService.produce(testSetKey1, "A");
        redisManagerService.produce(testSetKey1, "B");
        String testSetKey2 = "testSet2";
        redisManagerService.produce(testSetKey2, "B");
        redisManagerService.produce(testSetKey2, "C");

        Set<Object> diffSet = redisManagerService.diffSet(Arrays.asList(testSetKey1, testSetKey2));
        if (diffSet == null || !diffSet.contains("A")) {
            log.error("Failed to get correct diff set. Expected to find 'A', but got: {}", diffSet);
        } else {
            log.info("Successfully retrieved diff set: {}", diffSet);
        }
    }

    @Test
    public void testHashSet() {
        boolean isSet = redisManagerService.hashSet(testHashKey, "field1");
        if (!isSet) {
            log.error("Failed to set hash for key: {}", testHashKey);
        } else {
            log.info("Successfully set hash for key: {}", testHashKey);
        }
    }

    @Test
    public void testGetHashKeys() {
        redisManagerService.hashSet(testHashKey, "field1");
        redisManagerService.hashSet(testHashKey, "field2");

        Set<Object> hashKeys = redisManagerService.getHashKeys(testHashKey);
        if (hashKeys == null || hashKeys.size() != 2) {
            log.error("Failed to retrieve hash keys for key: {}. Expected 2 keys, but got: {}", testHashKey, hashKeys);
        } else {
            log.info("Successfully retrieved hash keys: {}", hashKeys);
        }
    }

    private final Long expireTimes = 60L; // 过期时间设置为60秒

    @Test
    public void testDeleteByMatchKey() {
        // 添加测试数据
        redisManagerService.setObjectAsString("checkoutUrl:queue:null:0509634743", "value1",expireTimes);
        redisManagerService.setObjectAsString("checkoutUrl:queue:null:123456", "value2",expireTimes);

        redisManagerService.deleteByMatchKey(matchKey);

        Long count = redisManagerService.countByMatchKey(matchKey);
        if (count != 0) {
            log.error("Failed to delete keys by match key. Expected count to be 0, but got: {}", count);
        } else {
            log.info("Successfully deleted keys by match key: {}", matchKey);
        }
    }

    @Test
    public void testListKeysByMatchKey() {
        // 添加测试数据
        redisManagerService.setObjectAsString("checkoutUrl:queue:null:0509634743", "value1",expireTimes);
        redisManagerService.setObjectAsString("checkoutUrl:queue:null:123456", "value2",expireTimes);

        List<String> keys = redisManagerService.listKeysByMatchKey(matchKey);
        if (keys == null || keys.size() != 2) {
            log.error("Failed to retrieve keys by match key. Expected 2 keys, but got: {}", keys);
        } else {
            log.info("Successfully retrieved keys by match key: {}", keys);
        }
    }

    @Test
    public void testCountByMatchKey() {
        // 添加测试数据
        redisManagerService.setObjectAsString("checkoutUrl:queue:null:0509634743", "value1",expireTimes);
        redisManagerService.setObjectAsString("checkoutUrl:queue:null:123456", "value2",expireTimes);

        Long count = redisManagerService.countByMatchKey(matchKey);
        if (count == null || count != 2) {
            log.error("Failed to count keys by match key. Expected 2, but got: {}", count);
        } else {
            log.info("Successfully counted keys by match key: {}", count);
        }
    }

    @Test
    public void testQueryListByMatchKey() {
        // 添加测试数据
        redisManagerService.setObjectAsString("checkoutUrl:queue:null:0509634743", "value1",expireTimes);
        redisManagerService.setObjectAsString("checkoutUrl:queue:null:123456", "value2",expireTimes);

        List<Object> values = redisManagerService.queryListByMatchKey(matchKey, 10, 1);
        if (values == null || values.size() != 2) {
            log.error("Failed to query values by match key. Expected 2 values, but got: {}", values);
        } else {
            log.info("Successfully queried values by match key: {}", values);
        }
    }

    // 查询当前被封禁到黑名单的IP
//    @Test
//    public void testGetBlacklist() {
//        // 使用模式匹配获取所有黑名单键
//        Set<String> keys = redisManagerService.getKeyByPrefix(LoginInterceptor.BLACKLIST_PREFIX);
//        if (keys.isEmpty()) {
//            log.info("No blacklisted IPs found.");
//        } else {
//            // 遍历每个键，获取对应的值
//            for (String key : keys) {
//                // 检查返回值的类型
//                Object value = redisManagerService.getValue(key, DataType.STRING);
//                log.info("Blacklisted IP: {}, Expiration Time: {}", key, value.toString());
//            }
//        }
//    }


    // 对Redis List进行item操作
    @Test
    public void testListOperations() {
        String listKey = "myList";
        List<MyObject> objects = Arrays.asList(
                new MyObject("Object 1", 1),
                new MyObject("Object 2", 2),
                new MyObject("Object 3", 3)
        );
        redisManagerService.setObjectListAsString(listKey, objects, null);

        MyObject addObject = new MyObject("Object 4", 4);
        boolean isAdd = redisManagerService.addElementToList(listKey, addObject);
        log.info("isAdd: {}", isAdd);

        List<MyObject> redisObjects = redisManagerService.getObjectListFromString(listKey, MyObject.class);

        for (MyObject redisObject : redisObjects) {
            log.info("add Redis Object{}: {}", redisObject.value, redisObject.toJsonString());
        }

        boolean isRemove = redisManagerService.removeElementFromList(listKey, addObject);
        log.info("isRemove: {}", isRemove);

        redisObjects = redisManagerService.getObjectListFromString(listKey, MyObject.class);

        for (MyObject redisObject : redisObjects) {
            log.info("remove Redis Object{}: {}", redisObject.value, redisObject.toJsonString());
        }
    }

    // 对Redis Set进行item操作
    @Test
    public void testSetOperations() {
        String setKey = "mySet";
        Set<MyObject> objects = new HashSet<>();
        objects.add(new MyObject("Object 1", 1));
        objects.add(new MyObject("Object 2", 2));
        objects.add(new MyObject("Object 3", 3));
        redisManagerService.setObjectSetAsString(setKey, objects, null);
        boolean isAdd = redisManagerService.addElementToSet(setKey, new MyObject("Object 4", 4));
        log.info("isAdd: {}", isAdd);
        Set<MyObject> redisObjects = redisManagerService.getObjectSetFromString(setKey, MyObject.class);
        for (MyObject redisObject : redisObjects) {
            log.info("add Redis Object{}: {}", redisObject.value, redisObject.toJsonString());
        }
        boolean isRemove = redisManagerService.removeElementFromSet(setKey, new MyObject("Object 4", 4));
        log.info("isRemove: {}", isRemove);
        redisObjects = redisManagerService.getObjectSetFromString(setKey, MyObject.class);
        for (MyObject redisObject : redisObjects) {
            log.info("remove Redis Object{}: {}", redisObject.value, redisObject.toJsonString());
        }
    }

    // 对Redis ZSet进行item操作
    @Test
    public void testZSetOperations() {
        String zSetKey = "myZSet";
        Set<MyObject> objects = new HashSet<>();
        objects.add(new MyObject("Object 1", 1));
        objects.add(new MyObject("Object 2", 2));
        objects.add(new MyObject("Object 3", 3));
        redisManagerService.setObjectSetAsZSet(zSetKey, objects, 1.0, null);
        boolean isAdd = redisManagerService.addElementToZSet(zSetKey, new MyObject("Object 4", 4), 4.0);
        log.info("isAdd: {}", isAdd);
        Set<MyObject> redisObjects = redisManagerService.getObjectSetFromZSet(zSetKey, MyObject.class);
        for (MyObject redisObject : redisObjects) {
            log.info("add Redis Object{}: {}", redisObject.value, redisObject.toJsonString());
        }
        boolean isRemove = redisManagerService.removeElementFromZSet(zSetKey, new MyObject("Object 4", 4));
        log.info("isRemove: {}", isRemove);
        redisObjects = redisManagerService.getObjectSetFromZSet(zSetKey, MyObject.class);
        for (MyObject redisObject : redisObjects) {
            log.info("remove Redis Object{}: {}", redisObject.value, redisObject.toJsonString());
        }
    }

    // 对Redis Map进行item操作
    @Test
    public void testMapOperations() {
        String mapKey = "myMap";
        Map<String, MyObject> objects = new HashMap<>();
        objects.put("key1", new MyObject("Object 1", 1));
        objects.put("key2", new MyObject("Object 2", 2));
        objects.put("key3", new MyObject("Object 3", 3));
        redisManagerService.setObjectMapAsHash(mapKey, objects, null);
        // 增
        boolean isAdd = redisManagerService.addElementToMap(mapKey, "key4", new MyObject("Object 4", 4));
        log.info("isAdd: {}", isAdd);
        Map<String, MyObject> redisObjects = redisManagerService.getObjectMapFromHash(mapKey, MyObject.class);
        for (Map.Entry<String, MyObject> entry : redisObjects.entrySet()) {
            log.info("add Redis Object{}: {}", entry.getKey(), entry.getValue().toJsonString());
        }
        // 删
        boolean isRemove = redisManagerService.removeElementFromMap(mapKey, "key4");
        log.info("isRemove: {}", isRemove);
        redisObjects = redisManagerService.getObjectMapFromHash(mapKey, MyObject.class);
        for (Map.Entry<String, MyObject> entry : redisObjects.entrySet()) {
            log.info("remove Redis Object{}: {}", entry.getKey(), entry.getValue().toJsonString());
        }
        // 改
        boolean isUpdate = redisManagerService.updateElementInMap(mapKey, "key1", new MyObject("Object 5", 6));
        log.info("isUpdate: {}", isUpdate);
        // 查
        MyObject redisObject = redisManagerService.getElementFromMap(mapKey, "key1", MyObject.class);
        log.info("get Redis Object{}: {}", "key1", redisObject.toJsonString());
    }
}
