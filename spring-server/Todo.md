#### TODO

在执行的时候要看业务场景题目，更好的设计整体架构。
项目整体中心转向人工智能:搜索算法+NLP;推荐算法
其次是:大数据+Java;最次之为Android.
分布式存储,分布式计算

##### 微服务拆分

###### 框架设计
1. 服务注册发现：Nacos服务发现（用spring-cloud-gateway替代掉了）
2. 均衡负载：
   * 外层：Nginx
   * 内层：Nacos的lb://serviceId
3. 跨服务调用：OpenFeign，gRPC（Dubbo），RabbitMq
    * 服务间同步调用：Dubbo
    * 服务间异步调用：RabbitMq
    * 跨网域调用：OpenFeign
4. 网关：spring-cloud-gateway
5. 事件驱动：
   * 服务间事件：RabbitMq
   * 服务内事件：Spring Event
6. 网络框架：Spring-webflux代替Spring-webMvc

###### 框架设计
拆分服务：
1. 网关层（gateway）
   * 职责：
     * 统一入口，负责请求路由、分布式分配、负载均衡、限流、鉴权[auth-service]
     * 负责对数据进行加解密[考虑中]
   * 技术选型：
     * Spring Cloud Gateway、Nginx，Nacos，Zookeeper
   * 功能
     * 路由转发：将请求转发到对应的微服务。
     * 限流：防止恶意请求或流量过大。
     * 对前端请求体解密，交给处理层
     * 对后端响应体加密，交给传输层
     * 对长连接的消息加解密
   * 关于拆分：可能要选择为依赖，因为消息队列降低速度，所以选择为依赖。使用地方为网关层（解密交给服务，返回进行加密）长连接层。
2. 权限（auth-service）
   * 职责： 
     * 用户认证、授权、令牌管理
   * 技术选型：
     * Spring Security + OAuth2、JWT
   * 功能
     * 用户登录、注册、注销
     * 生成和管理 JWT 令牌
     * 权限校验（如用户角色、权限），验证用户身份，确保请求合法。
3. 长连接服务（Netty Socket Service）
   * 职责：负责实时消息的推送和接收。
   * 技术选型：Netty
   * 功能：
     * 维护用户的长连接
     * 实时推送消息（如聊天消息、通知）
     * 处理心跳检测、断线重连
4. OSS 文件服务（File Service）
   * 职责：
     * 负责文件的上传、下载和管理。
   * 技术选型：MinIO、阿里云 OSS、AWS S3
5. 消息服务（Message Service）
   * 职责：
     * 负责消息的存储、查询和推送。
   * 技术选型：
     * MongoDB（热数据）、HBase（冷数据）、RabbitMQ（消息队列）。
   * 功能：
     * 消息存储：将聊天消息存储到数据库。
     * 消息查询：根据条件查询历史消息。
     * 消息推送：将消息推送到长连接服务。
6. 短信服务（Sms Service）
   * 职责：
     * 负责短信功能，短信接口限制。
   * 技术选型
     * Redis
   * 功能：
     * 1.短信发送
     * 2.短信验证
7. 用户服务（User Service）
   * 职责：
     * 负责用户信息的管理。
   * 技术选型：
     * MySQL、Redis（缓存）。
   * 功能：
      * 用户注册、登录的信息更新（管理用户相关数据而非相关逻辑）。
      * 用户关系管理（如好友列表、黑名单）。
      * 用户状态管理（如在线状态）。
8. 关系服务（RelationShip Service）
   * 职责：
     * 负责用户/群组的关系和会话
   * 技术选型：
     * MySQL、Redis（缓存）、AOP（权限验证 auth-service）
   * 功能：
     * 用户关系管理
     * 用户会话管理
     * 群组关系管理
     * 群组会话管理
     * 添加，删除好友
     * 创建，删除群聊
