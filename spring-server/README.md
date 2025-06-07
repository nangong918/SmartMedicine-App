**SmartMedicine Spring端**
=============

[返回主README](../README.md)

# 项目介绍

![后端架构](../assets/后端架构.png)

## 内容介绍

## 代码结构介绍
* [api](api)：api层:项目的entity,converter,api(dubbo远程调用)
* [base](base)：基本层:对外无感知;包括:netty长连接;oss文件服务;search通用搜索服务
* [business](business)：业务层:全部的业务逻辑
* [gateways](gateways)：网关层:分发;拦截;限流;熔断;日志;功能类似nginx
* [utils](utils)：工具类:包括java-utils:java的通用工具;spring-utils:spring的通用工具;spring-webflux:reactive工具类;spring-webmvc:mvc工具类;


## 微服务拆分介绍
* [netty-socket](base/netty-socket)

recommend-service
  在线层
feature-service
  近线层
offline-recommend-service
  离线层

# 环境配置

# 技术栈
## 数据库
* MySQL
  * 业务逻辑数据库
* Redis
  * 缓存数据库
* MongoDB
  * 文本数据
* ElasticSearch
  * 搜索数据库

## 消息队列
* RocketMQ
  * 可靠消息队列
* Kafka
  * 快速消息队列

## 网络
* Nginx
  * 反向代理，均衡负载
* Netty
  * 高性能网络通信框架

## 分布式
* Zookeeper
* Dubbo
* Redis集群
* MQ集群
