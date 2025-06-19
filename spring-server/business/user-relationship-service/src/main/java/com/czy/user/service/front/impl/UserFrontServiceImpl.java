package com.czy.user.service.front.impl;

import com.czy.api.api.oss.OssService;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.vo.user.UserVo;
import com.czy.user.mapper.mysql.user.UserMapper;
import com.czy.user.service.front.UserFrontService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 13225
 * @date 2025/6/10 16:28
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserFrontServiceImpl implements UserFrontService {

    private final UserMapper userMapper;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private OssService ossService;

    // user id -> user vo
    @Override
    public UserVo getUserVoById(Long userId) {
        UserDo userDo = userMapper.getUserById(userId);
        if (userDo == null || userDo.getId() == null){
            return null;
        }
        UserVo userVo = new UserVo();
        userVo.setUserId(userDo.getId());
        userVo.setUserName(userDo.getUserName());
        userVo.setAccount(userDo.getAccount());
        userVo.setPhone(userDo.getPhone());

        Long fileId = userDo.getAvatarFileId();
        if (fileId == null){
            userVo.setAvatarUrl("");
        }
        else {
            List<Long> fileIds = new ArrayList<>(1);
            fileIds.add(fileId);
            List<String> fileUrls = ossService.getFileUrlsByFileIds(fileIds);
            userVo.setAvatarUrl(fileUrls.get(0));
        }
        return userVo;
    }

}
