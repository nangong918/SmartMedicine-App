from bert4keras.backend import keras,set_gelu
from bert4keras.models import build_transformer_model



# GELU使用tanh函数的变体
# Sigmoid函数的取值范围在(0, 1)之间，它将输入值映射到一个概率分布，可以用于二分类问题的概率预测。而GELU函数的取值范围在(-1, +1)之间，更适用于多分类问题
set_gelu('tanh')




def text_cnn(inputs,kernel_initializer,dropout = 0.2):

    # 卷积窗口宽度大小设置：3，4，5

    cnn1 = keras.layers.Conv1D(
        256,
        3,
        strides=1,
        padding='same',
        activation='relu',
        kernel_initializer=kernel_initializer
    )(inputs)  # shape=[batch_size,maxlen-2,256]

    #全局最大池化层，对卷积层的输出 cnn1 进行池化操作
    cnn1 = keras.layers.GlobalMaxPooling1D()(cnn1)  # shape=[batch_size,256]


    cnn2 = keras.layers.Conv1D(
        256,
        4,
        strides=1,
        padding='same',
        activation='relu',
        kernel_initializer=kernel_initializer
    )(inputs)
    cnn2 = keras.layers.GlobalMaxPooling1D()(cnn2)


    cnn3 = keras.layers.Conv1D(
        256,
        5,
        strides=1,
        padding='same',
        kernel_initializer=kernel_initializer
    )(inputs)
    cnn3 = keras.layers.GlobalMaxPooling1D()(cnn3)

    # 特征向量拼接
    output = keras.layers.concatenate(
        [cnn1, cnn2, cnn3],
        axis=-1)
    # 0.2丢弃率
    output = keras.layers.Dropout(dropout)(output)

    return output




def build_bert_model(config_path,checkpoint_path,class_nums):
    bert = build_transformer_model(
        config_path=config_path,
        checkpoint_path=checkpoint_path,
        model='bert',
        return_keras_model=False)

    # 创建一个Lambda层，用于提取BERT模型的CLS标记    句子的分类表示:CLS标记是用于表示整个句子的特殊标记
    cls_features = keras.layers.Lambda(
        #提取每个样本的第一个位置的输出，即CLS标记
        lambda x: x[:, 0],
        name='cls-token'
    )(bert.model.output)  # shape=[batch_size,768]

    # 提取BERT模型的所有标记的嵌入表示
    all_token_embedding = keras.layers.Lambda(
        # 提取每个样本从第2个位置到第-2个位置的嵌入表示  除了CLS和SEP标记
        lambda x: x[:, 1:-1],
        name='all-token'
    )(bert.model.output)  # shape=[batch_size,maxlen-2,768]

    # 文本卷积
    cnn_features = text_cnn(
        all_token_embedding,bert.initializer
    )   #shape=[batch_size,cnn_output_dim]

    #CLS特征和文本卷积特征在最后一个维度上进行拼接，得到拼接后的特征 concat_features
    concat_features = keras.layers.concatenate(
        [cls_features, cnn_features],
        axis=-1)

    # 全连接层
    dense = keras.layers.Dense(
        units=512,
        activation='relu',
        # bert.initializer作为初始化权重
        kernel_initializer=bert.initializer
    )(concat_features)

    # 全连接层：分类512->15
    output = keras.layers.Dense(
        units=class_nums,
        activation='softmax',
        kernel_initializer=bert.initializer
    )(dense)

    # bert.model.input ： 定义输入形状与Bert的输入形状相同
    model = keras.models.Model(bert.model.input, output)

    return model


if __name__ == '__main__':
    import pydot
    from keras.utils.vis_utils import plot_model
    model = build_bert_model("Bert/bert_config.json", "Bert/bert_model.ckpt", 10)
    plot_model(model, to_file='Bert_model.png', show_shapes=True, show_layer_names=True)




