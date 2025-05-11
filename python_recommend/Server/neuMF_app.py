'''
    由于后期新注册用户之后需要从新训练模型，所以模型的数据要自动导入数据库
    从数据库中导出csv文件，csv文件喂给模型。模型训练之后重启flask

    1.修改用户评价文章方式：全局U-I矩阵需要每个用户看过每个文章，不然不会被推荐(√)
    2.问题：如果新加入文章，没有人看过，就不会有推荐数据（未被观看过的文章通过知识图谱实体推荐给用户）
    3.写一个根据数据库导出csv文件的函数(√)
    4.写一个新的从csv中读取数据的函数(√)
    5.写一个启动NeuMF后端的flask

    6.计算当前热门文章的函数
    7.用户行为判断：列出行为等级，根据不同的等级，计算给出不同的分数，然后存储数据到数据库
    8.外键设置(√)
'''
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







