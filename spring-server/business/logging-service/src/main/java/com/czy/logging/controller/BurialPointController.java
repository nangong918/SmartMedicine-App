package com.czy.logging.controller;

import com.czy.api.api.user.UserService;
import com.czy.api.constant.logging.LoggingConstant;
import com.czy.api.domain.ao.feature.UserCityLocationInfoAo;
import com.czy.api.domain.dto.base.BaseResponse;
import com.czy.api.domain.dto.http.request.UserBrowseTimeRequest;
import com.czy.api.domain.dto.http.request.UserCityLocationRequest;
import com.czy.api.domain.dto.http.request.UserClickPostRequest;
import com.czy.logging.service.UserActionRecordService;
import com.czy.springUtils.debug.DebugConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 13225
 * @date 2025/5/9 10:25
 * 埋点数据
 * 获取用户/帖子的隐形特征
 * 对于用户显性特征，消息将通过mq发送到此服务
 * 对于隐形特征，则需要App进行埋点操作，此controller就是收集隐性操作的Controller
 * 用户显性数据包括（显性数据不需要http，直接mq获取）：
 *      1.用户的搜索 [方向特征]
 *      2.操作数据 [方向特征]
 *          点赞，
 *          评论（BERT情感分类NLE：肯定态度，否定态度，中立态度），
 *          收藏；
 *          转发
 * 物品显性数据包括：
 *      1.物品的被搜索：各级匹配加不同的热度值 [热度特征]
 *      2.物品的点赞、评论、收藏、转发：不同的操作加上不同的热度值 [热度特征]
 *          （
 *              热度值要根据发布时间衰减：
 *              采用现场计算而不是大数据定时任务：大量的对天文数字级别的用户做离线定时任务是不明智的
 *              比如每个埋点数据记录特征和时间，获取用户特征的时候先筛选最近30天的数据，超过30天就直接不获取，并且全部埋点数据按照权重衰减
 *           ）
 *      3.物品的文本特征 [方向特征]
 * 用户隐性数据包括：
 *      1.用户的点击召回 [方向特征]
 *      2.用户的浏览时长（1.根据文章长度估算大概要读取的时间 - 用户已读取的时间 2.固定判断时长：超过30秒一定增加权重）[方向特征]
 *      3.用户的手机机型；用户的ip地址，用户的经纬度信息（如果能够获取：提供说明文档，让用户同意）[方向特征]
 * 物品隐形数据包括：
 *      1.点击率：点击次数/曝光次数 [热度特征]
 *      2.用户的浏览时长 [热度特征]
 * <p>
 * 基本上用户的特诊都是方向特征，物品的特征都是热度特征；物品的方向特征只从第一次标签喝内容获取
 * 整个推荐系统评估：曝光率，点击率
 */
@Slf4j
@CrossOrigin(origins = "*") // 跨域
@Deprecated                 // 过时，改为netty传输埋点事件
@RestController
@Validated // 启用校验
@RequiredArgsConstructor // 自动注入@Autowired
@RequestMapping(LoggingConstant.BurialPoint_CONTROLLER)
public class BurialPointController {

    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private UserService userService;
    private final UserActionRecordService userActionRecordService;
    private final DebugConfig debugConfig;

    // 上传用户的城市等信息
    @Deprecated
    @PostMapping("/city")
    public BaseResponse<String>
    uploadUserInfo(@Validated @RequestBody UserCityLocationRequest request) {
        if (!debugConfig.isRecordUserAccount()){
            return BaseResponse.getResponseEntitySuccess("后端debug模式不记录");
        }
        Long userId = userService.getIdByAccount(request.getUserAccount());
        if (userId == null){
            return BaseResponse.LogBackError("用户不存在");
        }
        UserCityLocationInfoAo ao = request.getAo();
        ao.setUserId(userId);
        userActionRecordService.uploadUserInfo(ao, request.getTimestamp());
        return BaseResponse.getResponseEntitySuccess("上传成功");
    }

    // 用户点击帖子（与浏览时长拆开，避免用户直接划掉后台）
    @Deprecated
    @PostMapping("/clickPost")
    public BaseResponse<String>
    clickPost(@Validated @RequestBody UserClickPostRequest request) {
        if (!debugConfig.isRecordUserAccount()){
            return BaseResponse.getResponseEntitySuccess("后端debug模式不记录");
        }
        Long userId = userService.getIdByAccount(request.getUserAccount());
        if (userId == null){
            return BaseResponse.LogBackError("用户不存在");
        }
        userActionRecordService.clickPost(userId, request.getPostId(), request.getTimestamp(), request.getTimestamp());
        return BaseResponse.getResponseEntitySuccess("上传成功");
    }

    // 上传用用的点击帖子 + 浏览时长
    @Deprecated
    @PostMapping("/browseTime")
    public BaseResponse<String>
    uploadClickPostAndBrowseTime(@Validated @RequestBody UserBrowseTimeRequest request) {
        if (!debugConfig.isRecordUserAccount()){
            return BaseResponse.getResponseEntitySuccess("后端debug模式不记录");
        }
        Long userId = userService.getIdByAccount(request.getUserAccount());
        if (userId == null){
            return BaseResponse.LogBackError("用户不存在");
        }
        userActionRecordService.uploadClickPostAndBrowseTime(userId, request.getPostId(), request.getBrowseDuration(), request.getTimestamp());
        return BaseResponse.getResponseEntitySuccess("上传成功");
    }

}
