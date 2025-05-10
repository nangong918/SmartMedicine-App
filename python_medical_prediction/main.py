import tensorflow as tf
import pandas as pd
from sklearn.preprocessing import StandardScaler
from sklearn.model_selection import train_test_split
import numpy as np
from sklearn.utils import resample

'''
    数据选取：
    心脏病发作,
    高血压,高胆固醇,身体质量指数BMI,
    吸烟,中风,
    体力运动,
    水果,蔬菜,重度饮酒,
    任何医疗保健,没有医疗花费,
    一般健康状况,心理健康,身体健康,
    行走困难,
    性别,年龄,教育水平,收入

    选取其中的20项数据
'''






diabetes_list = ['高血压','高胆固醇','身体质量指数BMI',
                 '吸烟','中风',
                 '体力运动',
                 '水果','蔬菜','重度饮酒',
                 '任何医疗保健','没有医疗花费',
                 '一般健康状况','心理健康','身体健康',
                 '行走困难',
                 '性别','年龄','教育水平','收入']

heart_disease_list = ['高血压','高胆固醇','身体质量指数BMI',
                      '吸烟','中风',
                      '体力运动',
                      '水果','蔬菜','重度饮酒',
                      '任何医疗保健','没有医疗花费',
                      '一般健康状况','心理健康','身体健康',
                      '行走困难',
                      '性别','年龄','教育水平','收入']

input_shape_1 = len(heart_disease_list)

print("input_shape_1", input_shape_1)






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


def get_model(lr = 1e-4):
    # 5. 构建模型
    model = tf.keras.Sequential([
        tf.keras.layers.Dense(1, activation='sigmoid', input_shape=(input_shape_1,))
    ])

    # 6. 编译模型
    optimizer = tf.keras.optimizers.Adam(learning_rate=lr)  # 设置学习率
    model.compile(optimizer=optimizer, loss='binary_crossentropy', metrics=['accuracy'])
    # model.compile(optimizer=optimizer, loss='mean_squared_error')

    return model

def train():
    heart_disease_path = 'dataset/心脏病.csv'
    diabetes_path = 'dataset/糖尿病.csv'

    data = pd.read_csv(heart_disease_path)
    data = balance_data(data, '心脏病发作')

    X = data[heart_disease_list]
    Y = data[['心脏病发作']]

    # print(X['高血压'][0])
    # print(Y['心脏病发作'][0])

    # 3. 数据预处理：将特征数据 X 进行缩放
    scaler = StandardScaler()
    X = scaler.fit_transform(X)

    # print(X[0])

    # 4. 划分训练集和测试集
    X_train, X_test, y_train, y_test = train_test_split(X, Y, test_size=0.2, random_state=42)


    # 5. 构建模型
    model = get_model()

    # 7. 训练模型
    model.fit(X_train, y_train, epochs=10, batch_size=32)

    # 8. 保存权重
    model.save_weights('model_weights.h5')


def test():
    heart_disease_path = 'dataset/心脏病.csv'
    diabetes_path = 'dataset/糖尿病.csv'

    data = pd.read_csv(heart_disease_path)
    data = balance_data(data, '心脏病发作')

    X = data[heart_disease_list]
    Y = data[['心脏病发作']]

    # 3. 数据预处理：将特征数据 X 进行缩放
    scaler = StandardScaler()
    X = scaler.fit_transform(X)

    # 4. 划分训练集和测试集
    X_train, X_test, y_train, y_test = train_test_split(X, Y, test_size=0.2, random_state=42)

    # 5. 构建模型
    model = get_model()


    model.load_weights('model_weights.h5')

    # 8. 模型评估
    loss = model.evaluate(X_test, y_test)
    print('测试集损失：', loss)

    # 9. 进行预测
    predictions = model.predict(X_test)

    # 输出预测结果
    num = 0
    for i in range(len(predictions)):
        pred = np.round(predictions[i])
        if pred == y_test.iloc[i]['心脏病发作']:
            num += 1
        print('样本', i + 1, '预测结果：', predictions[i], '患有真实标签：', y_test.iloc[i]['心脏病发作'])
    acc = num / len(predictions)
    print(acc)

    x_user = np.array([[1, 1, 30, 1, 0, 0, 1, 1, 0, 1, 0, 5, 30, 30, 1, 0, 9, 5, 1]])
    x_user = np.squeeze(np.array([1, 1, 30, 1, 0, 0, 1, 1, 0, 1, 0, 5, 30, 30, 1, 0, 9, 5, 1]))

    def predict_fn(x):
        x = np.reshape(x, (1, 19))  # 调整输入形状为(1, 19)
        predictions = model.predict(x)  # 使用模型的 predict 方法进行预测
        probabilities = np.zeros((len(predictions), 2))  # 创建一个全零的概率分数数组
        probabilities[:, 1] = predictions.flatten()  # 将展平后的预测结果作为正类的概率分数
        probabilities[:, 0] = 1 - predictions.flatten()  # 计算负类的概率分数
        return probabilities
    print(predict_fn(x_user))

    # 获取权重
    weights, bias = model.get_weights()

    # 计算相关系数
    correlation_coefficients = np.squeeze(weights)

    print("weights:")
    print(correlation_coefficients)
    print("bias:",bias)


