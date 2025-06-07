package com.czy.user.controller;




import com.czy.api.api.auth.SmsService;
import com.czy.api.api.user_relationship.LoginService;
import com.czy.api.api.user_relationship.UserService;
import com.czy.api.constant.user_relationship.UserConstant;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.ao.auth.LoginJwtPayloadAo;
import com.czy.api.domain.dto.base.BaseResponse;
import com.czy.api.domain.dto.http.request.LoginResetPasswordRequest;
import com.czy.api.domain.dto.http.request.LoginUserRequest;
import com.czy.api.domain.dto.http.request.PhoneLoginRequest;
import com.czy.api.domain.dto.http.request.RegisterUserRequest;
import com.czy.api.domain.dto.http.request.ResetUserInfoRequest;
import com.czy.api.domain.dto.http.request.SendSmsRequest;
import com.czy.api.domain.dto.http.response.LoginSignResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 登录Controller设计
 * 1. 密码注册
 *      1.访问次数限流
 *      2.非明文数据传输
 *      3.非明文数据存储
 *      4.高并发处理 [原子操作，防止重复操作]
 * 2. 密码登录
 *      1.访问次数限流
 *      2.非明文数据传输
 *      3.非明文数据存储
 *      4.高并发处理 [原子操作，防止重复操作]
 *      5.登录后的JWT，SignToken
 * 3. 重置密码
 *      1.访问次数限流
 *      2.非明文数据传输
 *      3.非明文数据存储
 *      4.高并发处理 [原子操作，防止重复操作]
 *      5.使用JWT，SignToken进行验证本人操作；Admin直接操作，无需Token
 * 4. 短信注册/登录
 *      1.访问次数限流
 *      2.非明文数据传输
 *      3.短信存储在Redis中，设置有效时间；使用次数也设置在Redis中，设置有效次数；被使用之后立刻销毁。
 *      4.高并发处理 [原子操作，防止重复操作]
 * <p>
 * 1. Security: 限制Controller访问权限；Admin/User区分
 * 2. 访问次数限流：Ip，UUID，Phone，Account；白名单：登录次数记录；黑名单：超过次数；防止恶意登录恶意攻击[Redis实现]
 * 3. 非明文数据传输：1.密文传输；SSL/TLS：确保所有数据传输使用 HTTPS 加密；2.过滤器，拦截器实现双向非对称加密
 * 4. 非明文数据存储：密码以(bcrypt、PBKDF2 或 Argon2)存储数据库
 * 5. 确定登录状态：
 *              登录之后WebSocket心跳请求确认在线状态，
 *              被顶号立刻退出，
 *              JWT，SignToken过期立刻退出。
 * 6. 数据有效时间：JWT，SignToken，验证码在Redis中设置有效时间
 * 7. 高并发处理：线程池 + RabbitMQ消息队列 + 同步锁(MySQL事务,Redis事务)
 *              RabbitMQ:
 *                  限流和访问控制:  当进行访问次数限流时，可以将每次登录请求发送到 RabbitMQ 队列中，由后台服务异步处理。
 *                                 这可以减少主线程的压力，提高系统的响应速度。
 *                  发送短信验证码:  在短信注册/登录中，发送短信验证码的请求可以通过 RabbitMQ 进行异步处理。
 *                                 这样可以避免在主线程中直接发送短信，减少响应时间，并能处理高并发请求。
 *                  记录登录日志:    每次登录操作（成功或失败）都可以发送一条消息到 RabbitMQ，专门用于记录操作日志。
 *                                 后端可以有一个专门的消费者来处理日志存储，避免对主业务逻辑的影响。
 *                  重置密码通知:    在重置密码的过程中，可以通过 RabbitMQ 发送通知（如电子邮件或短信），告知用户密码已被重置。
 *              Kafka:
 *                  事件记录:        埋点风控事件：使用 Kafka 记录用户的登录、注册、重置密码等事件。
 *                                  这样可以为后续的数据分析、审计和监控提供支持。
 *                  数据同步?:       在大型分布式系统中，可以使用 Kafka 来处理用户数据（如注册信息、登录状态等）的异步同步。
 *                  高并发处理:      在高并发的情况下，可以将用户请求的数据（如登录状态）发送到 Kafka，后端消费者异步处理这些请求，确保系统的高效性和可扩展性。
 *  RabbitMQ 更适合处理短时间的异步任务，如发送短信、记录日志等，关注于任务的可靠性和实时性。
 *           类似线程池，但是能过够持久化任务，还可跨服务的任务传递
 *  Kafka 更适合处理高吞吐量的数据流和事件记录，关注于数据的持久化和实时分析。
 */

@Slf4j
@Tag(name = "Login", description =
        "* Security：Admin/User区分" +
                "* 分配JWT(JSON Web Token)：访问其他接口的时候需要验证SignToken；存储在Redis,设置 Token 的失效时间" +
                "* 加密：自定义过滤器、拦截器进行双向非对称加密；SSL/TLS：确保所有数据传输使用 HTTPS 加密" +
                "* 密码加密：使用强哈希算法（如 bcrypt、Argon2）替代 MD5，以非明文存储到数据库" +
                "* 登录Ip次数限流：白名单：登录次数记录；黑名单：超过次数；防止恶意登录恶意攻击[Redis实现]" +
                "* 确定登录状态：登录之后WebSocket心跳请求，获取登录数据以及分配登录资源" +
                "* 短信登录：短信验证码，设置错误次数限制，有效时间[Redis实现]" +
                "* 限制并发登录：Redis 记录用户的登录状态，登号的时候提示是否顶号，被顶的号通过Websocket强制下线，并要求前端删除缓存" +
                "* 高并发处理：线程池 + RabbitMQ消息队列 + 同步锁(MySQL事务,Redis事务)"
)
@CrossOrigin(origins = "*") // 跨域
@RequiredArgsConstructor // 自动注入@Autowired
@RestController
@RequestMapping(UserConstant.Login_CONTROLLER)
public class LoginController {

