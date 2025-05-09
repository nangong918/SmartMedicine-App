from typing import List
from model import NeuMF_model,NeuMF_model_test
import tensorflow as tf
from typing import Tuple
from tool.callback import Evaluate_callback
from tool.common import get_score_fn
from tool.topK import TopK_data
# 绘制 loss 图
import matplotlib.pyplot as plt

# 将数据转化为张量
def dataset_prepare(train_data: List[Tuple[int, int, int]], test_data: List[Tuple[int, int, int]], batch_size: int) \
        -> Tuple[tf.data.Dataset, tf.data.Dataset]:

    def change_to_tensor(data):
        user_ids = tf.constant([d[0] for d in data], dtype=tf.int32)
        item_ids = tf.constant([d[1] for d in data], dtype=tf.int32)
        labels = tf.constant([d[2] for d in data], dtype=tf.keras.backend.floatx())
        return {'user_id': user_ids, 'item_id': item_ids}, labels

    train_ds = tf.data.Dataset.from_tensor_slices(change_to_tensor(train_data)).shuffle(len(train_data)).batch(batch_size)
    test_ds = tf.data.Dataset.from_tensor_slices(change_to_tensor(test_data)).batch(batch_size)

    return train_ds,test_ds


def _train(model: tf.keras.Model,
           train_ds, test_ds, topK_data,
           optimizer, loss_object, epochs):
    # 编译模型
    model.compile(optimizer=optimizer, loss=loss_object, metrics=['accuracy', 'AUC', 'Precision', 'Recall'])
    # 训练模型

    history = model.fit(train_ds, epochs=epochs, verbose=0, validation_data=test_ds,
                       callbacks=[
                           Evaluate_callback(topK_data, get_score_fn(model))
                       ])



    plt.figure(figsize=(8, 6))
    plt.plot(history.history['loss'], label='Training Loss')
    plt.plot(history.history['val_loss'], label='Validation Loss')
    plt.xlabel('Epoch')
    plt.ylabel('Loss')
    plt.title('Training and Validation Loss')
    plt.legend()
    plt.show()

    plt.figure(figsize=(8, 6))
    plt.plot(history.history['accuracy'], label='Training Accuracy')
    plt.plot(history.history['val_accuracy'], label='Validation Accuracy')
    plt.xlabel('Epoch')
    plt.ylabel('Accuracy')
    plt.title('Training and Validation Accuracy')
    plt.legend()
    plt.show()

    return history


# 基础训练
def train_basic(model: tf.keras.Model,
                train_data: List[Tuple[int, int, int]], test_data: List[Tuple[int, int, int]],
                topK_data: TopK_data, optimizer=None, loss_object=None, epochs=100, batch_size=512):

    """
    通用训练流程。

    :param model: 模型
    :param train_data: 训练集
    :param test_data: 测试集
    :param topK_data: 用于topK评估数据
    :param optimizer: 优化器，默认为Adam
    :param loss_object: 损失函数，默认为BinaryCrossentropy  二元交叉熵：用于二分类
    :param epochs: 迭代次数、
    :param batch_size: 批数量
    """

    if optimizer is None:
        optimizer = tf.keras.optimizers.Adam()

    if loss_object is None:
        loss_object = tf.keras.losses.BinaryCrossentropy()

    # 数据预处理
    train_ds, test_ds = dataset_prepare(train_data,test_data,batch_size)
    # 训练
    return _train(model,train_ds,test_ds,topK_data,optimizer,loss_object,epochs)






