import pandas as pd
import time
import requests
from getData import getHeaders
from getData import article_save_name
import json


def saveArticle():
    df = pd.read_csv(article_save_name)  # 读取的文件夹
    print(df)


import os

def download_and_FormatData():
    df = pd.read_csv(article_save_name)  # 读取的文件夹
    print(df)
    links = df['图片'].tolist()
    title = df['标题'].tolist()
    Time = df['时间'].tolist()
    Content = df['内容'].tolist()

    for i, (link, name) in enumerate(zip(links, title)):
        if len(link) > 5:
            time.sleep(3)
            folder_path = f'./FormatData/{i}/'
            os.makedirs(folder_path, exist_ok=True)  # 创建递增的文件夹

            get_image(link, folder_path)  # 保存图片
            save_title(folder_path, name)  # 保存标题
            print(name)

            data = {
                'Title': title[i],
                'Time': Time[i],
                'Content': Content[i]
            }

            with open(f'{folder_path}data.json', 'w', encoding='utf-8') as f:
                json.dump(data, f, ensure_ascii=False, indent=4)

def get_image(src, folder_path):
    url = src
    r = requests.get(url, headers=getHeaders())

    with open(f'{folder_path}0.png', 'wb') as f:
        f.write(r.content)

def save_title(folder_path, name):
    with open(f'{folder_path}title.txt', 'w', encoding='utf-8') as f:
        f.write(name)



if __name__ == '__main__':
    download_and_FormatData()