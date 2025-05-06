import pickle
import os
import numpy as np



class CLFModel(object):
    def __init__(self, model_save_path):
        self.model_save_path = model_save_path
        # id字典：将类别ID映射到类别标签
        self.id2label = pickle.load(open(os.path.join(self.model_save_path,'id2label.pkl'),'rb'))
        # 向量化器：将输入文本转换为特征向量
        self.vec = pickle.load(open(os.path.join(self.model_save_path,'vec.pkl'),'rb'))
        # 逻辑回归分类器
        self.LR_clf = pickle.load(open(os.path.join(self.model_save_path,'LR.pkl'),'rb'))
        # 梯度提升决策树分类器
        self.gbdt_clf = pickle.load(open(os.path.join(self.model_save_path,'gbdt.pkl'),'rb'))

    def predict(self,text):
        # 文本分割
        text = ' '.join(list(text.lower()))
        # 转换为特征向量   稀疏矩阵特征向量CSR
        text = self.vec.transform([text])
        # 对特征向量进行分类概率预测
        proba1 = self.LR_clf.predict_proba(text)
        proba2 = self.gbdt_clf.predict_proba(text)
        # 根据两个分类器的预测概率求平均
        label = np.argmax((proba1 + proba2) / 2, axis=1)
        return self.id2label.get(label[0])



if __name__ == '__main__':
    model = CLFModel('./model_file/')
    text='月经不调怎么治疗'
    label = model.predict(text)
    print(label)



