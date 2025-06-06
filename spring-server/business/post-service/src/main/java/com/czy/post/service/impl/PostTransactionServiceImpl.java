package com.czy.post.service.impl;

import com.czy.api.constant.exception.StorageException;
import com.czy.api.converter.domain.post.PostConverter;
import com.czy.api.domain.Do.neo4j.ChecksDo;
import com.czy.api.domain.Do.neo4j.DepartmentsDo;
import com.czy.api.domain.Do.neo4j.DiseaseDo;
import com.czy.api.domain.Do.neo4j.DrugsDo;
import com.czy.api.domain.Do.neo4j.FoodsDo;
import com.czy.api.domain.Do.neo4j.ProducersDo;
import com.czy.api.domain.Do.neo4j.RecipesDo;
import com.czy.api.domain.Do.neo4j.SymptomsDo;
import com.czy.api.domain.Do.post.post.PostDetailDo;
import com.czy.api.domain.Do.post.post.PostDetailEsDo;
import com.czy.api.domain.Do.neo4j.PostNeo4jDo;
import com.czy.api.domain.ao.post.PostAo;
import com.czy.api.mapper.ChecksRepository;
import com.czy.api.mapper.DepartmentsRepository;
import com.czy.api.mapper.DiseaseRepository;
import com.czy.api.mapper.DrugsRepository;
import com.czy.api.mapper.FoodsRepository;
import com.czy.api.mapper.PostRepository;
import com.czy.api.mapper.ProducersRepository;
import com.czy.api.mapper.RecipesRepository;
import com.czy.api.mapper.SymptomsRepository;
import com.czy.post.mapper.es.PostDetailEsMapper;
import com.czy.post.mapper.mongo.PostDetailMongoMapper;
import com.czy.post.service.PostTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

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
    private final org.neo4j.ogm.session.Session neo4jSession;

    @Transactional(rollbackFor = StorageException.class)
    @Override
    public void storePostToDatabase(PostAo postAo) {
        PostDetailEsDo postDetailEsDo = postConverter.toEsDo(postAo);
        postDetailEsMapper.save(postDetailEsDo);

        PostDetailDo postDetailDo = postConverter.toMongoDo(postAo);
        postDetailMongoMapper.savePostDetail(postDetailDo);
    }

    @Transactional(rollbackFor = StorageException.class)
    @Override
    public void deletePostContentById(Long id) {
        postDetailEsMapper.deleteById(id);
        postDetailMongoMapper.deletePostDetailById(id);
    }

    @Transactional(rollbackFor = StorageException.class)
    @Override
    public void updatePostContentToDatabase(PostAo postAo) {
        PostDetailEsDo postDetailEsDo = postConverter.toEsDo(postAo);
        postDetailEsMapper.save(postDetailEsDo);
        PostDetailDo postDetailDo = postConverter.toMongoDo(postAo);
        postDetailMongoMapper.savePostDetail(postDetailDo);
    }

    private final ChecksRepository checksRepository;
    private final DepartmentsRepository departmentsRepository;
    private final DiseaseRepository diseaseRepository;
    private final DrugsRepository drugsRepository;
    private final FoodsRepository foodsRepository;
    private final PostRepository postRepository;
    private final ProducersRepository producersRepository;
    private final RecipesRepository recipesRepository;
    private final SymptomsRepository symptomsRepository;

    @Override
    public void createRelationPostWithDiseases(PostNeo4jDo post, List<DiseaseDo> dos) {
        // 保存与疾病的关系
        for (DiseaseDo disease : dos) {
            // 确保疾病实体已存在于数据库中
            DiseaseDo existingDisease = diseaseRepository.findByName(disease.getName());
            if (existingDisease != null) {
                log.info("neo4j查找结果：DiseaseDo：{}", existingDisease.toJsonString());
                log.info("开始创建关系：{}, post_name: {}, disease_name: {}",
                        PostRepository.RELS_POST_DISEASES,
                        post.getName(),
                        disease.getName());
                String cql = postRepository.buildDynamicRelationshipCql(
                        post.getName(), DiseaseDo.nodeLabel,
                        disease.getName(), PostRepository.RELS_POST_DISEASES);
                log.info("Diseases 关系创建 cql: {}", cql);
                neo4jSession.query(cql, new HashMap<>());
            }
            else {
                log.warn("neo4j未查找结果：DiseaseDo：{}", disease.toJsonString());
            }
        }
    }

    @Override
    public void createRelationPostWithChecks(PostNeo4jDo post, List<ChecksDo> dos) {
        for (ChecksDo checks : dos) {
            ChecksDo existingChecks = checksRepository.findByName(checks.getName());
            if (existingChecks != null) {
                String cql = postRepository.buildDynamicRelationshipCql(
                        post.getName(), ChecksDo.nodeLabel,
                        checks.getName(), PostRepository.RELS_POST_CHECKS);
                neo4jSession.query(cql, new HashMap<>());
            }
        }
    }

    @Override
    public void createRelationPostWithDepartments(PostNeo4jDo post, List<DepartmentsDo> dos) {
        for (DepartmentsDo departments : dos) {
            DepartmentsDo existingDepartments = departmentsRepository.findByName(departments.getName());
            if (existingDepartments != null) {
                String cql = postRepository.buildDynamicRelationshipCql(
                        post.getName(), DepartmentsDo.nodeLabel,
                        departments.getName(), PostRepository.RELS_POST_DEPARTMENTS);
                neo4jSession.query(cql, new HashMap<>());
            }
        }
    }

    @Override
    public void createRelationPostWithDrugs(PostNeo4jDo post, List<DrugsDo> dos) {
        for (DrugsDo drugs : dos) {
            DrugsDo existingDrugs = drugsRepository.findByName(drugs.getName());
            if (existingDrugs != null) {
                String cql = postRepository.buildDynamicRelationshipCql(
                        post.getName(), DrugsDo.nodeLabel,
                        drugs.getName(), PostRepository.RELS_POST_DRUGS);
                neo4jSession.query(cql, new HashMap<>());
            }
        }
    }

    @Override
    public void createRelationPostWithFoods(PostNeo4jDo post, List<FoodsDo> dos) {
        for (FoodsDo foods : dos) {
            FoodsDo existingFoods = foodsRepository.findByName(foods.getName());
            if (existingFoods != null) {
                String cql = postRepository.buildDynamicRelationshipCql(
                        post.getName(), FoodsDo.nodeLabel,
                        foods.getName(), PostRepository.RELS_POST_FOODS);
                neo4jSession.query(cql, new HashMap<>());
            }
        }
    }

    @Override
    public void createRelationPostWithProducers(PostNeo4jDo post, List<ProducersDo> dos) {
        for (ProducersDo producers : dos) {
            ProducersDo existingProducers = producersRepository.findByName(producers.getName());
            if (existingProducers != null) {
                String cql = postRepository.buildDynamicRelationshipCql(
                        post.getName(), ProducersDo.nodeLabel,
                        producers.getName(), PostRepository.RELS_POST_PRODUCERS);
                neo4jSession.query(cql, new HashMap<>());
            }
        }
    }

    @Override
    public void createRelationPostWithRecipes(PostNeo4jDo post, List<RecipesDo> dos) {
        for (RecipesDo recipes : dos) {
            RecipesDo existingRecipes = recipesRepository.findByName(recipes.getName());
            if (existingRecipes != null) {
                String cql = postRepository.buildDynamicRelationshipCql(
                        post.getName(), RecipesDo.nodeLabel,
                        recipes.getName(), PostRepository.RELS_POST_RECIPES);
                neo4jSession.query(cql, new HashMap<>());
            }
        }
    }

    @Override
    public void createRelationPostWithSymptoms(PostNeo4jDo post, List<SymptomsDo> dos) {
        for (SymptomsDo symptoms : dos) {
            SymptomsDo existingSymptoms = symptomsRepository.findByName(symptoms.getName());
            if (existingSymptoms != null) {
                String cql = postRepository.buildDynamicRelationshipCql(
                        post.getName(), SymptomsDo.nodeLabel,
                        symptoms.getName(), PostRepository.RELS_POST_SYMPTOMS);
                neo4jSession.query(cql, new HashMap<>());
            }
        }
    }
}
