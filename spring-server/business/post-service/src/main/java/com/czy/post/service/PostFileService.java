package com.czy.post.service;

import com.czy.api.domain.ao.post.PostAo;
import domain.FileOptionResult;

/**
 * @author 13225
 * @date 2025/4/26 17:38
 */
public interface PostFileService {

    FileOptionResult deleteFileByPostAo(PostAo postAo);

}
