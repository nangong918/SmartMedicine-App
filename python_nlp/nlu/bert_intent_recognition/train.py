from bert4keras.tokenizers import Tokenizer
from bert4keras.snippets import DataGenerator,sequence_padding
import pandas as pd
from bert_model import build_bert_model
from bert4keras.optimizers import Adam
from bert4keras.backend import keras
from sklearn.metrics import classification_report




config_path = 'Bert/bert_config.json'
bert_model_path = 'Bert/bert_model.ckpt'
dict_path = 'Bert/vocab.txt'

# Bert的token转化器
tokenizer = Tokenizer(dict_path)



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
new_class_nums = 15
# 保存路径
bast_model_filepath = './weights_save/best_model.weights'
# 老数据集
train_data_path = './data/train.csv'
test_data_path = './data/test.csv'

# 新数据集
train_data_path_new = "./data/new/train.csv"
test_data_path_new = "./data/new/test.csv"
# 标签
label_path = 'label'
label_path_new = './data/new/label'


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
        val_loss = logs.get('val_loss')
        acc = logs.get('accuracy')
        val_acc = logs.get('val_accuracy')

        if loss is not None:
            self.train_loss.append(loss)
        if val_loss is not None:
            self.val_loss.append(val_loss)
        if acc is not None:
            self.train_acc.append(acc)
        if val_acc is not None:
            self.val_acc.append(val_acc)
# 训练
def train():
    # 加载数据集

    train_data = load_data(train_data_path_new)
    test_data = load_data(test_data_path_new)

    # 转换数据集
    train_generator = data_generator(train_data, batch_size)
    test_generator = data_generator(test_data, batch_size)

    model = build_bert_model(config_path,bert_model_path,new_class_nums)
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
        bast_model_filepath,
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

    # 绘制损失曲线
    plt.figure(figsize=(20, 7))
    plt.plot(train_loss, label='Training Loss')
    plt.plot(val_loss, label='Validation Loss')
    plt.xlabel('Iteration')
    plt.ylabel('Loss')
    plt.title('Training and Validation Loss')
    plt.legend()
    plt.grid(True)
    plt.savefig('loss_curve.png')
    plt.show()

    # 绘制准确率曲线
    plt.figure(figsize=(20, 7))
    plt.plot(train_acc, label='Training Accuracy')
    plt.plot(val_acc, label='Validation Accuracy')
    plt.xlabel('Iteration')
    plt.ylabel('Accuracy')
    plt.title('Training and Validation Accuracy')
    plt.legend()
    plt.grid(True)
    plt.savefig('accuracy_curve.png')
    plt.show()

# 测试
def test():
    # 模型
    model = build_bert_model(config_path, bert_model_path, new_class_nums)
    print(model.summary())

    test_data = load_data(test_data_path_new)
    test_generator = data_generator(test_data, batch_size)

    # 编译模型
    model.compile(
        # 稀疏分类交叉熵
        loss='sparse_categorical_crossentropy',
        optimizer=Adam(learning_rate),
        metrics=['accuracy'],
    )

    # 加载在验证集上表现最好的模型权重
    test_path = './weights/best_model.weights'
    model.load_weights(bast_model_filepath)
    #model.load_weights(bast_model_filepath)

    # 对测试集进行预测
    test_pred = []
    test_true = []
    for x, y in test_generator:
        p = model.predict(x)
        p = p.argmax(axis=1)
        test_pred.extend(p)
        for item in y:
            test_true.append(item[0])

    print("test_true",set(test_true))
    print("test_pred",set(test_pred))

    # 加载标签名称
    target_names = [line.strip() for line in open(label_path_new, 'r', encoding='utf8')]

    # classification_report函数计算并打印出测试集的分类指标，包括精确率、召回率和F1分数
    print(classification_report(test_true, test_pred, target_names=target_names))


# 继续训练
def train_continue():
    # 加载数据集
    train_data = load_data(train_data_path_new)
    test_data = load_data(test_data_path_new)

    # 转换数据集
    train_generator = data_generator(train_data, batch_size)
    test_generator = data_generator(test_data, batch_size)

    model = build_bert_model(config_path,bert_model_path,new_class_nums)

    # 编译模型
    model.compile(
        # 稀疏分类交叉熵
        loss='sparse_categorical_crossentropy',
        optimizer=Adam(learning_rate),
        metrics=['accuracy'],
    )

    model.load_weights(bast_model_filepath)
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
        bast_model_filepath,
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


# 单纯测试
def test_():
    test_data = load_data(test_data_path_new)
    test_generator = data_generator(test_data, batch_size)
    for x ,y in test_generator:
        pass
        #print("x:::",x)
        #print("y:::",y)
    pass



# 文本测试
def test_text(text):
    model = build_bert_model(config_path, bert_model_path, new_class_nums)

    # 编译模型
    model.compile(
        # 稀疏分类交叉熵
        loss='sparse_categorical_crossentropy',
        optimizer=Adam(learning_rate),
        metrics=['accuracy'],
    )

    model.load_weights(bast_model_filepath)

    def single_text_test(Text):
        token_ids, segment_ids = tokenizer.encode(Text, maxlen=maxlen)
        token_ids = sequence_padding([token_ids])
        segment_ids = sequence_padding([segment_ids])
        return [token_ids, segment_ids]

    p = model.predict(single_text_test(text))
    p = p.argmax(axis=1)

    # 加载标签名称
    target_names = [line.strip() for line in open(label_path_new, 'r', encoding='utf8')]
    print(target_names[p[0]])


def test2():
    from bert_intent_model import BertIntentModel
    bim = BertIntentModel()



if __name__ == '__main__':
    train()
    #test()
    #test_()
    #train_continue()
    # test_text("我刚刚填写了健康信息，你帮我看看我的健康状况怎么样？")
    # test_text("我最近胃口不好，你能推荐我两篇关于治疗胃口不好的文章吗？")
    pass





