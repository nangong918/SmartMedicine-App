'''
    生成User-Entity矩阵：关键词
        用户1：感冒，咽炎，痰多，伤寒，发烧，流鼻涕，慢性咽炎，甲流，头痛
        用户2：月经不调，月经稀少，痛经，绝经，阴道炎
        用户3：肥胖
        用户4：高血压，高脂血症，血压病
        用户5：糖尿病，糖尿，尿糖
        用户6：营养不良，消化不良，口苦，胃病，胃炎，胀痛，打嗝，反酸
        用户7：失眠，疲乏，乏力，心慌
        用户8：便秘
        用户9：老年便秘，老年痴呆，听力下降，青光眼，痴呆，慢性病，骨质增生
        用户10：心律失常，冠心病，心跳加快，心脏骤停，呼吸衰竭，心脏病，白内障
        用户11：血糖升高，血栓形成，血压下降
        用户12：哮喘，呼吸衰竭
        用户13：尿频，尿急，尿毒症，尿分叉，早泄
        用户14：口腔出血，牙龈出血
        用户15：尖锐湿疣，艾滋病，性冷淡
    用户评分矩阵生成：
        distance函数找到相关的距离，列出相关距离排行榜。
        距离0~4：5~1分，其他为0分；
            记录到程度level_list:(entity,level)元组，重复的叠加
            记录到评分score_list:(entity,score)                      Java代码
        拓展到user对每个文章的稀疏矩阵，记录分数（由score_list推演来）  在Java代码中生成
    数据存入：
        user：id,account,password,name
        User-Entity：id,user_id,entity_id
        User-ArticleEvaluate：id,score,user_id,article_id    在Java代码中生成
'''

def read_keywords_from_file(filename):
    keyword_list = []
    with open(filename, 'r', encoding='utf-8') as file:
        for line in file:
            keywords = line.strip().split(',')
            keyword_list.append(keywords)
    return keyword_list


from 知识图谱相似实体审查.test.distance_test import calculate_with_all_limitDistance,open_entity_file

from typing import Tuple,List
# 叠加
def superposed_result_level(user_entity_level: List[Tuple], result_list: List[Tuple]):
    if result_list:
        for result in result_list:
            if user_entity_level:
                for i, user_entity in enumerate(user_entity_level):
                    if result[0] == user_entity[0]: # 存在的情况下：原来+i
                        if result[1] == 0:# 路径0
                            user_entity_level[i] = (user_entity[0], user_entity[1] + 3)
                        else:
                            user_entity_level[i] = (user_entity[0], user_entity[1] + 1)
                        break
                    else:   # 不存在的情况下：
                        if result[1] == 0:# 路径0
                            user_entity_level.append((result[0], 3))
                        else:
                            user_entity_level.append((result[0], 1))
                        break
            else:
                user_entity_level.append((result[0], 1))
    return user_entity_level


def calculate_user_level(users_keyword_list,entities_name):
    limit_distance = 1
    users_entity_level = []
    i = 0
    for user_keyword_list in users_keyword_list:
        user_entity_level = []
        print('new user:',i)
        i += 1
        for user_keyword in user_keyword_list:
            result_list = calculate_with_all_limitDistance(user_keyword,entities_name,limit_distance)
            user_entity_level = superposed_result_level(user_entity_level,result_list)
        users_entity_level.append(user_entity_level)
    return users_entity_level

import csv
def export_user_level(user_entity_level:List[List[Tuple]],filename):
    with open(filename, 'w', newline='', encoding='utf-8-sig') as csvfile:
        writer = csv.writer(csvfile)
        writer.writerow(['user_id', 'entity', 'level'])  # 写入表头

        for i, sublist in enumerate(user_entity_level, start=1):
            for entity, level in sublist:
                writer.writerow([i, entity, level])
    print("导出成功")

def export_user_level2(user_entity_level: List[List[Tuple]], filename):
    with open(filename, 'a', newline='', encoding='utf-8-sig') as csvfile:  # 使用'a'模式追加数据
        writer = csv.writer(csvfile)
        for i, sublist in enumerate(user_entity_level, start=1):
            for entity, level in sublist:
                writer.writerow([i, entity, level])
    print("导出成功")

def ui_generate():
    filename = 'user_entity.txt'
    users_keyword_list = read_keywords_from_file(filename)
    print(users_keyword_list)

    entities_name = open_entity_file('../文章实体AC树/entities.csv')

    users_entity_level = calculate_user_level(users_keyword_list,entities_name)

    export_user_level(users_entity_level,'user_entity_matrix.csv')

def ui_generate2():
    filename = 'user_entity.txt'
    users_keyword_list = read_keywords_from_file(filename)
    print(users_keyword_list)

    entities_name = open_entity_file('../文章实体AC树/entities.csv')

    num_users = len(users_keyword_list)
    chunk_size = 5
    num_chunks = (num_users + chunk_size - 1) // chunk_size

    for chunk_index in range(num_chunks):
        start_index = chunk_index * chunk_size
        end_index = (chunk_index + 1) * chunk_size
        chunk_users = users_keyword_list[start_index:end_index]

        users_entity_level = calculate_user_level(chunk_users, entities_name)

        export_user_level(users_entity_level, 'user_entity_matrix.csv')

    print("存储成功")



if __name__ == '__main__':
    ui_generate()

