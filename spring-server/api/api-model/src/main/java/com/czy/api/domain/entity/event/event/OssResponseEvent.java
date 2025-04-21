package com.czy.api.domain.entity.event.event;

import com.czy.api.domain.entity.event.OssResponse;
import org.springframework.context.ApplicationEvent;

/**
 * @author 13225
 * @date 2025/4/21 15:25
 */
public class OssResponseEvent extends ApplicationEvent {

    public OssResponseEvent(OssResponse source) {
        super(source);
    }

    @Override
    public OssResponse getSource() {
        return (OssResponse) source;
    }
}
