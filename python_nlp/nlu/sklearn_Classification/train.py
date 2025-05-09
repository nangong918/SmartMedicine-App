import numpy as np
import os
import pickle
import random
from sklearn.linear_model import LogisticRegression
from sklearn.ensemble import GradientBoostingClassifier
from sklearn.feature_extraction.text import CountVectorizer, TfidfVectorizer
from sklearn.metrics import classification_report, confusion_matrix
from sklearn.model_selection import train_test_split








seed = 222
random.seed(seed)
np.random.seed(seed)
label_data_path = './data/intent_recog_data.txt'
model_save_path = './model_file_save'



def load_data(data_path):
    X,y = [],[]
    with open(data_path,'r',encoding='utf8') as f:
        for line in f.readlines():
            text,label = line.strip().split(',')
            text = ' '.join(list(text.lower()))
            X.append(text)
            y.append(label)

    index = np.arange(len(X))
    np.random.shuffle(index)
    X = [X[i] for i in index]
    y = [y[i] for i in index]
    return X,y


def run(data_path, model_save_path):
    # 加载数据
    X, y = load_data(data_path)

    # 将标签进行编码为数字
    # 标签去重
    label_set = sorted(list(set(y)))
    # 标签的Id字典
    label2id = {label: idx for idx, label in enumerate(label_set)}
    # id的标签字典
    id2label = {idx: label for label, idx in label2id.items()}
    # 全部的id
    y = [label2id[i] for i in y]

    # 排序+提取目标名称和标签值
    # 排序label2id.items()，    lambda kv:kv[1] ：匿名函数，按照kv中的第二个排序（id）  reverse=False升序
    label_names = sorted(label2id.items(), key = lambda kv:kv[1], reverse=False)
    # 取出标签和id
    target_names = [i[0] for i in label_names]
    labels = [i[1] for i in label_names]

    # 划分训练集和测试集
    train_X, text_X, train_y, text_y = train_test_split(X, y, test_size=0.15, random_state=42)

    # 特征提取
    '''
    将文本转换为稀疏向量，其中每个维度表示一个词语在文本中的重要性
    词语组合的范围:(1, 3) 表示既考虑单个词语，也考虑包含2个或3个连续词语的组合
    min_df=0: 指定词语的最小文档频率阈值。设置为 0 表示不排除任何词语，即考虑所有词语
    max_df=0.9: 指定词语的最大文档频率阈值。设置为 0.9 表示排除出现在超过 90% 文档中的词语，即排除高频词语
    analyzer='char': 指定分析器的类型。这里设置为 'char' 表示将文本按字符级别进行分析，而不是按词语进行分析
    use_idf=1: 是否使用 IDF 权重。设置为 1 表示使用 IDF 权重
    smooth_idf=1: 是否对 IDF 进行平滑处理。设置为 1 表示进行平滑处理
    sublinear_tf=1: 是否对词频进行子线性缩放。设置为 1 表示进行子线性缩放，可以降低高词频的影响
    '''
    vec = TfidfVectorizer(ngram_range=(1, 3), min_df=0, max_df=0.9, analyzer='char', use_idf=1, smooth_idf=1, sublinear_tf=1)
    train_X = vec.fit_transform(train_X)
    text_X = vec.transform(text_X)

    # 训练逻辑回归分类器
    '''
    进行分类任务
    C=8: 正则化强度的倒数，控制模型的正则化程度。较小的 C 值表示更强的正则化。
    dual=False: 是否使用对偶形式的解法。在大多数情况下，当样本数量（n_samples）大于特征数量（n_features）时，对偶形式的解法效果更好。这里设置为 False，使用原始形式的解法。
    n_jobs=4: 并行运算的任务数。这里设置为 4，表示使用 4 个处理器核心进行并行计算。
    max_iter=400: 最大迭代次数。算法在达到最大迭代次数之前会尽可能收敛。这里设置为 400。
    multi_class='ovr': 多类别分类的策略。'ovr' 表示采用一对多（One-vs-Rest）的方法进行多类别分类。
    random_state=122: 随机数种子。通过指定相同的随机数种子，可以确保每次运行时得到相同的结果。
    '''
    LR = LogisticRegression(C=8, dual=False, n_jobs=4, max_iter=400, multi_class='ovr', random_state=122)
    LR.fit(train_X, train_y)
    pred = LR.predict(text_X)
    print(classification_report(text_y, pred, target_names=target_names))
    print(confusion_matrix(text_y, pred, labels=labels))

    # 训练梯度提升决策树分类器
    '''
    n_estimators=450: 弱学习器（决策树）的数量。这里设置为 450，表示将训练 450 个决策树。
    learning_rate=0.01: 学习率，控制每个决策树的贡献程度。较小的学习率可以使模型更加稳定，但可能需要更多的决策树。这里设置为 0.01。
    max_depth=8: 决策树的最大深度。限制决策树的深度可以防止过拟合。这里设置为 8。 
    random_state=24: 随机数种子。通过指定相同的随机数种子，可以确保每次运行时得到相同的结果。
    '''
    gbdt = GradientBoostingClassifier(n_estimators=450, learning_rate=1e-2, max_depth=8, random_state=24)
    gbdt.fit(train_X, train_y)
    pred = gbdt.predict(text_X)
    print(classification_report(text_y, pred, target_names=target_names))
    print(confusion_matrix(text_y, pred, labels=labels))

    # 模型融合
    pred_prob1 = LR.predict_proba(text_X)
    pred_prob2 = gbdt.predict_proba(text_X)
    pred = np.argmax((pred_prob1 + pred_prob2) / 2, axis=1)
    print(classification_report(text_y, pred, target_names=target_names))
    print(confusion_matrix(text_y, pred, labels=labels))

    # 保存模型和其他必要的文件
    pickle.dump(id2label, open(os.path.join(model_save_path, 'id2label.pkl'), 'wb'))
    pickle.dump(vec, open(os.path.join(model_save_path, 'vec.pkl'), 'wb'))
    pickle.dump(LR, open(os.path.join(model_save_path, 'LR.pkl'), 'wb'))
    pickle.dump(gbdt, open(os.path.join(model_save_path, 'gbdt.pkl'), 'wb'))




def test():
    X, y = load_data(label_data_path)

    # 标签去重
    label_set = sorted(list(set(y)))
    # 标签的Id字典
    label2id = {label: idx for idx, label in enumerate(label_set)}
    # id的标签字典
    id2label = {idx: label for label, idx in label2id.items()}
    # 全部的id
    y = [label2id[i] for i in y]

    # 排序+提取目标名称和标签值
    # 排序label2id.items()，    lambda kv:kv[1] ：匿名函数，按照kv中的第二个排序（id）  reverse=False升序
    label_names = sorted(label2id.items(), key = lambda kv:kv[1], reverse=False)
    # 取出标签和id
    target_names = [i[0] for i in label_names]
    labels = [i[1] for i in label_names]






if __name__ == '__main__':
    #test()
    run(label_data_path,model_save_path)
    pass


















