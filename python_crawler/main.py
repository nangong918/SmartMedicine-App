import time

import pandas as pd
import requests
from lxml import etree
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.options import Options
from selenium.webdriver import Keys
from lxml import etree
import re

data = {
    '标题': [],
    '时间': [],
    '内容': [],
    '图片': [],
    '链接': [],
}

from getData import headers as Headers

headers = Headers


def get_text(url, date):
    r = requests.get(url=url, headers=headers)
    tree = etree.HTML(r.text)
    content = tree.xpath('.//div[@class="article-content"]//text()')
    try:
        img = tree.xpath('.//div[@class="article-content"]//img/@src')[0].strip()
    except:
        img = ''
    try:
        title = content[0].strip()
    except:
        title = ''
    try:
        text = ''.join(content[4:]).strip()
    except:
        text = ''
    print(url)
    print(title, date, img)
    print(text)

    data['标题'].append(title)
    data['时间'].append(date)
    data['内容'].append(text)
    data['图片'].append(img)
    data['链接'].append(url)


def geturl():
    # 操作已经打开的浏览器
    options = Options()
    options.add_experimental_option("debuggerAddress", "127.0.0.1:9222")
    driver = webdriver.Chrome(options=options)
    driver.implicitly_wait(10)

    # 获取网页源代码
    page = driver.page_source
    tree = etree.HTML(page)
    al = tree.xpath('.//.//div[@class="profile-article-card-wrapper"]')

    links = []
    dates = []
    for a in al:
        link = a.xpath('.//a[@class="title"]/@href')[0]
        date = a.xpath('.//div[@class="feed-card-footer-time-cmp"]/text()')[0]

        links.append(link)
        dates.append(date)
    print(links)
    print(dates)
    return links, dates


def pageDown():
    # 操作已经打开的浏览器
    options = Options()
    options.add_experimental_option("debuggerAddress", "127.0.0.1:9222")
    driver = webdriver.Chrome(options=options)
    driver.implicitly_wait(10)

    for i in range(50):
        time.sleep(3)
        driver.execute_script("var q=document.documentElement.scrollTop=100000")


def get_image(src, name):
    name = name.replace('/', '')
    url = src
    r = requests.get(url, headers=headers)

    with open(f'图片/{name}.png', 'wb') as f:
        f.write(r.content)


def main():
    for date, link in zip(geturl()[1], geturl()[0]):
        time.sleep(2)
        try:
            get_text(link, date)
        except:
            try:
                time.sleep(10)
                get_text(link, date)
            except:
                hh = input('hh')
                if hh == 'stop':
                    df = pd.DataFrame(data)
                    df.to_csv('今日头条文章2.csv', index=False, encoding='utf-8-sig')
                    break
                df = pd.DataFrame(data)
                df.to_csv('今日头条文章2.csv', index=False, encoding='utf-8-sig')
                continue

    df = pd.DataFrame(data)
    df.to_csv('今日头条文章2.csv', index=False, encoding='utf-8-sig')


def download():
    df = pd.read_csv('今日头条文章2.csv')  # 读取的文件夹
    print(df)
    links = df['图片'].tolist()
    names = df['标题'].tolist()
    for link, name in zip(links, names):
        if  len(link) > 5:
            time.sleep(3)
            get_image(link, name)
            print(name)

# 1.使用前，先在cmd输入命令打开chrome浏览器： "C:\Program Files\Google\Chrome\Application\chrome.exe" --remote-debugging-port=9222 --user-data-dir="C:\selenium\AutomationProfile"
# 2.在浏览器手动打开要爬取的文章列表， pageDown()--main()--download() 顺序运行


if __name__ == '__main__':
    main()
    pass


    # pageDown()  # 自动下滑

    # main()  # 获取数据

    # download()  # 下载图片
