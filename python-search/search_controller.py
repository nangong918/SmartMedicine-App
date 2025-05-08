import flask
from flask_cors import CORS
import tensorflow as tf
from python_nlp.bert.bert_intent_model import BertIntentModel
from python_nlp.ner.acTree.ner import MedicalNerAcTree
from keras.backend.tensorflow_backend import set_session
from gevent import pywsgi

# flask app
# app = flask.Flask(__name__)
# CORS(app)

# controller
search_controller = '/search'
nlp_api = '/nlp'

# Bert model
global model

config = tf.ConfigProto()
config.gpu_options.allow_growth=True
sess = tf.Session(config=config)
graph = tf.get_default_graph()
set_session(sess)

import os
current_dir = os.path.dirname(os.path.abspath(__file__))
path = os.path.dirname(current_dir) + "/"

label_path = path + "python_nlp/nlj/label"
weights_path = path + "python_nlp/nlj/weight/save/best_model.weights"

# bert intent model
BIM = BertIntentModel(label_path, weights_path)
# ner model
# NER = MedicalNerAcTree()

# C:\CodeLearning\smart-medicine\python_nlp\nlj\label

# *
# *planC
# *句子 -> bert - nlj识别是否是自然语言（准确率几乎100 %）
# *句子 -> bert - nlu意图识别模型：（标题检索；询问问题；寒暄）
# *问答分支：
#   *疾病属性问题集合：
#       *定义
#       *病因
#       *预防
#       *临床表现(病症表现)
#       *相关病症
#       *治疗方法
#       *所属科室
#       *传染性
#       *治愈率
#       *禁忌
#       *化验 / 体检方案
#       *治疗时间
#   *症状问诊意图
#       *多个症状进行共同疾病搜索
#    *推荐
#        *推荐内容检索 + post评分排序 + user context vector排序
#   *个人评价
#       *收集用户数据回答（帖子特征 + 用户健康数据 + 医疗预测结果）
#   *App问题
#       *识别出是App问题进入App规则集回答，如果规则集没有数据则回答不知道

if __name__ == '__main__':
    app = flask.Flask(__name__)
    CORS(app)

    @app.route(search_controller + nlp_api,
               methods=["GET","POST"])
    def nlp():
        data = {
            "code": 500,
            "message": "nlj模型处理失败"
        }
        result = None
        BIM.Predict(flask.request.args.get('text'))
        param = flask.request.get_json()
        print(param)

        text = param["text"]
        with graph.as_default():
            set_session(sess)
            result = BIM.Predict(text)

        data["type"] = result
        data["doce"] = 200

        return flask.jsonify(data)

    server = pywsgi.WSGIServer(("0.0.0.0",60001), app)
    print("NLJ服务器正常启动")
    server.serve_forever()