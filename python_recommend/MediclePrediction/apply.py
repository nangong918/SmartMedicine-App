import pandas as pd
from sklearn.preprocessing import StandardScaler
from sklearn.model_selection import train_test_split
import numpy as np
from MediclePrediction.data_tool import disease_list,balance_data
from MediclePrediction.model import get_model
from MediclePrediction.train import (
    heart_disease_path, diabetes_path,
    model_weights_heart_path, model_weights_diabetes_path, label_heart, label_diabetes
)


from typing import Tuple,List

# 超参数
Epochs = 30
Batch_size = 32
test_size = 0.2


def apply_test(label='心脏病发作'):
    model_path = None
    data_path = None
    if label == label_heart:
        model_path = model_weights_heart_path
        data_path = heart_disease_path
    elif label == label_diabetes:
        model_path = model_weights_diabetes_path
        data_path = diabetes_path

    assert model_path is not None and data_path is not None

    data = pd.read_csv(data_path)
    print("读取数据集data：", len(data))
    data = balance_data(data, label)
    print("平衡数据集balance_data：", len(data))

    X = data[disease_list]
    print("X:",X)
    Y = data[[label]]
    print("Y:",Y)

    X_array = np.array(X.values)
    print(X_array[0])
    Y_array = np.array(Y.values)

    # 3. 数据预处理：将特征数据 X 进行缩放
    scaler = StandardScaler()
    X_array = scaler.fit_transform(X_array)

    # 4. 划分训练集和测试集
    print("X长度：", len(X_array))

    # 5. 构建模型
    model = get_model()

    model.load_weights(model_path)

    y = model.predict(X_array)
    print(y)


def apply_test2(list,label='心脏病发作'):
    model_path = None
    data_path = None
    if label == label_heart:
        model_path = model_weights_heart_path
        data_path = heart_disease_path
    elif label == label_diabetes:
        model_path = model_weights_diabetes_path
        data_path = diabetes_path

    assert model_path is not None and data_path is not None
    data = pd.read_csv(data_path)
    print("读取数据集data：", len(data))
    data = balance_data(data, label)
    print("平衡数据集balance_data：", len(data))

    X = data[disease_list]
    print("X:",X)
    Y = data[[label]]
    print("Y:",Y)

    X_add = list

    X_array = np.array(X.values)
    X_array = np.concatenate(([X_add], X_array))
    print(X_array[0])
    Y_array = np.array(Y.values)

    # 3. 数据预处理：将特征数据 X 进行缩放
    scaler = StandardScaler()
    X_array = scaler.fit_transform(X_array)

    # 4. 划分训练集和测试集
    print("X长度：", len(X_array))

    # 5. 构建模型
    model = get_model()

    model.load_weights(model_path)

    y = model.predict(X_array)
    print(y)


def apply_for_user(list,data,model_path,label='心脏病发作'):

    print("读取数据集data：", len(data))
    data = balance_data(data, label)
    print("平衡数据集balance_data：", len(data))

    X = data[disease_list]

    X_add = list

    X_array = np.array(X.values)
    X_array = np.concatenate(([X_add], X_array))
    print(X_array[0])

    # 3. 数据预处理：将特征数据 X 进行缩放
    scaler = StandardScaler()
    X_array = scaler.fit_transform(X_array)

    # 4. 划分训练集和测试集
    print("X长度：", len(X_array))

    # 5. 构建模型
    model = get_model()

    model.load_weights(model_path)

    y = model.predict(X_array)
    return y[0]


if __name__ == '__main__':
    # test()
    # apply_test()
    user_list = [1, 1, 30, 1, 0, 0, 1, 1, 0, 1, 0, 5, 30, 30, 1, 0, 9, 5, 1]

    list2 = [0, 0, 25,
            0, 0,
            1,
            1, 1, 0,
            1, 0,
            5, 5, 5,
            0,
            0, 5, 5, 5]

    #apply_test2(list2)

    #apply_test('心脏病发作')

    heart_disease_data = pd.read_csv('D:\\pythonProject\\NeuMF Recommend\\MediclePrediction\\dataset\\心脏病.csv')
    diabetes_data = pd.read_csv('D:\\pythonProject\\NeuMF Recommend\\MediclePrediction\\dataset\\糖尿病.csv')
    model_weights_heart_path = 'D:\\pythonProject\\NeuMF Recommend\\MediclePrediction\\weights\\model_weights_heart.h5'
    model_weights_diabetes_path = 'D:\\pythonProject\\NeuMF Recommend\\MediclePrediction\\weights\\model_weights_diabetes.h5'
    predict_heart = apply_for_user(user_list, heart_disease_data, model_weights_heart_path, '心脏病发作')
    predict_diabetes = apply_for_user(user_list, diabetes_data, model_weights_diabetes_path, '糖尿病发作')
    print(predict_heart)
    print(predict_diabetes)

    pass