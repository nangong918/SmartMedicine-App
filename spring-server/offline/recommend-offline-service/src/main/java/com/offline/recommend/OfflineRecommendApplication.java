package com.offline.recommend;

import com.czy.springUtils.start.PortApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author 13225
 * @date 2025/1/10 18:25
 */
@SpringBootApplication(
        scanBasePackages = {
                "com.czy.springUtils",
                // 扫描api模块
                "com.czy.api",
                // 扫描本模块
                "com.offline.recommend",
//                // 扫描工具类 Webflux的异常处理
//                "com.utils.webflux.handler",
                // 扫描工具类springMvcUtils
                "com.utils.mvc"
        }
)
public class OfflineRecommendApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(OfflineRecommendApplication.class)
                .initializers(new PortApplicationContextInitializer())
                .run(args);
    }
}

/**
 召回:
    多路召回,一个不漏的召回,同时要滤掉已经评分的物品
    基于协同过滤召回:user-cf,item-cf,swing,mf
    基于向量召回:fm?deepFM?word2vec?item2vec?tf-idf?bert?neo4j图嵌入?
        Bert生成嵌入向量
        neo4j图嵌入
        tf-idf:文本特征
    基于图召回:Neo4j:共同邻居,图路径,图嵌入:向量
 粗排:
    截断:召回可能只是根据user,可能只是根据item,接下来要将数据按照user-item交叉特征截断(Swing\矩阵分解);统计截断率,过高过低都要调整召回策略.
    deepFM:结合用户特征和物品特征，进行初步的候选物品筛选
 精排:
    精排阶段通常关注正负样本的区分能力
    负采样
    deepFM:使用交叉特征进行二分欸:区分正负样本
    预测是否会点击
    构建二分类（是否点击）的CTR
 重排:
    精排层关注AUC,重排层关注NDCG
    NDCG 衡量的是推荐列表的排序质量，特别关注推荐结果中高排名物品的相关性。
    重排阶段关注用户实际的行为反馈，旨在优化用户体验，确保用户最可能感兴趣的物品排名靠前

 离线:
    使用用户特征做离线计算.(Neo4j,Redis)
    交叉特征:因子分解机(FM: Factorization Machine)

 定时任务

 暂时不用大数据；等需要的时候再加入Spark系列
 */
