import json

# 原始的 JSON 数组
with open('diseases.json', 'r', encoding='utf-8') as file:
    json_array = json.load(file)

# 遍历数组，筛选符合条件的病症
filtered_array = [item for item in json_array if len(item) <= 4]

# 保存覆盖原始的 JSON 数组，添加换行格式
with open('data.json', 'w', encoding='utf-8') as file:
    json.dump(filtered_array, file, ensure_ascii=False, indent=4)