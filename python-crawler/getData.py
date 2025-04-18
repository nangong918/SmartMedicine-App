import time
from selenium.webdriver.chrome.options import Options
from selenium import webdriver
from lxml import etree
import requests
import pandas as pd
import re
import json


# 创建一个空的数据字典data，用于存储爬取的数据
data = {
    '标题': [],
    '时间': [],
    '内容': [],
    '图片': [],
    '链接': [],
}

article_save_name = "文章数据.csv"

# 设置请求头信息:字典包含了HTTP请求的头部信息，包括Cookie和User-Agent

def getHeaders():
    with open('headers.json', 'r') as file:
        headers_data = json.load(file)
    headers = {
        'Cookie': headers_data['Cookie'],
        'User-Agent': headers_data['User-Agent']
    }

    return headers


def get_text(url):
    # 发送HTTP请求获取网页内容    对首页爬取的card URL进行访问
    r = requests.get(url=url, headers=getHeaders())
    # 使用lxml库中的etree模块解析HTML内容
    tree = etree.HTML(r.text)
    # 提取文章内容
    content = tree.xpath('.//div[@class="article-content"]//text()')
    # 时间提取
    date = re.findall('<span>(\d{4}-\d{2}-\d{2}[\s\S]*?)</span>', r.text)[0]

    # 提取第一张文章图片链接
    try:
        img = tree.xpath('.//div[@class="article-content"]//img/@src')[0].strip()
    # 空图片情况
    except:
        img = ''
        return
    # 提取文章标题
    try:
        title = content[0].strip()
    except:
        title = ''
        return
    # 提取文章正文
    try:
        # 没有换行符
        # text = ''.join(content[4:]).strip()
        # 有换行符
        #print("时间：！！！！！！！！！！！！！！！！"+content[1].strip())
        text = '\n'.join(content[4:]).strip()
    except:
        text = ''
        return
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
    # 创建一个Chrome浏览器实例
    options = Options()
    options.add_experimental_option("debuggerAddress", "127.0.0.1:9222")
    driver = webdriver.Chrome(options=options)
    # 最多等待数据10秒
    driver.implicitly_wait(10)

    # 获取网页源代码
    page = driver.page_source
    tree = etree.HTML(page)
    '''
        在HTML文档中查找所有具有class属性值为"profile-article-card-wrapper"的div元素
        .//表示从当前节点开始所有的div    .//.//表示从当前节点开始，所有+任意深度
        通过chrome浏览器F12移动到指定位置找到
        al是所有的文章
    '''
    al = tree.xpath('.//.//div[@class="profile-article-card-wrapper"]')

    links = []
    # dates = []
    for a in al:
        link = a.xpath('.//a[@class="title"]/@href')[0]
        # date = a.xpath('.//div[@class="feed-card-footer-time-cmp"]/text()')[0]

        links.append(link)
        # dates.append(date)
    return links    # , dates


def getData():
    #for date, link in zip(geturl()[1], geturl()[0]):
    urls = geturl()
    #for date, link in zip(urls[1], urls[0]):
    for link in urls:
        # 防止反爬
        time.sleep(2)
        try:
            get_text(link)
        except:
            try:
                time.sleep(10)
                get_text(link)
            except:
                OperationInstructions = input('OperationInstructions')
                if OperationInstructions == 'stop':
                    df = pd.DataFrame(data)
                    df.to_csv(article_save_name, index=False, encoding='utf-8-sig')
                    break
                df = pd.DataFrame(data)
                df.to_csv(article_save_name, index=False, encoding='utf-8-sig')
                continue

    df = pd.DataFrame(data)
    df.to_csv(article_save_name, index=False, encoding='utf-8-sig')


if __name__ == '__main__':
    getData()
