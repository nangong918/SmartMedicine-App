from mysql import initialize_database,execute_query
import flask
from flask_cors import CORS
from gevent import pywsgi
import tensorflow as tf
import pandas as pd

app = flask.Flask(__name__)
CORS(app)

h5_path = 'D:\\pythonProject\\NeuMF Recommend\\NeuMF\\h5\\neumf_model.h5'

# 加载模型
neumf_model_for_online = tf.keras.models.load_model(h5_path)

from NeuMF.single_user_recommend import get_user_recommend_topK_article_app

heart_disease_data = pd.read_csv('D:\\pythonProject\\NeuMF Recommend\\MediclePrediction\\dataset\\心脏病.csv')
diabetes_data = pd.read_csv('D:\\pythonProject\\NeuMF Recommend\\MediclePrediction\\dataset\\糖尿病.csv')
model_weights_heart_path = 'D:\\pythonProject\\NeuMF Recommend\\MediclePrediction\\weights\\model_weights_heart.h5'
model_weights_diabetes_path = 'D:\\pythonProject\\NeuMF Recommend\\MediclePrediction\\weights\\model_weights_diabetes.h5'


@app.route("/neumf_online", methods=["POST","GET"])
def NeuMF_for_onLine_server():
    data = {"success": 0}
    params = flask.request.get_json()
    print(params)
    user_id = None
    topK = 10
    if 'user_id' in params:
        user_id = params['user_id']
    else:
        data["error"] = "No message field provided."
        return
    if 'topK' in params:
        topK = params['topK']
    else:
        data["error"] = "No message field provided."
        return
    data = {"success": 1}
    topK_recommendations = get_user_recommend_topK_article_app(user_id,topK,neumf_model_for_online)
    data["list"] = topK_recommendations
    data['name'] = 'neumf'

    return flask.jsonify(data)

import json
from MediclePrediction.apply import apply_for_user
import numpy as np
@app.route("/medical_predict", methods=["POST","GET"])
def medical_predict():
    data = {"flag": False}
    params = flask.request.get_json()
    if 'flag' in params:
        if params['flag'] is False:
            data["error"] = "No message field provided."
            return
    else:
        data["error"] = "No message field provided."
        return
    if 'data' in params:
        params = params['data']
    else:
        data["error"] = "No message field provided."
        return
    user_list = None
    if 'health_array' in params:
        user_list = params['health_array']
    else:
        data["error"] = "No message field provided."
        return
    data = {"flag": True}
    predict_heart = apply_for_user(user_list,heart_disease_data,model_weights_heart_path,'心脏病发作')
    predict_diabetes = apply_for_user(user_list,diabetes_data,model_weights_diabetes_path, '糖尿病发作')

    data["data"] = {
        'heart':np.array(predict_heart).tolist(),
        'diabetes':np.array(predict_diabetes).tolist()
    }

    return flask.jsonify(data)


def open_service():
    server = pywsgi.WSGIServer(("0.0.0.0", 30003), app)
    print('在线NeuMF推荐，医疗预测服务器正常启动')
    server.serve_forever()



if __name__ == '__main__':
    # 启动服务器
    open_service()
