from nlu.sklearn_Classification.clf_model import CLFModel
import random
from config import *
import requests
import json
from chat_context_records.record_utils import load_user_dialogue_context
from cql_utils import get_cql_by_symptoms



clf_model = CLFModel('./nlu/sklearn_Classification/model_file/')


from url_util import *


def classifier(text):
    """
    判断是否是闲聊意图，以及是什么类型闲聊
    """
    return clf_model.predict(text)

def chat_robot(intent):
    """
    闲聊意图则随机抽取回答
    """
    return random.choice(
                simple_chat_content.get(intent)
            )

def intent_classifier(text):
    '''
    意图识别POST请求，回复格式：
    {
    "data": {
        "confidence": 0.9766830801963806,
        "name": "治疗方法"
    },
    "success": 1
}
    '''
    url = bert_intent_service_url
    data = {"text": text}
    # POST请求头定义
    headers = {'Content-Type': 'application/json;charset=utf8'}
    reponse = requests.post(url, data=json.dumps(data), headers=headers)
    if reponse.status_code == 200:
        reponse = json.loads(reponse.text)
        return reponse['data']
    else:
        return -1

def entity_recognizer(text,intent_type):
    '''
    实体命名识别POST请求，回复格式：
{
    "data": [
        {
            "entities": [
                {
                    "type": "disease",
                    "word": "感冒"
                }
            ],
            "recog_label": "model",
            "string": "我感冒了，该怎么治疗？"
        },
        {
            "entities": [
                {
                    "recog_label": "dict",
                    "type": "disease",
                    "word": "感冒"
                }
            ],
            "string": "我感冒了，该怎么治疗？"
        }
    ],
    "sucess": 1
}
    '''
    url = medical_ner_service_url
    data = {
        "text_list": [text],
        "intent_type": intent_type
    }
    headers = {'Content-Type': 'application/json;charset=utf8'}
    reponse = requests.post(url,data=json.dumps(data),headers=headers)
    if reponse.status_code == 200:
        reponse = json.loads(reponse.text)
        return reponse['data']
    else:
        return -1

def semantic_parser(text,user):
    """
    对文本进行解析
    intent = {"name":str,"confidence":float}
    """
    # 意图识别结果
    intent_result = intent_classifier(text)
    intent_type = intent_result.get("name")  # 结果传递给Ner

    # 实体识别结果
    entity_result = entity_recognizer(text, intent_type)

    if intent_result == -1 or entity_result == -1 or intent_result.get("name") == "其他":
        return semantic_slot.get("unrecognized")
    # 识别出来  ：保留 意图识别分类之后的 填词信息
    slot_info_after_intent = None

    # 实体字典
    slot_values = {}

    if intent_result.get("name") == "病因" :
        # 病因情况：取出所有字典识别的symptom实体就行，不需要model识别实体
        # 根据识别的意图找到相应的模板
        slot_info_after_intent = special_slot.get(intent_result.get("name"))
        # 找出实体类型
        slots = slot_info_after_intent.get("slot_list")
        slot = slots[0]
        slot_values[slot] = []
        for entity_rst in entity_result:
            if 'entities' in entity_rst and entity_rst['entities']:
                for e in entity_rst['entities']:
                    if 'recog_label' in e:
                        slot_values[slot].append(e['word'])
        for k in slot_values.keys():
            slot_values[k] = list(set(slot_values[k]))
    elif intent_result.get("name") == "个人评价":
        # 个人健康评价没任何实体
        slot_info_after_intent = special_slot.get(intent_result.get("name"))
    elif intent_result.get("name") == "推荐" :
        # 推荐的情况：1.取出疾病实体，2.取出要推荐的内容实体，3.取出模型识别实体
        # 根据识别的意图找到相应的模板
        slot_info_after_intent = special_slot.get(intent_result.get("name"))
        # 找出实体类型
        slots = slot_info_after_intent.get("slot_list")
        slot_values = {}
        # 取出要识别的类型：
        for slot in slots:
            slot_values[slot] = []
            for entity_rst in entity_result:
                if 'entities' in entity_rst and entity_rst['entities']:
                    for e in entity_rst['entities']:
                        if slot == e['type']:
                            slot_values[slot].append(e["word"])
        for k in slot_values.keys():
            slot_values[k] = list(set(slot_values[k]))
    else:
        # 根据识别的意图找到相应的模板
        slot_info_after_intent = semantic_slot.get(intent_result.get("name"))
        slots = slot_info_after_intent.get("slot_list")
        # 取出数组元素 : "Disease"
        for slot in slots:
            slot_values[slot] = []
            for entity_info in entity_result:
                for e in entity_info["entities"]:
                    if slot == e['type']:
                        # 实体对象改为了实体列表，将所有的实体加入
                        slot_values[slot].append(e['word'])

    '''
    上下文读取：
        方法1，使用json文件记录上一次的对话实体;
        方法2：将聊天数据存入json数组，用函数读取json数组内容存入逐个存入栈，实现主语逐层记录（问题：识别错误的也记录了）

        作用：AI询问问题的时候，人会回答：是、否，这样的回答并不带有实体，会导致不知道主语是谁
    '''
    last_slot_values = None
    is_null = False
    for k in slot_values.keys():
        if slot_values[k] is None:
            is_null = True
            break
    if is_null :
        last_slot_values = load_user_dialogue_context(user)["slot_values"]
    # 当且仅当：当前的slot_values = None的时候，使用上回的数据
    for k in slot_values.keys():
        if slot_values[k] is None:
            slot_values[k] = last_slot_values.get(k, None)

    slot_info_after_intent["slot_values"] = slot_values

    # 根据意图强度来确认回复策略
    confidence = intent_result.get("confidence")
    if confidence >= intent_threshold["accept"]:
        slot_info_after_intent["intent_strategy"] = "accept"
    elif confidence >= intent_threshold["deny"]:
        slot_info_after_intent["intent_strategy"] = "clarify"
    else:
        slot_info_after_intent["intent_strategy"] = "deny"

    return slot_info_after_intent

