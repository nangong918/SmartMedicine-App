from bert4keras.tokenizers import Tokenizer
from bert4keras.snippets import DataGenerator,sequence_padding
import pandas as pd
from bert_model import build_bert_model
from bert4keras.optimizers import Adam
from bert4keras.backend import keras
from sklearn.metrics import classification_report
import numpy as np
import os

# C:\CodeLearning\smart-medicine\python_nlp\nlu\bert
current_dir = os.path.dirname(os.path.abspath(__file__))  # 获取当前脚本文件的绝对路径
# C:\CodeLearning\smart-medicine
parent_dir_3 = os.path.dirname(os.path.dirname(current_dir)) + '/'

print(f"smart-medicine 项目 目录的绝对路径为：{parent_dir_3}")

config_path = os.path.join(parent_dir_3, 'files/Bert/bert_config.json')
bert_model_path = os.path.join(parent_dir_3, 'files/Bert/bert_model.ckpt')
dict_path = os.path.join(parent_dir_3, 'files/Bert/vocab.txt')

print(f"dict_path的绝对路径为：{dict_path}")

# Bert的token转化器
tokenizer = Tokenizer(dict_path)

current_file_path = os.path.abspath(__file__)
current_dir = os.path.dirname(current_file_path)  # 获取 train.py 所在的目录


# [batch_token_ids, batch_segment_ids], batch_labels
class data_generator(DataGenerator):
    """
    数据生成器
    """
    def __iter__(self,random=True):
        # 存储当前批次信息
        batch_token_ids, batch_segment_ids, batch_labels = [], [], []
        # 用迭代器取出数据
        for is_end, (text, label) in self.sample(random):
            # 将文本转换为BERT模型所需的输入格式
            token_ids, segment_ids = tokenizer.encode(text, maxlen=maxlen)  # [1,3,2,5,9,12,243,0,0,0]
            batch_token_ids.append(token_ids)
            batch_segment_ids.append(segment_ids)
            batch_labels.append([label])
            # 数量达到了设定的batch_size或已经达到数据集的末尾
            if len(batch_token_ids) == self.batch_size or is_end:
                # 相同长度填充
                batch_token_ids = sequence_padding(batch_token_ids)
                batch_segment_ids = sequence_padding(batch_segment_ids)
                batch_labels = sequence_padding(batch_labels)
                # yield语句生成一个批次的训练数据，即([batch_token_ids, batch_segment_ids], batch_labels)
                # yield语句将生成器的当前状态作为一个值返回，并在下次迭代时从上次离开的位置继续执行
                yield [batch_token_ids, batch_segment_ids], batch_labels
                # 清空批次列表
                batch_token_ids, batch_segment_ids, batch_labels = [], [], []


def load_data(filename):
    """加载数据
    单条格式：(文本, 标签id)
    """
    df = pd.read_csv(filename,header=0)
    return df[['text','label']].values



# 超参数
learning_rate = 5e-6
# 配置文件
batch_size = 16
maxlen = 60
class_nums = 13

# # 保存路径
# bast_model_filepath = '../demo/bert_intent_recognition/weights_save/best_model.weights'
# # 老数据集
# train_data_path = '../demo/bert_intent_recognition/data/train.csv'
# test_data_path = '../demo/bert_intent_recognition/data/test.csv'
#
# # 新数据集
# train_data_path_new = "../demo/bert_intent_recognition/data/new/train.csv"
# test_data_path_new = "../demo/bert_intent_recognition/data/new/test.csv"
# # 标签
# label_path = '../demo/bert_intent_recognition/label'
# label_path_new = '../demo/bert_intent_recognition/data/new/label'


import matplotlib.pyplot as plt
train_loss, val_loss, train_acc, val_acc = [], [], [], []

# 定义一个记录loss的回调函数
class LossHistory(keras.callbacks.Callback):
    def __init__(self, train_loss, val_loss, train_acc, val_acc):
        super().__init__()
        self.train_loss = train_loss
        self.val_loss = val_loss
        self.train_acc = train_acc
        self.val_acc = val_acc

    def on_batch_end(self, batch, logs=None):
        if logs is None:
            logs = {}
        loss = logs.get('loss')
        acc = logs.get('accuracy')

        if loss is not None:
            self.train_loss.append(loss)
        if acc is not None:
            self.train_acc.append(acc)

    def on_epoch_end(self, epoch, logs=None):
        if logs is None:
            logs = {}
        val_loss = logs.get('val_loss')
        val_acc = logs.get('val_accuracy')

        if val_loss is not None:
            self.val_loss.append(val_loss)
        if val_acc is not None:
            self.val_acc.append(val_acc)
'''
训练自然语言识别模型
'''
def train_nlj():
    train_data_path = parent_dir_3 + "python_nlp/nlj/train.csv"
    test_data_path = parent_dir_3 + "python_nlp/nlj/test.csv"
    model_save_path = parent_dir_3 + "python_nlp/nlj/weight/save/best_model.weights"
    acc_img_save_path = parent_dir_3 + "python_nlp/nlj/weight/save/acc.png"
    loss_img_save_path = parent_dir_3 + "python_nlp/nlj/weight/save/loss.png"
    print(f"loss_img_save_path的绝对路径为：{loss_img_save_path}")
    label_path = parent_dir_3 + "python_nlp/nlj/label"
    train(train_data_path, test_data_path,
          model_save_path,
          acc_img_save_path, loss_img_save_path,
          label_path
          )
    pass


