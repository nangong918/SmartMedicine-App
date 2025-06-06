package com.czy.imports.mapper;

import com.czy.api.domain.Do.post.post.PostDetailDo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author 13225
 * @date 2025/4/17 21:25
 */

@RequiredArgsConstructor
@Repository
public class PostDetailMongoMapper {

    private final MongoTemplate mongoTemplate;

    @PostConstruct
    public void init() {
        createIndexes();
    }

    private void createIndexes() {
        // 无需索引，id就够了，信息都在PostInfoDo里面
    }

    // 删除id帖子
    public void deletePostDetailById(Long id) {
        mongoTemplate.remove(
                Query.query(Criteria.where("id").is(id)),
                PostDetailDo.class
        );
    }

    // 删除ids帖子
    public void deletePostDetailsByIdList(List<Long> idList) {
        mongoTemplate.remove(
                Query.query(Criteria.where("id").in(idList)),
                PostDetailDo.class
        );
    }

    // 删除全部已有的帖子
    public void deleteAllPostDetails() {
        mongoTemplate.remove(
                Query.query(Criteria.where("id").exists(true)),
                PostDetailDo.class
        );
    }
}
