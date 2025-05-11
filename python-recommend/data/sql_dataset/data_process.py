from typing import Callable,List,Tuple
from collections import defaultdict
import numpy as np
import os
import random


# -----------------------------------------负采样-----------------------------------------



def negative_sample(data: List[tuple],ratio = 1,threshold = 0):
    """
    采集负样本
    保证了每个用户都有正样本，但是不保证每个物品都有正样本，可能会减少用户数量和物品数量

    正样本：明确喜欢
    负样本：明确不喜欢
    非正样本：可能因为没发现，也可能因为不喜欢（ 总体 - 正样本 ）

    data[user_id, article_id, score]

    :param data:            原数据，至少有三列，第一列是用户id，第二列是物品id，第三列是权重
    :param ratio:           负正样本比例
    :param threshold:       权重阈值，权重大于或者等于此值为正样例，小于此值既不是正样例也不是负样例
    :return:                带上负样本的数据集
    """
    print("开始负采样")
    # 物品权重
    global negative_sample_score
    negative_sample_score = {d[1]: 1 for d in data}

    # 定义每个用户正样本与非正样本集合
    user_positive_set, user_unpositive_set = defaultdict(set), defaultdict(set)

    # 分别划入对应的集合
    for d in data:
        user_id, article_id, score  = d[0], d[1], d[2]
        (user_positive_set if score >= threshold else user_unpositive_set)[user_id].add(article_id)

    # 各个user的正item与负item
    user_list = list(user_positive_set.keys())
    item_for_user_positive_set = [user_positive_set[user_id] for user_id in user_list]
    item_for_user_unpositive_set = [user_unpositive_set[user_id] for user_id in user_list]

    # 仅为有正样例的用户采集负样例
    # 并发替代for循环
    from concurrent.futures import ProcessPoolExecutor
    with ProcessPoolExecutor(max_workers=os.cpu_count()//2, initializer=negative_sample_init, initargs=(ratio, negative_sample_score)) as executor:
        sampled_negative_article = executor.map(negative_sample_single_user, item_for_user_positive_set, item_for_user_unpositive_set, chunksize=100)

    # 构建新的数据集
    new_data = []
    for user_id, negative_article in zip(user_list, sampled_negative_article):
        new_data.extend([(user_id, article_id, 0) for article_id in negative_article])
    for user_id, positive_article in user_positive_set.items():
        new_data.extend([(user_id, article_id, 1) for article_id in positive_article])
    return new_data

# 初始化
def negative_sample_init(_ratio, _negative_sample_score):
    global item_set, ratio, negative_sample_score
    negative_sample_score = _negative_sample_score
    ratio = _ratio
    item_set = set(_negative_sample_score.keys())

def negative_sample_single_user(positive_set: set, unpositive_set: set):
    """
    对单个用户进行负采样

    :param positive_set:        单个用户正样本集合
    :param unpositive_set:      单个用户非正本集合
    :return:
    """
    # 可以取负样例的物品id列表
    valid_negative_list = list(item_set - positive_set - unpositive_set)
    # 采集负样例数量
    n_negative_sample = min(int(len(positive_set) * ratio), len(valid_negative_list))
    if n_negative_sample <= 0:
        return []

    # 权重存储
    score = np.array([negative_sample_score[article_id] for article_id in valid_negative_list], dtype=float)
    # 归一化
    score /= score.sum()  # 负样本采集权重
    # 概率采样
    sample_choice = np.random.choice(range(len(valid_negative_list)), n_negative_sample, False, score)
    return [valid_negative_list[i] for i in sample_choice]

# -----------------------------------------数据处理-----------------------------------------
def neaten_id(data: List[tuple])\
        -> Tuple[List[Tuple[int, int, int]], int, int, dict, dict]:
    """
    用于规整数据库中不规范的id
    对数据的用户id和物品id进行规整化，使其id变为从0开始到数量减1

    :param data: 原数据，有三列，第一列是用户id，第二列是物品id，第三列是标签
    :return: 新数据，用户数量，物品数量，用户id旧到新映射，物品id旧到新映射
    """
    new_data = []
    n_user, n_article = 0, 0
    user_id_new, item_id_new = {}, {}
    for user_id_old, item_id_old, label in data:
        if user_id_old not in user_id_new:
            user_id_new[user_id_old] = n_user
            n_user += 1
        if item_id_old not in item_id_new:
            item_id_new[item_id_old] = n_article
            n_article += 1
        new_data.append((user_id_new[user_id_old], item_id_new[item_id_old], label))
    return new_data, n_user, n_article, user_id_new, item_id_new


def split(data: List[tuple], test_ratio=0.4, shuffle=True) \
        -> Tuple[List[tuple], List[tuple]]:
    """
    将数据切分为训练集数据和测试集数据

    :param data: 原数据，第一列为用户id，第二列为物品id，第三列为标签
    :param test_ratio: 测试集数据占比，这个值在0和1之间
    :param shuffle: 是否对原数据随机排序
    :return: 训练集数据和测试集数据
    """

    if shuffle:
        random.shuffle(data)
    n_test = int(len(data) * test_ratio)
    test_data, train_data = data[:n_test], data[n_test:]

    return train_data, test_data


# -----------------------------------------topK-----------------------------------------

from tool.topK import TopK_data

def prepare_topK(train_data: List[Tuple[int, int, int]],
                 test_data: List[Tuple[int, int, int]],
                 n_user: int, n_item: int, n_sample_user = None):
    """
    准备用于topK评估的数据

    :param train_data: 训练集数据，有三列，分别是user_id, item_id, label
    :param test_data: 测试集数据，有三列，分别是user_id, item_id, label
    :param n_user: 用户数量
    :param n_item: 物品数量
    :param n_sample_user: 用户取样数量，为None则表示采样所有用户
    :return: 用于topK评估的数据，类型为TopKData，其包括在测试集里每个用户的（可推荐物品集合）与（有行为物品集合）
    """

    if n_sample_user is None or n_sample_user > n_user:
        n_sample_user = n_user

    # 随机取样
    user_set = np.random.choice(range(n_user), n_sample_user, False)

    # 获取用户的物品集合
    def get_user_item_set(data: List[Tuple[int, int, int]], only_positive=False):
        user_item_set = {user_id: set() for user_id in user_set}
        for user_id, item_id, label in data:
            if user_id in user_set and (not only_positive or label == 1):
                user_item_set[user_id].add(item_id)
        return user_item_set

    # 函数获取测试集中每个用户的可推荐物品集合，并将其与所有物品集合求差集，得到 test_user_item_set
    test_user_item_set = {user_id: set(range(n_item)) - item_set
                          for user_id, item_set in get_user_item_set(train_data).items()}
    # 获取测试集中每个用户的有行为物品集合
    test_user_positive_item_set = get_user_item_set(test_data, only_positive=True)
    return TopK_data(test_user_item_set, test_user_positive_item_set)


# -----------------------------------------打包-----------------------------------------



def pack(
        data_load_fun: Callable[[str], List[tuple]],
        negative_sample_ratio = 1,
        negative_sample_threshold = 0,
        negative_sample_method='random',
        split_test_ratio = 0.4,
        shuffle_before_split = True,
        split_ensure_positive = False,
        topK_sample_user = 300
):
    """
    读数据，负采样，训练集测试集切分，准备TopK评估数据

    :param data_load_fun:                       接受的数据生成函数
    :param negative_sample_ratio:               负正样本比例，为0代表不采样
    :param negative_sample_threshold:           负采样的权重阈值，权重大于或者等于此值为正样例，小于此值既不是正样例也不是负样例
    :param negative_sample_method:              负采样方法，值为'random'或'popular'
    :param split_test_ratio:                    切分时测试集占比，这个值在0和1之间
    :param shuffle_before_split:                切分前是否对数据集随机顺序
    :param split_ensure_positive:               切分时是否确保训练集每个用户都有正样例
    :param topK_sample_user:                    用来计算TopK指标时用户采样数量，为None则表示采样所有用户
    :return:    用户数量，物品数量，训练集，测试集，用于TopK评估数据
    """

    # 数据采样
    data = data_load_fun('No')
    # 负采样
    if negative_sample_ratio > 0:
        data = negative_sample(data, negative_sample_ratio, negative_sample_threshold, negative_sample_method)
    else:
        data = [(d[0], d[1], d[2]) for d in data]  # 丢掉时间戳
    data, n_user, n_item, _, _ = neaten_id(data)
    train_data, test_data = split(data, split_test_ratio, shuffle_before_split, split_ensure_positive)
    topK_data = prepare_topK(train_data, test_data, n_user, n_item, topK_sample_user)

    return n_user, n_item, train_data, test_data, topK_data

def test():

    # from data import data_loader
    # data = data_loader.read_ml100k()
    # data = [(d[0], d[1], d[2]) for d in data]
    # print(data)

    pass


if __name__ == '__main__':
    test()
    pass