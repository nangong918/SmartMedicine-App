from enum import Enum

class NlpResultEnum(Enum):
    NONE = (-1, "无结果、Error，异常等")
    NOT_NL = (0, "非自然语言")
    # 要求python这边识别意图之后用TF-IDF识别不同的寒暄类型然后返回语句放在message中返回给前端
    GREETING = (1, "寒暄")
    SEARCH = (2, "搜索意图（问题中识别为其他的时候）")
    RECOMMEND = (3, "问题意图:推荐请求")
    PERSONAL_EVALUATION = (4, "问题意图:个人评价请求")
    APP_QUESTION = (5, "问题意图:App问题")
    SYMPTOM_SEARCH_QUESTION = (6, "问题意图:症状问诊:多个症状进行共同疾病搜索")
    DISEASE_DEFINITION = (7, "问题意图:疾病问诊:定义")
    DISEASE_CAUSE = (8, "问题意图:疾病问诊:病因")
    DISEASE_PREVENTION = (9, "问题意图:疾病问诊:预防")
    DISEASE_SYMPTOM = (10, "问题意图:疾病问诊:临床表现(病症表现)")
    DISEASE_RELATED_SYMPTOM = (11, "问题意图:疾病问诊:相关病症")
    DISEASE_TREATMENT = (12, "问题意图:疾病问诊:治疗方法")
    DISEASE_DEPARTMENT = (13, "问题意图:疾病问诊:所属科室")
    DISEASE_INFECTION = (14, "问题意图:疾病问诊:传染性")
    DISEASE_CURE_RATE = (15, "问题意图:疾病问诊:治愈率")
    DISEASE_TABOO = (16, "问题意图:疾病问诊:禁忌")
    DISEASE_LABORATORY = (17, "问题意图:疾病问诊:化验/体检方案")
    DISEASE_TREATMENT_TIME = (18, "问题意图:疾病问诊:治疗时间")

def create_response():
    response = {}
    for enum in NlpResultEnum:
        response[enum.name] = enum.value[0]  # 将枚举名称及其代码添加到响应中
    return response

# 示例：生成响应体并打印
if __name__ == "__main__":
    response_body = create_response()
    print(response_body)

## 初步方案：取消：传染性，治愈率，治疗时间，化验/体检方案；合并[临床表现(病症表现) + 症状问诊，相关病症]
## 详细操作：
# ,传染性,7 -> ,其他,12
# ,治愈率,8 -> ,其他,12
# ,治疗时间,11 -> ,其他,12
# ,化验/体检方案,10 -> ,其他,12
# ,症状问诊,16 -> ,临床表现(病症表现),3
# ,相关病症,4 -> ,临床表现(病症表现),3

# ,所属科室,6 -> ,所属科室,4
# ,禁忌,9 -> ,禁忌,5
# ,其他,12 -> ,其他,6
# ,推荐,13 -> ,推荐,7
# ,个人评价,14 -> ,个人评价,8
# ,App问题,15 -> ,App问题,9
# ,非自然语言,17 -> ,非自然语言,10