def train_nlu():
    train_data_path = parent_dir_3 + "files/dataset/nlp/train.csv"
    test_data_path = parent_dir_3 + "files/dataset/nlp/test.csv"
    model_save_path = parent_dir_3 + "python_nlp/nlu/weight/save/best_model.weights"
    acc_img_save_path = parent_dir_3 + "python_nlp/nlu/weight/save/acc.png"
    loss_img_save_path = parent_dir_3 + "python_nlp/nlu/weight/save/loss.png"
    print(f"loss_img_save_path的绝对路径为：{loss_img_save_path}")
    label_path = parent_dir_3 + "files/dataset/nlp/label"
    train(train_data_path, test_data_path,
          model_save_path,
          acc_img_save_path, loss_img_save_path,
          label_path
          )
    pass


def train(train_data_path, test_data_path,
          bast_weight_save_path,
          acc_img_save_path,
          loss_img_save_path, label_path):
    # 声明使用全局变量
    global val_loss
    global val_acc
    # 加载数据集

    train_data = load_data(train_data_path)
    test_data = load_data(test_data_path)

    # 转换数据集
    train_generator = data_generator(train_data, batch_size)
    test_generator = data_generator(test_data, batch_size)

    label_list = [line.strip() for line in open(label_path, 'r', encoding='utf8')]
    class_num = len({idx: label for idx, label in enumerate(label_list)})

    print("class_num: " + str(class_num))

    model = build_bert_model(config_path,bert_model_path,class_num)
    print(model.summary())

    # 编译模型
    model.compile(
        # 稀疏分类交叉熵
        loss='sparse_categorical_crossentropy',
        optimizer=Adam(learning_rate),
        metrics=['accuracy'],
    )

    # 早停
    early_stop = keras.callbacks.EarlyStopping(
        monitor='val_loss',
        # 如果连续3个训练周期验证集损失值没有改善，则提前停止训练
        patience=3,
        verbose=2,
        mode='min'
    )

    # 权重验证:验证集上监测损失值，并保存在验证集上表现最好的模型权重到指定路径
    checkpoint = keras.callbacks.ModelCheckpoint(
        bast_weight_save_path,
        monitor='val_loss',
        verbose=1,
        save_best_only=True,
        mode='min'
    )

    # 开始训练 : 传入训练集生成器、验证集生成器和回调函数
    model.fit_generator(
        train_generator.forfit(),
        steps_per_epoch=len(train_generator),
        epochs=10,
        validation_data=test_generator.forfit(),
        validation_steps=len(test_generator),
        shuffle=True,
        callbacks=[
            early_stop,
            checkpoint,
            LossHistory(train_loss, val_loss, train_acc, val_acc)
        ]
    )


    # 调整测试集合数据
    # 复制 train_loss 的第一个元素到 val_loss 的开头
    val_loss.insert(0, train_loss[0])
    # 将 val_loss 的其他数据向后偏移
    val_loss = val_loss[:-1] + [val_loss[-1]]  # 在末尾添加一个新元素，保持长度不变

    val_acc.insert(0, train_acc[0])
    val_acc = val_acc[:-1] + [val_acc[-1]]

    # 计算验证集应该在的 x 坐标位置
    val_x = np.arange(len(val_loss)) * len(train_generator)  # 每个 epoch 结束时记录 val_loss
    # 例如：len(train_generator)=102，则 val_x = [0, 102, 204, 306, ..., 1020]

    # 绘制损失曲线
    plt.figure(figsize=(20, 7))
    plt.plot(train_loss, label='Training Loss')
    plt.plot(val_x, val_loss, label='Validation Loss')
    plt.xlabel('Iteration (Training) / Epoch (Validation)')
    plt.ylabel('Loss')
    plt.title('Training and Validation Loss')
    plt.legend()
    plt.grid(True)
    plt.savefig(loss_img_save_path)
    plt.show()

    # 绘制准确率曲线
    plt.figure(figsize=(20, 7))
    plt.plot(train_acc, label='Training Accuracy')
    plt.plot(val_x, val_acc, label='Validation Accuracy')
    plt.xlabel('Iteration (Training) / Epoch (Validation)')
    plt.ylabel('Accuracy')
    plt.title('Training and Validation Accuracy')
    plt.legend()
    plt.grid(True)
    plt.savefig(acc_img_save_path)
    plt.show()


    print(val_loss)
    print(val_acc)