# 根据模板得到cql语句
# def get_cql(cql_template,slot_values):
#     cql = []
#     # 列表的情况
#     # **slot_values 将 slot_values 字典中的键值对以关键字参数的形式传递给 format() 方法
#     if isinstance(cql_template, list):
#         for cql_t in cql_template:
#             if isinstance(cql_t, list):
#                 for c in cql_t:
#                     cql.append(c.format(Disease=slot_values))
#             else:
#                 cql.append(cql_t.format(Disease=slot_values))
#     # 非列表的情况
#     else:
#         cql = cql_template.format(Disease=slot_values)
#     print(cql)
#     return cql
import re
def get_cql(cql_template, slot_values):
    cql = []
    # 列表的情况
    if isinstance(cql_template, list):
        for cql_t in cql_template:
            processed_cql = process_cql_statement(cql_t.format(Disease=slot_values))
            cql.append(processed_cql)
    # 非列表的情况
    else:
        processed_cql = process_cql_statement(cql_template.format(Disease=slot_values))
        cql = processed_cql
    print(cql)
    return cql

def process_cql_statement(cql):
    if "'{'Disease" in cql:
        match = re.search(r"'{'Disease': \['(.+?)'\]}'", cql)
        if match:
            chinese_text = match.group(1)
            cql = cql.replace("'{'Disease': ['" + chinese_text + "']}'", "'" + chinese_text + "'")
    return cql




from py2neo import Graph
graph = Graph(
            "bolt://localhost:7687",
            auth=("neo4j", "12345678")
        )

# 图数据库中查询
def neo4j_searcher(cql_list):
    answer = ""
    if isinstance(cql_list, list):
        for cql in cql_list:
            get_data = []
            data = graph.run(cql).data()
            if not data:
                continue
            for d in data:
                d = list(d.values())
                if isinstance(d[0],list):
                    get_data.extend(d[0])
                else:
                    get_data.extend(d)

            data = "、".join([str(i) for i in get_data])
            answer += data+"\n"
    else:
        data = graph.run(cql_list).data()
        if not data:
            return answer
        get_data = []
        for d in data:
            d = list(d.values())
            if isinstance(d[0],list):
                get_data.extend(d[0])
            else:
                get_data.extend(d)
        # 使用"、"连起来
        data = "、".join([str(i) for i in get_data])
        answer += data

    return answer


