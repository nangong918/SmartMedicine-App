package com.czy.springUtils.util;

import java.net.InetAddress;

/**
 * @author 13225
 * @date 2025/1/27 17:04
 */
public class IpUtil {
    public static String getLocalIp() {
        try {
            // 获取本机的 InetAddress
            InetAddress localHost = InetAddress.getLocalHost();
            // 返回 IP 地址
            return localHost.getHostAddress();
        } catch (Exception e) {
            // 发生异常时返回 null
            return null;
        }
    }

    public static void main(String[] args) {
        String localIp = getLocalIp();
        System.out.println("本机 IP 地址: " + localIp);
    }
}