def train_main(n_user: int, n_item: int, train_data, test_data, topK_data, gmf_dim: int, mlp_dim: int, layers: List, l2:float):

    neumf_model, gmf_model, mlp_model = NeuMF_model(n_user, n_item, gmf_dim=gmf_dim, mlp_dim=mlp_dim, layers=layers, L2=l2)

    history = []

    print('\n预训练GMF部分：矩阵分解')
    history1 = train_basic(gmf_model, train_data, test_data, topK_data,loss_object = tf.keras.losses.MeanSquaredError(), epochs=13, batch_size=512)
    history.append(history1)
    print('\n预训练MLP部分：多层感知机')
    history2 = train_basic(mlp_model, train_data, test_data, topK_data,loss_object = tf.keras.losses.MeanSquaredError(), epochs=10, batch_size=512)
    history.append(history2)
    # 两个权重矩阵在垂直方向上进行拼接      权重：weights[0]
    out_weight = tf.concat((gmf_model.get_layer('gmf_out').get_weights()[0],mlp_model.get_layer('mlp_out').get_weights()[0]),0)
    # 偏置相加                          偏置：weights[0]
    out_bias = gmf_model.get_layer('gmf_out').get_weights()[1] + mlp_model.get_layer('mlp_out').get_weights()[1]
    # 对权重和偏置进行平均化处理
    neumf_model.get_layer('out').set_weights([out_weight * 0.5, out_bias * 0.5])

    print('\n训练NeuMF部分')
    history3 = train_basic(neumf_model, train_data, test_data, topK_data,loss_object = tf.keras.losses.MeanSquaredError(), optimizer=tf.keras.optimizers.Adam(), epochs=15, batch_size=512)
    history.append(history3)
    #train_basic(neumf_model, train_data, test_data, topK_data, epochs=10, batch_size=512)

    plt.figure(figsize=(8, 6))
    plt.plot(history[0].history['val_loss'], label='GMF val_Loss')
    plt.plot(history[1].history['val_loss'], label='MLP val_Loss')
    plt.plot(history[2].history['val_loss'], label='NeuMF val_Loss')
    plt.xlabel('Epoch')
    plt.ylabel('Loss')
    plt.title('Model Loss')
    plt.legend()
    plt.show()

    plt.figure(figsize=(8, 6))
    plt.plot(history[0].history['val_accuracy'], label='GMF val_Accuracy')
    plt.plot(history[1].history['val_accuracy'], label='MLP val_Accuracy')
    plt.plot(history[2].history['val_accuracy'], label='NeuMF val_Accuracy')
    plt.xlabel('Epoch')
    plt.ylabel('Accuracy')
    plt.title('Model Accuracy')
    plt.legend()
    plt.show()

    # 保存模型
    # neumf_model.save('neumf_model.h5')
    # gmf_model.save('gmf_model.h5')
    # mlp_model.save('mlp_model.h5')
    #
    # print("\n模型保存成功")

    return history[2]



def train_main_test(n_user: int, n_item: int, train_data, test_data, topK_data, dim=3, layers=[6,3]):

    neumf_model, gmf_model, mlp_model = NeuMF_model_test(n_user, n_item, dim=dim, layers=layers)

    history = []

    print('\n预训练GMF部分：矩阵分解')
    history1 = train_basic(gmf_model, train_data, test_data, topK_data, epochs=10, batch_size=512)
    history.append(history1)
    print('\n预训练MLP部分：多层感知机')
    history2 = train_basic(mlp_model, train_data, test_data, topK_data, epochs=10, batch_size=512)
    history.append(history2)
    # 两个权重矩阵在垂直方向上进行拼接      权重：weights[0]
    out_weight = tf.concat((gmf_model.get_layer('gmf_out').get_weights()[0],mlp_model.get_layer('mlp_out').get_weights()[0]),0)
    # 偏置相加                          偏置：weights[0]
    out_bias = gmf_model.get_layer('gmf_out').get_weights()[1] + mlp_model.get_layer('mlp_out').get_weights()[1]
    # 对权重和偏置进行平均化处理
    neumf_model.get_layer('out').set_weights([out_weight * 0.5, out_bias * 0.5])

    print('\n训练NeuMF部分')
    history3 = train_basic(neumf_model, train_data, test_data, topK_data, optimizer=tf.keras.optimizers.Adam(), epochs=15, batch_size=512)
    history.append(history3)
    #train_basic(neumf_model, train_data, test_data, topK_data, epochs=10, batch_size=512)

    plt.figure(figsize=(8, 6))
    plt.plot(history[0].history['val_loss'], label='GMF val_Loss')
    plt.plot(history[1].history['val_loss'], label='MLP val_Loss')
    plt.plot(history[2].history['val_loss'], label='NeuMF val_Loss')
    plt.xlabel('Epoch')
    plt.ylabel('Loss')
    plt.title('Model Loss')
    plt.legend()
    plt.show()

    plt.figure(figsize=(8, 6))
    plt.plot(history[0].history['val_accuracy'], label='GMF val_Accuracy')
    plt.plot(history[1].history['val_accuracy'], label='MLP val_Accuracy')
    plt.plot(history[2].history['val_accuracy'], label='NeuMF val_Accuracy')
    plt.xlabel('Epoch')
    plt.ylabel('Accuracy')
    plt.title('Model Accuracy')
    plt.legend()
    plt.show()

    return history[2]










