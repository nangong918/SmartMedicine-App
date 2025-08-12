package service;

import com.czy.api.constant.user_relationship.UserConstant;
import com.czy.api.domain.Do.user.LoginUserDo;
import com.czy.user.UserServiceApplication;
import com.utils.mvc.redisson.RedissonService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * @author 13225
 * @date 2025/6/25 19:06
 */
@Slf4j
@SpringBootTest(classes = UserServiceApplication.class)
@TestPropertySource("classpath:application.yml")
public class RedisTests {

    @Autowired
    private RedissonService redissonService;

    // LoginUserDo
    @Test
    public void storageAndGetTest(){
        LoginUserDo loginUserDo = new LoginUserDo();
        loginUserDo.setId(1L);
        loginUserDo.setUserName("张三");
        loginUserDo.setAccount("SweetLemon77");
        loginUserDo.setPassword("123456");
        loginUserDo.setPhone("12345678901");
        loginUserDo.setPermission(1);
        loginUserDo.setRegisterTime(System.currentTimeMillis());
        loginUserDo.setLastOnlineTime(loginUserDo.getRegisterTime());
        loginUserDo.setAvatarFileId(1L);

        String key = UserConstant.USER_REGISTER_REDIS_KEY + loginUserDo.getPhone();
        boolean result = redissonService.setObjectByJson(key, loginUserDo, UserConstant.USER_CHANGE_KEY_EXPIRE_TIME);
        if (!result){
            log.warn("用户注册失败，account: {}", loginUserDo.getAccount());
        }
        else {
            loginUserDo = redissonService.getObjectFromJson(key, LoginUserDo.class);
            log.info("已经将用户信息缓存到Redis，redis-key：{}，存储信息：{}", key, loginUserDo.toJsonString());
        }
    }
}
