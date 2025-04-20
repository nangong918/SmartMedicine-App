'''
    1.负采样
        整理出数据集里面所有的article
        协同过滤（负采样）：计算出所有能够用来推荐的集合
    2.TopK
        将所有能推荐的集合交给TopK
        输入K的值，返回推荐的结果
'''

from data.sql_dataset.data_loader import read_sql_dataset
import tensorflow as tf
import numpy as np


def get_recommendable_articles(user_id: int):
    data = read_sql_dataset('Yes')

    # 构建用户正样本集合
    positive_set = set()
    for u_id, a_id, _ in data:
        if u_id == user_id:
            positive_set.add(a_id)

    # 构建全样本集合
    all_articles = set()
    for _, a_id, _ in data:
        all_articles.add(a_id)

    # 用全样本集合 - 正样本集合 得到负样本集合
    negative_set = all_articles - positive_set

    # 返回负样本集合
    return negative_set

def topK_recommend(k: int, negative_set: set, user_id: int):
    if len(negative_set) <= k:
        k = len(negative_set)
    print('推荐数量为:', k)

    h5_path = 'h5/neumf_model.h5'

    # 加载模型
    neumf_model = tf.keras.models.load_model(h5_path)

    # 将negative_set转化为list
    negative_list = list(negative_set)

    # 存储预测分数和对应的物品ID
    scores = []

    # 使用模型预测每个负样本的分数
    for item_id in negative_list:
        # 构建模型输入
        user_input = np.array([user_id])
        item_input = np.array([item_id])

        # 进行预测
        score = neumf_model.predict([user_input, item_input])[0][0]
        scores.append((item_id, score))

    # 按照预测分数从高到低排序
    scores.sort(key=lambda x: x[1], reverse=True)

    # 取出前K个推荐结果
    topK_recommendations = [item_id for item_id, _ in scores[:k]]

    return topK_recommendations


def topK_recommend_app(k: int, negative_set: set, user_id: int,neuMF):
    if len(negative_set) <= k:
        k = len(negative_set)
    print('推荐数量为:', k)

    # 将negative_set转化为list
    negative_list = list(negative_set)

    # 存储预测分数和对应的物品ID
    scores = []

    # 使用模型预测每个负样本的分数
    for item_id in negative_list:
        # 构建模型输入
        user_input = np.array([user_id])
        item_input = np.array([item_id])

        # 进行预测
        score = neuMF.predict([user_input, item_input])[0][0]
        scores.append((item_id, score))

    # 按照预测分数从高到低排序
    scores.sort(key=lambda x: x[1], reverse=True)

    # 取出前K个推荐结果
    topK_recommendations = [item_id for item_id, _ in scores[:k]]

    return topK_recommendations


def get_user_recommend_topK_article(user_id:int,topK:int):
    negative_set = get_recommendable_articles(user_id)
    topK_recommendations = topK_recommend(topK, negative_set, user_id)
    return topK_recommendations



def get_user_recommend_topK_article_app(user_id:int,topK:int,neuMF):
    negative_set = get_recommendable_articles(user_id)
    topK_recommendations = topK_recommend_app(topK, negative_set, user_id,neuMF)
    return topK_recommendations


def test():
    negative_set = get_recommendable_articles(1)
    # print(negative_set)
    # print(len(negative_set))

    topK_recommendations = topK_recommend(10,negative_set,1)
    print(topK_recommendations)

    pass

if __name__ == '__main__':
    test()















