package redis;


import com.utils.mvc.SpringMvcApplication;
import com.utils.mvc.redisson.RedissonService;
import json.BaseBean;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.TestPropertySource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author 13225
 * @date 2025/1/13 15:32
 */

@Slf4j
@SpringBootTest(classes = SpringMvcApplication.class)
@TestPropertySource("classpath:application.properties")
public class RedisTests {

    // 定义一个简单的测试对象
    @Data
    public static class MyObject implements BaseBean, Serializable {
        private String name;
        private Integer value;

        // 无参构造函数
        public MyObject() {
        }

        public MyObject(String name, Integer value) {
            this.name = name;
            this.value = value;
        }
    }

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Value:
     * 访问时，通常需要获取整个对象。获取后需要反序列化成原始对象，适合简单的数据结构。
     * <p>
     * Hash:
     * 可以独立地访问或更新单个字段。例如，你可以只更新 name 字段，而不需要重新存储整个对象。
     */
    // Value:直接存储Java数据结构 [通过RedisConfig配置，能直接存储Java对象]
    @Test
    public void testStorageJavaDataStructure() {
        String key = "JavaDataStructure:Value";
        MyObject myObject = new MyObject("test", 1);
        redisTemplate.opsForValue().set(key, myObject);
        MyObject myObject1 = (MyObject) redisTemplate.opsForValue().get(key);
        log.info("myObject1:{}", Objects.requireNonNull(myObject1).toJsonString());
    }

    // Hash:可以独立地访问或更新单个字段
    @Test
    public void testStorageJavaDataStructureWithHash() {
        String key = "JavaDataStructure:Hash";
        MyObject myObject = new MyObject("test", 1);

        // 存储对象属性到 Hash
        redisTemplate.opsForHash().put(key, "name", myObject.getName());
        redisTemplate.opsForHash().put(key, "value", myObject.getValue());

        // 从 Hash 中获取对象属性
        String name = (String) redisTemplate.opsForHash().get(key, "name");
        Integer value = (Integer) redisTemplate.opsForHash().get(key, "value");

        MyObject myObject1 = new MyObject(name, value);
        log.info("myObject1:{}", Objects.requireNonNull(myObject1).toJsonString());
    }

    // List:存储Java对象列表
    @Test
    public void testStorageList() {
        String key = "MyObjectList";

        // 创建 List 并添加数据
        List<MyObject> list = new ArrayList<>();
        list.add(new MyObject("object1", 1));
        list.add(new MyObject("object2", 2));
        list.add(new MyObject("object3", 3));

        // 将 List 存入 Redis
        for (MyObject myObject : list) {
            redisTemplate.opsForList().rightPush(key, myObject);
        }

        // 再往 Redis 中的 List 添加一个新的 MyObject
        MyObject newObject = new MyObject("object4", 4);
        redisTemplate.opsForList().rightPush(key, newObject);

        // 从 Redis 中获取并打印 List
        List<Object> storedList = redisTemplate.opsForList().range(key, 0, -1);
        if(storedList == null){
            log.warn("storedList is null");
            return;
        }
        for (Object obj : storedList) {
            MyObject myObject = (MyObject) obj;
            log.info(myObject.toJsonString());
        }
    }

    // 存储 Set<MyObject>
    @Test
    public void testStorageSet() {
        String setKey = "MyObjectSet";

        // 创建 Set 并添加数据
        Set<MyObject> myObjectSet = new HashSet<>();
        myObjectSet.add(new MyObject("setObject1", 1));
        myObjectSet.add(new MyObject("setObject2", 2));
        myObjectSet.add(new MyObject("setObject3", 3));

        // 将 Set 存入 Redis
        for (MyObject myObject : myObjectSet) {
            redisTemplate.opsForSet().add(setKey, myObject);
        }

        // 添加一个新对象到 Set
        MyObject newSetObject = new MyObject("setObject4", 4);
        redisTemplate.opsForSet().add(setKey, newSetObject);

        // 从 Redis 中获取并打印 Set
        Set<Object> storedSet = redisTemplate.opsForSet().members(setKey);
        if(storedSet == null){
            log.warn("storedSet is null");
            return;
        }
        for (Object obj : storedSet) {
            MyObject myObject = (MyObject) obj;
            log.info(myObject.toJsonString());
        }
    }

    // 存储 ZSet<MyObject>
    @Test
    public void testStorageZSet() {
        String zSetKey = "MyObjectZSet";

        // 将对象和分数存入 ZSet
        redisTemplate.opsForZSet().add(zSetKey, new MyObject("zsetObject1", 1), 1.0);
        redisTemplate.opsForZSet().add(zSetKey, new MyObject("zsetObject2", 2), 2.0);
        redisTemplate.opsForZSet().add(zSetKey, new MyObject("zsetObject3", 3), 3.0);

        // 添加一个新对象到 ZSet
        MyObject newZSetObject = new MyObject("zsetObject4", 4);
        redisTemplate.opsForZSet().add(zSetKey, newZSetObject, 4.0); // 设定分数为 4.0

        // 从 Redis 中获取并打印 ZSet
        Set<Object> storedZSet = redisTemplate.opsForZSet().range(zSetKey, 0, -1);
        if(storedZSet == null){
            log.warn("storedZSet is null");
            return;
        }
        for (Object obj : storedZSet) {
            MyObject myObject = (MyObject) obj;
            log.info(myObject.toJsonString());
        }
    }

    // 存储 Map<String, MyObject>
    @Test
    public void testStorageMap() {
        String mapKey = "MyObjectMap";

        // 创建 Map 并添加数据
        Map<String, MyObject> myObjectMap = new HashMap<>();
        myObjectMap.put("key1", new MyObject("mapObject1", 1));
        myObjectMap.put("key2", new MyObject("mapObject2", 2));
        myObjectMap.put("key3", new MyObject("mapObject3", 3));

        // 将 Map 存入 Redis
        for (Map.Entry<String, MyObject> entry : myObjectMap.entrySet()) {
            redisTemplate.opsForHash().put(mapKey, entry.getKey(), entry.getValue());
        }

        // 添加一个新对象到 Map
        MyObject newMapObject = new MyObject("mapObject4", 4);
        redisTemplate.opsForHash().put(mapKey, "key4", newMapObject);

        // 从 Redis 中获取并打印 Map
        Map<Object, Object> storedMap = redisTemplate.opsForHash().entries(mapKey);
        for (Map.Entry<Object, Object> entry : storedMap.entrySet()) {
            String key = (String) entry.getKey();
            MyObject myObject = (MyObject) entry.getValue();
            log.info("Key: {}, Value: {}", key, myObject.toJsonString());
        }
    }

    @Autowired
    RedissonService redissonService;
    @Test
    public void testRedissonJson() throws Exception {
        redissonService.setObjectByJson("redisson_json_test", new MyObject("test", 1), 1000L);
        MyObject myObject = redissonService.getObjectFromJson("redisson_json_test", MyObject.class);
        log.info("redisson_json_test::myObject:{}", myObject.toJsonString());
    }

    @Test
    public void testRedissonSerializable() throws Exception {
        redissonService.setObjectBySerializable("redisson_serializable_test", new MyObject("test", 1), 1000L);
        MyObject myObject = redissonService.getObjectFromSerializable("redisson_serializable_test", MyObject.class);
        log.info("redisson_serializable_test::myObject:{}", myObject.toJsonString());
    }
}