# 测试
def test(test_data_path,
         bast_weight_save_path,
         label_path):
    label_list = [line.strip() for line in open(label_path, 'r', encoding='utf8')]
    class_num = len({idx: label for idx, label in enumerate(label_list)})
    # 模型
    model = build_bert_model(config_path, bert_model_path, class_num)
    print(model.summary())

    test_data = load_data(test_data_path)
    # test_generator = data_generator(test_data, batch_size)


    # 编译模型
    model.compile(
        # 稀疏分类交叉熵
        loss='sparse_categorical_crossentropy',
        optimizer=Adam(learning_rate),
        metrics=['accuracy'],
    )

    # 加载在验证集上表现最好的模型权重
    model.load_weights(bast_weight_save_path)

    # 对测试集进行预测
    # 直接处理测试数据
    test_pred = []
    test_true = []
    for text, true_label in test_data:
        # 对每个文本进行编码和预测
        token_ids, segment_ids = tokenizer.encode(text, maxlen=maxlen)
        token_ids = sequence_padding([token_ids])  # 添加batch维度
        segment_ids = sequence_padding([segment_ids])

        # 预测
        p = model.predict([token_ids, segment_ids])
        pred_label = p.argmax(axis=1)[0]  # 取第一个预测结果

        test_pred.append(pred_label)
        test_true.append(true_label)

    print("test_true",test_true)
    print("test_pred",test_pred)

    # 加载标签名称
    target_names = [line.strip() for line in open(label_path, 'r', encoding='utf8')]

    # classification_report函数计算并打印出测试集的分类指标，包括精确率、召回率和F1分数
    print(classification_report(test_true, test_pred, target_names=target_names))

    # 随机选取两条测试数据并输出
    import random
    random_indices = random.sample(range(len(test_data)), 10)  # 随机选2个不重复的索引

    for idx in random_indices:
        # 确保索引在有效范围内
        if 0 <= idx < len(test_data):
            print("\n随机测试样本 {}:".format(idx))
            print("原始数据:", test_data[idx])  # 假设test_data存储的是原始文本数据
            print("预测标签:", target_names[test_pred[idx]])
            print("真实标签:", target_names[test_true[idx]])

def test_nlj():
    test_data_path = parent_dir_3 + "python_nlp/nlj/test.csv"
    model_save_path = parent_dir_3 + "python_nlp/nlj/weight/save/best_model.weights"
    label_path = parent_dir_3 + "python_nlp/nlj/label"
    test(test_data_path, model_save_path, label_path)

def test_nlu():
    train_data_path = parent_dir_3 + "files/dataset/nlp/train.csv"
    test_data_path = parent_dir_3 + "files/dataset/nlp/test.csv"
    model_save_path = parent_dir_3 + "python_nlp/nlu/weight/save/best_model.weights"
    acc_img_save_path = parent_dir_3 + "python_nlp/nlu/weight/save/acc.png"
    loss_img_save_path = parent_dir_3 + "python_nlp/nlu/weight/save/loss.png"
    print(f"loss_img_save_path的绝对路径为：{loss_img_save_path}")
    label_path = parent_dir_3 + "files/dataset/nlp/label"
    test(test_data_path, model_save_path, label_path)

# 继续训练
def train_continue(train_data_path, test_data_path, bast_weight_save_path, label_path):
    # 加载数据集
    train_data = load_data(train_data_path)
    test_data = load_data(test_data_path)

    # 转换数据集
    train_generator = data_generator(train_data, batch_size)
    test_generator = data_generator(test_data, batch_size)

    label_list = [line.strip() for line in open(label_path, 'r', encoding='utf8')]
    class_num = len({idx: label for idx, label in enumerate(label_list)})
    model = build_bert_model(config_path,bert_model_path,class_num)

    # 编译模型
    model.compile(
        # 稀疏分类交叉熵
        loss='sparse_categorical_crossentropy',
        optimizer=Adam(learning_rate),
        metrics=['accuracy'],
    )

    model.load_weights(bast_weight_save_path)
    print(model.summary())

    # 早停
    early_stop = keras.callbacks.EarlyStopping(
        monitor='val_loss',
        # 如果连续3个训练周期验证集损失值没有改善，则提前停止训练
        patience=3,
        verbose=2,
        mode='min'
    )

    # 权重验证:验证集上监测损失值，并保存在验证集上表现最好的模型权重到指定路径
    checkpoint = keras.callbacks.ModelCheckpoint(
        bast_weight_save_path,
        monitor='val_loss',
        verbose=1,
        save_best_only=True,
        mode='min'
    )

    # 开始训练 : 传入训练集生成器、验证集生成器和回调函数
    model.fit_generator(
        train_generator.forfit(),
        steps_per_epoch=len(train_generator),
        epochs=10,
        validation_data=test_generator.forfit(),
        validation_steps=len(test_generator),
        shuffle=True,
        callbacks=[early_stop, checkpoint]
    )




def test2():
    from bert_intent_model import BertIntentModel
    # bim = BertIntentModel()



if __name__ == '__main__':
    # train_nlj()
    # test_nlj()
    #train_continue()
    # test_text("我刚刚填写了健康信息，你帮我看看我的健康状况怎么样？")
    # test_text("我最近胃口不好，你能推荐我两篇关于治疗胃口不好的文章吗？")
    train_nlu()
    pass





