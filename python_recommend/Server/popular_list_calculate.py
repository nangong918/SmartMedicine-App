from mysql import initialize_database,execute_query





# 导出数据库所有用户的实体，level叠加起来，排序，取出前K个
def calculate_entity_topK_level(topK:int):
    # 构建 SQL 查询语句，导出实体ID和级别的二元组，并按实体ID分组
    sql = "SELECT entity_id, SUM(level) AS total_level " \
          "FROM user_entity " \
          "GROUP BY entity_id " \
          "ORDER BY total_level DESC " \
          "LIMIT %s"

    # 执行查询
    result = execute_query(sql, (topK,))

    # 提取实体ID和级别的二元组
    entity_levels = [(row[0], row[1]) for row in result]
    # 转化为十进制
    entity_levels = [(int(entity_id), int(level)) for entity_id, level in entity_levels]

    # 返回排名前topK的实体ID
    return entity_levels

# 搜索数据库中匹配的文章，score叠加起来，排序，取出前N个，返回
from typing import List,Tuple
def calculate_article_topN_score(topN: int, entityId_list: List[int]):
    # 第一步：根据entityId_list查询相关的article_id
    articleId_list = []
    for entityId in entityId_list:
        sql = "SELECT article_id FROM article_entity WHERE entity_id = %s"
        result = execute_query(sql, (entityId,))
        articleId_list.extend([row[0] for row in result])

    # 第二步：叠加score并取出前N个article_id和score的二元组列表
    article_scores = {}
    for articleId in articleId_list:
        sql = "SELECT score FROM user_article_evaluate WHERE article_id = %s"
        result = execute_query(sql, (articleId,))
        scores = [int(row[0]) for row in result]  # 将score转换为整数类型
        total_score = sum(scores)
        article_scores[articleId] = total_score

    # 根据score降序排序，并取出前topN个article_id和score的二元组列表
    sorted_articles = sorted(article_scores.items(), key=lambda x: x[1], reverse=True)
    topN_articles = sorted_articles[:topN]

    return topN_articles

def calculate_main(topK:int,topN: int)->List[Tuple[int,int]]:
    result = calculate_entity_topK_level(topK)

    entityId_list = [row[0] for row in result]
    article_list = calculate_article_topN_score(topN,entityId_list)

    return article_list

def clear_calculation_article(article_list:List[Tuple[int,int]]):
    article_list = [article_id for article_id,_ in article_list]
    return article_list

if __name__ == '__main__':
    topK = 30
    topN = 100

    # 检查数据库连接
    initialize_database()

    article_list = calculate_main(topK,topN)
    print(article_list)

    article_id_list = clear_calculation_article(article_list)
    print(article_id_list)





