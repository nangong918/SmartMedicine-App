package com.czy.feature.service.impl;

import com.czy.api.constant.feature.FeatureTypeChanger;
import com.czy.api.domain.Do.neo4j.DiseaseDo;
import com.czy.api.domain.Do.neo4j.PostNeo4jDo;
import com.czy.api.domain.Do.neo4j.UserFeatureNeo4jDo;
import com.czy.api.domain.Do.neo4j.base.BaseNeo4jDo;
import com.czy.api.mapper.*;
import com.czy.feature.service.FeatureStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 13225
 * @date 2025/5/7 16:42
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class FeatureStorageServiceImpl implements FeatureStorageService {

    // post的更新是删除全部更新特征，但是user并不是，user是在原有的基础上添加
    private final UserFeatureRepository userFeatureRepository;
//    private final ChecksRepository checksRepository;
//    private final DepartmentsRepository departmentsRepository;
//    private final DiseaseRepository diseaseRepository;
//    private final DrugsRepository drugsRepository;
//    private final FoodsRepository foodsRepository;
//    private final PostRepository postRepository;
//    private final ProducersRepository producersRepository;
//    private final RecipesRepository recipesRepository;
//    private final SymptomsRepository symptomsRepository;

    private void saveUserRelation(UserFeatureNeo4jDo user) {
        UserFeatureNeo4jDo findUserDo = userFeatureRepository.findByName(user.getName());
        if (findUserDo == null){
            userFeatureRepository.save(user);
        }
    }

    @Override
    public void createRelationsUserWithEntities(UserFeatureNeo4jDo user, List<? extends BaseNeo4jDo> entities) {
        saveUserRelation(user);
        for (BaseNeo4jDo entity : entities) {
            try {
                String relationType = FeatureTypeChanger.nodeLabelToRelation(entity.getNodeLabel());

                // 存储关系
                userFeatureRepository.createUserEntityPostRelation(
                        user.getId(), entity.getNodeLabel(),
                        entity.getName(), relationType
                );
            } catch (Exception ignored) {
                // 因为存储的时候实体可能不存在，但是业务逻辑也不能在此创建，所以就直接continue
                continue;
            }
        }
    }


    @Override
    public void createRelationUserWithDiseases(UserFeatureNeo4jDo user, List<DiseaseDo> dos) {
        // 存储关系 (单独写因为DiseaseDo不是常规实体，可能存在拓展功能)
        createRelationsUserWithEntities(user, dos);
    }


    @Override
    public void createRelationUserWithPosts(UserFeatureNeo4jDo user, List<PostNeo4jDo> dos) {
        // 存储关系 (单独写因为PostNeo4jDo不是常规实体，可能存在拓展功能)
        createRelationsUserWithEntities(user, dos);
    }

    @Override
    public void deleteUserRelation(UserFeatureNeo4jDo user) {
        userFeatureRepository.deletePostWithRelations(user.getName());
    }
}
