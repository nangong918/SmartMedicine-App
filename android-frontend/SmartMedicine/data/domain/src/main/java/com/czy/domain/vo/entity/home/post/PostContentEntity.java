package com.czy.domain.vo.entity.home.post;

import java.io.Serializable;


/**
 * @author 13225
 * @date 2025/9/5 11:26
 */
public class PostContentEntity implements Serializable, Cloneable {
    public String content;
    public PostImage image;

    @Override
    public PostContentEntity clone() throws CloneNotSupportedException {
        PostContentEntity entity = (PostContentEntity) super.clone();
        if (this.image != null) {
            entity.image = image.clone();
        }
        return entity;
    }
}
