import flask
from flask_cors import CORS
from gevent import pywsgi
from Ner import MedicalNerAcTree


#初始化模型
nerAcTree = MedicalNerAcTree()


if __name__ == '__main__':
    # 为当前模块启动flask服务器
    app = flask.Flask(__name__)
    # 启用跨源资源共享
    CORS(app)

    @app.route("/service/api/medical_ner",methods=["GET","POST"])
    def medical_ner():
        #定义返回json数据
        data = {"success":0}

        #POST请求处理
        text_list = flask.request.get_json()["text_list"]
        intent_type = flask.request.get_json()["intent_type"]

        #模型预测
        result = [] #存储识别的结果
        result = nerAcTree.Predict(text_list, intent_type)

        data["success"] = 1
        data["data"] = result

        return flask.jsonify(data)

    #启动服务器，并使用所有可用的网络接口 0.0.0.0
    server = pywsgi.WSGIServer(("0.0.0.0",30002), app)
    print("NER服务器正常启动")
    server.serve_forever()

















