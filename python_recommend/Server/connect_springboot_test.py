import flask
from flask_cors import CORS
from gevent import pywsgi
import tensorflow as tf


app = flask.Flask(__name__)
CORS(app)

h5_path = 'D:\\pythonProject\\NeuMF Recommend\\NeuMF\\h5\\neumf_model.h5'

# 加载模型
neumf_model = tf.keras.models.load_model(h5_path)
from NeuMF.single_user_recommend import get_user_recommend_topK_article_app

@app.route("/recommend", methods=["POST","GET"])
def NeuMF_server():
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
    topK_recommendations = get_user_recommend_topK_article_app(user_id,topK,neumf_model)
    data["list"] = topK_recommendations

    return flask.jsonify(data)



if __name__ == '__main__':
    server = pywsgi.WSGIServer(("0.0.0.0", 30001), app)
    print("NeuMF服务器正常启动")
    server.serve_forever()