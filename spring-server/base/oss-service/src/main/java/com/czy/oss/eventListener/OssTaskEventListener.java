package com.czy.oss.eventListener;

import com.czy.api.api.oss.OssService;
import com.czy.api.constant.oss.OssTaskTypeEnum;
import com.czy.api.domain.Do.oss.OssFileDo;
import com.czy.api.domain.entity.event.OssTask;
import com.czy.api.domain.entity.event.event.OssTaskEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;


/**
 * @author 13225
 * @date 2025/4/2 14:57
 * 大数据收集监听者，事件驱动系统种一份事件能被多个监听者收到。
 * 此份监听者是用于监听用户的全部大数据数据，然后交给Kafka存入数据平台做统计用的监听者。
 */

@Slf4j
@RequiredArgsConstructor
@Component
public class OssTaskEventListener implements ApplicationListener<OssTaskEvent> {

    private final OssService ossService;

    @Override
    public void onApplicationEvent(@NotNull OssTaskEvent event) {
        if (event.getSource() == null || event.getSource().getOssTaskType() == OssTaskTypeEnum.NULL.getCode()){
            log.warn("OssTaskEvent事件为空");
            return;
        }
        OssTask ossTask = event.getSource();
        OssFileDo ossFileDo = null;
        // 组合类型
        int ossTaskOptionType = -1;
        if (!ObjectUtils.isEmpty(ossTask.getOssFileId())){
            // ossFileId查询
            ossTaskOptionType = 0;
            ossFileDo = ossService.getFileInfoByFileId(ossTask.getOssFileId());
        }
        else if (!ObjectUtils.isEmpty(ossTask.getUserId()) && StringUtils.hasText(ossTask.getFileStorageName()) && StringUtils.hasText(ossTask.getBucketName())){
            ossTaskOptionType = 1;
            ossFileDo = ossService.getFileInfoByUserIdAndFileStorageName(ossTask.getUserId(), ossTask.getFileStorageName(), ossTask.getBucketName());
        }
        else if (!ObjectUtils.isEmpty(ossTask.getUserId()) && StringUtils.hasText(ossTask.getFileName())){
            ossTaskOptionType = 2;
            ossFileDo = ossService.getFileInfoByUserIdAndFileName(ossTask.getUserId(), ossTask.getFileName(), ossTask.getBucketName());
        }
        if (ossTaskOptionType == -1){
            log.warn("OssTaskEvent事件执行参数为空，参数：{}", ossTask);
            return;
        }
        if (ossFileDo == null){
            log.warn("查询到的ossFileDo为空，ossTaskOptionType：{}，参数：{}", ossTaskOptionType, ossTask);
            return;
        }
        // 删除的情况
        if (OssTaskTypeEnum.DELETE.getCode() == event.getSource().getOssTaskType()){
            ossService.deleteFileByFileId(ossFileDo.getId());
        }
        // netty通知前端删除成功
    }
}
