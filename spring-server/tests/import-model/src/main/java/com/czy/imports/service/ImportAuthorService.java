package com.czy.imports.service;

import domain.FileOptionResult;

/**
 * @author 13225
 * @date 2025/6/3 9:34
 */
public interface ImportAuthorService {

    // 1.上传file（头像 + 文章）
    FileOptionResult uploadFiles(String filePath, String bucketName);

    // 2.创建user信息存储到login_user
    void createUser(String userName, String account, Integer phone, Long fileId);

    // 3.创建post信息，mysql存储到post_info和post_files；postDetail->mongodb;postTitle->es
    void createPost(String title, String content, String publishTime, String filePath);

    // 4.导入评论
}
