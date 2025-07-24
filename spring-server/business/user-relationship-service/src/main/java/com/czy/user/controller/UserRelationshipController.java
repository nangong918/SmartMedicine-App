package com.czy.user.controller;


import com.czy.api.api.user_relationship.UserRelationshipService;
import com.czy.api.api.user_relationship.UserService;
import com.czy.api.constant.user_relationship.RelationshipConstant;
import com.czy.api.constant.user_relationship.newUserGroup.ApplyStatusEnum;
import com.czy.api.constant.user_relationship.newUserGroup.HandleStatusEnum;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.ao.relationship.MyFriendItemAo;
import com.czy.api.domain.ao.relationship.NewUserItemAo;
import com.czy.api.domain.ao.relationship.SearchFriendApplyAo;
import com.czy.api.domain.dto.base.BaseResponse;
import com.czy.api.domain.dto.http.base.BaseHttpRequest;
import com.czy.api.domain.dto.http.request.GetMyFriendsRequest;
import com.czy.api.domain.dto.http.request.SearchUserByNameRequest;
import com.czy.api.domain.dto.http.request.SearchUserRequest;
import com.czy.api.domain.dto.http.response.GetAddMeRequestListResponse;
import com.czy.api.domain.dto.http.response.GetHandleMyAddUserResponseListResponse;
import com.czy.api.domain.dto.http.response.GetMyFriendsResponse;
import com.czy.api.domain.dto.http.response.SearchUserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @author 13225
 * @date 2025/2/19 11:12
 */

@Slf4j
@CrossOrigin(origins = "*") // 跨域
@RequiredArgsConstructor // 自动注入@Autowired
@RestController
@RequestMapping(RelationshipConstant.Relationship_CONTROLLER)
public class UserRelationshipController {
//    private final LoginService loginService;
//    private final ToSocketMqSender clusterEventsPusher;
    private final UserRelationshipService userRelationshipService;
    private final UserService userService;

    /**
     * like模糊搜索用户
     * @param request   请求体，包含senderId和receiverId；其中receiverId是模糊的account
     * @return List搜索结果。之所以是list是因为模糊搜索可能出现一系列匹配
     */
    @PostMapping(RelationshipConstant.Search_User_ByAccount)
    public BaseResponse<SearchUserResponse>
    searchUserByAccount(@Validated @RequestBody SearchUserRequest request) {
//        UserDo receiverDo = userService.getUserById(request.getReceiverId());
        UserDo receiverAccountDo = userService.getUserByAccount(request.getUserData());
        UserDo receiverPhoneDo = userService.getUserByPhone(request.getUserData());
//        UserDo receiverNameDO = userService.getUserByAccount(request.getUserData());
        SearchUserResponse searchUser = new SearchUserResponse();
        List<SearchFriendApplyAo> searchByAccountList = searchFriend(request.getSenderId(), receiverAccountDo);
        List<SearchFriendApplyAo> searchByPhoneList = searchFriend(request.getSenderId(), receiverPhoneDo);
        // 合并并去重
        List<SearchFriendApplyAo> finalList = Stream.concat(searchByAccountList.stream(), searchByPhoneList.stream())
                .distinct() // 去重
                .collect(Collectors.toList());
        searchUser.setUserList(finalList);
        return BaseResponse.getResponseEntitySuccess(searchUser);
    }

    private List<SearchFriendApplyAo> searchFriend(Long senderId, UserDo receiverDo) {
        if (receiverDo == null || receiverDo.getId() == null){
            return new ArrayList<>();
        }
        return userRelationshipService.searchFriend(senderId, receiverDo.getAccount());
    }

    /**
     * es + userName搜索用户
     * @param request
     * @return
     */
    @PostMapping(RelationshipConstant.Search_User_ByName)
    public Mono<BaseResponse<SearchUserResponse>>
    searchUserByName(@Validated @RequestBody SearchUserByNameRequest request) {
        List<SearchFriendApplyAo> searchFriendApplyAoList =
                userRelationshipService.searchFriendByName(request.getSenderId(), request.getUserName());
        SearchUserResponse searchUser = new SearchUserResponse();
        searchUser.setUserList(searchFriendApplyAoList);
        return Mono.just(BaseResponse.getResponseEntitySuccess(searchUser));
    }

