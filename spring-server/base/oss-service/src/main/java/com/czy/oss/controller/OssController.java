package com.czy.oss.controller;

import com.czy.api.api.oss.OssService;
import com.czy.api.constant.oss.OssConstant;
import com.czy.api.domain.Do.oss.OssFileDo;
import com.czy.api.domain.ao.oss.FileResAo;
import com.czy.api.domain.dto.base.BaseResponse;
import com.czy.api.domain.dto.http.request.GetFilesUrlByIdRequest;
import com.czy.api.domain.dto.http.request.GetFilesUrlByNameRequest;
import com.czy.api.domain.dto.http.response.FileDownloadResponse;
import com.czy.api.exception.OssExceptions;
import com.utils.mvc.service.MinIOService;
import domain.ErrorFile;
import domain.FileOptionResult;
import exception.OssException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
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
    private final MinIOService minIOService;

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
        FileOptionResult result = ossService.uploadFiles(fileList, userId, globalOssBucket);

        if (!ObjectUtils.isEmpty(result.getErrorFiles())){
            StringBuilder sb = new StringBuilder();
            sb.append("上传失败的文件：\n");
            log.warn("存在上传失败的文件");
            for (ErrorFile errorFile : result.getErrorFiles()) {
                sb.append(errorFile.getFileName()).append(": ").append(errorFile.getErrorMessage()).append(";\n");
            }
            return sb.toString();
        }
        else {
            return "上传成功";
        }
    }

    // test
    @PostMapping("/uploadTest")
    public String uploadTest(
            @RequestParam("file") MultipartFile file) {
        List<MultipartFile> fileList = new ArrayList<>();
        fileList.add(file);
        FileOptionResult result = minIOService.uploadMultipartFiles(fileList, globalOssBucket);

        if (!ObjectUtils.isEmpty(result.getErrorFiles())){
            StringBuilder sb = new StringBuilder();
            sb.append("上传失败的文件：\n");
            log.warn("存在上传失败的文件");
            for (ErrorFile errorFile : result.getErrorFiles()) {
                sb.append(errorFile.getFileName()).append(": ").append(errorFile.getErrorMessage()).append(";\n");
            }
            return sb.toString();
        }
        else {
            return "上传成功";
        }
    }

    // test
    @PostMapping("/downloadTest")
    public BaseResponse<FileDownloadResponse>
    downloadTest(@RequestParam("fileStorageName") String fileStorageName) {
        String url = ossService.getFileUrlsByFileStorageName(fileStorageName, globalOssBucket);
        FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();
        FileResAo fileResAo = new FileResAo();
        fileResAo.setFileUrl(url);
        fileDownloadResponse.setFileResAo(fileResAo);
        return BaseResponse.getResponseEntitySuccess(fileDownloadResponse);
    }

    @GetMapping("/downloadByStorageName")
    public void downloadByName(
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

    @GetMapping("/downloadById")
    public void downloadById(
            @RequestParam("fileId") Long fileId,
            HttpServletResponse response) {
        if (fileId == null){
            return;
        }

        InputStream stream = ossService.downloadFileByFileId(fileId);

        // 设置响应内容类型和头部
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; fileId=\"" + fileId + "\"");

        // 将 MinIO 文件流拷贝到 HTTP 响应
        try {
            IOUtils.copy(stream, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            log.error("MinIO 文件流拷贝到 HTTP 响应失败", e);
        }
    }

    @GetMapping("/getFileUrlById")
    public BaseResponse<FileDownloadResponse> getFileUrlById(
            @RequestParam("fileId") Long fileId) {

        if (fileId == null){
            return BaseResponse.LogBackError(OssExceptions.FILE_NOT_EXIST);
        }

        OssFileDo ossFileDo = ossService.getFileInfoByFileId(fileId);
        if (ossFileDo == null || ossFileDo.getId() == null){
            return BaseResponse.LogBackError(OssExceptions.FILE_NOT_EXIST);
        }

        List<Long> fileIds = new ArrayList<>();
        fileIds.add(fileId);
        List<String> fileUrls = ossService.getFileUrlsByFileIds(fileIds);

        FileResAo fileResAo = new FileResAo();
        fileResAo.setFileId(fileId);
        fileResAo.setFileUrl(fileUrls.get(0));

        FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();
        fileDownloadResponse.setFileResAo(fileResAo);
        return BaseResponse.getResponseEntitySuccess(fileDownloadResponse);
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
    @GetMapping("/getFileUrlByFileNames")
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

    @PostMapping("/getFileUrlByFileIds")
    public List<String> getFileUrlByFileIds(
            @RequestBody GetFilesUrlByIdRequest request) {
        try {
            return ossService.getFileUrlsByFileIds(request.getFileIds());
        } catch (Exception e){
            log.warn("获取文件地址失败", e);
            return null;
        }
    }

    @PostMapping("/getFileUrlByFileNames")
    public List<String> getFileUrlByFileNames(
            @RequestBody GetFilesUrlByNameRequest request) {
        try {
            return ossService.getFileUrlsByBucketNameAndFileNames(
                    request.getBucketName(),
                    request.getFileNames()
            );
        } catch (Exception e){
            log.warn("获取文件地址失败", e);
            return null;
        }
    }

}
