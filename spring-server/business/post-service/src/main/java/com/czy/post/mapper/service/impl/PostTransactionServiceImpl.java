package com.czy.post.mapper.service.impl;

import com.czy.api.constant.exception.StorageException;
import com.czy.api.converter.domain.post.PostConverter;
import com.czy.api.domain.Do.post.post.PostDetailDo;
import com.czy.api.domain.Do.post.post.PostDetailEsDo;
import com.czy.api.domain.ao.post.PostAo;
import com.czy.post.mapper.es.PostDetailEsMapper;
import com.czy.post.mapper.mongo.PostDetailMongoMapper;
import com.czy.post.mapper.service.PostTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 13225
 * @date 2025/4/18 21:10
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class PostTransactionServiceImpl implements PostTransactionService {

    private final PostDetailEsMapper postDetailEsMapper;
    private final PostDetailMongoMapper postDetailMongoMapper;
    private final PostConverter postConverter;

    @Transactional(rollbackFor = StorageException.class)
    @Override
    public void storePostToDatabase(PostAo postAo, Long id) {
        PostDetailEsDo postDetailEsDo = postConverter.toEsDo(postAo, id);
        postDetailEsMapper.save(postDetailEsDo);

        PostDetailDo postDetailDo = postConverter.toMongoDo(postDetailEsDo);
        postDetailMongoMapper.savePostDetail(postDetailDo);
    }

    @Transactional(rollbackFor = StorageException.class)
    @Override
    public void deletePostContentById(Long id) {
        postDetailEsMapper.deleteById(id);
        postDetailMongoMapper.deletePostDetailById(id);
    }
}
