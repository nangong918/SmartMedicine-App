package com.czy.imports.service;

import domain.FileOptionResult;

import java.util.List;

/**
 * @author 13225
 * @date 2025/6/3 9:34
 */
public interface ImportAuthorService {

    // 1.上传file（头像 + 文章）到minIO的oss + mysql的oss_info
    FileOptionResult uploadFiles(String filePath, String bucketName);

    // 2.创建user信息存储到login_user
    void createUser(long userId, String userName, String account, String phone, Long fileId, boolean haveToCheck);

    // 3.创建post信息，mysql存储到post_info和post_files；postDetail->mongodb;postTitle->es
    void createPost(long postId, String title, String content, Long publishTime, List<Long> fileIdList, Long userId);

    // 4.导入评论

    /**
     * 批量导入：需要启动:
     * user-relationship-service, post-service, oss-service
     */
    void importAllData();

    // 删除全部导入数据
    void clearAllTestData();
}
