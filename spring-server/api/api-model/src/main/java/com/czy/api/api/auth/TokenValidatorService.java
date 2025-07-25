package com.czy.api.api.auth;


import jwt.BaseJwtPayloadAo;
import jwt.TokenStatue;

/**
 * @author 13225
 * @date 2025/1/14 11:45
 */
public interface TokenValidatorService {

    /**
     * 验证token是否属于某个用户 (是LoginPayloadAo)
     * @param token
     * @param userId
     * @return              true:属于
     * @throws Exception     Exception
     */
    boolean checkTokenBelongUser(String token, Long userId) throws Exception;

    /**
     * 验证token状态
     * @param accessToken
     * @param key
     * @return
     */
    TokenStatue checkTokenStatus(String accessToken, String key);

    /**
     * accessToken验证逻辑 [多个key轮番验证]
     * @param accessToken   accessToken
     * @return  true:验证
     */
    boolean validateToken(String accessToken, String key);

    /**
     * 验证token是否正确
     * @param token  token
     * @param key    key
     * @return       true:正确
     */
    boolean checkTokenIsValid(String token, String key);

    /**
     * 验证token是否没过期
     * @param token     token
     * @param key       key
     * @return          true:否过期
     */
    boolean checkTokenIsEffective(String token, String key);

    /**
     * 获取token中的payload
     * @param token     token
     * @param clazz     payload类型
     * @return          payload
     * @param <T>       payload类型
     * @throws Exception    Exception
     */
    <T extends BaseJwtPayloadAo> T getJwtTokenAo(String token, Class<T> clazz) throws Exception;
}
