package com.czy.relationship.mapper;




import com.czy.api.domain.Do.relationship.FriendApplyDo;
import com.czy.api.domain.bo.relationship.NewUserItemBo;
import com.czy.api.domain.bo.relationship.SearchFriendApplyBo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 13225
 * @date 2025/2/25 11:56
 */


@Mapper
public interface FriendApplyMapper {

    // 插入好友申请
    int insertFriendApply(FriendApplyDo friendApply);

    // 根据 ID 删除好友申请
    int deleteFriendApply(Long id);

    // 更新好友申请状态
    int updateFriendApply(FriendApplyDo friendApply);

    // 根据 ID 查询好友申请
    FriendApplyDo getFriendApplyById(Long id);

    // 根据申请用户 ID 查询所有被申请的用户list
    List<FriendApplyDo> getMyAppliesBySenderId(Long senderId);

    // 根据处理用户 ID 查询所有对他发出申请的用户list
    List<FriendApplyDo> getHandleAppliesByHandlerId(Long handleId);

    // 根据申请用户 ID 和处理用户 ID 联合查询
    FriendApplyDo getFriendApplyByUserIds(@Param("applyUserId") Long applyUserId, @Param("handleUserId") Long handleUserId);

    // 根据申请id 和 模糊的 处理用户id 查询
    List<SearchFriendApplyBo> fuzzySearchHandlerByApplyAccount(@Param("applyAccount") String applyAccount, @Param("handlerAccount") String handlerAccount);

    // 根据applyUserId 查询 List<NewUserItemBo>
    List<NewUserItemBo> getHandleMyAddUserResponseList(Long applyUserId);

    // 根据handleUserId 查询 List<NewUserItemBo>
    List<NewUserItemBo> getAddMeRequestList(Long handleUserId);
}
