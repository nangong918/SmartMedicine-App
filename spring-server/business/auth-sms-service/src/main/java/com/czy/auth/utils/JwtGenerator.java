package com.czy.auth.utils;

import cn.hutool.core.bean.BeanUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import io.jsonwebtoken.security.WeakKeyException;
import jwt.BaseJwtPayloadAo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @author 13225
 * @date 2025/4/4 13:10
 */
@Slf4j
@Component
public class JwtGenerator {

    /**
     * 加密算法
     */
    private final static SecureDigestAlgorithm<SecretKey, SecretKey> ALGORITHM = Jwts.SIG.HS256;


    /**
     * 生成Token
     * @param jwtPayload    jwtPayload
     * @return              token
     * @param <T>           jwtPayloadAo
     */
    public <T extends BaseJwtPayloadAo> String generateToken(T jwtPayload, String key, Long expireSec) {
        if (jwtPayload == null){
            log.warn("jwtPayload is null.");
            return null;
        }
        // 简化，暂时用对称加密
        return generateToken_s(
                jwtPayload,
                key,
                expireSec
        );
    }

    /**
     * 生成token
     * @param jwtPayload    jwtPayload 用于设置Claims
     * @param secretKey     密钥Key
     * @param expireSec     过期时间
     * @return              jwtToken
     * @param <T>           jwtPayload类型
     * @throws WeakKeyException
     * io.jsonwebtoken.security.WeakKeyException: The specified key byte array is 72 bits which is not secure enough for any JWT HMAC-SHA algorithm.  The JWT JWA Specification (RFC 7518, Section 3.2) states that keys used with HMAC-SHA algorithms MUST have a size >= 256 bits (the key size must be greater than or equal to the hash output size)
     * secretKey至少为256位，32字节
     */
    public static <T extends BaseJwtPayloadAo> String generateToken_s(T jwtPayload, String secretKey, Long expireSec) throws WeakKeyException{

        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        // 设置过期时间 毫秒
        Date expirationDate = new Date(System.currentTimeMillis() + expireSec * 1000L);

        return Jwts.builder()
                .claims(BeanUtil.beanToMap(jwtPayload))
                .subject(jwtPayload.getSubject())
                .issuedAt(new Date())
                .expiration(expirationDate)
                .signWith(key, ALGORITHM)
                .compact();
    }

}
