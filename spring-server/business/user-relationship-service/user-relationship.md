# Controller
### LoginController
用户的注册，登录相关

* 检查phone是否注册
* 密码注册（+上传头像到oss）
* 密码登录
* 重置密码（登录之后检查jwt）
* 召回密码（登录之前检查phone + vcode）
* 修改userName       o
* 修改userAvatarUrl  o
* 发送短信
* 短信登录

### UserFileController
用户文件上传

* 上传用户头像
* 更新用户头像

### UserRelationshipController
用户关系

* 通过账号搜索
* 通过名字搜索
* 获取添加我的申请列表
* 获取我处理的我添加的申请列表
* 好友列表
* 获取未处理的好友申请数量

# NettyHandler
### FriendApi
* 添加好友的请求
* 删除好友的请求
* 处理添加好友的请求