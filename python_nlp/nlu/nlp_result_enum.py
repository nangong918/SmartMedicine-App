from enum import Enum

class NlpResultEnum(Enum):
    NONE = (-1, "无结果、Error，异常等")
    NOT_NL = (0, "非自然语言")
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