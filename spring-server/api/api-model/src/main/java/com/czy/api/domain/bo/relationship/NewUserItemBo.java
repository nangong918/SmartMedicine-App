package com.czy.api.domain.bo.relationship;



import com.czy.api.constant.user_relationship.newUserGroup.AddSourceEnum;
import lombok.Data;


/**
 * 联合查询：
 * 需要查询：
 * 源于：login_user: userAccount,  userName, phone, avatarFileId;
 * 源于：friend_apply: applyTime, handleTime, addSource, applyStatus, handleStatus, isBlack;
 * 源于联合【因为friend_apply表存储的是apply_id,handler_id】：lu JOIN fa: applyAccount, handlerAccount
 * SQL联合查询
 SELECT
     la.user_name AS userName,  -- 用户名称
     la.phone AS handlerPhone,  -- 处理人的电话
     la.avatar_file_id AS avatarFileId,  -- 处理人的头像 URL
     lu.user_account AS handlerAccount,  -- 处理人的账号
     la.user_account AS userAccount, -- 申请者的账号
     fa.apply_account AS applyAccount,  -- 申请者账号
     IFNULL(fa.apply_time, 0) AS applyTime,  -- 申请时间，默认值为 0
     IFNULL(fa.handle_time, NULL) AS handleTime,  -- 处理时间，默认值为 NULL
     IFNULL(fa.add_source, 0) AS addSource,  -- 申请来源，默认值为 0
     IFNULL(fa.chat_list, '[]') AS chatList,  -- 聊天列表，默认返回空 JSON List
     IFNULL(fa.apply_status, 0) AS applyStatus,  -- 申请状态，默认值为 0
     IFNULL(fa.handle_status, 0) AS handleStatus,  -- 处理状态，默认值为 0
     IFNULL(fa.is_black, 0) AS isBlack,  -- 是否拉黑，默认值为 0
 CASE
     WHEN fa.handle_user_id = #{handleUserId} THEN true
     ELSE false
 END AS isAddMeNotResponse  -- 判断是否是添加我的请求
 FROM login_user lu
     LEFT JOIN friend_apply fa ON lu.user_account = fa.handler_account  -- 处理人的账号
     LEFT JOIN login_user la ON fa.apply_account = la.user_account  -- 连接申请者账号
 WHERE fa.handle_user_id = #{handleUserId}  -- 精确匹配 handlerId
 */
@Data
public class NewUserItemBo {

    // 是否是添加我的请求 / 是否对我添加别人请求的响应
    public boolean isAddMeNotResponse = true;

    // 添加记录对话信息 (JSON)
    public String chatList;

    // 被查询到的user的Account
    public String userAccount;

    // 被查询到的user名称
    public String userName;

    // 用户头像
    public Long avatarFileId;

    // 用户头像url
    public String avatarUrl;

    // 申请时间
    public Long applyTime;

    // 处理时间
    public Long handleTime;

    // 添加来源：手机，账号，扫码，群
    public Integer addSource = AddSourceEnum.PHONE.code;

    // 添加状态
    // 申请状态
    public int applyStatus;
    // 处理状态
    public int handleStatus;
    // 是否拉黑 0.否 1.是
    public int isBlack;
    // applyAccount + handlerAccount -> applyAccount是否是本号主 -> 判断此View是否是被添加
    // applyAccount
    public String applyAccount;
    // handlerAccount
    public String handlerAccount;
}
