# Controller
### ChatController
聊天记录相关功能
* 从redis拉取近期和全部用户的聊天记录
* 从数据库分页拉去用户之间的聊天记录
* 从elasticSearch获取某个关键词的全部聊天记录

### ChatFileController
聊天文件资源相功能
* 上传并发送文件消息

# NettyHandler
### ChatApi
* 发送文本消息                                √
* 发送图片消息
### ConnectApi
* 用户连接
* 用户断开连接
### ToServiceApi
* Ping心跳请求                               o
* 已读消息

# Netty的id从account改为user_id
# 数据查询: 分页查询
# 数据存储: 分库分表