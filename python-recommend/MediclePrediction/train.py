import pandas as pd
from MediclePrediction.data_tool import disease_list
from sklearn.preprocessing import StandardScaler
from sklearn.model_selection import train_test_split
from MediclePrediction.model import get_model,get_model_attention,get_model_MSE,get_model_MultiFullyConnectedLayer,get_model_Feature_Interaction
from MediclePrediction.data_tool import balance_data
import os
import numpy as np
import matplotlib.pyplot as plt

current_dir = os.path.dirname(os.path.abspath(__file__))  # 获取当前脚本文件的绝对路径
heart_disease_path = os.path.join(current_dir,'dataset\\心脏病.csv')
diabetes_path = os.path.join(current_dir,'dataset\\糖尿病.csv')
model_weights_heart_path = os.path.join(current_dir, 'weights\\model_weights_heart.h5')
model_weights_diabetes_path = os.path.join(current_dir, 'weights\\model_weights_diabetes.h5')
label_heart = '心脏病发作'
label_diabetes = '糖尿病发作'

# 超参数
Epochs = 15
Batch_size = 32
test_size = 0.2

def train(label:str):
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
    Y = data[[label]]

    X_array = np.array(X.values)
    Y_array = np.array(Y.values)
    print("X:", X_array[0])
    print("Y:", Y_array[0])

    # 3. 数据预处理：将特征数据 X 进行缩放
    scaler = StandardScaler()
    X_array = scaler.fit_transform(X_array)

    # 4. 划分训练集和测试集
    X_train, X_test, y_train, y_test = train_test_split(X_array, Y_array, test_size=test_size, random_state=42)
    print("训练集长度：", len(X_train))
    print("测试集长度：", len(X_test))

    # 5. 构建模型
    model = get_model()

    # 7. 训练模型
    model.fit(X_train, y_train, validation_data=(X_test, y_test),epochs=Epochs, batch_size=Batch_size)

    # 8. 保存权重
    # model.save_weights(model_path)

    # 9.绘图
    # 获取训练过程中的 loss 和 accuracy 数据
    train_loss = model.history.history['loss']
    train_acc = model.history.history['accuracy']

    val_loss = model.history.history['val_loss']
    val_acc = model.history.history['val_accuracy']

    # plt.figure(figsize=(8, 6))
    # plt.plot(train_loss, label='Model Loss')
    # plt.plot( val_loss, label='Model val_Loss')
    # plt.xlabel('Epoch')
    # plt.ylabel('Loss')
    # plt.title('Model Loss')
    # plt.legend()
    # plt.show()
    #
    # plt.figure(figsize=(8, 6))
    # plt.plot(train_acc, label='Model Accuracy')
    # plt.plot( val_acc, label='Model Accuracy')
    # plt.xlabel('Epoch')
    # plt.ylabel('Accuracy')
    # plt.title('Model Accuracy')
    # plt.legend()
    # plt.show()
    return val_loss,val_acc

def train_basic(label:str,fun):
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
    Y = data[[label]]

    X_array = np.array(X.values)
    Y_array = np.array(Y.values)
    print("X:", X_array[0])
    print("Y:", Y_array[0])

    # 3. 数据预处理：将特征数据 X 进行缩放
    scaler = StandardScaler()
    X_array = scaler.fit_transform(X_array)

    # 4. 划分训练集和测试集
    X_train, X_test, y_train, y_test = train_test_split(X_array, Y_array, test_size=test_size, random_state=42)
    print("训练集长度：", len(X_train))
    print("测试集长度：", len(X_test))

    # 5. 构建模型
    model = fun()

    # 7. 训练模型
    model.fit(X_train, y_train, validation_data=(X_test, y_test),epochs=Epochs, batch_size=Batch_size)

    # 8. 保存权重
    # model.save_weights(model_path)

    # 9.绘图
    # 获取训练过程中的 loss 和 accuracy 数据

    val_loss = model.history.history['val_loss']
    val_acc = model.history.history['val_accuracy']

    return val_loss, val_acc

def training_comparison():
    val_loss1, val_acc1 = train_basic(label_heart,get_model_MultiFullyConnectedLayer)
    val_loss,val_acc = train(label_heart)
    plt.figure(figsize=(8, 6))
    plt.plot(val_loss, label='Model 1 val_Loss')
    plt.plot( val_loss1, label='Model 2 val_Loss')
    plt.xlabel('Epoch')
    plt.ylabel('Loss')
    plt.title('Model Loss')
    plt.legend()
    plt.show()

    plt.figure(figsize=(8, 6))
    plt.plot(val_acc, label='Model 1 val_Accuracy')
    plt.plot( val_acc1, label='Model 2 val_Accuracy')
    plt.xlabel('Epoch')
    plt.ylabel('Accuracy')
    plt.title('Model Accuracy')
    plt.legend()
    plt.show()

def training_comparison2():
    val_loss,val_acc = train(label_heart)
    val_loss1, val_acc1 = train_basic(label_heart, get_model_MSE)
    val_loss2, val_acc2 = train_basic(label_heart, get_model_attention)
    val_loss3, val_acc3 = train_basic(label_heart, get_model_MultiFullyConnectedLayer)
    val_loss4, val_acc4 = train_basic(label_heart, get_model_Feature_Interaction)
    plt.figure(figsize=(8, 6))
    plt.plot(val_loss, label='FullyConnectedLayer val_Loss')
    plt.plot( val_loss1, label='MSE val_Loss')
    plt.plot(val_loss2, label='attention val_Loss')
    plt.plot(val_loss3, label='MultiFullyConnectedLayer val_Loss')
    plt.plot(val_loss4, label='Feature Interaction val_Loss')
    plt.xlabel('Epoch')
    plt.ylabel('Loss')
    plt.title('Model Loss')
    plt.legend()
    plt.show()

    plt.figure(figsize=(8, 6))
    plt.plot(val_acc, label='FullyConnectedLayer val_Accuracy')
    plt.plot( val_acc1, label='MSE val_Accuracy')
    plt.plot(val_acc2, label='attention val_Accuracy')
    plt.plot(val_acc3, label='MultiFullyConnectedLayer val_Accuracy')
    plt.plot(val_acc4, label='Feature Interaction val_Accuracy')
    plt.xlabel('Epoch')
    plt.ylabel('Accuracy')
    plt.title('Model Accuracy')
    plt.legend()
    plt.show()

def train_continue(label:str):
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
    data = balance_data(data, label)

    X = data[disease_list]
    Y = data[[label]]

    # 3. 数据预处理：将特征数据 X 进行缩放
    scaler = StandardScaler()
    X = scaler.fit_transform(X)

    # 4. 划分训练集和测试集
    X_train, X_test, y_train, y_test = train_test_split(X, Y, test_size=test_size, random_state=42)

    # 5. 构建模型
    model = get_model()

    # 6.加载权重
    model.load_weights(model_path)

    # 7. 训练模型
    model.fit(X_train, y_train, epochs=Epochs, batch_size=Batch_size)

    # 8. 保存权重
    model.save_weights(model_path)

if __name__ == '__main__':
    #train(label_heart)
    # train_continue(label_diabetes)
    training_comparison2()
    pass