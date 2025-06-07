package com.czy.api.domain.Do.post.post;

import com.czy.api.domain.ao.post.PostNerResult;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.List;

/**
 * @author 13225
 * @date 2025/4/16 21:04
 * 朋友圈Details帖子Do
 * 存放在mongoDB + ES
 */

@org.springframework.data.mongodb.core.mapping.Document("post_detail")
@Data
public class PostDetailDo implements Serializable {
    // id；postDetails的id与postInfo的id一致
    @Id
    private Long id;
    // title；not null
    private String title;
    // content；not null
    private String content;
    // ner特诊
    private List<PostNerResult> nerResults;
}
