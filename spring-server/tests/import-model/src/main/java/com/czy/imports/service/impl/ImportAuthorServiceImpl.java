package com.czy.imports.service.impl;

import com.czy.imports.service.ImportAuthorService;
import com.utils.mvc.service.MinIOService;
import domain.FileOptionResult;
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

    @Override
    public FileOptionResult uploadFiles(String filePath, String bucketName) {
        File file = new File(filePath);
        List<File> files = new ArrayList<>();
        files.add(file);
        return minIOService.uploadFiles(files, bucketName);
    }

    @Override
    public void createUser(String userName, String account, Integer phone, Long fileId) {

    }

    @Override
    public void createPost(String title, String content, String publishTime, String filePath) {

    }
}