9. 搜索服务（Search Service）
   * 职责：
     * 负责搜索用户的、群组的、聊天消息等。
     * 存储搜索索引。
   * 技术选型：
     * Elasticsearch。
   * 功能：
     * 全文搜索：根据关键词搜索聊天记录。
     * 搜索结果排序和分页。
10. 单点视频通话服务（p2p-service）
    * 职责：
      * 负责单点视频通话功能。
    * 技术选型：
      * WebRTC、Netty
    * 功能：
      * 单点视频通话功能。
11. 直播服务（live-service）
    * 职责：
      * 负责直播功能。
    * 技术选型：
      * RTMP + FFmpeg。
    * 功能：
      * 直播推流、拉流量
      * 弹幕
12. 朋友圈服务（post-service）
    * 职责：
      * 负责朋友圈的帖子的发布、删除、评论、点赞。
    * 技术选型：
      * MySQL（所属关系）、Redis（缓存）、MongoDB（内容）、AOP（权限验证 auth-service）
    * 功能：
      * 发布、删除、评论、点赞
      * 朋友的朋友圈列表
13. 推荐服务（recommend-service）
    * 职责：
      * 负责推荐系统的功能
    * 技术选型：
      * Elasticsearch：关键词匹配
      * Numpy，Sklearn：推荐算法
      * TensorFlow，Pytorch：模型训练
      * Drool：规则引擎
      * OpenFeign：跨集群调用（Java服务调用Python服务）
14. 大数据日志服务（BigData-Log Service）
    * 职责：
      * 负责系统日志的收集和分析。
      * 负责消息服务的用户消息收集，用于分析。
      * 负责系统的监控和告警。
    * 技术选型：
      * ELK（Elasticsearch + Logstash + Kibana）、Fluentd，Kafka。
      * Prometheus、Grafana。
    * 功能：
      * 用户行为收集，用于规则引擎，推荐算法。
      * 日志收集：从各个微服务的Kafka收集日志。
      * 日志存储：将日志存储到 Elasticsearch。
      * 日志分析：通过 Kibana 进行可视化分析
      * 监控系统性能（如 CPU、内存、请求量）。
      * 设置告警规则（如请求超时、服务宕机）。

###### 测试
1. api √
2. netty
3. oss √
4. auth √
5. message
    * mapper √
    * service
    * handler
6. relationship √
7. sms √
8. user √
9. gateways
    * service √
    * network √
10. springMvcUtils

netty和message需要联合前端测试（netty环境只有Android前端能实现）

###### 补充任务
1. 服务放入Docker镜像
2. 服务发版到服务器
3. JMeter 压力测试
4. Swagger 文档
5. 大量数据读写：读写分离，分库分表，分页查询
6. 大量文件读写：文件Gzip压缩
7. Spring Security OAuth2认证
8. Kafka快速收集消息
9. AOP 鉴权
10. 数据库连接池
11. 定时任务
12. Dubbo调用异常响应
13. 服务容错 Sentinel
14. 事务Transaction；分布式事务；分布式锁Zookeeper，Redis


###### 关于search-service
处理：账号，消息，资源，帖子的搜索
1. 加好友：elastic-search搜索用户名称（这个名称包含字符串，也就是说涉及分词）
2. 个人聊天：elastic-search搜索聊天记录。elastic-search搜索oss服务中这个用户发过的文件资源
3. 主页搜索：elastic-search搜索帖子标题的分词
   * 其中主页搜索是模糊搜索，需要实现：
     1. 分词匹配：比如说搜索中国，他要能搜到中华人民共和国。搜深圳房价，它要能搜到深圳南山区房价。
     2. 搜索关键词A要能搜索到其大概意思相同的词的内容；词语相似度应该会用NLP技术，大概是Bert，因为GPT是文本生成。大致意思相同的方法可能会借鉴推荐系统的思路,离线训练一些关键词关系.
* 参考Github上的搜索引擎: ElasticSearch + NLP

###### 其他
1. Python爬虫爬取微博数据填充数据库
2. 数据清洗 -> 数据标记 -> 特征提取


