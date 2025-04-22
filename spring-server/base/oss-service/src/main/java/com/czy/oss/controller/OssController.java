package com.czy.oss.controller;

import com.czy.api.api.oss.OssService;
import com.czy.api.constant.exception.OssException;
import com.czy.api.constant.oss.OssConstant;
import com.czy.api.domain.ao.oss.ErrorFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * @author czy
 * @date 2025/4/18
 * OSS文件上传下载
 * TODO：Oss上传下载对User进行鉴权
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(OssConstant.OSS_CONTROLLER)
public class OssController {

    // 需要符合Amazon S3 存储桶命名规则
    private final String globalOssBucket;
    private final OssService ossService;

    /**
     * 上传文件
     */
    @PostMapping("/upload")
    public String upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId) {
        if (ObjectUtils.isEmpty(userId)){
            log.warn("userId is null");
            return "userId is null";
        }
        List<MultipartFile> fileList = new ArrayList<>();
        fileList.add(file);
        List<ErrorFile> list = ossService.uploadFiles(fileList, userId, globalOssBucket);


        if (!ObjectUtils.isEmpty(list)){
            StringBuilder sb = new StringBuilder();
            sb.append("上传失败的文件：\n");
            log.warn("存在上传失败的文件");
            for (ErrorFile errorFile : list) {
                sb.append(errorFile.getFileName()).append(": ").append(errorFile.getErrorMessage()).append(";\n");
            }
            return sb.toString();
        }
        else {
            return "上传成功";
        }
    }

    @GetMapping("/downloadByStorageName")
    public void download(
            @RequestParam("fileStorageName") String fileStorageName,
            @RequestParam("userId") Long userId,
            HttpServletResponse response) {

        //            // 从 MinIO 获取对象
//            InputStream stream = minioClient.getObject(GetObjectArgs.builder()
//                    .bucket(globalOssBucket) // 存储桶名称
//                    .object(fileName) // 要下载的文件名
//                    .build());
        InputStream stream = ossService.downloadFileByStorageName(userId, fileStorageName, globalOssBucket);
        // 设置响应内容类型和头部
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; fileStorageName=\"" + fileStorageName + "\"");

        // 将 MinIO 文件流拷贝到 HTTP 响应
        try {
            IOUtils.copy(stream, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            log.error("MinIO 文件流拷贝到 HTTP 响应失败", e);
        }
    }

    // downloadByFileName
    @GetMapping("/downloadByFileName")
    public void downloadByFileName(
            @RequestParam("fileName") String fileName,
            @RequestParam("userId") Long userId,
            HttpServletResponse response) {
        try (InputStream stream = ossService.downloadFileByFileName(userId, fileName)) {
            // 设置响应内容类型和头部
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; fileName=\"" + fileName + "\"");

            IOUtils.copy(stream, response.getOutputStream());
            response.flushBuffer();
        } catch (OssException e){
            log.warn(e.getMessage());
        } catch (Exception e){
            log.warn("MinIO 文件流拷贝到 HTTP 响应失败", e);
        }
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/delete")
    public String delete(
            @RequestParam("fileName") String fileName,
            @RequestParam("userId") Long userId) {
        boolean result = ossService.deleteFileByFileName(userId, fileName, globalOssBucket);
        if (result){
            return "删除成功";
        }
        else {
            return "删除失败";
        }
    }

    // 通过文件fileName + userId获取文件url
    @GetMapping("/getFileUrlByFileName")
    public List<String> getFileUrlByFileName(
            @RequestParam("fileNames") List<String> fileNames,
            @RequestParam("userId") Long userId) {
        try {
            return ossService.getFileUrlsByUserIdAndFileNames(userId, fileNames);
        } catch (Exception e){
            log.warn("获取文件地址失败", e);
            return null;
        }
    }

}
