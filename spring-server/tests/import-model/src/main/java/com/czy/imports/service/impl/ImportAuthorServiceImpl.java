package com.czy.imports.service.impl;

import com.czy.api.api.post.PostImportService;
import com.czy.api.api.user.UserService;
import com.czy.api.constant.imports.ImportsConstant;
import com.czy.api.domain.Do.oss.OssFileDo;
import com.czy.imports.domain.Do.ArticleDo;
import com.czy.imports.domain.ao.AuthorAo;
import com.czy.imports.manager.CrawlerDataManager;
import com.czy.imports.mapper.OssMapper;
import com.czy.imports.service.ImportAuthorService;
import com.utils.mvc.service.MinIOService;
import domain.FileOptionResult;
import domain.SuccessFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
            ossFileDo.setId(successFile.getFileId());
            log.info("fileId:{}", successFile.getFileId());
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
        log.info("start createPost:{}", title);
        // post信息，mysql存储到post_info和post_files；postDetail->mongodb;postTitle->es
        postImportService.importPost(title, content, publishTime, fileIdList, userId);
    }

    private final CrawlerDataManager crawlerDataManager;

    @Override
    public void importAllData() {
        List<AuthorAo> users = crawlerDataManager.readCrawlerAuthorData();
        long startIndex = 0L;
        for (AuthorAo authorAo : users){
            String phone = String.valueOf(ImportsConstant.IMPORT_USER_PHONE_START + startIndex);
            importSingle(authorAo, phone);
            startIndex += 1L;
        }
    }

    private void importSingle(AuthorAo authorAo, String userPhone){
        Long authorImageId = null;
        // 1. 上传头像并获取id
        String authorImagePath = authorAo.getAuthorInfoAo().getUserImagePath();
        if (StringUtils.hasText(authorImagePath)){
            FileOptionResult result = uploadFiles(
                    authorImagePath,
                    ImportsConstant.AUTHOR_IMAGE_BUCKET_NAME
            );
            if (!result.getSuccessFiles().isEmpty()){
                // 默认取第0个
                authorImageId = result.getSuccessFiles().get(0).getFileId();
            }
        }
        // 2. 上传postFileList
        // 2.1 获取postFileList
        List<ArticleDo> articleDos = authorAo.getArticleDos();
        if (CollectionUtils.isEmpty(articleDos)){
            log.warn("author:{} has no article", authorAo.getUserAccount());
            return;
        }
        List<List<Long>> postFilesIds = new ArrayList<>(articleDos.size());
        for (ArticleDo articleDo : articleDos){
            String articleImagePath = articleDo.getArticleImagePath();
            if (StringUtils.hasText(articleImagePath)){
                FileOptionResult result = uploadFiles(
                        articleImagePath,
                        ImportsConstant.POST_IMAGE_BUCKET_NAME
                );
                if (!result.getSuccessFiles().isEmpty()){
                    List<Long> postFileIds = result.getSuccessFiles().stream()
                            .map(SuccessFile::getFileId)
                            .collect(Collectors.toList());
                    postFilesIds.add(postFileIds);
                }
                else {
                    postFilesIds.add(new ArrayList<>());
                }
            }
            else {
                postFilesIds.add(new ArrayList<>());
            }
        }

        // 3.创建user
        long userId = createUser(
                authorAo.getAuthorInfoAo().getUserName(),
                authorAo.getUserAccount(),
                String.valueOf(userPhone),
                authorImageId
        );

        // 4.创建post
        for (int i = 0; i < articleDos.size(); i++){
            ArticleDo articleDo = articleDos.get(i);
            createPost(
                    articleDo.getTitle(),
                    articleDo.getContent(),
                    getTimestamp(articleDo.getTime()),
                    postFilesIds.get(i),
                    userId
            );
        }
    }

    private static long getTimestamp(String time){
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = simpleDateFormat.parse(time);
            return date.getTime();
        }
        catch (Exception e){
            return 0L;
        }
    }
}
