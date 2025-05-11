from py2neo import Graph
from 文章实体AC树.AcTree import titleEntity_tool

# 连接到Neo4j图数据库
graph = Graph(
            "bolt://localhost:7687",
            auth=("neo4j", "12345678")
        )


'''
    一、实体分类：
        疾病，症状，药品，食物
    二、关系查找
        疾病：has_symptom,recommand_drug,acompany_with,cure_department,belongs_to,do_eat,not_eat
        症状：has_symptom
        药品：recommend_drug
        食物：do_eat,not_eat
    三、相似度计算
        Jaccard系数问题：实体关系越多则分母越大，相似度越低
        相似数量越多，分数越高
    四、传入实体，给出实体分数列表
        对用户协同过滤，计算出每个实体的相似度
'''

# 类型检查器
tool_diseases = titleEntity_tool('../diseases.json')
tool_drug = titleEntity_tool('../drugs.json')
tool_foods = titleEntity_tool('../foods.json')
tool_symptoms = titleEntity_tool('../symptoms.json')


# 检查实体类型
def check_entity_type(entity):
    if tool_diseases.get_entity_type(entity):
        return 1
    elif tool_drug.get_entity_type(entity):
        return 2
    elif tool_foods.get_entity_type(entity):
        return 3
    elif tool_symptoms.get_entity_type(entity):
        return 4
    else:
        return 5


def query_disease_relationships():
    # 症状查询疾病
    symptoms_diseases = "MATCH (d:`疾病`)-[:has_symptom]->(s1:`症状` {name:'腹痛'}) RETURN d.name"
    # 疾病查询症状
    diseases_symptoms = "MATCH (d:`疾病` {name:'感冒'})-[:has_symptom]->(s:`症状`) RETURN s.name"
    # 疾病查询药物
    diseases_drugs = "MATCH (d:`疾病` {name:'感冒'})-[:recommand_drug]->(s:`药品`) RETURN s.name"
    # 疾病查询疾病
    diseases_diseases = "MATCH (d:`疾病`)-[:acompany_with]->(s1:`疾病` {name:'感冒'}) RETURN d.name"

    # 执行CQL查询语句
    result = graph.run(
        diseases_drugs
    )

    print(result)

    # # 将查询结果存储在列表中
    # symptom_names = [record['s.name'] for record in result]
    #
    # # 输出完整的查询结果列表
    # print(symptom_names)



def test():
    print(check_entity_type("鸭肉"))
    print(check_entity_type('感冒'))


if __name__ == '__main__':
    query_disease_relationships()
    pass
