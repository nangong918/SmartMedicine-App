package com.czy.springUtils.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;

/**
 * @author 13225
 * @date 2024/9/6 13:40
 */
@Slf4j
public class TokenAesUtil {

    private static final String KEY = "VIQzQdhnOYqum6A4";
    public static final String key = new String(Base64.getEncoder().encode(KEY.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8).toLowerCase(Locale.ROOT);

    /**
     * 加密
     */
    public static String encryptECB(String sSrc, String sKey) {
        try {
            if (sKey == null) {
                return null;
            }
            byte[] raw = sKey.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            //"算法/模式/补码方式"
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes(StandardCharsets.UTF_8));
            //此处使用BASE64做转码功能，同时能起到2次加密的作用。
            return Base64Utils.encodeToString(encrypted);
        } catch (Exception e) {
            log.error("Aes加密出错: " , e);
            return null;
        }
    }

    /**
     * 解密
     */
    public static String decryptECB(String sSrc, String sKey) {
        try {
            // 判断Key是否正确
            if (sKey == null) {
                log.error("AeKey为空null");
                return null;
            }
            byte[] raw = sKey.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            //先用base64解密
            byte[] encrypted1 = Base64Utils.decode(sSrc.getBytes(StandardCharsets.UTF_8));
            byte[] original = cipher.doFinal(encrypted1);
            return new String(original, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            log.error("Aes解密出错: " , ex);
            return null;
        }
    }

    // 固定的IV，长度必须为16字节
    private static final String FIXED_IV_STRING = KEY; // 16字节的字符串
    private static final byte[] FIXED_IV = FIXED_IV_STRING.getBytes(StandardCharsets.UTF_8); // CBC加密模式的向量
    /**
     * 加密
     */
    public static String encryptCBC(String sSrc, String sKey) {


        try {
            if (sKey == null) {
                return null;
            }
            byte[] raw = sKey.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");

            IvParameterSpec ivParams = new IvParameterSpec(FIXED_IV);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParams);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            log.error("Aes加密出错: " , e);
            return null;
        }
    }

    /**
     * 解密
     */
    public static String decryptCBC(String sSrc, String sKey) {
        try {
            if (sKey == null) {
                log.error("AesKey为空null");
                return null;
            }

            byte[] raw = sKey.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");

            // Base64解码
            byte[] encrypted = Base64.getDecoder().decode(sSrc);

            IvParameterSpec ivParams = new IvParameterSpec(FIXED_IV);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivParams);
            byte[] original = cipher.doFinal(encrypted);
            return new String(original, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            log.error("Aes解密出错: " , ex);
            return null;
        }
    }

}
