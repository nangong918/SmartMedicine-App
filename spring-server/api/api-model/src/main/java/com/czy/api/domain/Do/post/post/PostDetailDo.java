package com.czy.api.domain.Do.post.post;

import com.czy.api.domain.Do.post.post.content.PostContentEntity;
import com.czy.api.domain.ao.post.PostNerResult;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author 13225
 * @date 2025/4/16 21:04
 * 朋友圈Details帖子Do
 * 存放在mongoDB + ES
 * 我来解释一下为什么要用MongoDB:
 * MongoDB存储Json格式, 字段比较灵活, 但是代价是JOIN联合查询不能实现.
 * 此处post查询都是用id去直接获取, 不会出现JOIN
 * 并且post文章需求很可能出现文章本身内部哪里在哪行是存在图片的这种，mysql是不好实现的, 但是mongodb就好实现.
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
    private List<PostContentEntity> postContents;
    // old版本的数据兼容
    private String content;
    // ner特诊
    private List<PostNerResult> nerResults;

    public void setPostContentsByOnlyString(String content){
        postContents = new ArrayList<>();
        PostContentEntity contentEntity = new PostContentEntity();
        contentEntity.setContent(content);
        postContents.add(contentEntity);
    }

    public String getOnlyContent() {
        String newContent = Optional.ofNullable(postContents)
                .filter(list -> !list.isEmpty())
                .map(list -> list.stream()
                        .map(PostContentEntity::getContent)
                        .filter(Objects::nonNull)
                        .reduce("", (s1, s2) -> s1 + "\n" + s2)
                )
                .orElse("");
        if (StringUtils.hasText(newContent)){
            return newContent;
        }
        return content;
    }
}
