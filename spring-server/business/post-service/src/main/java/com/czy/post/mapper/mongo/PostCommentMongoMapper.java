package com.czy.post.mapper.mongo;

import com.czy.api.domain.Do.post.PostCommentDo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author 13225
 * @date 2025/4/17 21:26
 */

@RequiredArgsConstructor
@Repository
public class PostCommentMongoMapper {

    private final MongoTemplate mongoTemplate;

    @PostConstruct
    public void init() {
        createIndexes();
    }

    private void createIndexes() {
        // 创建索引：postId
        mongoTemplate.indexOps(PostCommentDo.class).ensureIndex(new Index().on("postId", Sort.Direction.ASC));

        // 创建索引：commenterId
        mongoTemplate.indexOps(PostCommentDo.class).ensureIndex(new Index().on("commenterId", Sort.Direction.ASC));

        // 创建索引：replyCommentId（可为空）
        mongoTemplate.indexOps(PostCommentDo.class).ensureIndex(new Index().on("replyCommentId", Sort.Direction.ASC));
    }

    // 保存消息
    public void saveComment(PostCommentDo comment) {
        mongoTemplate.save(comment);
    }

    // 保存List消息
    public void saveComments(List<PostCommentDo> comments) {
//        mongoTemplate.insertAll(comments);
        mongoTemplate.insert(comments, PostCommentDo.class);
    }

    // 根据id查询消息
    public PostCommentDo findCommentById(Long id) {
        return mongoTemplate.findById(id, PostCommentDo.class);
    }

    // 根据List<Long> ids 查询消息
    public List<PostCommentDo> findCommentsByIdList(List<Long> idList) {
        return mongoTemplate.find(
                Query.query(Criteria.where("id").in(idList)),
                PostCommentDo.class
        );
    }

    // 查询postId并且replyCommentId==null的List评论（一级评论）
    public List<PostCommentDo> findCommentsByPostIdAndReplyCommentIdIsNull(Long postId) {
        return mongoTemplate.find(
                Query.query(
                        Criteria.where("postId").is(postId)
                                .and("replyCommentId").is(null)
                )                .with(Sort.by("timestamp")),
                PostCommentDo.class
        );
    }

    // 查询postId并且replyCommentId的List评论（二级评论）
    public List<PostCommentDo> findCommentsByPostIdAndReplyCommentId(Long postId, Long replyCommentId) {
        return mongoTemplate.find(
                Query.query(
                        Criteria.where("postId").is(postId)
                                .and("replyCommentId").is(replyCommentId)
                )                .with(Sort.by("timestamp")),
                PostCommentDo.class
        );
    }

    /**
     * 查询postId并且replyCommentId==null的List评论（一级评论）+ 分页
     * @param postId    帖子id
     * @param page      页码       从0开始
     * @param size      每页大小
     * @return          List<PostCommentDo>
     */
    public List<PostCommentDo> findCommentsByPostIdAndReplyCommentIdIsNullPaging(Long postId, int page, int size) {
        return mongoTemplate.find(
                Query.query(
                        Criteria.where("postId").is(postId)
                                .and("replyCommentId").is(null)
                )                .skip((long) page * size)
                        .limit(size)
                        .with(Sort.by("timestamp")),
                PostCommentDo.class
        );
    }

    /**
     * 查询postId并且replyCommentId的List评论（二级评论）+ 分页
     * @param postId            帖子id
     * @param replyCommentId    二级评论id
     * @param page              页码       从0开始
     * @param size              每页大小
     * @return                  List<PostCommentDo>
     */
    public List<PostCommentDo> findCommentsByPostIdAndReplyCommentIdPaging(Long postId, Long replyCommentId, int page, int size) {
        return mongoTemplate.find(
                Query.query(
                        Criteria.where("postId").is(postId)
                                .and("replyCommentId").is(replyCommentId)
                )                .skip((long) page * size)
                        .limit(size)
                        .with(Sort.by("timestamp")),
                PostCommentDo.class
        );
    }

    // 根据id删除消息
    public void deleteCommentById(Long id) {
        mongoTemplate.remove(
                Query.query(Criteria.where("id").is(id)),
                PostCommentDo.class
        );
    }

    // 删除ids帖子
    public void deleteCommentsByIdList(List<Long> idList) {
        mongoTemplate.remove(
                Query.query(Criteria.where("id").in(idList)),
                PostCommentDo.class
        );
    }
}
