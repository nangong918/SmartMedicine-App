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

目测Java全部系统构建需要到5月中旬；5月中旬之后停止Java代码开发。
开始Java业务场景分析 + Java架构设计。
以上完成之后则算完成Java系统，开始场景题分析 + Java架构设计。

##### user行为埋点
1. 对帖子的任何改动（增删改查）
2. 对帖子的任何搜索（文本处理）
3. 对帖子点击率，浏览时间
4. 问诊对话的实体命名识别

##### 完成基础post框架

1. 内容特征工程：post发布的时候AcTree自动机实体命名识别，识别Attribute
2. post存储Neo4j图数据库
3. 知识图谱快速搜索召回

##### 搜索引擎
Java调用Python服务：暂时使用Http，后期优化使用gRpc
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

### python_nlp

#### TODO：

1. 分析模型，查询资料，看Hugging Face，找是否存再最新的可替代方案。

### big-data
1. 构建Neo4j全部实体
2. 构建Neo4j的post和user实体
* 
1. python的NLJ实现，并且实现Java调用Python的NLJ
2. Neo4j的全部实体，医疗相关的全部实体 + post、user实体
3. 发布帖子的时候进行审核：用NLJ审核是否是自然语言。
4. 特征提取：对帖子进行AcTree特征提取 + Bert模型意图分类；（需要设计具体分为哪些类别，按照什么角度进行切分？是否能够实现）
5. 简单的前端demo进行测试


* 数据集收集:寒暄数据集\搜索/问诊意图数据集\推荐意图数据集\个人评价意图数据集\app功能查询数据集
* 特征工程 -> 用于推荐意图


* 感觉特征工程很多计算量都很大,要用大数据技术:离线,进线,在线

* TODO 先了解好特征工程的原理架构再进行设计，可能会用到大数据：离线，近线，在线计算的思想架构
* 特征工程算法层

* 安装好kafka
* 跑完kafka的spring-label教程
* kafka接入事件埋点
* 项目性能测试以及优化方案.

1. 写测试点,进行单点测试 + 稳定性测试
2. 完善推荐系统
3. 完善搜索系统
4. 完善分布式测试
   * 完善分布式的MQ
5. java-learning继续学习
   * 数据库:Mysql,Redis,ES(Mongodb,neo4j)
   * 异步调用消息队列:kafka,RabbitMq
   * 同步调用:Dubbo
   * 微服务\分布式
   * 线上部署:nacos,docker,k8s,阿尔萨斯
   * Java:多线程,网络
6. 集中本地测试
7. 完善上线:
   * 阿尔萨斯
   * Docker
   * K8s


todo 搭建Elk（Elasticsearch, Logstash, Kibana）
集成：Hadoop；HDFS


做个冷热的区分
想这种30天离线数据就可以放在hive 用hivesql直接做计算
3天的话可以放StarRocks / ClickHouse 配合flink
如果需要查询频次高的话加ES

离线层 (Hive)
近线层 (StarRocks / ClickHouse)
在线层 (Elasticsearch + Redis)


验收标准：
1. 正常发布帖子
2. 正常浏览帖子
3. 正常推荐
4. 行为特征上传

##### 在家梳理
梳理user-relationship-service；作流程图
参考《大麦》看看是否存在优化空间

#### 排期
##### 跑通阶段    (功能极简,跑通就行.后续不重构,本系统用于学习Java的优化方案)
* 数据导入到数据库：
  * 3天
  * 6月4~6 （调用代码，进行导入数据库测试）
  * 获取postList
  * 获取postDetail信息测试
  * 搜索post测试
* 重构user-service
  * 4天
  * 6月7~11
  * 梳理整套user注册登录逻辑
  * user 头像存储的两次http请求
  * 注册user;user信息填写入mysql,elasticsearch,neo4j
* 帖子发布测试
  * 2天
  * 6月12~13
  * 发布post,存入neo4j,mysql,elasticsearch,mongodb
  * 完成python功能:自然语言标签分析
  * 支持上传视频(断点续传)

* App消息长连接
  * 学习Mq -> 重构Mq (记录笔记,不能白开发,留下笔记)
    * 学习       2天 (16~17)
    * 重构Mq测试  1天 (18)
  * 重构netty,message,user服务的handler;logging服务的kafka-handler;netty的kafka-sender
    * 4天 (19~23): 启动App测试Netty,调整通过一个mq再修改其他的mq
      * 重构Spring + 构建测试App
      * Kafka:netty的post行为,埋点;
      * Kafka:搜索行为
      * Kafka:发布帖子行为
      * Kafka:帖子评论行为
