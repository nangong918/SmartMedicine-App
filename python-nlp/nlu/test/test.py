from bert4keras.backend import keras, set_gelu
from bert4keras.models import build_transformer_model
from bert4keras.tokenizers import Tokenizer
import numpy as np

# GELU使用tanh函数的变体
set_gelu('tanh')


def build_bert_similarity_model(config_path, checkpoint_path):
    bert = build_transformer_model(
        config_path=config_path,
        checkpoint_path=checkpoint_path,
        model='bert',
        return_keras_model=False
    )

    # 提取CLS特征
    cls_features = keras.layers.Lambda(
        lambda x: x[:, 0],
        name='cls-token'
    )(bert.model.output)

    # 构建模型
    model = keras.models.Model(inputs=bert.model.input, outputs=cls_features)

    return model


if __name__ == '__main__':
    config_path = '../bert_intent_recognition/Bert/bert_config.json'
    bert_model_path = '../bert_intent_recognition/Bert/bert_model.ckpt'
    dict_path = '../bert_intent_recognition/Bert/vocab.txt'

    model = build_bert_similarity_model(config_path,bert_model_path)

    print(model.summary())

    # 示例输入数据
    text1 = '胃疼'
    text2 = '胃痛'

    # 使用Tokenizer对文本进行编码
    tokenizer = Tokenizer(dict_path)
    token_ids1, segment_ids1 = tokenizer.encode(text1)
    token_ids2, segment_ids2 = tokenizer.encode(text2)

    # 扩展维度，以便匹配模型输入形状
    token_ids1 = np.expand_dims(token_ids1, 0)
    segment_ids1 = np.expand_dims(segment_ids1, 0)
    token_ids2 = np.expand_dims(token_ids2, 0)
    segment_ids2 = np.expand_dims(segment_ids2, 0)

    # 向量
    vec = model.predict([token_ids1,segment_ids1])
    vec2 = model.predict([token_ids2,segment_ids2])

    # print("vec",vec)
    # print("vec2",vec2)

    from sklearn.metrics.pairwise import cosine_similarity

    # 计算余弦相似度
    similarity = cosine_similarity(vec, vec2)[0][0]

    print("相似度：", similarity)