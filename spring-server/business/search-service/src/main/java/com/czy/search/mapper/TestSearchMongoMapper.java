package com.czy.search.mapper;

import com.czy.api.domain.Do.test.TestSearchDo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author 13225
 * @date 2025/4/24 17:58
 */
@RequiredArgsConstructor
@Repository
public class TestSearchMongoMapper {

    private final MongoTemplate mongoTemplate;

    @PostConstruct
    public void init() {
        createIndexes();
    }

    private void createIndexes() {
        // 无需索引，id就够了，信息都在PostInfoDo里面
    }

    // save TestSearchDo
    public void saveSearchTestDo(TestSearchDo testSearchDo) {
        mongoTemplate.save(testSearchDo);
    }

    // find TestSearchDo by id
    public TestSearchDo findSearchTestDoById(Long id) {
        return mongoTemplate.findById(id, TestSearchDo.class);
    }

    // 删除
    public void deleteSearchTestDoById(Long id) {
        mongoTemplate.remove(
                Query.query(Criteria.where("id").is(id)),
                TestSearchDo.class
        );
    }

    // 删除all
    public void deleteSearchTestDoAll() {
        mongoTemplate.remove(
                Query.query(Criteria.where("id").exists(true)),
                TestSearchDo.class
        );
    }

    // 模糊查询
    public List<TestSearchDo> findSearchTestDoByLikeName(String likeName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("searchName").regex(likeName, "i")); // "i" for case insensitive
        return mongoTemplate.find(query, TestSearchDo.class);
    }

}
