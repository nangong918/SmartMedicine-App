import pandas as pd
from sklearn.utils import resample



disease_list = ['高血压', '高胆固醇', '身体质量指数BMI',
                      '吸烟','中风',
                      '体力运动',
                      '水果','蔬菜','重度饮酒',
                      '任何医疗保健','没有医疗花费',
                      '一般健康状况','心理健康','身体健康',
                      '行走困难',
                      '性别','年龄','教育水平','收入']

list_no = [1,1,70,
           1,1,
           0,
           0,0,1,
           0,1,
           1,1,1,
           1,
           1,80,1,1]

list_yes = [0,0,18,
           0,0,
           1,
           1,1,0,
           1,0,
           30,30,30,
           0,
           1,10,5,5]

def balance_data(data, target_column):
    # 将数据分为两个类别
    class_0 = data[data[target_column] == 0]
    class_1 = data[data[target_column] == 1]

    # 获取较少的类别数据数量
    minority_count = min(len(class_0), len(class_1))

    # 对较多的类别进行下采样
    class_0_downsampled = resample(class_0, replace=False, n_samples=minority_count, random_state=42)

    # 合并两个类别的数据
    balanced_data = pd.concat([class_0_downsampled, class_1])

    return balanced_data
