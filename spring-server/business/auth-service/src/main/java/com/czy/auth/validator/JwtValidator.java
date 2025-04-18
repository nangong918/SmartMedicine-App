package com.czy.auth.validator;



import com.czy.api.constant.auth.JwtConstant;
import com.czy.auth.utils.JwtValidUtil;
import jwt.BaseJwtPayloadAo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


/**
 * @author 13225
 * @date 2025/1/7 15:56
 * <p>
 * 由于每个服务都可以独立地验证和解析 JWT，它们无需共享会话存储；
 *             A服务生成的Token，只要B服务有公钥就能验证；
 * <p>
 * 因为Jwt验证多个服务都需要用所以放在SpringUtils，避免每次Dubbo造成资源浪费。
 * Jwt生成只有AuthService使用，所以单独拆分出了JwtGenerator
 */
@Slf4j
@Component
public class JwtValidator {


    /**
     * 验证Token 2个Token轮流验证
     * @param token token
     * @return  true/false
     */
    public boolean validateToken(String token, String key) {
        if (!StringUtils.hasText(token)){
            log.warn("JwtValidator.validateToken() token is null or empty");
            return false;
        }
        if (!StringUtils.hasText(key)){
            log.warn("JwtValidator.validateToken() key is null or empty");
            return false;
        }
        boolean isTokenEffective = JwtValidUtil.isTokenEffective(token, key);
        boolean isTokenCorrect = JwtValidUtil.isTokenCorrect(token, key);
        return isTokenEffective && isTokenCorrect;
    }

    /**
     * 通过Token获取JwtPayloadAo
     * @param token token
     * @param clazz clazz
     * @return  JwtPay
     * @param <T>   JwtPayloadAo类型
     * @throws Exception
     */
    public <T extends BaseJwtPayloadAo> T getJwtTokenAo(String token, Class<T> clazz) throws Exception{
        if (!StringUtils.hasText(token)){
            log.warn("JwtValidator.getJwtTokenAo() token is null or empty");
            return null;
        }
        try {
            return JwtValidUtil.getJwtTokenAo(token, clazz, JwtConstant.ACCESS_TOKEN_GENERATE_KEY);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    /**
     * 判断Token是否正确
     * @param token token
     * @param key   key
     * @return  true/false
     */
    public boolean isTokenCorrect(String token, String key) {
        try {
            return JwtValidUtil.isTokenCorrect(token, key);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断Token是否有效
     * @param token token
     * @param key   key
     * @return      true/false
     */
    public boolean isTokenEffective(String token, String key) {
        try {
            return JwtValidUtil.isTokenEffective(token, key);
        } catch (Exception e) {
            return false;
        }
    }
}
