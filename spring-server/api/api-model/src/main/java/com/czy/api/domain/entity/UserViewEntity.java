package com.czy.api.domain.entity;


import com.czy.api.domain.Do.user.UserDo;
import json.BaseBean;
import lombok.Data;

/**
 * 获取好友的方法：
 * 需要获取的：account，user_name，avatar_file_id
 * 属性全部来自表：login_user
 * 关系来自表：user_friend
 * 其中：user_friend存储方式是(id,user_id,friend_id)；user可能作为user也可能作为friend存储
 * 所以查询user_account的全部好友的方法：
 * 先思考连接表：
 * login_user lu JOIN user_friend uf
 * 连接条件：lu.id = uf.user_id
 * 上述是user作为user存储，还有user作为friend存储
 * login_user lu JOIN user_friend uf
 * 连接条件：lu.id = uf.friend_id
 * 综合连接：
 * login_user lu JOIN user_friend uf
 * ON (lu.id = uf.user_id OR lu.id = uf.friend_id)
 * 由于入参是account而不是id，所以要关联account和lu.id
 * WHERE lu.account = #{userAccount}
 * 综合：
 * SELECT
 *      lu.account,
 *      lu.user_name,
 *      luf.avatar_file_id
 * FROM
 *      login_user lu JOIN user_friend uf
 *      ON (lu.id = uf.user_id OR lu.id = uf.friend_id)
 * WHERE lu.account = #{userAccount}
 */


@Data
public class UserViewEntity implements BaseBean {

    // user账号
    public String userAccount;

    // user名称
    public String userName;

    // 用户头像
    public Long avatarFileId;

    // 用户头像url
    public String avatarUrl;

    public void setByLoginUserDo(UserDo userDo) {
        if (userDo == null){
            return;
        }
        this.userAccount = userDo.getAccount();
        this.userName = userDo.getUserName();
        this.avatarFileId = userDo.getAvatarFileId();
    }
}
