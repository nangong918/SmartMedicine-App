from typing import List,Tuple
import os


# 相对路径
dataset_path =os.path.join(os.path.dirname(__file__), 'dataset')


# 基础读取
def read_base(relative_path: str, separator: str) \
        -> List[Tuple[int,int,int,int]]:
    data = []
    with open(os.path.join(dataset_path,relative_path),'r') as f:
        for line in f.readlines():
            values = line.strip().split(separator)
            user_id, movie_id, rating, timestamp = int(values[0]), int(values[1]), int(values[2]), int(values[3])
            data.append((user_id, movie_id, rating, timestamp))
    return data


# ml100k读取
def read_ml100k(check_data: str = 'No') \
        -> List[Tuple[int, int, int, int]]:
    print('开始读取数据')
    data = read_base('ml-100k/u.data','\t')
    if check_data == 'Yes':
        data_len = len(data)
        n_user, n_item, n_score = len(set(d[0] for d in data)), len(set(d[1] for d in data)), len(set(d[2] for d in data))
        print('data_length = ',data_len)
        print('user_num = ',n_user)
        print('item_num = ',n_item)
        print('n_score = ', n_score)
    return data


def test():
    # data = read_ml100k()
    # print(data[0])

    # read_ml100k('Yes')

    read_ml100k('Yes')
    pass


if __name__ == '__main__':
    test()
    pass





