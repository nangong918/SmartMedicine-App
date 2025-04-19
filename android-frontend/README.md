**SmartMedicine Android端**
=============

[返回](../README.md)

# 项目介绍

## 内容介绍

## 代码结构介绍
* [App](app)：App主入口
* [DataLib](data/DataLib)：网络请求、数据库、缓存
* [CustomViewLib](view/CustomViewLib)：自定义View
* [AppCoreLib](core/AppCoreLib)：App相关核心业务逻辑
* [BaseUtilsLib](core/BaseUtilsLib)：App通用工具

# 环境配置

# 技术栈
网络：Okhttp3，Retrofit2
数据库：Room，SQLite
C库兼容：JNI，NDK

# 设计模式
MVVM设计模式
* 拆分为Model-View-ViewModel。
  * Model：对应[DataLib](data/DataLib)。包含Repository和Model
    * Repository：App的网络请求数据逻辑；App的数据库逻辑；App的缓存逻辑
    * Model：领域模型：DO（DataBase Object）；BO（Business Object）；AO（Application Object）；VO（View Object）；DTO（Data Transfer Object）
  * View：对应[CustomViewLib](view/CustomViewLib)和[app](app)；CustomViewLib负责自定义view；app负责Activity和Fragment
  * ViewModel：对应[AppCore](core/AppCoreLib)和[BaseUtilsLib](core/BaseUtilsLib)；AppCore是App相关核心；BaseUtilsLib是任何App的通用工具。
