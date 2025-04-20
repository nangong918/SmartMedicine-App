
def open_entity_file(path):
    import csv
    entities_name = []
    with open(path, 'r', encoding='utf-8') as file:
        reader = csv.reader(file)
        next(reader)  # 跳过第一行
        for row in reader:
            entities_name.append(row[0])
    return entities_name


limit_distance = 4


# 计算实体之间的关系路径长度
def calculate_path_length(graph, start_entity, end_entity):
    if start_entity == end_entity:
        return 0
    query = f"MATCH path=shortestPath((start)-[*]-(end)) WHERE start.name='{start_entity}' AND end.name='{end_entity}' RETURN length(path) AS path_length"
    result = graph.run(query).evaluate()
    # 没有路径的情况无穷大
    return result if result is not None else float('inf')


def calculate_with_all(entity, match_entity_list):
    result_list = []
    for match_entity in match_entity_list:
        path_length = calculate_path_length(entity, match_entity)
        result_list.append((match_entity, path_length))

    result_list.sort(key=lambda x: x[1])  # 按长度从小到大排序

    return result_list

from typing import Tuple,List
def calculate_with_all_limitDistance(graph, entity, match_entity_list, limit_distance: int)\
        ->List[Tuple]:
    result_list = []
    if limit_distance <= 0:
        result_list.append((entity,0))
        return result_list
    else:
        for match_entity in match_entity_list:
            path_length = calculate_path_length(graph=graph,start_entity=entity, end_entity=match_entity)
            if path_length <= limit_distance:
                result_list.append((match_entity, path_length))
        result_list.sort(key=lambda x: x[1])  # 按长度从小到大排序

    return result_list



if __name__ == '__main__':
    from py2neo import Graph
    graph_database = Graph("bolt://localhost:7687", auth=("neo4j", "12345678"))
    entities_name = open_entity_file('../entities.csv')
    result_list = calculate_with_all_limitDistance(graph_database,'胃癌',entities_name,5)
    for result in result_list:
        print(f"实体：{result[0]}，路径长度：{result[1]}")
    pass



















