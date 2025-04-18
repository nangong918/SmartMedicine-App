package com.czy.springUtils.util;

import cn.hutool.crypto.digest.BCrypt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.codec.digest.HmacUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * @author 13225
 * @date 2025/1/13 23:44
 */
@Slf4j
public class EncryptUtil {


    /**
     * 加密算法
     */
    private final static SecureDigestAlgorithm<SecretKey, SecretKey> ALGORITHM_ = Jwts.SIG.HS256;
    private static final String ALGORITHM = "HS256";


    /**
     * 获取Claims
     * @param token     jwtToken
     * @param secretKey 密钥
     * @return          Claims  JWT 的有效载荷部分。具体来说，Claims 是一个包含关于用户及其权限的声明或信息的集合
     * @throws Exception 获取Claims失败
     */
    private static Claims getClaimsFromToken(String token, String secretKey) throws Exception{
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

//    private static String generateSignToken(String jwe) {
//        String salt = generateSalt(); // 生成随机盐
//        // 使用用户数据、JWT 和盐进行加密
//        // 这里可以使用 HMAC、SHA-256 或其他加密算法
//        // 例如，使用 HMAC-SHA256 进行加密
//        return HmacUtils.hmacSha256Hex(salt, jwe);
//    }

    private static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static Key stringToKey(String key){
        return Keys.hmacShaKeyFor(key.getBytes());
    }


    // Bcrypt 加密
    public static String bcryptEncrypt(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    // Bcrypt 验证
    public static boolean bcryptVerify(String originalPassword, String encryptedPassword) {
        return BCrypt.checkpw(originalPassword, encryptedPassword);
    }

    public static void main(String[] args) {
        String originalPassword = "123456";
        String password = bcryptEncrypt(originalPassword);
        System.out.println("password = " + password);
        boolean result = bcryptVerify(originalPassword, password);
        System.out.println("result = " + result);
        boolean result2 = bcryptVerify(originalPassword, "password");
        System.out.println("result = " + result2);
        boolean resulte3 = bcryptVerify("123456", "$2a$10$HYPfgnwQZ7nVQKVtFeB5n.Q17zL8VoAU1NXf.jvz4V23bt8fGTeDW");
        System.out.println("result = " + resulte3);
    }

}