##### 微服务部署
1. Spring微服务一键启动链式：Docker容器化
2. Spring项目部署到服务器

##### 继续完善功能
1. search-service：
   * 用elastic-search + 搜索算法实现搜索功能
   * 排序算法实现排序
   * TODO：elasticsearch学习 + 搜索引擎service的搭建 + 搜索算法utils + 排序算法utils
2. 补充
   1. auth-service：
      * Spring Security + OAuth2，JWT 认证
      * AOP鉴权（用于群管理权限验证）
   2. oss-service：
      * oss的service完成，实现可dubbo调用
   3. netty-socket + api-model
      * 优化mapstruct，禁止使用反射来找到属性
   4. message-service:
      * 优化聊天记录存储逻辑：mongoDB + MySQL
3. 群组功能
   * 群会话
   * 群创建（事务@Transaction），群权限（AOP [auth-service]）
4. 通话功能
   * 单点视频通话（p2p-service）：Netty/WebRTC
   * 群聊视频通话（live-service）：RTMP + FFmpeg
5. 朋友圈功能
   * 发布删除评论点赞
   * 朋友圈：根据user的朋友列表顺序排列，无任何推荐功能。
6. 社区功能
   * 发布删除评论点赞（功能同朋友圈功能）
   * 社区（recommend-service）：
     1. 规则集：根据用户行为进行推荐；例如根据用户的关注，点赞，浏览，搜索，评论等内容进行推荐
     2. 决策引擎Drools：根据规则集进行推荐
     3. 推荐算法：
        * 召回模型
           * 协同过滤：UserCF，ItemCF，Swing，矩阵分解：ALS，SVM
           * 向量：FM
             * item2vec：word2vec，item2vec
             * 双塔
             * 图
             * 序列
           * 树模型
           * NCF
        * 排序模型
          * 特征交叉
          * WideNDeep系列
          * 改进Deep侧
          * 改进Wide侧
          * 序列模型
          * 多任务学习

###### 存储策略
* 缓存存入Redis,内存,速度最快
* 文本存入mongo,非orm,不用分表,比mysql快
* 检索存入elasticsearch,倒排索引,文本索引最快.
* 文件存入minIO,oss能存储管理文件.
* 关于数值一致性:oss和mysql同步存储的时候抛异常手动撤回,不用事务.因为事务的执行会对数据库上锁.
* 删除的时候先执行mysql的删除,在执行oss的删除,不执行事务.
* 相同数据同步:mongo和mysql这种相同数据同步可以使用事务.

###### 建议
* 先跑通最简单的,再逐步增加功能.

当前建议顺序:
Todo：
Android前端随便搞搞得了，重心不在Android
1. 各个模块内部的搜索引擎：
   1. user-service：账号like搜索，名称分词搜索
   2. (先完成2)message-service：es搜索：a，b用户之间的聊天记录；a用户自己的全部相关聊天记录
   3. 搜索历史记录存储Redis
2. 消息记录迁移：mongoDB
   1. 构建完整的mongoDB增删改查
   2. 了解为什么消息记录要存储再mongo而不是MySQL
3. friends-service：
   1. 帖子的发布，增删改查 -> MySQL索引，mongoDB内容，ElasticSearch标题索引，MinIO存储资源
   2. 分页拉取朋友的帖子
   3. 帖子的点赞，搜藏，评论
4. search-service
    1. 分词功能
    2. NLP近似语义服务
5. social-service
    1. 热门帖子
    2. 个性推荐帖子
6. relationship-service：
    1. 创建群聊
    2. 群会话，群消息
    3. 群管理
7. p2p-service：
   1. WebRtc视频通话
8. live-service
   1. RTMP直播
**(Android相关放在最后)**

###### 目前任务

TODO ：
1. oss和mysql数据库数据需要一致，需要@Transactional
2. oss文件上传service实现,controller实现
3. oss文件上传下载测试
4. 重点项目:搜索NLP;推荐算法;转型升级
