
内容分发网络(CDN)
IM系统使用ProtoBuf或ProtoStuff + MapStruct；避免使用JSON去反射


### rabbitmq处理

删除全部队列和节点
打开: RabbitMQ Command Prompt (sbin dir)
```shell
# 首先停止 RabbitMQ 应用
rabbitmqctl stop_app
# 重置节点
rabbitmqctl reset
# 重新启动应用
rabbitmqctl start_app

# 查询结果
rabbitmqctl list_queues name messages
```
