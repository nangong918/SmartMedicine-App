


def cql_test():
    limit_num = 10

    disease_inquiry = {
        "slot_list": ["symptom"],
        "slot_values": None,
        "cql_match": "MATCH (d:`疾病`)"
    }

    # 根据接收到的症状数量动态生成匹配语句的一部分
    symptom_matches = []
    string_1 = "MATCH (d:`疾病`)-[:has_symptom]->(s"
    string_2 = ":`症状` {name:"
    string_3 = "})"
    # 假设你的症状值存储在一个列表中，命名为symptoms
    symptoms = ["腹痛", "胃痛","鼻子痛"]

    index = 1
    for symptom in symptoms:
        match_1 = "{index}".format(index=index)
        match_2 = "'{symptom}'".format(symptom=symptom)
        symptom_match = string_1 + match_1 + string_2 + match_2 + string_3 + "\n"
        symptom_matches.append(symptom_match)
        index += 1

    cql_match = "".join(symptom_matches)

    cql_match = cql_match + "RETURN d.name LIMIT " + " {limit_num}".format(limit_num=limit_num)

    disease_inquiry["cql_match"] = cql_match

    print(disease_inquiry["cql_match"])



def get_cql_by_symptoms(symptoms,limit_num):
    symptom_matches = []
    string_1 = "MATCH (d:`疾病`)-[:has_symptom]->(s"
    string_2 = ":`症状` {name:"
    string_3 = "})"

    index = 1
    for symptom in symptoms:
        match_1 = "{index}".format(index=index)
        match_2 = "'{symptom}'".format(symptom=symptom)
        symptom_match = string_1 + match_1 + string_2 + match_2 + string_3 + "\n"
        symptom_matches.append(symptom_match)
        index += 1

    cql_match = "".join(symptom_matches)

    cql_match = cql_match + "RETURN d.name LIMIT " + " {limit_num}".format(limit_num=limit_num)
    return cql_match


if __name__ == '__main__':
    #cql_test()

    symptoms = ["腹痛", "胃痛", "鼻子痛","鼻子痛","鼻子痛","鼻子痛","鼻子痛"]
    limit_num = 10
    cql = get_cql_by_symptoms(symptoms,limit_num)
    print(cql)

    pass