def get_answer(slot_info_after_intent):


    # 回复模板
    reply_template = slot_info_after_intent.get("reply_template")
    # 询问模板
    ask_template = slot_info_after_intent.get("ask_template")
    # 插槽主体
    slot_values = slot_info_after_intent.get("slot_values")
    # 意图强度
    strategy = slot_info_after_intent.get("intent_strategy")


    slots = slot_info_after_intent.get("slot_list")
    type_ = ''.join(slots[0])
    # 没有问题主语(Error情况)
    if not slot_values and type_ != 'evaluate':
        return slot_info_after_intent

    # 病的定义
    if type_ == 'Disease':
        # cql模板
        cql_template = slot_info_after_intent.get("cql_template")

        # 疾病实体
        entity = None
        if slot_values['Disease']:
            entity = slot_values['Disease'][0]
        else:
            slot_info_after_intent["replay_answer"] = "抱歉，我并不知道这个问题的答案"
            return slot_info_after_intent

        # 回答确信的问题
        if strategy == "accept":
            cql = get_cql(cql_template, entity)
            answer = neo4j_searcher(cql)
            if not answer:
                slot_info_after_intent["replay_answer"] = "抱歉，我并不知道这个问题的答案"
            else:
                # 先把回复模板的疾病替换上去
                pattern = reply_template.format(**slot_values)
                slot_info_after_intent["replay_answer"] = pattern + answer

        # 澄清用户是否问该问题
        elif strategy == "clarify":
            # 从模板中取出问题
            pattern = ask_template.format(**slot_values)
            slot_info_after_intent["replay_answer"] = pattern

            cql = get_cql(cql_template, slot_values)
            answer = neo4j_searcher(cql)
            if not answer:
                slot_info_after_intent["replay_answer"] = "抱歉，我并不知道这个问题的答案"
            else:
                # 等待回答确认的答案之后抽取出"choice_answer"的内容
                pattern = reply_template.format(**slot_values)
                slot_info_after_intent["choice_answer"] = pattern + answer
        elif strategy == "deny":
            slot_info_after_intent["replay_answer"] = slot_info_after_intent.get("deny_response")
    # 问诊的情况
    elif type_ == 'symptom':
        # print("strategy",strategy)
        # if strategy == "accept":
        #     entities_symptom = slot_info_after_intent["slot_values"]['symptom']
        #     if entities_symptom and len(entities_symptom) > 0:
        #         cql = get_cql_by_symptoms(entities_symptom,10)
        #         answer = neo4j_searcher(cql)
        #         print("answer",answer)
        #         if not answer:
        #             slot_info_after_intent["replay_answer"] = "抱歉，我不知道这种病"
        #         else:
        #             # 先把回复模板的疾病替换上去
        #             pattern = reply_template.format(**slot_values)
        #             slot_info_after_intent["replay_answer"] = pattern + answer
        #     else:
        #         slot_info_after_intent["replay_answer"] = "抱歉，有的症状我并不知道是什么意思"
        # elif strategy == "clarify":
        #     # 从模板中取出问题
        #     pattern = ask_template.format(**slot_values)
        #     slot_info_after_intent["replay_answer"] = pattern
        #
        #     entities_symptom = slot_info_after_intent["slot_values"]['symptom']
        #     if entities_symptom and len(entities_symptom) > 0:
        #         cql = get_cql_by_symptoms(entities_symptom,10)
        #         answer = neo4j_searcher(cql)
        #         if not answer:
        #             slot_info_after_intent["choice_answer"] = "抱歉，我并不知道这个问题的答案"
        #         else:
        #             # 等待回答确认的答案之后抽取出"choice_answer"的内容
        #             pattern = reply_template.format(**slot_values)
        #             slot_info_after_intent["choice_answer"] = pattern + answer
        #     else:
        #         slot_info_after_intent["choice_answer"] = "抱歉，有的症状我并不知道是什么意思"
        # elif strategy == "deny":
        #     slot_info_after_intent["replay_answer"] = slot_info_after_intent.get("deny_response")
    # 推荐
        entities_symptom = slot_info_after_intent["slot_values"]['symptom']
        if entities_symptom and len(entities_symptom) > 0:
            cql = get_cql_by_symptoms(entities_symptom, 10)
            answer = neo4j_searcher(cql)
            print("answer", answer)
            if not answer:
                slot_info_after_intent["replay_answer"] = "抱歉，我不知道这种病"
            else:
                # 先把回复模板的疾病替换上去
                pattern = reply_template.format(**slot_values)
                slot_info_after_intent["replay_answer"] = pattern + answer
    elif type_ == 'recommend':
        if strategy == "accept":
            if slot_info_after_intent["slot_values"]['recommend'] and len(slot_info_after_intent["slot_values"]['recommend']) > 0:
                entity = slot_info_after_intent["slot_values"]['recommend']
                my_string = ' '.join(entity)
                pattern = reply_template.format(recommend=my_string)
                slot_info_after_intent["replay_answer"] = pattern
            else:
                slot_info_after_intent["replay_answer"] = "抱歉，我并不知道要推荐您什么"
        elif strategy == "clarify":
            if slot_info_after_intent["slot_values"]['recommend'] and len(slot_info_after_intent["slot_values"]['recommend']) > 0:
                entity = slot_info_after_intent["slot_values"]['recommend']
                my_string = ' '.join(entity)

                # 询问
                slot_info_after_intent["replay_answer"] = ask_template.format(recommend=my_string)
                # 抽取
                slot_info_after_intent["choice_answer"] = reply_template.format(recommend=my_string)
            else:
                # 问题对象都不清楚
                slot_info_after_intent["replay_answer"] = "抱歉，我并不知道要推荐您什么"

        elif strategy == "deny":
            slot_info_after_intent["replay_answer"] = slot_info_after_intent.get("deny_response")
    # 评价
    elif type_ == 'evaluate':
        if strategy == "accept":
            slot_info_after_intent["replay_answer"] = reply_template

        elif strategy == "clarify":
            slot_info_after_intent["replay_answer"] = ask_template
            slot_info_after_intent["choice_answer"] = reply_template

        elif strategy == "deny":
            slot_info_after_intent["replay_answer"] = slot_info_after_intent.get("deny_response")

    return slot_info_after_intent

