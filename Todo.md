## TODO

在执行的时候要看业务场景题目，更好的设计整体架构。
项目整体中心转向人工智能:搜索算法+NLP;推荐算法
其次是:大数据+Java;最次之为Android.
分布式存储,分布式计算

## 提醒

1. 学习推荐系统架构\学习Java场景题(由场景入八股)
2. 学习推荐系统的业务 -> 架构 + 大数据 + 算法

## 目前任务

### spring-server

2025年4月26日，Java系统基本完善。
不应开发新功能，接下来要做的应该是完善系统。

从新学习微服务：
1. 分布式多实例
2. 分布式缓存redis
3. 分布式Redis锁
4. 分布式消息队列 + Redis消息队列锁
5. 事务；分布式事务
6. 数据库锁（虽然分布式锁存在，但是多实例操控一张表的情况仍然存在）
7. 分布式Dubbo远程调用，异常处理
8. spring-cloud-gateway均衡负载；nginx均衡负载

从新学习架构
1. 消息队列 ALL
2. Dubbo ALL
3. MySQL ALL
4. Redis ALL
5. ElasticSearch ALL
6. Spring ALL
7. Spring Cloud ALL
8. Java ALL

#### TODO：

##### 完成基础post框架

1. 完成PostController功能:增删改查帖子
   * oss那边上传的代码 + mq消息 + netty消息
   * 测试
2. 完成帖子的点赞,评论,收藏,转发[全是netty]
3. 完成帖子的点赞数,评论数,收藏数,转发数获取[全是http]
4. 完成获取帖子的一级评论,获取帖子的二级评论[全是http]
5. 完成搜索引擎
6. 分布式实例测试，分布式锁测试，分布式消息队列测试，分布式dubbo测试，分布式事务测试

目测Java全部系统构建需要到5月中旬；5月中旬之后停止Java代码开发。
开始Java业务场景分析 + Java架构设计。
以上完成之后则算完成Java系统，开始场景题分析 + Java架构设计。

搜索引擎：
* 1.零级搜索（MySQL–like）:确定不需要分词的搜索用MySQL的like，比如搜索账号。
* 2.一级搜索（es单词）:绝对索引，讲语句分词用elastic search搜索关键词（新冠病毒–>新冠病毒治疗）
* 3.二级搜索（es句子多级）:输入是句子，将句子分词，让句子的词语分开去匹配数据（当一级搜索结束且用户前端下拉的情况才调用）（新冠病毒怎么治疗–>新冠病毒＇如何＇治疗）
* 4.三级搜索（类推荐）:输入是句子，将句子分词但是二级搜索也找不到，查询知识图谱，知识图谱查询到其实体最相近的物品，将物品返回到搜索结果（新冠病毒怎么治疗–>感冒怎么治疗）
* 5.四级搜索（nlp）:输入句子，Bert意图分类，分成不同的cql/sql匹配类，知识图谱匹配，返回规则集组合（有没有关于感冒医治的文章）

ElasticSearch：
1. DSL语句 + Spring-Data-ElasticSearch的封装的关键词匹配和功能是什么
2. 将专业性词典加入到ElasticSearch中
3. 搜索引擎最重要的就是匹配度 + 速度 + 排序
   * 只要是模糊搜索了就存在匹配度，匹配度需要规则集

Neo4j：
1. 查询关系
2. 查询Top-K相似实体：Jaccard

向量数据库：
提供一个向量，找到其相似的向量

##### 完成搜索框架

### python-recommend

推荐系统这块理论繁多，建议定时学习，不要浮躁：21点~23点 两小时

#### TODO：

1. 新闻推荐数据集 -> 跑出算法效果，给出可执行方案【可执行方案出来之后慢慢重构】
2. 看项目fun-rec-master
3. 看文档【推荐系统-常考面试题】
4. 看架构：抖音上搜小红书，抖音推荐架构，梳理
5. 问AI，大概推荐架构问AI，帮忙构建
6. 看算法【PaddleRec-master】
7. 写python-learning，模仿别人的算法 + kaggle数据集做实验
8. 看完视频，梳理可执行方案

### python-nlp

#### TODO：

1. 分析模型，查询资料，看Hugging Face，找是否存再最新的可替代方案。

### big-data

#### TODO：

1. 看完视频，梳理可执行方案

写一份python的requirements.txt

