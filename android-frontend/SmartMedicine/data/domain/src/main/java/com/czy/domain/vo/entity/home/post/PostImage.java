package com.czy.domain.vo.entity.home.post;


import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/9/5 11:29
 */
public class PostImage implements Serializable, Cloneable {
    public Long fileId;
    public String description;

    @Override
    public PostImage clone() throws CloneNotSupportedException {
        return (PostImage) super.clone();
    }
}