    private final LoginService loginService;
    private final UserService userService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private SmsService smsService;


    // Admin账号重置登录密码
//    @PreAuthorize("hasRole('SUPER_ADMIN')")
    // Admin账号重置登录密码
    @GetMapping("/resetUserPassword/{account}")
    public Mono<BaseResponse<LoginUserRequest>> resetUserPasswordAdmin(@PathVariable("account") String account) {
        // Get方法手动验证
        if (!StringUtils.hasText(account)) {
            String warningMessage = String.format("用户account不能为空，account: %s", account);
            return Mono.just(BaseResponse.LogBackError(warningMessage, log));
        }
        else if (userService.checkAccountExist(account) <= 0) {
            String warningMessage = String.format("用户account不存在，account: %s", account);
            return Mono.just(BaseResponse.LogBackError(warningMessage, log));
        }
        else {
            LoginUserRequest newUser = loginService.resetUserPasswordByAdmin(account, null);
            return Mono.just(BaseResponse.getResponseEntitySuccess(newUser));
        }
    }

    // 密码注册
    @PostMapping(UserConstant.Password_Register)
    public Mono<BaseResponse<LoginUserRequest>> passwordRegisterUser(@RequestBody @Validated RegisterUserRequest request) {
        String errorMessage = "";
        if (userService.checkAccountExist(request.getAccount()) > 0) {
            errorMessage = "用户账号已存在";
            return Mono.just(BaseResponse.LogBackError(errorMessage, log));
        }
        else {
            LoginUserRequest newUser = loginService.registerUser(request.getUserName(), request.getAccount(), request.getPassword());
            return Mono.just(BaseResponse.getResponseEntitySuccess(newUser));
        }
    }


    // 密码登录
    @PostMapping(UserConstant.Password_Login)
    public Mono<BaseResponse<LoginSignResponse>> passwordLoginUser(@Validated @RequestBody LoginUserRequest request) {
        String userAccount = null;
        if (userService.checkAccountExist(request.getAccount()) > 0) {
            userAccount = request.getAccount();
        }
        else {
            String errorMessage = "用户账号不存在";
            return Mono.just(BaseResponse.LogBackError(errorMessage, log));
        }
        boolean result = loginService.checkPassword(userAccount, request.getPassword());
        if (!result) {
            String errorMessage = "用户密码错误";
            return Mono.just(BaseResponse.LogBackError(errorMessage, log));
        }
        LoginJwtPayloadAo loginJwtPayloadAo = new LoginJwtPayloadAo(userAccount, request.getUuid(), UserConstant.JWT_FUNCTION_LOGIN);
        LoginSignResponse LoginSignResponse = loginService.loginUser(loginJwtPayloadAo);
        return Mono.just(BaseResponse.getResponseEntitySuccess(LoginSignResponse));
    }

    // 重置密码 JWT 校验
    @PostMapping(UserConstant.Reset_Password)
    public Mono<BaseResponse<LoginUserRequest>> resetUserPassword(@Validated @RequestBody LoginResetPasswordRequest loginResetPasswordDTO) {
        LoginUserRequest newUser = loginService.resetUserPasswordByUser(
                loginResetPasswordDTO.getAccount(),
                loginResetPasswordDTO.getPassword(),
                loginResetPasswordDTO.getNewPassword()
        );
        if (newUser != null) {
            return Mono.just(BaseResponse.getResponseEntitySuccess(newUser));
        }
        return Mono.just(BaseResponse.LogBackError("重置密码失败", log));
    }

    // 重置userInfo
    @PostMapping(UserConstant.Reset_UserInfo)
    public Mono<BaseResponse<UserDo>> resetUserInfo(@Validated @RequestBody ResetUserInfoRequest request) {
        UserDo newUser = userService.resetUserInfo(
                request.getAccount(),
                request.getUserName(),
                request.getAvatarFileId()
        );
        if (newUser != null) {
            return Mono.just(BaseResponse.getResponseEntitySuccess(newUser));
        }
        return Mono.just(BaseResponse.LogBackError("重置用户信息失败", log));
    }


    // 发送短信
    @PostMapping(UserConstant.Send_Sms)
    public Mono<BaseResponse<String>> sendSms(@Validated @RequestBody SendSmsRequest request) {
        if (smsService.sendSms(request.getPhone())) {
            return Mono.just(BaseResponse.getResponseEntitySuccess(String.format("发送短信成功phone：%s", request.getPhone())));
        } else {
            return Mono.just(BaseResponse.LogBackError("发送短信失败", log));
        }
    }


    // 短信注册/登录
    @PostMapping(UserConstant.Sms_Login)
    public Mono<BaseResponse<LoginSignResponse>> smsRegisterOrLogin(@Validated @RequestBody PhoneLoginRequest request) {
        // 直接使用 request，无需手动校验
        String phone = request.getPhone();
        String code = request.getCode();
        boolean checkSms = smsService.checkSms(phone, code);
        if (checkSms){
            UserDo userDo = userService.getUserByPhone(phone);
            LoginJwtPayloadAo loginJwtPayloadAo = new LoginJwtPayloadAo(
                    userDo.getAccount(),
                    request.getUuid(),
                    UserConstant.JWT_FUNCTION_REGISTER
            );
            LoginSignResponse signResponse = loginService.loginUser(loginJwtPayloadAo);
            if (signResponse != null) {
                return Mono.just(BaseResponse.getResponseEntitySuccess(signResponse));
            }
        }
        return Mono.just(BaseResponse.LogBackError("短信注册/登录失败", log));
    }
}
