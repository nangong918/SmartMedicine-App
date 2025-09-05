package com.czy.api.domain.Do.post.post.content;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/9/5 11:29
 */
@Data
public class PostImage implements Serializable, Cloneable {
    public Long fileId;
    public String description;

    @Override
    public PostImage clone() throws CloneNotSupportedException {
        return (PostImage) super.clone();
    }
}
