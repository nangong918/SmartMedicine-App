package com.czy.api.domain.dto.http.response;


import com.czy.api.domain.dto.http.PostCommentDto;
import lombok.Data;

import java.util.List;


/**
 * @author 13225
 * @date 2025/4/21 17:25
 */
@Data
public class GetPostCommentsResponse {
    // 一般来说第一次默认是第1（0）页面 + 20条
    // 适用dto是因为ao的文件是id，在dto是url
    public List<PostCommentDto> postCommentDtosList;
}
