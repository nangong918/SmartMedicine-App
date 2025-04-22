package com.czy.oss.controller;


import com.czy.api.domain.dto.base.BaseResponse;
import com.czy.api.domain.dto.http.response.FileDownloadBytesResponse;
import com.czy.api.domain.dto.http.response.FileUploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
// https://cloud.tencent.com/developer/article/1594124

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileController {

    // 接入文件管理服务MinIO
    private final String uploadFilePath;
    private final ThreadPoolTaskExecutor ruleExecutor;

    //--------------------------------上传--------------------------------
    /**
     * 上传单个文件
     * @param file 传递的文件；MultipartFile类型
     * @return ResponseEntity<FileUploadRespond> 返回提示内容
     */
    @Deprecated
    @PostMapping("/uploadImage")
    public Mono<BaseResponse<FileUploadResponse>> fileUpload(
            @RequestPart("file") MultipartFile file,
            @RequestPart("name") String name,
            @RequestPart("timestamp") String timestamp) {
        // 文件服务使用消息队列将文件延迟定时保存到MinIO
        if (file == null){
            return Mono.just(BaseResponse.LogBackError("文件上传失败: 文件为空", log));
        }
        // 获取文件扩展名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename != null && originalFilename.contains(".") ?
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ""; // 获新的文件名字，添加扩展名
        String newFileName = name + "_" + timestamp + fileExtension;
        // 构建文件保存路径
        Path filePath = Paths.get(uploadFilePath, newFileName);
        // 创建文件保存目录
        File dest = new File(filePath.toString());

        try {
            if (!dest.getParentFile().exists()) {
                boolean makeResult = dest.getParentFile().mkdirs();
                if (makeResult) {
                    // 保存文件
                    file.transferTo(dest);
                    log.info("创建文件保存目录, Files.write写入成功:,路径:{}", filePath);
                }
            }
            else {
                ruleExecutor.submit(() -> {
                    try {
                        Files.write(filePath, file.getBytes());
                        log.info("Files.write写入成功:,路径:{}", filePath);
                        // 成功了交给Socket通知前端
                    } catch (IOException e) {
                        String warningMessage = "文件上传失败: 获取字节数组保存失败" + e.getMessage();
                        log.error(warningMessage);
                        // 失败了Mq交给Socket通知前端
                    }
                });
            }
            return Mono.just(BaseResponse.getResponseEntitySuccess(new FileUploadResponse("文件上传请求已处理")));
        } catch (IOException e) {
            String warningMessage = "文件上传失败: 获取字节数组保存失败" + e.getMessage();
            return Mono.just(BaseResponse.LogBackError(warningMessage, log));
        }
    }

    //--------------------------------下载--------------------------------


    /**
     * FileBytesRespond这个DTO对象进行传输
     * @param url 文件名
     * @return ResponseEntity<FileBytesRespond>的DTO类型 （其中byte[]字节数组也进行了Base64 encode编码处理，需要在Android端进行decode解码）
     */
    @Deprecated
    @GetMapping("/downloadImage")
    public Mono<BaseResponse<FileDownloadBytesResponse>>
    fileDownLoadFileBytesRespondByUrl(
            @RequestParam("url") String url) {
        try {
            // 构建文件下载路径
            Path filePath = Paths.get(uploadFilePath, url); // 使用 url 作为文件名
            File file = new File(filePath.toString());
            if (!file.exists()) {
                // 如果文件不存在,返回404 Not Found
                String warningMessage = "文件下载失败: 文件不存在";
                return Mono.just(BaseResponse.LogBackError(warningMessage, log));
            }
            // 此处存在问题，应该交给MinIO抛出一个url让前端异步下载，而不是在此处等待Http响应
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            FileDownloadBytesResponse response = new FileDownloadBytesResponse(fileBytes, url);
            return Mono.just(BaseResponse.getResponseEntitySuccess(response));
        } catch (IOException e) {
            String warningMessage = "下载失败：fileDownLoad：IO错误";
            return Mono.just(BaseResponse.LogBackError(warningMessage, log));
        } catch (Exception e) {
            String warningMessage = "下载失败：fileDownLoad：" + e.getMessage();
            return Mono.just(BaseResponse.LogBackError(warningMessage, log));
        }
    }


}
