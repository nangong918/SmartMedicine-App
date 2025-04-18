package com.czy.api.domain.entity.event;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/2/11 22:56
 */

@Data
public class Session implements Serializable {
    /**
     * 数据库主键ID
     */
    private Long id;

    /**
     * 登录传递收到的消息
     */
    private String uuid;
    private String deviceId;
    private String deviceName;
    private String appVersion;
    private String osVersion;
    private String packageName;
    private String language;

    /**
     * session绑定的用户账号
     */
    private String uid;

    /**
     * session在本台服务器上的ID
     */
    private String nid;

    /**
     * session绑定的服务器IPv6
     */
    private String ipv6;

    /**
     * 登录时间
     */
    private Long bindTime = System.currentTimeMillis();

    /**
     * 消息类型
     */
    private String type;

    public Session() {
    }
}
