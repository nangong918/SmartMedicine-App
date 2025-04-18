import pandas as pd
import time
import requests
from getData import getHeaders
from getData import article_save_name


def get_image(src, name):
    name = name.replace('/', '')
    url = src
    r = requests.get(url, headers=getHeaders())

    with open(f'图片/{name}.png', 'wb') as f:
        f.write(r.content)



def download():
    df = pd.read_csv(article_save_name)  # 读取的文件夹
    print(df)
    links = df['图片'].tolist()
    names = df['标题'].tolist()
    for link, name in zip(links, names):
        if  len(link) > 5:
            time.sleep(3)
            get_image(link, name)
            print(name)



if __name__ == '__main__':
    download()