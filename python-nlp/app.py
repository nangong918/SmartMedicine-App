import flask
from flask_cors import CORS
from keras.backend.tensorflow_backend import set_session
from nlu.bert_intent_recognition.bert_intent_model import BertIntentModel
import tensorflow as tf
from gevent import pywsgi
from ner.AcTree.Ner import MedicalNerAcTree
from py2neo import Graph
from recommend_database.check_entities_distance import open_entity_file,calculate_with_all_limitDistance,limit_distance
import pymysql

# 连接到Neo4j图数据库
graph_database = Graph("bolt://localhost:7687", auth=("neo4j", "12345678"))

# MySQL数据库连接配置
host = 'localhost'
port = 3306
user = 'root'
password = '123456'
database = 'medical_articles'

# 创建MySQL连接
conn = pymysql.connect(host=host, port=port, user=user, password=password, database=database,charset='utf8')

entities_dict = open_entity_file('entities.csv')

global graph,model,sess

config = tf.ConfigProto()
config.gpu_options.allow_growth=True
sess = tf.Session(config=config)
graph = tf.get_default_graph()
set_session(sess)

BIM = BertIntentModel()
NER = MedicalNerAcTree()


app = flask.Flask(__name__)
CORS(app)


@app.route("/service/api/bert_intent_recognize", methods=["GET", "POST"])
def bert_intent_recognize():
    data = {"success": 0}
    result = None

    param = flask.request.get_json()

    text = param["text"]
    with graph.as_default():
        set_session(sess)
        result = BIM.Predict(text)

    data["data"] = result
    print(result)
    data["success"] = 1

    return flask.jsonify(data)



@app.route("/service/api/medical_ner",methods=["GET","POST"])
def medical_ner():
    #定义返回json数据
    data = {"success":0}

    #POST请求处理
    text_list = flask.request.get_json()["text_list"]
    intent_type = flask.request.get_json()["intent_type"]

    #模型预测
    result = [] #存储识别的结果
    result = NER.Predict(text_list, intent_type)

    data["success"] = 1
    data["data"] = result
    print(result)

    return flask.jsonify(data)


from recommend_database.check_user_article import check_articlesId_by_entitiesId,check_user_behavior_set,check_entitiesId_by_entitiesName
from typing import List, Tuple
def sort_entities_by_distance(entities_List: List[List[Tuple]]) -> List[str]:
    entitiesName = []
    for sublist in entities_List:
        sublist.sort(key=lambda x: x[1])  # 按距离从小到大排序
        for entity, _ in sublist:
            entitiesName.append(entity)
    return entitiesName

# 完善：传入user_id,然后通过user_id进行过滤
@app.route("/service/api/entities_distance",methods=["GET","POST"])
def entities_distance():
    #定义返回json数据
    data = {"success":0}

    #POST请求处理
    entityList = flask.request.get_json()["entityList"]
    user_id = flask.request.get_json()["user_id"]
    entities_List = []
    print('开始处理实体')
    for entity in entityList:
        print('当前处理实体：',entity)
        result_list = calculate_with_all_limitDistance(graph=graph_database,entity=entity, match_entity_list=entities_dict, limit_distance=limit_distance)
        entities_List.append(result_list)

    entitiesName = sort_entities_by_distance(entities_List)
    entitiesId = check_entitiesId_by_entitiesName(conn,entitiesName)
    choose_set = check_articlesId_by_entitiesId(conn,entitiesId)
    behavior_set = check_user_behavior_set(conn,user_id)
    recommend_set = choose_set - behavior_set
    recommend_list = list(recommend_set)  # 将set转换为列表
    data['recommend_list'] = recommend_list
    data["success"] = 1

    data['name'] = 'knowledge'

    return flask.jsonify(data)




def open_service():
    server = pywsgi.WSGIServer(("0.0.0.0", 30002), app)
    print("NLU,AcTree,entities_distance服务器正常启动")
    server.serve_forever()


if __name__ == '__main__':
    # 启动服务器
    open_service()

