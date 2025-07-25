package com.czy.post.controller;

import com.czy.api.api.user_relationship.UserService;
import com.czy.api.constant.post.PostConstant;
import com.czy.api.domain.ao.post.PostInfoAo;
import com.czy.api.domain.dto.base.BaseResponse;
import com.czy.api.domain.dto.http.request.UserBriefRequest;
import com.czy.api.domain.dto.http.response.UserBriefResponse;
import com.czy.api.domain.entity.UserViewEntity;
import com.czy.api.domain.vo.post.PostPreviewVo;
import com.czy.api.exception.CommonExceptions;
import com.czy.api.exception.UserExceptions;
import com.czy.post.front.PostFrontService;
import com.czy.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author 13225
 * @date 2025/7/24 10:05
 */
@Slf4j
@CrossOrigin(origins = "*") // 跨域
@RestController
@Validated // 启用校验
@RequiredArgsConstructor // 自动注入@Autowired
@RequestMapping(PostConstant.USER_BRIEF_CONTROLLER)
public class UserBriefController {

    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private UserService userService;
    private final PostService postService;
    private final PostFrontService postFrontService;

    @PostMapping("/get")
    public BaseResponse<UserBriefResponse> getUserBrief(@Valid @RequestBody UserBriefRequest request){
        if (request.getReceiverId() == null){
            return BaseResponse.LogBackError(CommonExceptions.PARAM_ERROR);
        }

        UserViewEntity beQueryUser = userService.getUserViewEntity(request.getReceiverId());
        if (beQueryUser == null || beQueryUser.getUserId() == null){
            return BaseResponse.LogBackError(UserExceptions.USER_NOT_EXIST);
        }

        List<PostInfoAo> postInfoAos = postService.findPublishedPostsByUserId(
                beQueryUser.getUserId(),
                request.getPostNum(),
                request.getPostSize()
        );
        List<PostPreviewVo> postPreviewVos = postFrontService.toPostPreviewVoList(postInfoAos);

        UserBriefResponse response = new UserBriefResponse();
        response.setUserView(beQueryUser);
        response.setUserPosts(postPreviewVos);

        return BaseResponse.getResponseEntitySuccess(response);
    }
}
