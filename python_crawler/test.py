from getData import getHeaders
import time
from selenium.webdriver.chrome.options import Options
from selenium import webdriver
from lxml import etree
import requests
import pandas as pd
import re
import json

#网页结构测试

data = {
    '标题': [],
    '时间': [],
    '内容': [],
    '图片': [],
    '链接': [],
}

from getData import headers as Headers

headers = Headers

def test_data():
    # 创建一个Chrome浏览器实例
    options = Options()
    options.add_experimental_option("debuggerAddress", "127.0.0.1:9222")
    driver = webdriver.Chrome(options=options)

    # 最多等待数据10秒
    driver.implicitly_wait(10)

    # 获取网页源代码
    page = driver.page_source
    tree = etree.HTML(page)

    al = tree.xpath('.//div[@class="profile-article-card-wrapper"]')

    urls = []
    for b in al:
        link = b.xpath('.//a[@class="title"]/@href')[0]
        urls.append(link)

    print("urls",urls)
    url = urls[0]

    time.sleep(2)

    # r = requests.get(url=url, headers=getHeaders())
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
    print(title, img)
    print(text)

def test_url():
    # 创建一个Chrome浏览器实例
    options = Options()
    options.add_experimental_option("debuggerAddress", "127.0.0.1:9222")
    driver = webdriver.Chrome(options=options)

    # 最多等待数据10秒
    driver.implicitly_wait(10)

    # 获取网页源代码
    page = driver.page_source
    tree = etree.HTML(page)

    al = tree.xpath('.//div[@class="profile-article-card-wrapper"]')

    urls = []
    for b in al:
        link = b.xpath('.//a[@class="title"]/@href')[0]
        urls.append(link)

    print("urls", urls)

    url = urls[0]

    time.sleep(2)

    # r = requests.get(url=url, headers=getHeaders())
    r = requests.get(url=url, headers=headers)

    from getData import get_text

    try:
        get_text(url)
    except:
        time.sleep(10)
        get_text(url)

def test_specialContent():
    # 操作已经打开的浏览器
    # 创建一个Chrome浏览器实例
    options = Options()
    options.add_experimental_option("debuggerAddress", "127.0.0.1:9222")
    driver = webdriver.Chrome(options=options)
    # 最多等待数据10秒
    driver.implicitly_wait(10)

    # 获取网页源代码
    page = driver.page_source
    tree = etree.HTML(page)

    al = tree.xpath('.//.//div[@class="profile-article-card-wrapper"]')

    titles = []
    links = []

    for a in al:
        link = a.xpath('.//a[@class="title"]/@href')[0]
        links.append(link)
        title1 = a.xpath('.//a[@class="title"]/@aria-label')[0]
        titles.append(title1)

    urls = []
    Titles = []
    from getData import specialContent_tool
    spt = specialContent_tool()
    specialNums = spt.get_specialNum(titles)

    for index in specialNums:
        if index < len(links):
            urls.append(links[index])
            Titles.append(titles[index])
    print(urls)
    print(Titles)

def test():
    pass

if __name__ == '__main__':

    #test_data()

    test_specialContent()

    #test()

    pass






