from typing import List,Tuple
import os

relative_path = 'user_article_evaluate.csv'
dataset_path = 'D:\\pythonProject\\NeuMF Recommend\\data\\sql_dataset\\user_article_evaluate.csv'

# 基础数据读取

import csv

def read_base(relative_path: str) -> List[Tuple[int, int, int]]:
    data = []
    with open(dataset_path, 'r', newline='') as file:
        reader = csv.reader(file)
        header = next(reader)  # 读取第一行作为标题行
        for row in reader:
            user_id, article_id, score = map(int, row)
            data.append((user_id, article_id, score))
    return data

def read_sql_dataset(check_data: str = 'Yes')->List[Tuple[int,int,int]]:
    print('开始读取数据库数据')
    data = read_base(dataset_path)
    data_len = len(data)
    if check_data == 'Yes':
        n_user, n_article, n_score = len(set(d[0] for d in data)),len(set(d[1] for d in data)), len(set(d[2] for d in data))
        print('data_length = ', data_len)
        print('user_num = ', n_user)
        print('article_num = ', n_article)
        print('score_num = ', n_score)
        print('不存在的id:')
        for i in range(1,138):
            Bool = True
            for num in set(d[0] for d in data):
                if num == i:
                    Bool = False
                    break
            if Bool :
                print(i)
    return data

if __name__ == '__main__':
    read_sql_dataset()