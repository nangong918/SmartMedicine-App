package com.czy.imports.domain.Do;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author 13225
 * @date 2025/5/30 15:57
 */
@Data
public class ArticleDo {
    @JSONField(name = "Title")
    private String title;
    @JSONField(name = "Time")
    private String time;
    @JSONField(name = "Content")
    private String content;
    private String articleImagePath;
}
