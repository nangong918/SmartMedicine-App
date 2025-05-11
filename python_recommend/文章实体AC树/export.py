from AcTree import titleEntity_tool
import csv
import json
import ahocorasick

tool = titleEntity_tool('特殊内容字典.json')

# 导出entity

def export_entities():
    titles = []
    with open('../titles/titles.csv', 'r', encoding='utf-8') as file:
        reader = csv.reader(file)
        for row in reader:
            titles.append(row[0])

    entities = []
    for title in titles:
        matches = tool.get_titleEntity(title)
        entities.extend(matches)

    entity_counts = {}
    for entity in entities:
        if entity in entity_counts:
            entity_counts[entity] += 1
        else:
            entity_counts[entity] = 1

    with open('entities.csv', 'w', newline='', encoding='utf-8') as file:
        writer = csv.writer(file)
        writer.writerow(['entity', 'number'])  # Write header
        for entity, count in entity_counts.items():
            writer.writerow([entity, count])


# 从高到底计算entity的数量
def count_entities():
    titles = []
    with open('../titles/titles.csv', 'r', encoding='utf-8') as file:
        reader = csv.reader(file)
        for row in reader:
            titles.append(row[0])

    entities = []
    for title in titles:
        matches = tool.get_titleEntity(title)
        entities.extend(matches)


    entity_counts = {}
    for entity in entities:
        if entity in entity_counts:
            entity_counts[entity] += 1
        else:
            entity_counts[entity] = 1

    sorted_counts = sorted(entity_counts.items(), key=lambda x: x[1], reverse=True)

    for entity, count in sorted_counts:
        print(f"实体: {entity}，数量: {count}")


# 切换字符变为id
def entities_change(entities_name, entities_id, entities):
    entities_change = []

    for entity in entities:
        if entity in entities_name:
            index = entities_name.index(entity)
            entity_id = entities_id[index]
            entities_change.append(entity_id)

    return entities_change


# 导出文章和它的entity （titles.csv改为titles+id）
def export_title_entity():
    titles = []
    ids = []
    with open('../titles/titles.csv', 'r', encoding='utf-8') as file:
        reader = csv.reader(file)
        for row in reader:
            titles.append(row[0])
            ids.append(row[1])

    entities_name = []
    entities_id = []
    with open('./export_entities.csv', 'r', encoding='utf-8') as file:
        reader = csv.reader(file)
        for row in reader:
            entities_name.append(row[0])
            entities_id.append(row[1])

    title_entity_id_list = []
    i = 0
    for title in titles:
        entities = tool.get_titleEntity(title)
        entities_change_list = entities_change(entities_name,entities_id,entities)

        if len(entities_change_list) > 0:
            for ecl in entities_change_list:
                title_entity_tuple = (ids[i],ecl)
                title_entity_id_list.append(title_entity_tuple)
        i += 1

    #print(title_entity_id_list)

    with open('title_entity_id.csv', 'w', newline='', encoding='utf-8') as file:
        writer = csv.writer(file)
        writer.writerow(['article_id', 'entity_id'])  # Write header
        for article_id,entity_id in title_entity_id_list:
            writer.writerow([article_id, entity_id])



if __name__ == '__main__':

    #export_entities()

    #count_entities()

    export_title_entity()

    pass