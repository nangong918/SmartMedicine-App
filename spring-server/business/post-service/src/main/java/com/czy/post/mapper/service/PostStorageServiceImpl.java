package com.czy.post.mapper.service;

import com.czy.api.converter.domain.post.PostConverter;
import com.czy.api.domain.Do.post.post.PostDetailDo;
import com.czy.api.domain.ao.post.PostAo;
import com.czy.post.mapper.mongo.PostDetailMongoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author 13225
 * @date 2025/4/18 21:23
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class PostStorageServiceImpl implements PostStorageService {

    private final PostTransactionService postTransactionService;
    private final PostDetailMongoMapper postDetailMongoMapper;
    private final PostConverter postConverter;
    @Override
    public void storePostContentToDatabase(PostAo postAo, Long id) {
        postTransactionService.storePostToDatabase(postAo, id);
    }

    @Override
    public void storePostInfoToDatabase(PostAo postAo, Long id) {
        PostDetailDo postDetailDo = postConverter.toDo(postAo, id);
        postDetailMongoMapper.savePostDetail(postDetailDo);
    }
}
