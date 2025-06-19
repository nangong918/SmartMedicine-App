# service

[logging.md](business/logging-service/logging.md)
[message.md](business/message-service/message.md)
[post.md](business/post-service/post.md)
[search.md](business/search-service/search.md)
[user-relationship.md](business/user-relationship-service/user-relationship.md)

# 优化方向
* Redis多级缓存
* MySQL优化
* 分库分表
* 搜索优化
* 网关:Nginx + Spring Cloud Gateway
* Mq设计模式,Mq分散设计
* 分布式化
* 集群化:Mq集群,MySQL集群,Redis集群
* 日志收集框架:ELK
* 服务监控:Spring Boot Admin
* 链路追踪:SkyWalking
* 网络请求优化:Netty NIO
* 事件驱动
* oss:分布存储,Redis缓存url
* 消息幂等性

# 问题
* 缓存: 缓存雪崩,缓存穿透,缓存不一致
* 消息: 消息幂等,消息丢失
* 高并发: 数据库卡顿,NIO,深分页
* AOP: 切面,方法事务