def medical_robot(text,user):
    """
    如果确定是诊断意图则使用该方法进行诊断问答
    """
    slot_info_after_intent = semantic_parser(text,user)
    #return slot_info_after_intent
    answer = get_answer(slot_info_after_intent)
    return answer

def test1():
    # slot_info = semantic_slot.get("相关病症")
    # slots = slot_info.get("slot_list")
    # for s in slots:
    #     print(s)
    #print(neo4j_searcher("MATCH(p:疾病) WHERE p.name='月经不调' RETURN p.cure_way"))

    text = "有没有关于治疗癌症的推文"
    text2 = "我感头晕，乏力是生了什么病了？"
    text3 = "感冒，发烧，咳嗽，月经不调的定义是什么？"
    text4 = "我最近月经不调，肌肉酸痛，浑身发热，感冒，虚寒，你能推荐我一篇关于治疗月经不调的文章吗？"
    text5 = "可以评价一下我最近的健康状况吗？"
    text6 = "月经不调怎么治疗？"
    #rst = semantic_parser_test(text4,'推荐',0.9)

    # rst = semantic_parser_test(text2, '病因', 0.9)
    # print("rst",rst)
    rst = semantic_parser(text2,'user')
    print("rst", rst)

    # rst2 = semantic_parser_test(text5, '个人评价', 0.5)
    # print("rst",rst2)

    # cql = get_cql_by_symptoms(rst['slot_values']['symptom'],10)
    # answer = neo4j_searcher(cql)

    # if not answer:
    #     print(11111)
    # else:
    #     print(answer)

    a = get_answer(rst)
    print("replay_answer",a["replay_answer"])
    #dict_test()

    #recommend_test()

def test2():
    cql_template = "MATCH(p:疾病) WHERE p.name='胃癌' RETURN p.desc"
    cql = process_cql_statement(cql_template)
    print(cql)

if __name__ == '__main__':
    test2()

    pass