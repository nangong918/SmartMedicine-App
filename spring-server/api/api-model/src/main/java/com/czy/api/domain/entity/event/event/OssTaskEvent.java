package com.czy.api.domain.entity.event.event;

import com.czy.api.domain.entity.event.OssTask;
import org.springframework.context.ApplicationEvent;

/**
 * @author 13225
 * @date 2025/4/18 23:48
 */
public class OssTaskEvent extends ApplicationEvent {

    public OssTaskEvent(OssTask task) {
        super(task);
    }

    @Override
    public OssTask getSource() {
        return (OssTask) super.getSource();
    }
}
