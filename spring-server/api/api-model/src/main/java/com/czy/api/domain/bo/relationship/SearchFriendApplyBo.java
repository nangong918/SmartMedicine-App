package com.czy.api.domain.bo.relationship;

import lombok.Data;

/**
 * @author 13225
 * @date 2025/2/28 21:42
 * 需要联合查询的表：
 * 1.login_user:account, userName, phone, avatarFileId
 * 2.friend_apply:applyTime, handleTime, source, chatList, applyStatus, handleStatus, isBlack, applyAccount, handlerAccount
 * 联合条件：applyAccount = ? AND handlerAccount = ?
 * 因为account是有索引的，所以可以直接用account查询
 * <p>
 * 联合查询sql：
 * SELECT
 *    lu.account As account,
 *    lu.user_name As userName,
 *    lu.phone As phone,
 *    lu.avatar_file_id As avatarFileId,
 *    IFNULL(fa.apply_time, 0) AS applyTime,
 *    IFNULL(fa.handle_time, 0) AS handleTime,
 *    IFNULL(fa.source, 0) AS source,
 *    IFNULL(fa.chat_list, '[]') AS chatList,
 *    IFNULL(fa.apply_status, 0) AS applyStatus,
 *    IFNULL(fa.handle_status, 0) AS handleStatus,
 *    IFNULL(fa.is_black, 0) AS isBlack,  -- 0表未拉黑，1表示拉黑
 * FROM login_user lu
 *      LEFT JOIN friend_apply fa
 *              ON lu.account = fa.apply_account AND fa.handler_account = #{handlerAccount}
 *              WHERE lu.account = #{applyAccount};
 */
@Data
public class SearchFriendApplyBo {

    public String account;
    public String userName;
    public String phone;
    // 申请时间 (时间戳)
    public Long applyTime;
    // 处理时间 (时间戳，可以为空)
    public Long handleTime;
    // 申请来源 (根据需要添加)
    public Integer source;
    // 聊天列表 (JSON 格式)
    public String chatList;
    // 头fileId
    public Long avatarFileId;
    // 申请状态
    public int applyStatus;
    // 处理状态
    public int handleStatus;
    // 是否拉黑
    public boolean isBlack = false;
    // 申请人账号
    public String applyAccount;
    // 处理人账号
    public String handlerAccount;
}
