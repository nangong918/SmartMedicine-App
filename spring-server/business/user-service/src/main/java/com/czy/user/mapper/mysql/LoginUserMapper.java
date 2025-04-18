package com.czy.user.mapper.mysql;




import com.czy.api.domain.Do.user.LoginUserDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
 * @author 13225
 * @date 2025/1/2 13:51
 */
//@org.springframework.stereotype.Repository
@Mapper
public interface LoginUserMapper {

    // 数量
    int getLoginUserCount(String phone);

    // 增
    int insertLoginUser(@Param("do") LoginUserDo loginUserDo);

    // 删
    int deleteLoginUser(Integer id);

    // 改
    int updateLoginUser(@Param("do") LoginUserDo loginUserDo);

    // 查:id,account,count(account)
    LoginUserDo getLoginUser(Integer id);
    LoginUserDo getLoginUserByAccount(String account);
    LoginUserDo getLoginUserByPhone(String phone);
    int getLoginUserCountByAccount(String account);

    // 根据account获取Id
    Integer getIdByAccount(String account);

    // login
    void setLastOnlineTime(
            @Param("id") Long id,
            @Param("lastOnlineTime") Long lastOnlineTime
    );
}
