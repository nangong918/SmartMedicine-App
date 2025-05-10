from dataclasses import dataclass
from typing import Tuple,List


k_list = [10, 20, 50, 100]

@dataclass
class TopK_data:
    # 选择集合
    select_set : dict
    # 行为结合
    behavior_set : dict

@dataclass
class TopK_evaluate:
    # 命中数量
    hit_num : int = 0
    # 推荐数量
    recommend_num : int = 0
    # 行为数量
    behavior_num : int = 0


def topK_evaluate(topK_data: TopK_data, score_fn, K_list=None) \
        -> Tuple[List[float], List[float]]:
    if K_list is None:
        K_list = k_list
    # 评价字典
    kv_dict = {k: TopK_evaluate() for k in K_list}

    for user_id,item_set in topK_data.select_set.items():
        # 创建符合model输入的字典    :list[user_id,item_id]
        ui_dict = {'user_id': [user_id] * len(item_set),'item_id':list(item_set)}
        item_score_list = list(zip(item_set,score_fn(ui_dict)))
        # 排序：以x的第二排序，放到第一个元素
        sorted_item_list = [x[0] for x in sorted(item_score_list,key=lambda x:x[1],reverse=True)]

        user_behavior_set = topK_data.behavior_set[user_id]
        for k in K_list:
            topK_set = set(sorted_item_list[:k])
            kv_dict[k].hit_num += len(topK_set & user_behavior_set)
            kv_dict[k].recommend_num += len(topK_set)
            kv_dict[k].behavior_num += len(user_behavior_set)
    """
    Precision = (命中的项数量) / (推荐的总项数量)
    Recall = (命中的项数量) / (所有相关项的数量)
    """
    precision_list = [kv_dict[k].hit_num / kv_dict[k].recommend_num for k in K_list]
    recall_list = [kv_dict[k].hit_num / kv_dict[k].behavior_num for k in K_list]
    return precision_list,recall_list

