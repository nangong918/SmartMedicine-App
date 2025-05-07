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
import com.czy.api.domain.Do.post.post.PostNeo4jDo;
import com.czy.api.domain.ao.post.PostAo;
import com.czy.post.mapper.es.PostDetailEsMapper;
import com.czy.post.mapper.mongo.PostDetailMongoMapper;
import com.czy.post.mapper.neo4j.ChecksRepository;
import com.czy.post.mapper.neo4j.DepartmentsRepository;
import com.czy.post.mapper.neo4j.DiseaseRepository;
import com.czy.post.mapper.neo4j.DrugsRepository;
import com.czy.post.mapper.neo4j.FoodsRepository;
import com.czy.post.mapper.neo4j.PostRepository;
import com.czy.post.mapper.neo4j.ProducersRepository;
import com.czy.post.mapper.neo4j.RecipesRepository;
import com.czy.post.mapper.neo4j.SymptomsRepository;
import com.czy.post.service.PostTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        // 保存 PostDo 实体
        PostNeo4jDo savedPost = postRepository.save(post);

        // 保存与疾病的关系
        for (DiseaseDo disease : dos) {
            // 确保疾病实体已存在于数据库中
            DiseaseDo existingDisease = diseaseRepository.findByName(disease.getName());
            if (existingDisease != null) {
                postRepository.createDynamicRelationship(
                        savedPost.getName(), "疾病",
                        disease.getName(), PostRepository.RELS_POST_DISEASES);
            }
        }
    }

    @Override
    public void createRelationPostWithChecks(PostNeo4jDo post, List<ChecksDo> dos) {
        PostNeo4jDo savedPost = postRepository.save(post);
        for (ChecksDo checks : dos) {
            ChecksDo existingChecks = checksRepository.findByName(checks.getName());
            if (existingChecks != null) {
                postRepository.createDynamicRelationship(
                        savedPost.getName(), "检查",
                        checks.getName(), PostRepository.RELS_POST_CHECKS);
            }
        }
    }

    @Override
    public void createRelationPostWithDepartments(PostNeo4jDo post, List<DepartmentsDo> dos) {
        PostNeo4jDo savedPost = postRepository.save(post);
        for (DepartmentsDo departments : dos) {
            DepartmentsDo existingDepartments = departmentsRepository.findByName(departments.getName());
            if (existingDepartments != null) {
                postRepository.createDynamicRelationship(
                        savedPost.getName(), "科室",
                        departments.getName(), PostRepository.RELS_POST_DEPARTMENTS);
            }
        }
    }

    @Override
    public void createRelationPostWithDrugs(PostNeo4jDo post, List<DrugsDo> dos) {
        PostNeo4jDo savedPost = postRepository.save(post);
        for (DrugsDo drugs : dos) {
            DrugsDo existingDrugs = drugsRepository.findByName(drugs.getName());
            if (existingDrugs != null) {
                postRepository.createDynamicRelationship(
                        savedPost.getName(), "药品",
                        drugs.getName(), PostRepository.RELS_POST_DRUGS);
            }
        }
    }

    @Override
    public void createRelationPostWithFoods(PostNeo4jDo post, List<FoodsDo> dos) {
        PostNeo4jDo savedPost = postRepository.save(post);
        for (FoodsDo foods : dos) {
            FoodsDo existingFoods = foodsRepository.findByName(foods.getName());
            if (existingFoods != null) {
                postRepository.createDynamicRelationship(
                        savedPost.getName(), "食物",
                        foods.getName(), PostRepository.RELS_POST_FOODS);
            }
        }
    }

    @Override
    public void createRelationPostWithProducers(PostNeo4jDo post, List<ProducersDo> dos) {
        PostNeo4jDo savedPost = postRepository.save(post);
        for (ProducersDo producers : dos) {
            ProducersDo existingProducers = producersRepository.findByName(producers.getName());
            if (existingProducers != null) {
                postRepository.createDynamicRelationship(
                        savedPost.getName(), "药企",
                        producers.getName(), PostRepository.RELS_POST_PRODUCERS);
            }
        }
    }

    @Override
    public void createRelationPostWithRecipes(PostNeo4jDo post, List<RecipesDo> dos) {
        PostNeo4jDo savedPost = postRepository.save(post);
        for (RecipesDo recipes : dos) {
            RecipesDo existingRecipes = recipesRepository.findByName(recipes.getName());
            if (existingRecipes != null) {
                postRepository.createDynamicRelationship(
                        savedPost.getName(), "菜谱",
                        recipes.getName(), PostRepository.RELS_POST_RECIPES);
            }
        }
    }

    @Override
    public void createRelationPostWithSymptoms(PostNeo4jDo post, List<SymptomsDo> dos) {
        PostNeo4jDo savedPost = postRepository.save(post);
        for (SymptomsDo symptoms : dos) {
            SymptomsDo existingSymptoms = symptomsRepository.findByName(symptoms.getName());
            if (existingSymptoms != null) {
                postRepository.createDynamicRelationship(
                        savedPost.getName(), "症状",
                        symptoms.getName(), PostRepository.RELS_POST_SYMPTOMS);
            }
        }
    }
}
