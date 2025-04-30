package com.czy.api.domain.ao.relationship;



import lombok.Data;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/3/3 18:06
 */
@Data
public class HandleAddedMeAo implements Serializable {
    public Integer handleType;
    public String applyAccount;
    public String handlerAccount;
    public Long handleTime;
    public String additionalContent;
}
