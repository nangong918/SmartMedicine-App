package com.czy.api.domain.dto.http.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author 13225
 * @date 2025/4/21 17:26
 */
@Data
public class GetPostInfoListRequest {
    @NotEmpty(message = "帖子 IDs 不能为空")
    public List<Long> postIds;
}
