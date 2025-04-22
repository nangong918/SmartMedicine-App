package com.czy.post.service.impl;

import com.czy.api.converter.domain.post.PostConverter;
import com.czy.api.domain.Do.post.post.PostDetailDo;
import com.czy.api.domain.Do.post.post.PostFilesDo;
import com.czy.api.domain.Do.post.post.PostInfoDo;
import com.czy.api.domain.ao.post.PostAo;
import com.czy.post.mapper.mongo.PostDetailMongoMapper;
import com.czy.post.mapper.mysql.PostFilesMapper;
import com.czy.post.mapper.mysql.PostInfoMapper;
import com.czy.post.service.PostStorageService;
import com.czy.post.service.PostTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 13225
 * @date 2025/4/18 21:23
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class PostStorageServiceImpl implements PostStorageService {

    private final PostTransactionService postTransactionService;
    private final PostInfoMapper postInfoMapper;
    private final PostDetailMongoMapper postDetailMongoMapper;
    private final PostConverter postConverter;
    private final PostFilesMapper postFilesMapper;
    @Override
    public void storePostContentToDatabase(PostAo postAo) {
        postTransactionService.storePostToDatabase(postAo);
    }

    @Override
    public void storePostInfoToDatabase(PostAo postAo) {
        PostInfoDo postDetailDo = postConverter.toInfoDo(postAo);
        postInfoMapper.insertPostInfoDo(postDetailDo);
    }

    @Override
    public void storePostFilesToDatabase(PostAo postAo) {
        List<PostFilesDo> postFilesDoList = postConverter.toPostFilesList(postAo);
        if (!CollectionUtils.isEmpty(postFilesDoList)){
            postFilesMapper.insertPostFilesDoList(postFilesDoList);
        }
    }

    @Override
    public void deletePostContentFromDatabase(Long id) {
        postTransactionService.deletePostContentById(id);
    }

    @Override
    public void deletePostInfoFromDatabase(Long id) {
        postInfoMapper.deletePostInfoDoById(id);
    }

    @Override
    public PostAo findPostAoById(Long postId) {
        PostInfoDo postInfoDo = postInfoMapper.getPostInfoDoById(postId);
        PostDetailDo postDetailDo = postDetailMongoMapper.findPostDetailById(postId);
        List<PostFilesDo> postFilesDoList = postFilesMapper.getPostFilesDoListByPostId(postId);
        return postConverter.doToAo(postDetailDo, postInfoDo, postFilesDoList);
    }

    @Override
    public List<PostAo> findPostAoByIds(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)){
            return new ArrayList<>();
        }
        List<PostInfoDo> postInfoDoList = postInfoMapper.getPostInfoDoListByIdList(idList);
        List<PostDetailDo> postDetailDoList = postDetailMongoMapper.findPostDetailsByIdList(idList);
        List<PostAo> postAoList = new ArrayList<>();
        for(int i = 0; i < idList.size(); i++){
            PostInfoDo postInfoDo = postInfoDoList.get(i);
            PostDetailDo postDetailDo = postDetailDoList.get(i);
            Long postId = postInfoDo.getId();
            List<PostFilesDo> postFilesDoList = postFilesMapper.getPostFilesDoListByPostId(postId);
            PostAo postAo = postConverter.doToAo(postDetailDo, postInfoDo, postFilesDoList);
            postAoList.add(postAo);
        }
        return postAoList;
    }

    @Override
    public void updatePostContentToDatabase(PostAo postAo) {
        postTransactionService.updatePostContentToDatabase(postAo);
    }

    @Override
    public void updatePostInfoToDatabase(PostAo postAo) {
        PostInfoDo postInfoDo = postConverter.toInfoDo(postAo);
        postInfoMapper.updatePostInfoDoById(postInfoDo);
    }

    @Override
    public void updatePostFilesToDatabase(PostAo postAo) {
        List<PostFilesDo> postFilesDoList = postConverter.toPostFilesList(postAo);
        if (!CollectionUtils.isEmpty(postFilesDoList)){
            postFilesMapper.updatePostFilesDoByPostDos(postFilesDoList);
        }
    }
}
