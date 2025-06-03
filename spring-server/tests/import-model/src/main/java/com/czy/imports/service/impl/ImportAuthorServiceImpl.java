package com.czy.imports.service.impl;

import cn.hutool.core.util.IdUtil;
import com.czy.api.domain.Do.neo4j.UserFeatureNeo4jDo;
import com.czy.api.domain.Do.oss.OssFileDo;
import com.czy.api.domain.Do.user.LoginUserDo;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.mapper.UserFeatureRepository;
import com.czy.imports.mapper.LoginUserMapper;
import com.czy.imports.mapper.OssMapper;
import com.czy.imports.mapperEs.UserEsMapper;
import com.czy.imports.service.ImportAuthorService;
import com.utils.mvc.service.MinIOService;
import domain.FileOptionResult;
import domain.SuccessFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 13225
 * @date 2025/6/3 10:44
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ImportAuthorServiceImpl implements ImportAuthorService {

    private final MinIOService minIOService;
    private final OssMapper ossMapper;

//    private final UserMapper userMapper;
    private final LoginUserMapper loginUserMapper;
    private final UserEsMapper userEsMapper;
    private final UserFeatureRepository userFeatureRepository;

    @Override
    public FileOptionResult uploadFiles(String filePath, String bucketName) {
        File file = new File(filePath);
        List<File> files = new ArrayList<>();
        files.add(file);
        FileOptionResult result = minIOService.uploadFiles(files, bucketName);

        // 存储到数据库
        List<SuccessFile> successFiles = result.getSuccessFiles();
        List<OssFileDo> ossFileDos = new ArrayList<>();
        for (SuccessFile successFile : successFiles){
            OssFileDo ossFileDo = new OssFileDo();
            ossFileDo.setFileName(successFile.getFileName());
            ossFileDo.setFileSize(successFile.getFileSize());
            ossFileDo.setBucketName(bucketName);
            ossFileDo.setFileStorageName(successFile.getFileStorageName());
            ossFileDo.setUploadTimestamp(System.currentTimeMillis());
            ossFileDos.add(ossFileDo);
        }
        ossMapper.insertBatch(ossFileDos);
        return result;
    }

    private static final String defaultPassword = "123456";

    @Override
    public void createUser(String userName, String account, String phone, Long fileId) {
        long userId = IdUtil.getSnowflakeNextId();
        // 登录信息
        LoginUserDo loginUserDo = new LoginUserDo();
        loginUserDo.setId(userId);
        loginUserDo.setUserName(userName);
        loginUserDo.setAccount(account);
        loginUserDo.setPassword(defaultPassword);
        loginUserDo.setPhone(phone);
        loginUserDo.setPermission(1);
        loginUserDo.setRegisterTime(System.currentTimeMillis());
        loginUserDo.setLastOnlineTime(System.currentTimeMillis());
        loginUserDo.setAvatarFileId(fileId);

        // 存储到mysql
        loginUserMapper.insertLoginUser(loginUserDo);


        // 基本信息 es + mysql
        UserDo userDo = new UserDo();
        userDo.setId(userId);
        userDo.setUserName(userName);
        userDo.setAccount(account);
        userDo.setPhone(phone);
        userDo.setAvatarFileId(fileId);
        userDo.setRegisterTime(System.currentTimeMillis());
        userDo.setLastOnlineTime(System.currentTimeMillis());

        // 存储 es
//        userMapper.insertUserInfo(userDo);
        userEsMapper.save(userDo);

        // 存储到neo4j
        UserFeatureNeo4jDo userFeatureNeo4jDo = new UserFeatureNeo4jDo();
        userFeatureNeo4jDo.setId(userId);
        userFeatureNeo4jDo.setName(userName);
        userFeatureNeo4jDo.setAccount(account);
        userFeatureRepository.save(userFeatureNeo4jDo);

    }

    @Override
    public void createPost(String title, String content, String publishTime, String filePath) {

    }
}
