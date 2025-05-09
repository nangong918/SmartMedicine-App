import json
import re

# 要读取的文件名列表
file_names = [
    "checks.json",
    "departments.json",
    "diseases.json",
    "drugs.json",
    "foods.json",
    "producers.json",
    "recipes.json",
    "symptoms.json"
]


if __name__ == '__main__':

    # 用于存储所有数据的集合
    all_terms = set()

    # 读取每个文件并提取数据
    for file_name in file_names:
        file_name = 'graph_data/' + file_name
        try:
            with open(file_name, 'r', encoding='utf-8') as f:
                data = json.load(f)
                all_terms.update(data)  # 使用集合避免重复项
        except FileNotFoundError:
            print(f"文件 {file_name} 未找到。")
        except json.JSONDecodeError:
            print(f"文件 {file_name} 解析错误。")

    # 过滤条件：剔除单个字、非汉字字符和空字符串
    filtered_terms = {term for term in all_terms if len(term) > 1 and re.search(r'[\u4e00-\u9fa5]', term)}

    # 导出到 medical.dic 文件
    with open('medical.dic', 'w', encoding='utf-8') as dic_file:
        for term in sorted(filtered_terms):  # 排序后写入
            dic_file.write(term + '\n')

    print("数据已成功导出到 medical.dic。")