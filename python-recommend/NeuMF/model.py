import tensorflow as tf
from typing import Tuple


'''

NeuMF 算法：

    通用结构：
        Input层：输入
            稀疏的向量   （one-hot编码的二值化稀疏向量）
        Embedding层：特征向量     （全连接层，用于将输入层的稀疏表示映射成一个稠密向量）
            用户特征向量u-vec
            物品特征向量i-vec
        Neural CF层：神经写通过过滤
            将潜在特征向量映射成预测分数
            loss函数：均方差误差（预测值-真实值的平方）
        Output层：输出
            用激活函数预测分数（sigmoid）

    模型
        GMF广义矩阵分解
            Pi(*)Qu     其中(*)表示逐行相乘
            Yui = Aout(W(Pi(*)Qu))  其中Aout表示激活函数，W表示权重
        MLP多层感知机
            Z1 = %(Pi,Qu)
            %(Zn) = Aout(Wn*Z1 + bn)
            Yui = sigmoid(H*%(Zn))
        NCF张量拼接
            GMF + MLP
'''



def NeuMF_model(n_user: int, n_item: int, gmf_dim=8, mlp_dim=32, layers=[32, 16, 8], L2=1e-6) \
        -> Tuple[tf.keras.Model, tf.keras.Model, tf.keras.Model]:

    # 超参数
    l2 = tf.keras.regularizers.l2(1e-6)

    # 通用输入层
    user_id = tf.keras.Input(shape=(), name='user_id', dtype=tf.int32)
    item_id = tf.keras.Input(shape=(), name='item_id', dtype=tf.int32)

    # 特征工程
    u = tf.keras.layers.Embedding(n_user, gmf_dim, embeddings_regularizer=l2)(user_id)
    i = tf.keras.layers.Embedding(n_item, gmf_dim, embeddings_regularizer=l2)(item_id)

    # 矩阵分解
    gmf = u * i

    # GMF输出层
    gmf_out = tf.keras.layers.Dense(1, activation='sigmoid', kernel_regularizer=l2, name='gmf_out')(gmf)

    u = tf.keras.layers.Embedding(n_user, mlp_dim, embeddings_regularizer=l2)(user_id)
    i = tf.keras.layers.Embedding(n_item, mlp_dim, embeddings_regularizer=l2)(item_id)

    # MLP输入拼接
    mlp = tf.concat([u, i], axis=1)
    l2 = tf.keras.regularizers.l2(4e-4)
    # MLP多层感知机
    for n in layers:
        mlp = tf.keras.layers.Dense(n, activation='relu', kernel_regularizer=l2)(mlp)

    # MLP输出层
    mlp_out = tf.keras.layers.Dense(1, activation='sigmoid', kernel_regularizer=l2, name='mlp_out')(mlp)

    # NeuMF张量拼接
    x = tf.concat([gmf, mlp], axis=1)
    l2 = tf.keras.regularizers.l2(1e-6)

    # NeuMF输出层
    x = tf.keras.layers.Dense(16, activation='relu', kernel_regularizer=l2)(x)
    x = tf.keras.layers.Attention()([x, x])
    out = tf.keras.layers.Dense(1, activation='sigmoid', kernel_regularizer=l2, name='out')(x)


    return tf.keras.Model(inputs=[user_id, item_id], outputs=out),\
           tf.keras.Model(inputs=[user_id, item_id], outputs=gmf_out),\
           tf.keras.Model(inputs=[user_id, item_id], outputs=mlp_out)


def NeuMF_model_test(n_user: int, n_item: int, dim=3, layers=[6,3]) \
        -> Tuple[tf.keras.Model, tf.keras.Model, tf.keras.Model]:

    # 通用输入层
    user_id = tf.keras.Input(shape=(), name='user_id', dtype=tf.int32)
    item_id = tf.keras.Input(shape=(), name='item_id', dtype=tf.int32)

    # 特征工程
    u = tf.keras.layers.Embedding(n_user, dim)(user_id)
    i = tf.keras.layers.Embedding(n_item, dim)(item_id)

    # 矩阵分解
    gmf = u * i

    # GMF输出层
    gmf_out = tf.keras.layers.Dense(1, activation='sigmoid', name='gmf_out')(gmf)

    # MLP输入拼接
    mlp = tf.concat([u, i], axis=1)
    # MLP多层感知机
    for n in layers:
        mlp = tf.keras.layers.Dense(n, activation='relu')(mlp)

    # MLP输出层
    mlp_out = tf.keras.layers.Dense(1, activation='sigmoid', name='mlp_out')(mlp)

    # NeuMF张量拼接
    x = tf.concat([gmf, mlp], axis=1)

    # NeuMF输出层
    out = tf.keras.layers.Dense(1, activation='sigmoid', name='out')(x)


    return tf.keras.Model(inputs=[user_id, item_id], outputs=out),\
           tf.keras.Model(inputs=[user_id, item_id], outputs=gmf_out),\
           tf.keras.Model(inputs=[user_id, item_id], outputs=mlp_out)


if __name__ == '__main__':
    # tf.keras.utils.plot_model(NeuMF_model(100, 100)[0], 'graph/NeuMF.png', show_shapes=True, rankdir='BT')
    # tf.keras.utils.plot_model(NeuMF_model(100, 100)[1], 'graph/gmf.png', show_shapes=True, rankdir='BT')
    # tf.keras.utils.plot_model(NeuMF_model(100, 100)[2], 'graph/mlp.png', show_shapes=True, rankdir='BT')
    tf.keras.utils.plot_model(NeuMF_model_test(100, 100)[0], 'graph/NeuMF_test.png', show_shapes=True, rankdir='BT')














