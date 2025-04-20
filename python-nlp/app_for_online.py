import flask
from flask_cors import CORS
from gevent import pywsgi
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

app = flask.Flask(__name__)
CORS(app)

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
@app.route("/service/api/entities_distance_online",methods=["GET","POST"])
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


from chat import *
from R_data_util import *
@app.route("/reply", methods=["POST","GET"])
def get_reply():
    print("接收到信息了:")
    r_data_back = R_dataType(flag=False)
    params = flask.request.get_json()
    if keyword_flag in params and keyword_data in params:
        r_data = R_dataType(flag=params[keyword_flag],data=params[keyword_data])
        message = r_data.get_in_data("text")
        reply = reply_to_user(message)
        print("\n收到信息：",message)
        print("\n回复信息：",reply)
        data = {"reply": reply}
        r_data_back = R_dataType(flag=True,data=data)
    else:
        data = {"error": "No message field provided."}
        r_data_back = R_dataType(flag=False, data=data)

    return flask.jsonify(r_data_back.__dict__())



# LiveServer前端请求逻辑

@app.route("/test", methods=["GET"])
def test():
    # 获取请求参数
    param1 = flask.request.args.get("param1")

    # 构建响应数据
    data = {
        "param1": param1,
    }

    return flask.jsonify(data)


def open_service():
    server = pywsgi.WSGIServer(("0.0.0.0", 30004), app)
    print("DialogAI,在线entities_distance_online服务器正常启动")
    server.serve_forever()

if __name__ == '__main__':
    open_service()