    /**
     * 获取添加我的申请列表
     * @param request
     * @return
     */
    @PostMapping(RelationshipConstant.Get_Add_Me_Request_List)
    public Mono<BaseResponse<GetAddMeRequestListResponse>>
    getAddMeRequestList(@Validated @RequestBody BaseHttpRequest request){
        List<NewUserItemAo> list = userRelationshipService.getAddMeRequestList(request.getSenderId());
        GetAddMeRequestListResponse response = new GetAddMeRequestListResponse();
        response.setAddMeRequestList(list);
        return Mono.just(BaseResponse.getResponseEntitySuccess(response));
    }

    /**
     * 获取我处理的我添加的申请列表
     * @param request
     * @return
     */
    @PostMapping(RelationshipConstant.Get_Handle_My_Add_User_Response_List)
    public Mono<BaseResponse<GetHandleMyAddUserResponseListResponse>>
    getHandleMyAddUserResponseList(@Validated @RequestBody BaseHttpRequest request){
        List<NewUserItemAo> list = userRelationshipService.getHandleMyAddUserResponseList(request.getSenderId());
        GetHandleMyAddUserResponseListResponse response = new GetHandleMyAddUserResponseListResponse();
        response.setHandleMyAddUserResponseList(list);
        return Mono.just(BaseResponse.getResponseEntitySuccess(response));
    }

    /**
     * 好友列表
     * @param request
     * @return
     */
    @PostMapping(RelationshipConstant.Get_My_Friend_List)
    public Mono<BaseResponse<GetMyFriendsResponse>>
    getMyFriendList(@Validated @RequestBody GetMyFriendsRequest request){
        List<MyFriendItemAo> list = userRelationshipService.getMyFriendList(request.getSenderId());
        GetMyFriendsResponse response = new GetMyFriendsResponse();
        response.setAddMeRequestList(list);
        return Mono.just(BaseResponse.getResponseEntitySuccess(response));
    }

    /**
     * 获取未处理的好友申请数量
     * 可以写成service然后交给netty
     * @param request
     * @return
     */
    @PostMapping(RelationshipConstant.Get_My_Friend_Apply_List)
    public Mono<BaseResponse<Integer>>
    getMyFriendApplyList(@Validated @RequestBody BaseHttpRequest request){
        // 状态判断：添加我的，我的处理状态是未处理或者空
        int response = 0;
        List<NewUserItemAo> addMeList = userRelationshipService.getAddMeRequestList(request.getSenderId());
//        List<NewUserItemAo> myAddList = userService.getHandleMyAddUserResponseList(senderId);
//        Integer response = addMeList.size() + myAddList.size();

        for (NewUserItemAo newUserItemAo : addMeList){
            if (newUserItemAo.addUserStatusAo != null){
                // 还在申请且未处理
                if (newUserItemAo.addUserStatusAo.applyStatus == ApplyStatusEnum.APPLYING.code &&
                        newUserItemAo.addUserStatusAo.handleStatus == HandleStatusEnum.NOT_HANDLE.code
                ){
                    response += 1;
                }
            }
        }

        return Mono.just(BaseResponse.getResponseEntitySuccess(response));
    }

    // 添加用户好友 (废弃：已改为Netty请求)
//    @PostMapping("/addUser")
//    public Mono<ResponseEntity<BaseResponse<Void>>>
//    addUserFriend(@Valid @RequestBody AddUserRequest request) {
//        // 检查账号是否已存在
//        if (loginService.checkAccount(request.getAddUserAccount()) <= 0) {
//            String warningMessage = String.format("用户account不存在，account: %s", request.getAddUserAccount());
//            return Mono.just(BaseResponse.LogBackError(warningMessage, log));
//        }
//
//        // 转为响应体
//        AddUserToTargetUserResponse response = new AddUserToTargetUserResponse();
//        response.setType(MessageTypeTranslator.translateClean(request.getType()));
//        response.setReceiverId(request.getSenderId());
//        response.setSenderId(request.getReceiverId());
//        response.setAppliedUserAccount(request.getAddUserAccount());
//        response.setAppliedUserName(request.getMyName());
//        response.setAppliedUserAddContent(request.getAddContent());
//        response.AppliedUserApplyStatus = request.applyType;
//
//        // 构建Message
//        Message msg = response.getToMessage();
//
//        // 推送消息
//        clusterEventsPusher.push(msg);
//
//        // 添加到MySQL持久化
//        AddUserAo addUserAo = new AddUserAo();
//        addUserAo.applyAccount = request.getSenderId();
//        addUserAo.handlerAccount = request.getReceiverId();
//        addUserAo.applyTime = Long.valueOf(request.getTimestamp());
//        addUserAo.applyContent = request.addContent;
//        addUserAo.source = request.source;
//        addUserAo.applyStatus = request.applyType;
//
//        // 添加好友
//        userService.addUserFriend(addUserAo);
//
//        return Mono.just(BaseResponse.getResponseEntitySuccess(null));
//    }

