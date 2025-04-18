package com.czy.api.domain.ao.relationship;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/2/25 21:18
 */

@Data
public class AddUserAo implements Serializable {
    // 不可为空字符串
    public String applyAccount;
    // 不可为空字符串
    public String handlerAccount;
    public Long applyTime;
    // 可以null
    public String applyContent;
    public Integer source;
    // 申请状态 (0:未申请 1:申请中 2:已处理)
    public Integer applyStatus;
}