def predict_test():
    import lime
    import lime.lime_tabular

    heart_disease_path = 'dataset/心脏病.csv'
    diabetes_path = 'dataset/糖尿病.csv'

    data = pd.read_csv(heart_disease_path)
    data = balance_data(data, '心脏病发作')

    X = data[heart_disease_list]
    Y = data[['心脏病发作']]

    # 1. 加载模型和权重
    model = get_model()
    model.load_weights('model_weights.h5')

    # 2. 定义特征名称
    feature_names = heart_disease_list


    X_train, X_test, y_train, y_test = train_test_split(X, Y, test_size=0.2, random_state=42)

    # 4. 创建LIME解释器
    explainer = lime.lime_tabular.LimeTabularExplainer(X_train,
                                                       feature_names=feature_names,
                                                       mode='classification',
                                                       class_names=['患有心脏病', '未患有心脏病'],
                                                       discretize_continuous=False)

    def predict_fn(x):
        predictions = model.predict(x)  # 使用模型的 predict 方法进行预测
        probabilities = np.zeros((len(predictions), 2))  # 创建一个全零的概率分数数组
        probabilities[:, 1] = predictions.flatten()  # 将展平后的预测结果作为正类的概率分数
        probabilities[:, 0] = 1 - predictions.flatten()  # 计算负类的概率分数
        return probabilities

    '''
        数据选取：
        心脏病发作,
        高血压,高胆固醇,身体质量指数BMI,
        吸烟,中风,
        体力运动,
        水果,蔬菜,重度饮酒,
        任何医疗保健,没有医疗花费,
        一般健康状况,心理健康,身体健康,
        行走困难,
        性别,年龄,教育水平,收入

        选取其中的20项数据
    '''

    # 5. 解释用户输入数据
    user_data = [1, 1, 30,
                 1, 1,
                 0,
                 0, 0, 1,
                 0, 1,
                 2, 10, 10,
                 1,
                 0, 10, 1, 1]
    x = np.array([user_data])
    predictions = model.predict(x)
    print("患病可能性：",predictions)
    x_user = np.squeeze(np.array(user_data))
    explanation = explainer.explain_instance(x_user, predict_fn, num_features=len(feature_names))

    # 6. 打印解释结果
    print('用户输入数据相关性解释:')
    for feature, weight in explanation.as_list():
        print(feature, ':', weight)


def train_continue():
    heart_disease_path = 'dataset/心脏病.csv'

    data = pd.read_csv(heart_disease_path)
    data = balance_data(data, '心脏病发作')

    X = data[heart_disease_list]
    Y = data[['心脏病发作']]

    # 3. 数据预处理：将特征数据 X 进行缩放
    scaler = StandardScaler()
    X = scaler.fit_transform(X)

    # 4. 划分训练集和测试集
    X_train, X_test, y_train, y_test = train_test_split(X, Y, test_size=0.2, random_state=42)

    # 5. 构建模型
    model = get_model()

    # 6.加载权重
    model.load_weights('model_weights.h5')

    # 7. 训练模型
    model.fit(X_train, y_train, epochs=50, batch_size=32)

    # 8. 保存权重
    model.save_weights('model_weights.h5')



if __name__ == '__main__':
    #train()
    #train_continue()
    test()
    #predict_test()
