    // 取消申请 (废弃：已改为Netty请求)
//    @PostMapping("/cancelAddUser")
//    public Mono<ResponseEntity<BaseResponse<Void>>>
//    cancelAddUser(@Valid @RequestBody BaseHttpRequest request){
//
//        // 添加到MySQL持久化
//        AddUserAo addUserAo = new AddUserAo();
//        addUserAo.applyAccount = request.getSenderId();
//        addUserAo.handlerAccount = request.getReceiverId();
//        addUserAo.applyTime = request.getTimestamp();
//
//        // 取消添加好友
//        userService.cancelAddUserFriend(addUserAo);
//
//        CancelAddMeResponse response = new CancelAddMeResponse();
//        // 更换两个Id
//        response.receiverId = request.getSenderId();
//        response.senderId = request.getReceiverId();
//        response.type = MessageTypeTranslator.translateClean(request.getType());
//        response.timestamp = request.getTimestamp();
//        response.isAgree = ApplyStatusEnum.NOT_APPLY.code;
//
//        // 构建Message
//        Message msg = response.getToMessage();
//
//        // 推送消息
//        clusterEventsPusher.push(msg);
//
//        return Mono.just(BaseResponse.getResponseEntitySuccess(null));
//    }

    // 处理加好友的响应 (废弃：已改为Netty请求)
//    @PostMapping("/handleAddedUser")
//    public Mono<ResponseEntity<BaseResponse<Void>>>
//    handleAddedUser(@Valid @RequestBody HandleAddedUserRequest request){
//        HandleAddedMeAo handleAddedMeAo = new HandleAddedMeAo();
//        handleAddedMeAo.setByRequest(request);
//
//        Message msg = userService.handleAddedUser(handleAddedMeAo);
//        log.info("处理加好友的响应: {}", (msg != null));
//
//        // 推送消息
//        clusterEventsPusher.push(msg);
//
//        return Mono.just(BaseResponse.getResponseEntitySuccess(null));
//    }


}

/*
 * 先设计成先直接从数据库拉取吧。
 * 数据库设计：apply_user_id;handle_user_id;apply_time;handle_time;handle_state
 * 好友存储数据库：未添加3个月内的未添加消息存储在Redis；
 * 添加了的存储在MySQL持久化。
 * Redis设计：UserId_FriendList:List<AddUserRequest> ；Key是UserId；设置过期时间7天。
 */

/**
 * 收到前端消息：Http请求和Socket消息：
 * Nginx均衡负载
 * 客户端A发送方用Netty将Socket请求发送到服务器。
 * 服务器用Netty将Socket消息根据消息类型分类然后注册为MessageEvent发布
 * MessageListener获取订阅的Redis队列或者RabbitMQ队列的消息。
 * MessageListener对消息处理然后发送给客户端B
 * 服务端用Redis缓存并设置消息过期时间，用于准备B客户端的Http请求。
 * 服务端使用RabbitMq消峰填谷异步批量存储消息到MySQL
 * 客户端B收到Socket消息之后想要查看全部消息，调用了Http请求全部消息
 * 服务端用WebFlux进行分页查询分页响应。
 * 查询到Redis存在数据就将B用户的好友列表返回给客户端B
 * 不存在就查询MySQL的数据，数据存放在Redis中，然后返回给客户端B（或者可以考虑使用ES服务器）
 * Redis分布式锁进行分布式管理
 *
 * 文件上传服务：语音，图片，视频，文件的支持
 *
 * 推荐系统：推荐系统算法，规则集，模型
 */
