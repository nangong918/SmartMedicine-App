from bert4keras.tokenizers import Tokenizer
from nlu.bert_intent_recognition.bert_model import build_bert_model
import os


class BertIntentModel(object):
    def __init__(self):
        super(BertIntentModel, self).__init__()
        self.current_dir = os.path.dirname(os.path.abspath(__file__))  # 获取当前脚本文件的绝对路径
        self.dict_path = os.path.join(self.current_dir, 'Bert\\vocab.txt')
        self.config_path = os.path.join(self.current_dir, 'Bert\\bert_config.json')
        self.model_path = os.path.join(self.current_dir, 'Bert\\bert_model.ckpt')
        self.label_path = os.path.join(self.current_dir, 'data\\new\\label')
        self.model_weights_path = os.path.join(self.current_dir, 'weights_save\\best_model.weights')

        # 打开 'label' 文件，并逐行读取文件内容
        self.label_list = [line.strip() for line in open(self.label_path, 'r', encoding='utf8')]
        # 按照序号将label做成字典类型
        self.id2label = {idx: label for idx, label in enumerate(self.label_list)}

        # 词表用Token转化为Bert可接收的类型
        self.tokenizer = Tokenizer(self.dict_path)
        self.model = build_bert_model(self.config_path, self.model_path, len(self.id2label))
        #self.model.load_weights('./weights/best_model.weights')
        self.model.load_weights(self.model_weights_path)

    def Predict(self, text):
        # 使用token对输入文本进行编码
        token_ids, segment_ids = self.tokenizer.encode(text, maxlen=60)
        proba = self.model.predict([[token_ids], [segment_ids]])
        # 创建一个字典:   标签与预测概率 proba[0] 一一对应地组合起来
        result = {l: p for l, p in zip(self.label_list, proba[0])}
        # 按照概率值从高到低进行排序     key=lambda kv: kv[1]:以第二个元素（概率排序）   reverse=True: 表示按降序排列
        result = sorted(result.items(), key=lambda kv: kv[1], reverse=True)
        # 取出概率最高的标签和对应的置信度（概率值）
        name, confidence = result[0]
        return {"name": name, "confidence": float(confidence)}





if __name__ == '__main__':
    BIM = BertIntentModel()
    print(len(BIM.id2label))
    r = BIM.Predict("淋球菌性尿道炎的症状")
    print(r)