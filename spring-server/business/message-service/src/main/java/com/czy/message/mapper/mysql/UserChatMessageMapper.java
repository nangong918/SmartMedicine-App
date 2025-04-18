package com.czy.message.mapper.mysql;


import com.czy.api.domain.Do.message.UserChatMessageDo;
import com.czy.api.domain.bo.message.UserChatMessageBo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 13225
 * @date 2025/2/26 17:15
 * [入参两个用户的id都可以作为senderId/receiverId，因为senderId/receiverId是唯一索引记录，下面同理]
 */
@Mapper
public interface UserChatMessageMapper {

    // 增加一条 by object
    int insert(UserChatMessageDo userChatMessage);

    // 删除一条 by id
    int deleteById(Long id);

    // 查询一条 by id
    UserChatMessageDo selectById(Long id);

    // 更新一条 by id
    int updateById(UserChatMessageDo userChatMessage);

    // 批量导入 by List<object>
    int batchInsert(List<UserChatMessageDo> list);

    // 批量删除 by List<id>
    int batchDelete(List<Long> ids);

    // 批量查询 by List<id>
    List<UserChatMessageDo> batchSelect(List<Long> ids);

    // 批量更新 by List<object>
    int batchUpdate(List<UserChatMessageDo> list);

    // 根据senderId/receiverId查询从timestamp开始往前limitCount数量的 List<id>
    List<Long> selectIdsBefore(@Param("senderId") Long senderId,
                               @Param("receiverId") Long receiverId,
                               @Param("timestamp") Long timestamp,
                               @Param("limitCount") Integer limitCount);

    // 根据senderId/receiverId查询从timestamp开始往前limitCount数量的 List<object>
    List<UserChatMessageBo> selectMessagesBefore(@Param("senderId") Long senderId,
                                                 @Param("receiverId") Long receiverId,
                                                 @Param("timestamp") Long timestamp,
                                                 @Param("limitCount") Integer limitCount);

    // 根据senderId/receiverId查询从timestamp开始往后limitCount数量的 List<id>
    List<Long> selectIdsAfter(@Param("senderId") Long senderId,
                              @Param("receiverId") Long receiverId,
                              @Param("timestamp") Long timestamp,
                              @Param("limitCount") Integer limitCount);

    // 根据senderId/receiverId查询从timestamp开始往后limitCount数量的 List<object> TODO 待验证timestamp是否生效
    List<UserChatMessageBo> selectMessagesAfter(@Param("senderId") Long senderId,
                                                @Param("receiverId") Long receiverId,
                                                @Param("timestamp") Long timestamp,
                                                @Param("limitCount") Integer limitCount);

    // 根据senderId/receiverId删除senderId和receiverId的聊天记录
    int deleteBySenderReceiver(@Param("senderId") Long senderId,
                               @Param("receiverId") Long receiverId);
}
