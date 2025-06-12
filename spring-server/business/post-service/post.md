# Controller
### PostController
帖子增删改查相关

* 帖子发布                              √
* 删除帖子
* 修改帖子(只改内容)
* 修改帖子(内容 + 文件(image,video))

* ids获取postList预览
* id获取post的内容 + 分页评论
* 获取一级评论
* 获取二级评论

### PostFileController
* 发布帖子(文件)
* 修改帖子(文件)
* 删除帖子(文件)

重构，改为可以上传视频，并且能够通过netty通知前端上传进度

# NettyHandler
* 收藏帖子
* 创建收藏夹
* 评论帖子
* 转发帖子
* 点赞帖子
* 不感兴趣