* App聊天
  * 重构netty和app服务,将长连接改为userId而不是userAccount 1天(24)
  * 好友相关 3天 (21~23)
    * 列表视图 (21,22)
    * 添加,删除好友 (23)
    * 好友列表 (23)
  * 聊天相关 2天 (24~25)
    * 发送文本消息
    * 发送图片消息
  * 学习MySQL,MongoDB,ElasticSearch,Neo4j,Hive特性
  * 学习查询分页,学习数据库分库分表
  * 聊天记录相关
    * 临时聊天记录列表
    * 单个好友分页聊天记录
    * 关键词ElasticSearch查询聊天记录
    * 聊天记录存储的分库分表设计
* App获取帖子联调 3天(28~30)
  * url存储在redis重构
  * redis学习
* App行为上传I（浏览帖子 + 点赞等）
  * 3天
  * 6月16~6月18
* 在线层推荐
  * 2天
  * 6~8(3天)
* App搜索帖子 2天(31~1)
  * 3天
  * 6月21~24
* 收藏帖子 (简化:取消创建帖子收藏夹)
  * 2天
  * 2~5 (2天)
* 评论帖子 (优先)
  * 3天
  * 6月27~7月1
* 转发帖子
  * 1天
  * 7月2
* App行为上传II（搜索帖子，收藏帖子，转发帖子，评论帖子）
  * 3天
  * 7月3~7月5

todo 推荐系统已经推荐的post编辑进入已推荐过,召回阶段不再选取
todo 明天继续跑通IM和post
商品购物系统需要加入(后台配置秒杀活动)

##### 重构+细化
* spring学习
  * 大麦
* service绘图
  * netty-socket
  * oss
  * auth-sms增加aop的权限层
* service重新设计
  * 合并auth和sms
* 合并service
  * 合并部分分布式微服务，避免内存溢出。
  * JVM调参，避免内存爆炸
* 单元测试
  * java-learning技术可行性测试
  * java-learning测试代码迁移
  * 测试点文档测试
  * 接口链路调用测试
* 中间件
  * Mq
    * 学习并重新设计RabbitMq和Kafka
    * 重构netty-mq,user-mq,message-mq
    * 前端编写, 用前端测试netty
    * 1.学习mq,2.重构netty,3.编写前端

先自己梳理好逻辑,再进行测试,微服务的重启成本太高

##### 最终功能补充 (不要着急写新功能,在当前系统未重构优化完成之间,禁止开发新功能)
* 商品购物 (联合推荐系统 + 大麦的pay系统) [一般的公司都有支付系统,此功能至少实现]
* 用药提醒
* 医疗预测
* AI问诊
* 语音视频通话
* 群组+直播
* 后台管理

##### 购物模块
购物系统设计:
* 用户系统
  * 评价
  * 购物车
* 商品商户系统
  * 商品发布
  * 商品库存管理
* 推荐系统
  * 商品推荐
  * 搜索                 (采纳)
* 支付系统
  * 支付 (**秒杀支付**)   (采纳)
* 订单系统
  * 订单                 (采纳)
  * 物流实时更新
* IM系统
  * 售前售后              (采纳)

##### JMeter压测



##### 临时note
项目mq重构
1. 使用json
2. netty-socket尝试重构为微服务集群
3. 尝试使用netty或者虚拟线程来改造提高请求的速度 (netty的http封装就是webflux;暂时不添加)
4. 重构netty-socket;使其能分布式部署


先跑通再优化
优化点:
1. mysql,mongodb,elasticsearch分库分页分表,数据库连接池,用户聊天记录存储优化.
2. sql优化
3. 缓存结构优化: redis; jvm
4. 消息队列mq优化: kafka,rabbitmq,rocketmq
5. 网关结构优化: nginx; spring-cloud-gateway
6. 线程优化:线程,线程池,定时任务
7. 网络优化:netty(分布式netty集群); webflux
8. oss优化, url存储在redis中,并且redis.ttl < oss.ttl
9. 搜索,排序算法优化.
10. 微服务分布式优化:集群,均衡负载,分布式锁,分布式事务,链路,服务注册,服务发现,服务熔断,服务限流

##### 问题
尝试解决Redis RDB问题:
`1160:M 26 Jun 2025 11:18:11.079 * 1 changes in 3600 seconds. Saving...
1160:M 26 Jun 2025 11:18:11.123 * Background saving started by pid 10054
10054:C 26 Jun 2025 11:18:11.128 # Failed opening the RDB file dump.rdb (in server root dir /Redis) for saving: No error
1160:M 26 Jun 2025 11:18:11.253 # Background saving error`