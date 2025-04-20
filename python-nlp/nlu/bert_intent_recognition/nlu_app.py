import flask
from flask_cors import CORS
from keras.backend.tensorflow_backend import set_session
from bert_intent_model import BertIntentModel
import tensorflow as tf
from gevent import pywsgi




global graph,model,sess




config = tf.ConfigProto()
config.gpu_options.allow_growth=True
sess = tf.Session(config=config)
graph = tf.get_default_graph()
set_session(sess)



BIM = BertIntentModel()


if __name__ == '__main__':
    app = flask.Flask(__name__)
    CORS(app)

    @app.route("/service/api/bert_intent_recognize",methods=["GET","POST"])
    def bert_intent_recognize():
        data = {"success": 0}
        result = None

        param = flask.request.get_json()
        print(param)

        text = param["text"]
        with graph.as_default():
            set_session(sess)
            result = BIM.Predict(text)

        data["data"] = result
        data["success"] = 1

        return flask.jsonify(data)

    server = pywsgi.WSGIServer(("0.0.0.0",60062), app)
    print("NLU服务器正常启动")
    server.serve_forever()

