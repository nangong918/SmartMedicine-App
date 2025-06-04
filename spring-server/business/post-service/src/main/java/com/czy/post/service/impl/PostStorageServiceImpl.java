package com.czy.post.service.impl;

import com.czy.api.api.user.UserService;
import com.czy.api.constant.post.DiseasesKnowledgeGraphEnum;
import com.czy.api.converter.domain.post.PostConverter;
import com.czy.api.domain.Do.neo4j.*;
import com.czy.api.domain.Do.neo4j.rels.UserPublishPostRelation;
import com.czy.api.domain.Do.post.post.PostDetailDo;
import com.czy.api.domain.Do.post.post.PostFilesDo;
import com.czy.api.domain.Do.post.post.PostInfoDo;
import com.czy.api.domain.ao.post.PostAo;
import com.czy.api.domain.ao.post.PostInfoAo;
import com.czy.api.domain.ao.post.PostNerResult;
import com.czy.api.mapper.PostRepository;
import com.czy.api.mapper.UserFeatureRepository;
import com.czy.api.mapper.rels.UserPublishPostRelationRepository;
import com.czy.post.mapper.mongo.PostDetailMongoMapper;
import com.czy.post.mapper.mysql.PostFilesMapper;
import com.czy.post.mapper.mysql.PostInfoMapper;
import com.czy.post.service.PostStorageService;
import com.czy.post.service.PostTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private final PostRepository postRepository;
    private final UserPublishPostRelationRepository userPublishPostRelationRepository;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private UserService userService;
    private final UserFeatureRepository userFeatureRepository;
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
    public void storePostFeatureToNeo4j(PostAo postAo, List<PostNerResult> featureList) {
        if (CollectionUtils.isEmpty(featureList)){
            return;
        }
        PostNeo4jDo postNeo4jDo = postConverter.toNeo4jDo(postAo);
        List<ChecksDo> checksDoList = new ArrayList<>();
        List<DepartmentsDo> departmentsDoList = new ArrayList<>();
        List<DiseaseDo> diseasesDoList = new ArrayList<>();
        List<DrugsDo> drugsDoList = new ArrayList<>();
        List<FoodsDo> foodsDoList = new ArrayList<>();
        List<ProducersDo> producerDoList = new ArrayList<>();
        List<RecipesDo> recipesDoList = new ArrayList<>();
        List<SymptomsDo> symptomsDoList = new ArrayList<>();

        for (PostNerResult postNerResult : featureList){
            if (postNerResult == null || postNerResult.isEmpty()){
                continue;
            }
            if (DiseasesKnowledgeGraphEnum.CHECKS.getName().equals(postNerResult.getNerType())){
                ChecksDo checksDo = new ChecksDo();
                checksDo.setName(postNerResult.getKeyWord());
                checksDoList.add(checksDo);
            }
            else if (DiseasesKnowledgeGraphEnum.DEPARTMENTS.getName().equals(postNerResult.getNerType())){
                DepartmentsDo departmentsDo = new DepartmentsDo();
                departmentsDo.setName(postNerResult.getKeyWord());
                departmentsDoList.add(departmentsDo);
            }
            else if (DiseasesKnowledgeGraphEnum.DISEASES.getName().equals(postNerResult.getNerType())){
                DiseaseDo diseaseDo = new DiseaseDo();
                diseaseDo.setName(postNerResult.getKeyWord());
                diseasesDoList.add(diseaseDo);
            }
            else if (DiseasesKnowledgeGraphEnum.DRUGS.getName().equals(postNerResult.getNerType())){
                DrugsDo drugsDo = new DrugsDo();
                drugsDo.setName(postNerResult.getKeyWord());
                drugsDoList.add(drugsDo);
            }
            else if (DiseasesKnowledgeGraphEnum.FOODS.getName().equals(postNerResult.getNerType())){
                FoodsDo foodsDo = new FoodsDo();
                foodsDo.setName(postNerResult.getKeyWord());
                foodsDoList.add(foodsDo);
            }
            else if (DiseasesKnowledgeGraphEnum.PRODUCERS.getName().equals(postNerResult.getNerType())){
                ProducersDo producersDo = new ProducersDo();
                producersDo.setName(postNerResult.getKeyWord());
                producerDoList.add(producersDo);
            }
            else if (DiseasesKnowledgeGraphEnum.RECIPES.getName().equals(postNerResult.getNerType())){
                RecipesDo recipesDo = new RecipesDo();
                recipesDo.setName(postNerResult.getKeyWord());
                recipesDoList.add(recipesDo);
            }
            else if (DiseasesKnowledgeGraphEnum.SYMPTOMS.getName().equals(postNerResult.getNerType())){
                SymptomsDo symptomsDo = new SymptomsDo();
                symptomsDo.setName(postNerResult.getKeyWord());
                symptomsDoList.add(symptomsDo);
            }
        }

        if (!checksDoList.isEmpty()){
            postTransactionService.createRelationPostWithChecks(postNeo4jDo, checksDoList);
        }
        if (!departmentsDoList.isEmpty()){
            postTransactionService.createRelationPostWithDepartments(postNeo4jDo, departmentsDoList);
        }
        if (!diseasesDoList.isEmpty()){
            postTransactionService.createRelationPostWithDiseases(postNeo4jDo, diseasesDoList);
        }
        if (!drugsDoList.isEmpty()){
            postTransactionService.createRelationPostWithDrugs(postNeo4jDo, drugsDoList);
        }
        if (!foodsDoList.isEmpty()){
            postTransactionService.createRelationPostWithFoods(postNeo4jDo, foodsDoList);
        }
        if (!producerDoList.isEmpty()){
            postTransactionService.createRelationPostWithProducers(postNeo4jDo, producerDoList);
        }
        if (!recipesDoList.isEmpty()){
            postTransactionService.createRelationPostWithRecipes(postNeo4jDo, recipesDoList);
        }
        if (!symptomsDoList.isEmpty()){
            postTransactionService.createRelationPostWithSymptoms(postNeo4jDo, symptomsDoList);
        }
    }

    @Override
    public void storePostAuthorRelationToNeo4j(PostAo postAo, Long userId){
        UserPublishPostRelation userPublishPostRelation = new UserPublishPostRelation();
        Optional<UserFeatureNeo4jDo> userResult = userFeatureRepository.findByUserId(userId);
        userResult.ifPresent(userPublishPostRelation::setUser);
        Optional<PostNeo4jDo> postResult = postRepository.findByPostId(postAo.getId());
        postResult.ifPresent(userPublishPostRelation::setPost);
        userPublishPostRelationRepository.save(userPublishPostRelation);
    }

    @Override
    public void updatePostFeatureToNeo4j(PostAo postAo, List<PostNerResult> featureList) {
        // 删除已有关系
        deletePostFeatureFromNeo4j(postAo.getId());
        // 添加新的关系
        storePostFeatureToNeo4j(postAo, featureList);
    }

    @Override
    public void deletePostFeatureFromNeo4j(Long postId) {
        postRepository.deletePostByIdWithRelations(postId);
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
        assert idList.size() == postInfoDoList.size() && postDetailDoList.size() == idList.size();
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
    public List<PostInfoAo> findPostInfoAoList(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)){
            return new ArrayList<>();
        }
        List<PostInfoAo> postInfoAoList = new ArrayList<>();
        List<PostInfoDo> postInfoDoList = postInfoMapper.getPostInfoDoListByIdList(idList);
        assert idList.size() == postInfoDoList.size();
        for (int i = 0; i < idList.size(); i++){
            PostInfoDo postInfoDo = postInfoDoList.get(i);
            PostInfoAo ao = postConverter.postInfoDoToAo(postInfoDo);
            Long postId = postInfoDo.getId();
            List<PostFilesDo> postFilesDoList = postFilesMapper.getPostFilesDoListByPostId(postId);
            // 存在可能帖子没图片的情况
            if (!CollectionUtils.isEmpty(postFilesDoList)){
                PostFilesDo postFilesDo = postFilesDoList.get(0);
                Long fileId = postFilesDo.getFileId();
                ao.setFileId(fileId);
            }
            postInfoAoList.add(ao);
        }
        return postInfoAoList;
    }

    @Override
    public void updatePostContentToDatabase(PostAo postAo) {
        postTransactionService.updatePostContentToDatabase(postAo);
    }

    @Override
    public void updatePostInfoToDatabase(PostAo postAo) {
        PostInfoDo postInfoDo = postConverter.toInfoDo(postAo);
        postInfoMapper.updatePostInfoDo(postInfoDo);
    }

    @Override
    public void updatePostFilesToDatabase(PostAo postAo) {
        List<PostFilesDo> postFilesDoList = postConverter.toPostFilesList(postAo);
        if (!CollectionUtils.isEmpty(postFilesDoList)){
            postFilesMapper.updatePostFilesDoByPostDos(postFilesDoList);
        }
    }

    @Override
    public Long findPostIdByAuthorIdAndTitle(Long authorId, String title) {
        return postInfoMapper.findPostIdByAuthorIdAndTitle(authorId, title);
    }
}
