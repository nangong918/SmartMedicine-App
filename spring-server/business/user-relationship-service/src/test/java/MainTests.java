import com.czy.api.api.user_relationship.LoginService;
import com.czy.api.api.user_relationship.UserSearchService;
import com.czy.api.api.user_relationship.UserService;
import com.czy.api.constant.user_relationship.UserConstant;
import com.czy.api.domain.Do.user.LoginUserDo;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.ao.auth.LoginJwtPayloadAo;
import com.czy.api.domain.ao.user.UserInfoAo;
import com.czy.api.domain.dto.http.request.LoginUserRequest;
import com.czy.api.domain.dto.http.response.LoginSignResponse;
import com.czy.api.domain.vo.user.UserVo;
import com.czy.user.UserServiceApplication;
import com.czy.user.mapper.mysql.user.LoginUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;


/**
 * @author 13225
 * @date 2025/3/29 10:08
 */
@Slf4j
@SpringBootTest(classes = UserServiceApplication.class)
@TestPropertySource("classpath:application.yml")
public class MainTests {

    // 测试环境，比如nacos，service依赖树，redis，mysql什么的
    // 启动user-relationship-service服务之前需要先启动auth和sms Service
    @Test
    public void test() {
        System.out.println("hello world");
    }

    @Autowired
    LoginService loginService;

    @Autowired
    LoginUserMapper loginUserMapper;

    // checkAccountExist
    @Test
    public void checkAccountExist() throws Exception {
        String account = "1231231412";
        Long id = loginUserMapper.getIdByAccount(account);
        if (id == null) {
            String warningMessage = String.format("用户account不存在，account: %s", account);
            log.warn(warningMessage);
        }
        else {
            log.info("用户account存在，id: {}", id);
        }
    }

    // registerUser
    @Test
    public void registerUser() throws Exception {
        LoginUserRequest loginUserRequest = loginService.registerUser("纳贡same3", "1231231414", "123456");
        log.info("registerUser: {}", loginUserRequest);
    }

    @Test
    public void sqlTest(){
        // getLoginUser
        LoginUserDo loginUserDo = loginUserMapper.getLoginUser(12L);
        System.out.println("loginUserDo = " + loginUserDo);
    }

    @Test
    public void getLoginUserByAccount(){
        LoginUserDo loginUserDo = loginUserMapper.getLoginUserByAccount("admin11");
        System.out.println("loginUserDo = " + loginUserDo);
    }

    // checkPassword
    @Test
    public void checkPassword() throws Exception {
        // 纳贡same
        String account = "1231231412";
        String password = "123456";
        boolean checkPassword = loginService.checkAccountPassword(account, password);
        System.out.println("checkPassword = " + checkPassword);
    }

    // loginUser
    @Test
    public void loginUser() throws Exception {
        // Dubbo传输的对象必须需要进行序列化
        LoginJwtPayloadAo loginJwtPayloadAo = new LoginJwtPayloadAo(
                1L,
                "13225",
                "13225",
                "123456",
                UserConstant.JWT_FUNCTION_LOGIN);
        LoginSignResponse loginUserRequest = loginService.loginUser(loginJwtPayloadAo);
        System.out.println("loginUserRequest = " + loginUserRequest);
    }


    @Autowired
    private UserService userService;
    // resetUserInfo
    @Test
    public void resetUserInfo() throws Exception {
        UserVo userVo = userService.resetUserInfo(
                UserInfoAo.builder()
                        .account("test_user")
                        .username("yayayay")
                        .build());
        System.out.println("loginUserRequest = " + userVo);
    }

    @Autowired
    private UserSearchService userSearchService;

    // Mysql like
    @Test
    public void mysqlLike() throws Exception {
        String userAccount = "test";
        List<UserDo> userDos = userSearchService.searchUserByLikeAccount(userAccount);
        System.out.println("userDos = " + userDos);
    }

    // Es ik
    @Test
    public void esIk() throws Exception {
        String userName = "天下";
        List<UserDo> userDos = userSearchService.searchUserByIkName(userName);
        System.out.println("userDos = " + userDos);
    }

}
