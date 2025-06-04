package com.czy.imports.service.impl;

import com.czy.api.api.post.PostImportService;
import com.czy.api.api.user.UserService;
import com.czy.api.domain.Do.oss.OssFileDo;
import com.czy.imports.mapper.OssMapper;
import com.czy.imports.service.ImportAuthorService;
import com.utils.mvc.service.MinIOService;
import domain.FileOptionResult;
import domain.SuccessFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
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
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private UserService userService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private PostImportService postImportService;

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
    public long createUser(String userName, String account, String phone, Long fileId) {
        return userService.registerUser(
                userName,
                account,
                defaultPassword,
                phone,
                fileId
        );
    }

    @Override
    public void createPost(String title, String content, Long publishTime, List<Long> fileIdList, Long userId) {
        // post信息，mysql存储到post_info和post_files；postDetail->mongodb;postTitle->es
        postImportService.importPost(title, content, publishTime, fileIdList, userId);
    }
}
