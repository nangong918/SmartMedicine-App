package com.api.mapper.post.mongo;

import com.czy.api.domain.Do.post.comment.PostCommentMongoDo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @deprecated Comment评论暂时不用支持Post文章那种Json的文本格式, 不需要用MongoDb
 * Use {@link com.api.mapper.post.mybatis.PostCommentMapper} instead.
 */
@Deprecated
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
        mongoTemplate.indexOps(PostCommentMongoDo.class).ensureIndex(new Index().on("postId", Sort.Direction.ASC));

        // 创建索引：commenterId
        mongoTemplate.indexOps(PostCommentMongoDo.class).ensureIndex(new Index().on("commenterId", Sort.Direction.ASC));

        // 创建索引：replyCommentId（可为空）
        mongoTemplate.indexOps(PostCommentMongoDo.class).ensureIndex(new Index().on("replyCommentId", Sort.Direction.ASC));
    }

    // 保存消息
    public void saveComment(PostCommentMongoDo comment) {
        mongoTemplate.save(comment);
    }

    // 保存List消息
    public void saveComments(List<PostCommentMongoDo> comments) {
//        mongoTemplate.insertAll(comments);
        mongoTemplate.insert(comments, PostCommentMongoDo.class);
    }

    // 根据id查询消息
    public PostCommentMongoDo findCommentById(Long id) {
        return mongoTemplate.findById(id, PostCommentMongoDo.class);
    }

    // 根据List<Long> ids 查询消息
    public List<PostCommentMongoDo> findCommentsByIdList(List<Long> idList) {
        return mongoTemplate.find(
                Query.query(Criteria.where("id").in(idList)),
                PostCommentMongoDo.class
        );
    }

    // 查询postId并且replyCommentId==null的List评论（一级评论）
    public List<PostCommentMongoDo> findLevel1CommentsByPostId(Long postId) {
        return mongoTemplate.find(
                Query.query(
                        Criteria.where("postId").is(postId)
                                .and("replyCommentId").is(null)
                )                .with(Sort.by("timestamp")),
                PostCommentMongoDo.class
        );
    }

    // 分页获取某个post的一级comment （replyCommentId==null）（一页多少条n + 第几页m）（comment在mongodb需要用Page）
    public List<PostCommentMongoDo> findLevel1CommentsByPostIdAndPaging(Long postId, int page, int size) {
        return mongoTemplate.find(
                Query.query(
                        Criteria.where("postId").is(postId)
                                .and("replyCommentId").is(null)
                )                .skip((long) page * size)
                        .limit(size)
                        .with(Sort.by("timestamp")),
                PostCommentMongoDo.class
        );
    }

    // 查询postId并且replyCommentId的List评论（二级评论）
    public List<PostCommentMongoDo> findLevel2CommentsByPostIdAndReplyCommentId(Long postId, Long replyCommentId) {
        return mongoTemplate.find(
                Query.query(
                        Criteria.where("postId").is(postId)
                                .and("replyCommentId").is(replyCommentId)
                )                .with(Sort.by("timestamp")),
                PostCommentMongoDo.class
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
    public List<PostCommentMongoDo> findLevel2CommentsByPostIdAndReplyCommentIdPaging(Long postId, Long replyCommentId, int page, int size) {
        return mongoTemplate.find(
                Query.query(
                        Criteria.where("postId").is(postId)
                                .and("replyCommentId").is(replyCommentId)
                )                .skip((long) page * size)
                        .limit(size)
                        .with(Sort.by("timestamp")),
                PostCommentMongoDo.class
        );
    }

    // 根据id删除消息
    public void deleteCommentById(Long id) {
        mongoTemplate.remove(
                Query.query(Criteria.where("id").is(id)),
                PostCommentMongoDo.class
        );
    }

    // 删除ids帖子
    public void deleteCommentsByIdList(List<Long> idList) {
        mongoTemplate.remove(
                Query.query(Criteria.where("id").in(idList)),
                PostCommentMongoDo.class
        );
    }

    // 根据postId和commentId删除消息
    public void deleteCommentByPostIdAndCommentId(Long postId, Long commentId) {
        mongoTemplate.remove(
                Query.query(
                        Criteria.where("postId").is(postId)
                                .and("id").is(commentId)
                ),
                PostCommentMongoDo.class
        );
    }
}
