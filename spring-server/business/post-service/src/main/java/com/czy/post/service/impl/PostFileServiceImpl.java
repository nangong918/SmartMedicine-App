package com.czy.post.service.impl;

import com.czy.api.api.oss.OssService;
import com.czy.api.domain.ao.post.PostAo;
import com.czy.post.mapper.mysql.PostInfoMapper;
import com.czy.post.service.PostFileService;
import com.utils.mvc.service.MinIOService;
import domain.ErrorFile;
import domain.FileOptionResult;
import domain.SuccessFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 13225
 * @date 2025/4/26 17:46
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class PostFileServiceImpl implements PostFileService {

    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private OssService ossService;
    private final ApplicationContext applicationContext;
    @Override
    public FileOptionResult deleteFileByPostAo(PostAo postAo) {
        List<Long> fileIds = postAo.getFileIds();
        if (CollectionUtils.isEmpty(fileIds)){
            return new FileOptionResult();
        }
        List<ErrorFile> errorFileList = new ArrayList<>();
        List<SuccessFile> successFileList = new ArrayList<>();
        fileIds.forEach(fileId -> {
            boolean deleteFile = ossService.deleteFileByFileId(fileId);
            if (deleteFile){
                SuccessFile successFile = new SuccessFile();
                successFile.setFileId(fileId);
                successFileList.add(successFile);
            }else {
                ErrorFile errorFile = new ErrorFile();
                errorFile.setFileId(fileId);
                errorFileList.add(errorFile);
            }
        });
        return new FileOptionResult(errorFileList, successFileList);
    }
}
