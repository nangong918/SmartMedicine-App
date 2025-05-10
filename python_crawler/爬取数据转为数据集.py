import csv
import os

# 输出当前目录


current_dir = os.path.dirname(os.path.abspath(__file__))
project_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

# 设置爬取数据目录
crawler_data_dir = os.path.join(project_dir, "爬取数据")

print(crawler_data_dir)

# 输出的文件名
output_file = os.path.join(current_dir, "train.csv")

# 递归查询文件
def read_titles_from_dir(directory, titles):
    # 列出该目录下的所有文件和文件夹
    for item in os.listdir(directory):
        item_path = os.path.join(directory, item)

        # 如果是文件且是title.txt，读取内容
        if os.path.isfile(item_path) and item == 'title.txt':
            with open(item_path, 'r', encoding='utf-8') as title_file:
                content = title_file.read()
                titles.append(content)  # 添加内容到列表

        # 如果是文件夹，递归调用
        elif os.path.isdir(item_path):
            read_titles_from_dir(item_path, titles)

# 主程序
try:
    # 存储标题的列表
    titles_list = []
    # 开始递归查找
    read_titles_from_dir(crawler_data_dir, titles_list)

    # 将数据写入train.csv
    with open(output_file, 'w', encoding='utf-8', newline='') as csvfile:
        csv_writer = csv.writer(csvfile)
        # 写入表头
        csv_writer.writerow(['text', 'label_class', 'label'])
        # 写入数据行
        for title in titles_list:
            csv_writer.writerow([title, 'search', 2])  # 固定label_class为'search'，label为2

    print(f"所有数据已成功写入 {output_file}。")
except Exception as e:
    print(f"发生错误: {e}")