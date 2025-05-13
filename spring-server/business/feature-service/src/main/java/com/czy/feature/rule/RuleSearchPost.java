package com.czy.feature.rule;


import com.czy.api.constant.search.SearchLevel;
import com.czy.api.domain.ao.feature.PostExplicitEntityScoreAo;
import com.czy.api.domain.ao.feature.PostExplicitLabelScoreAo;
import com.czy.api.domain.ao.feature.PostExplicitPostScoreAo;
import com.czy.api.domain.ao.post.PostNerResult;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;


/**
 * 搜索帖子分数设置
 */


@Slf4j
@Component
public class RuleSearchPost {

    /**
     * 暂时一个简单的规则及，按照等级来给予分值
     * @param
     * @return
     */
    private static final Double[] SCORE_LEVEL = new Double[]{
            // 0级匹配
            0.10,
            // 1级匹配
            0.08,
            // 2级匹配
            0.06,
            // 3级匹配
            0.04,
            // 4级匹配
            0.02,
            // 5级匹配
            0.005
    };

    /**
     * 计算帖子搜索分值
     * @param levelsPostIdMap  搜索等级和帖子id的映射
     * @return  计算后的帖子搜索分值
     */
    public List<PostExplicitPostScoreAo> calculatePostScore(Map<Integer, List<Long>> levelsPostIdMap, Long timestamp) {
        return calculateScore(levelsPostIdMap, (postId, level) -> {
            PostExplicitPostScoreAo ao = new PostExplicitPostScoreAo();
            ao.setPostId(postId);
            ao.setTimestamp(timestamp);
            ao.setScore(SCORE_LEVEL[level]);
            return ao;
        });
    }

    /**
     * 计算帖子搜索实体分值
     * @param levelsPostEntityScoreMap  搜索等级和实体的映射
     * @return  计算后的帖子搜索实体分值
     */
    public List<PostExplicitEntityScoreAo> calculatePostEntityScore(Map<Integer, List<PostNerResult>> levelsPostEntityScoreMap, Long timestamp) {
        return calculateScore(levelsPostEntityScoreMap, (postNerResult, level) -> {
            PostExplicitEntityScoreAo ao = new PostExplicitEntityScoreAo();
            ao.setEntityName(postNerResult.getKeyWord());
            ao.setEntityLabel(postNerResult.getNerType());
            ao.setTimestamp(timestamp);
            ao.setScore(SCORE_LEVEL[level]);
            return ao;
        });
    }

    /**
     * 计算帖子搜索标签分值
     * @param levelsPostLabelScoreMap   搜索等级和标签的映射
     * @return                          计算后的帖子搜索标签分值
     */
    public List<PostExplicitLabelScoreAo> calculatePostLabelScore(Map<Integer, List<Integer>> levelsPostLabelScoreMap, Long timestamp) {
        return calculateScore(levelsPostLabelScoreMap, (labelType, level) -> {
            PostExplicitLabelScoreAo ao = new PostExplicitLabelScoreAo();
            ao.setLabel(labelType);
            ao.setTimestamp(timestamp);
            ao.setScore(SCORE_LEVEL[level]);
            return ao;
        });
    }

//    public List<PostSearchScoreAo> calculatePostScore(@NotNull Map<Integer, List<Long>> levelsPostIdList){
//        if (CollectionUtils.isEmpty(levelsPostIdList)){
//            return new ArrayList<>();
//        }
//
//        List<PostSearchScoreAo> list = new ArrayList<>();
//        for (Map.Entry<Integer, List<Long>> entry : levelsPostIdList.entrySet()){
//            Integer level = entry.getKey();
//            if (SearchLevel.MINUS_ONE.getCode().equals(level)){
//                continue;
//            }
//            else if (level < SearchLevel.FIVE.getCode() &&
//            level > SearchLevel.MINUS_ONE.getCode()){
//                for (Long postId : entry.getValue()){
//                    PostSearchScoreAo postSearchScoreAo = new PostSearchScoreAo();
//                    postSearchScoreAo.setPostId(postId);
//                    postSearchScoreAo.setScore(SCORE_LEVEL[level]);
//                    list.add(postSearchScoreAo);
//                }
//            }
//            else {
//                log.warn("搜索等级{}不支持", level);
//            }
//        }
//        return list;
//    }

//    public List<PostSearchEntityScoreAo> calculatePostEntityScore(@NotNull Map<Integer, List<PostNerResult>> levelsPostEntityScoreList){
//        if (CollectionUtils.isEmpty(levelsPostEntityScoreList)){
//            return new ArrayList<>();
//        }
//
//        List<PostSearchEntityScoreAo> list = new ArrayList<>();
//        for (Map.Entry<Integer, List<PostNerResult>> entry : levelsPostEntityScoreList.entrySet()){
//            Integer level = entry.getKey();
//            if (SearchLevel.MINUS_ONE.getCode().equals(level)){
//                continue;
//            }
//            else if (level < SearchLevel.FIVE.getCode() &&
//            level > SearchLevel.MINUS_ONE.getCode()){
//                for (PostNerResult postNerResult : entry.getValue()){
//                    PostSearchEntityScoreAo postSearchEntityScoreAo = new PostSearchEntityScoreAo();
//                    postSearchEntityScoreAo.setEntityName(postNerResult.getKeyWord());
//                    postSearchEntityScoreAo.setEntityLabel(postNerResult.getNerType());
//                    postSearchEntityScoreAo.setScore(SCORE_LEVEL[level]);
//                    list.add(postSearchEntityScoreAo);
//                }
//            }
//            else {
//                log.warn("搜索等级{}不支持", level);
//            }
//        }
//        return list;
//    }


    /**
     * 通用计算方法
     * @param levelsList 等级数据映射
     * @param processor 接收item和level的函数
     */
    private <T, R> List<R> calculateScore(
            @NotNull Map<Integer, List<T>> levelsList,
            BiFunction<T, Integer, R> processor) {
        if (CollectionUtils.isEmpty(levelsList)) {
            return Collections.emptyList();
        }

        List<R> resultList = new ArrayList<>();
        levelsList.forEach((level, items) -> {
            if (SearchLevel.MINUS_ONE.getCode().equals(level)) return;

            if (isValidLevel(level)) {
                items.forEach(item ->
                        resultList.add(processor.apply(item, level))
                );
            } else {
                log.warn("搜索等级{}不支持", level);
            }
        });
        return resultList;
    }

    private boolean isValidLevel(int level) {
        return level > SearchLevel.MINUS_ONE.getCode() &&
                level < SearchLevel.FIVE.getCode();
    }
}
