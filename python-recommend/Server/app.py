'''
任务：做一个完整的推荐系统
    1.NeuMF算法：
        flask服务器获得的list传回前端，前端吧list传给javaSpring服务器
    2.从新导入新的用户数据，训练新的NeuMF模型（√）
    3.计算当前热门文章：（√）
        根据所有用户的实体列表叠加，得到一个前k的实体列表
        前k的实体列表逐个找对应的文章，将文章全部找到，将文章在所有用户中的评分叠加起来。得到当前热门文章
        （思考上述在服务器中怎么进行多线程计算）
    4.用户行为代码（Android）：
        在用户注册之后，调查用户的兴趣偏好
        在注册完成之后也可以自己设置喜好关键词
        用户行为函数计算：
            多次推荐用户的文章没有点开记低分
            用户点开阅读率低的文章记低分
            用户点了不感兴趣或者收藏的文章分别记录低分或高分
    5.基于内容推荐（知识图谱服务器）：
        写一个基于内容推荐的分页，用于进行对实体的在知识图谱中的推荐
    6.用户冷热程度值，根据用户的冷热程度计算当前用neuMF和基于内容推荐的比例（最大1：1）

    7.推荐系统前端的页面刷新与预加载
        JavaSpring的预先加载：推荐列表
        Flask的NeuMF模型预先计算推荐的文章列表
        Flask的知识图谱服务器提前计算相关的文章
        还需要一个临时的NeuMF用户过滤，用于存储已经推荐的。
        JavaSpring和Flask直接联系（√）

        逻辑：登录：向SpringBoot请求（√）
            推荐系统：
            （SpringBoot统计登录情况，需求（例如：计算热门文章，计算NeuMF，计算知识图谱实体），然后告知Flask服务器）
                计算（除开UE矩阵之外的）全体用户的：
                    创建两个线程，1.计算离线用户的推荐栈，2.计算在线用户的推荐栈
                    NeuMF推荐栈（登录情况下，推荐栈10；未登录的情况下推荐栈3）
                    基于知识图谱实体推荐栈：（登录情况下，推荐栈10；未登录的情况下推荐栈3）
                计算当前热门文章：（√）
                    服务器开启之后自动计算当前热门文章，然后存储到列表中。每隔30分钟计算一次

    8.生成初始用户
'''
from mysql import initialize_database,execute_query
import flask
from flask_cors import CORS
from gevent import pywsgi
import tensorflow as tf

# 并发执行代码
# from gevent import monkey
# monkey.patch_all()

app = flask.Flask(__name__)
CORS(app)

h5_path = 'D:\\pythonProject\\NeuMF Recommend\\NeuMF\\h5\\neumf_model.h5'

# 加载模型
neumf_model = tf.keras.models.load_model(h5_path)

from NeuMF.single_user_recommend import get_user_recommend_topK_article_app

@app.route("/neumf", methods=["POST","GET"])
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
    data = {"success": 1}
    topK_recommendations = get_user_recommend_topK_article_app(user_id,topK,neumf_model)
    data["list"] = topK_recommendations
    data['name'] = 'neumf'

    return flask.jsonify(data)


from popular_list_calculate import clear_calculation_article,calculate_main
@app.route("/popular", methods=["POST","GET"])
def PopularArticle_server():
    data = {"success": 0}
    params = flask.request.get_json()
    print(params)
    topK = 10
    topN = 10
    if 'topK' in params:
        topK = params['topK']
    else:
        data["error"] = "No message field provided."
        return
    if 'topN' in params:
        topN = params['topN']
    else:
        data["error"] = "No message field provided."
        return
    data = {"success": 1}
    article_list = calculate_main(topK=topK,topN=topN)
    article_id_list = clear_calculation_article(article_list)
    data["list"] = article_id_list


    return flask.jsonify(data)

def open_service():
    server = pywsgi.WSGIServer(("0.0.0.0", 30001), app)
    print('NeuMF推荐服务器正常启动')
    print('popularArticle服务器正常启动')
    server.serve_forever()



if __name__ == '__main__':
    # 数据库连接
    initialize_database()

    # 启动服务器
    open_service()





