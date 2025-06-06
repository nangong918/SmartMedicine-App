package com.czy.api.api.post;

import java.util.List;

/**
 * @author 13225
 * @date 2025/6/4 14:43
 */
public interface PostImportService {
    long importPost(String title, String content, Long publishTime, List<Long> fileIdList, Long userId);
}
