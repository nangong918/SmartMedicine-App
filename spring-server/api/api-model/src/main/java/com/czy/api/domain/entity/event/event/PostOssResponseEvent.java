package com.czy.api.domain.entity.event.event;

import com.czy.api.domain.entity.event.PostOssResponse;
import org.springframework.context.ApplicationEvent;

/**
 * @author 13225
 * @date 2025/4/21 15:25
 */
public class PostOssResponseEvent extends ApplicationEvent {

    public PostOssResponseEvent(PostOssResponse source) {
        super(source);
    }

    @Override
    public PostOssResponse getSource() {
        return (PostOssResponse) source;
    }
}
