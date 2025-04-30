package com.czy.api.domain.Do.user;

import com.czy.api.constant.es.FieldAnalyzer;
import io.swagger.v3.oas.annotations.media.Schema;
import json.BaseBean;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/1/2 13:48
 */
@Schema(description = "用户基本信息")
@Document(indexName = "user", // 索引名 必须是小写
        shards = 1, // 默认索引分区数
        replicas = 0, // 每个分区的备份数
        refreshInterval = "-1" // 刷新间隔
)
@Data
public class UserDo implements BaseBean, Serializable {
    private Long id;
    @Schema(description = "用户名称", type = "string", example = "张三")
    @Field(analyzer = FieldAnalyzer.IK_MAX_WORD, type = FieldType.Text)
    private String userName;
    @Schema(description = "用户登录账号", type = "string", example = "SweetLemon77")
    private String account;
    private String phone;
    // register_time
    @Schema(description = "注册时间", type = "long", example = "1643676800")
    private Long registerTime;
    // last_online_time
    @Schema(description = "最后登录时间", type = "long", example = "1643676800")
    private Long lastOnlineTime;
    @Schema(description = "头像FileId，需要转为url：https://www.baidu.com/img/bd_logo1.png", type = "string", example = "31341241")
    private Long avatarFileId;
}
