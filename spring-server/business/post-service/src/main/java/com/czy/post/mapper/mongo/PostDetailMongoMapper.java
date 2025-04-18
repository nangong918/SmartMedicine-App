package com.czy.post.mapper.mongo;

import com.czy.api.domain.Do.post.PostDetailDo;
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

    // 保存帖子
    public void savePostDetail(PostDetailDo postDetail) {
        mongoTemplate.save(postDetail);
    }

    // 保存帖子列表
    public void savePostDetails(List<PostDetailDo> postDetails) {
        mongoTemplate.insert(postDetails, PostDetailDo.class);
    }

    // 根据id获取帖子
    public PostDetailDo findPostDetailById(Long id) {
        return mongoTemplate.findById(id, PostDetailDo.class);
    }

    // 根据List<Long> ids 查询帖子
    public List<PostDetailDo> findPostDetailsByIdList(List<Long> idList) {
        return mongoTemplate.find(
                Query.query(Criteria.where("id").in(idList)),
                PostDetailDo.class
        );
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
}
