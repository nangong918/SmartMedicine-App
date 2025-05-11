import flask
from flask_cors import CORS
import tensorflow as tf
from python_nlp.bert.bert_intent_model import BertIntentModel
# from python_nlp.ner.acTree.ner import MedicalNerAcTree
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
import time
if __name__ == '__main__':
    app = flask.Flask(__name__)
    CORS(app)

    @app.route(search_controller + nlp_api, methods=["GET", "POST"])
    def nlp():
        response = {
            "code": 500,
            "message": "",
            "nlj_time": 0.0,
        }
        result = None
        request = flask.request.get_json()
        print(request)

        text = request["text"]

        try:
            with graph.as_default():
                set_session(sess)
                start_time = time.time()  # 记录开始时间
                result = BIM.Predict(text)
                end_time = time.time()  # 记录结束时间
                model_processing_time = end_time - start_time  # 计算处理时间
                print("模型处理时间：{:.2f}s".format(model_processing_time))
                response["nlj_time"] = model_processing_time
        except Exception as e:
            print(e)
            response["message"] = "nlj服务器异常: {}".format(str(e))
            return flask.jsonify(response)

        response["type"] = result
        response["code"] = 200

        return flask.jsonify(response)

    host = "0.0.0.0"
    port = 60001
    server = pywsgi.WSGIServer((host, port), app)
    print(f'NLJ服务器正常启动，访问地址为: http://{host}:{port}')
    server.serve_forever()


## Todo 1.java调用python服务其的demo
##  2.用python给IK导出一份词典
##  3.继续完成bert nlp
##  4.构建特征工程

## 数据集收集:寒暄数据集\搜索/问诊意图数据集\推荐意图数据集\个人评价意图数据集\app功能查询数据集
## 帖子的特征分类(#日常分享 #专业医疗知识 #养生技巧 #医疗新闻 #其他)