推荐系统中的user向量是来自于user-item矩阵，还是来自于user-attribute矩阵。
我现在是能明确通过nlp技术获取到item的attribute，并且也有item-attribute矩阵。
并且user-item矩阵总是及其稀疏的


user-attribute矩阵
user-item矩阵
item-attribute矩阵

user偏好数据收集使用user-item
然后后续生成user-attribute矩阵
推荐流程：
user-attribute矩阵的top-k 召回
然后通过user-item矩阵过滤
当结果小于20就进行attribute相似图谱推荐

我现在有个问题想要请教你，帮我设计搜索系统，
现在是这样的，我有一张及其完善的知识图谱，我能通过实体快速找到相似实体，也就是attribute，
但是这个实体不一定有对应的item，因为item是用户发布的或者我爬取的，
不知道怎么存储实体和item的关系，
我现在的需求是在neo4j搜索阶段就要知道这个实体是否有对应的item，没有就不返回这个实体。
因为都不存在对应的item，我也就没有必要进行相似度计算了。
因为我的相似度计算是这样的：
@Query("MATCH (d1:疾病 {name: $diseaseName})-[:has_symptom|recommand_drug|do_eat|not_eat|acompany_with]-(neighbor1) " +
"WITH d1, collect(id(neighbor1)) AS neighbors1 " +
"MATCH (d2:疾病)-[:has_symptom|recommand_drug|do_eat|not_eat|acompany_with]-(neighbor2) " +
"WHERE d2 <> d1 " +
"WITH d1, d2, neighbors1, collect(id(neighbor2)) AS neighbors2 " +
"WITH d1, d2, neighbors1, neighbors2, " +
"     [id IN neighbors1 WHERE id IN neighbors2] AS commonNeighbors " +
"WITH d1, d2, commonNeighbors, " +
"     size(neighbors1) + size(neighbors2) - size(commonNeighbors) AS allNeighborsCount " +
"RETURN d2.name AS diseaseName, " +
"       size(commonNeighbors) AS commonNeighborsCount, " +
"       allNeighborsCount, " +
"       CASE allNeighborsCount " +
"           WHEN 0 THEN 0.0 " +
"           ELSE size(commonNeighbors) * 1.0 / allNeighborsCount " +
"       END AS similarityScore " +
"ORDER BY similarityScore DESC " +
"LIMIT 10")
List<Map<String, Object>> findTopSimilarDiseasesByNeighbor(@Param("diseaseName") String diseaseName);
@Query("MATCH (d1:疾病 {name: $name})-[:has_symptom|acompany_with|has_common_drug|recommand_drug|do_eat|not_eat|need_check|cure_department]->(related1), " +
"(d2:疾病)-[:has_symptom|acompany_with|has_common_drug|recommand_drug|do_eat|not_eat|need_check|cure_department]->(related2) " +
"WHERE d2.name <> $name " +
"WITH d2, collect(id(related1)) AS ids1, collect(id(related2)) AS ids2 " +
"WITH d2, ids1, ids2, " +
"  [id IN ids1 WHERE id IN ids2] AS intersection " +
"RETURN d2.name AS diseaseName, " +
"       CASE size(ids1) + size(ids2) " +
"           WHEN 0 THEN 0.0 " +
"           ELSE size(intersection) * 1.0 / (size(ids1) + size(ids2)) " +
"       END AS jaccardIndex " +
"ORDER BY jaccardIndex DESC " +
"LIMIT 10")
List<Map<String, Object>> findTopSimilarDiseasesByJaccard(@Param("name") String name);
很明显相似度计算要消耗大量资源，
现在不能等计算完返回实体list再去item-attribute矩阵中找item是否存在
我应该怎么存储？
我现在是这样的，attitude存在知识图谱neo4j，
item内容存放在mongoDB，item和attitude的关系不知道存在哪里

因为我的item都是帖子嘛，特征也全都是帖子标题和内容提取出来的。
我现在在想，能不能将item直接存入知识图谱，存储属性有：id，title，和attitude的关系，内容不存储太大了肯定存在mongo。
这样的话不仅能快速找到item的特征，也能直接筛选特征是否有存在关系的item

* 数据业务很重要，存在数据缺失。
* 1.知识图谱并不代表整个post系统
* 2.需要记录知识图谱和post的关系表？
* 3.user向量构建
* 知识图谱，加入user，item
* 现在的需求：
* 1.需要找到一个实体是否和任何user/item相关