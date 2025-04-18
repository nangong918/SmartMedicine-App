package com.czy.api.domain.bo.gateway;

import lombok.Getter;

/**
 * @author 13225
 * @date 2025/1/14 14:37
 */
@Getter
public class RsaKeysBo {
    private final String publicKey;
    private final String privateKey;
    public RsaKeysBo(String publicKey, String privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }
}
