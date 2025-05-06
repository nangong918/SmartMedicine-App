import flask
from flask_cors import CORS
import tensorflow as tf
from python_nlp.bert.bert_intent_model import BertIntentModel
from python_nlp.ner.acTree.ner import MedicalNerAcTree
from keras.backend.tensorflow_backend import set_session

# flask app
app = flask.Flask(__name__)
CORS(app)

# controller
search_controller = '/search'
nlj_api = '/nlj'

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
NER = MedicalNerAcTree()

# C:\CodeLearning\smart-medicine\python_nlp\nlj\label

# @app.route(
#     search_controller + nlj_api,
#     methods=["GET", "POST"])
# def nlj():
#     pass