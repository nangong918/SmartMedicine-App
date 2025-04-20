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

headers = {
    'Cookie': '__ac_signature=_02B4Z6wo00f01BBkzsQAAIDBc24Or4iLMEwQRMpAAGEz02; ttcid=7b65723fe75545fb877839b93f4564d619; csrftoken=bd6c93fd81e7772389ff49e0e8f1d8cf; ttwid=1%7C7D_k4_Tg9YjdME7OOyu3W8_RHc0AvKf6ghV1YCgisHw%7C1697973447%7C7fa2f0df2420b003ecbb5e431cbb41f0b600aec7a132898e7412a0a9fbdda97c; local_city_cache=%E5%8C%97%E4%BA%AC; s_v_web_id=verify_ls44zve4_skl7ZACQ_MAYP_4LS9_9970_stVZ4Q6ONVsl; _S_DPR=1; _S_IPAD=0; passport_csrf_token=fb98abebbc048612e63d7bb3c3e185f8; passport_csrf_token_default=fb98abebbc048612e63d7bb3c3e185f8; n_mh=EXYlCtoC_XliiRkUy8yXN7WegU5DjrbBR3aL3Rx05Ys; sso_auth_status=18801482c299273286b1c93b053388af; sso_auth_status_ss=18801482c299273286b1c93b053388af; sso_uid_tt=767859106f4a0a567c7c0edd75b960d4; sso_uid_tt_ss=767859106f4a0a567c7c0edd75b960d4; toutiao_sso_user=3a214a5bfe9a45046fccefef41eed05b; toutiao_sso_user_ss=3a214a5bfe9a45046fccefef41eed05b; sid_ucp_sso_v1=1.0.0-KGVjYmQxODdkNGNlMTE0NjJjNjk1MDZlMjU3OTBjNmFjYzE5NTE2NDYKHgjc4pCZj_S2BhCxkvetBhgYIAww3Oe86AU4AkDxBxoCaGwiIDNhMjE0YTViZmU5YTQ1MDQ2ZmNjZWZlZjQxZWVkMDVi; ssid_ucp_sso_v1=1.0.0-KGVjYmQxODdkNGNlMTE0NjJjNjk1MDZlMjU3OTBjNmFjYzE5NTE2NDYKHgjc4pCZj_S2BhCxkvetBhgYIAww3Oe86AU4AkDxBxoCaGwiIDNhMjE0YTViZmU5YTQ1MDQ2ZmNjZWZlZjQxZWVkMDVi; passport_auth_status=9dfd7c93e02c12ab41bdcbeb4899567d%2Cd9883e5f5f5287654ae67c9f832fff2e; passport_auth_status_ss=9dfd7c93e02c12ab41bdcbeb4899567d%2Cd9883e5f5f5287654ae67c9f832fff2e; sid_guard=06db64a34130173b1efce7734951d0a3%7C1706936625%7C5184002%7CWed%2C+03-Apr-2024+05%3A03%3A47+GMT; uid_tt=7bde0da73e5539c8596c78c67969ac63; uid_tt_ss=7bde0da73e5539c8596c78c67969ac63; sid_tt=06db64a34130173b1efce7734951d0a3; sessionid=06db64a34130173b1efce7734951d0a3; sessionid_ss=06db64a34130173b1efce7734951d0a3; sid_ucp_v1=1.0.0-KDRhNmJmY2E4OTdmYTU3MTg5ZWEyNmNkMWJiMzI1YzU3OWIxOGIwODEKGAjc4pCZj_S2BhCxkvetBhgYIAw4AkDxBxoCbGYiIDA2ZGI2NGEzNDEzMDE3M2IxZWZjZTc3MzQ5NTFkMGEz; ssid_ucp_v1=1.0.0-KDRhNmJmY2E4OTdmYTU3MTg5ZWEyNmNkMWJiMzI1YzU3OWIxOGIwODEKGAjc4pCZj_S2BhCxkvetBhgYIAw4AkDxBxoCbGYiIDA2ZGI2NGEzNDEzMDE3M2IxZWZjZTc3MzQ5NTFkMGEz; store-region=cn-bj; store-region-src=uid; _S_WIN_WH=1920_953; odin_tt=be8688562e97268ce4a71bf99085904736848a8717f8c16ddadab125755e532f7c8430fbf65b0c0fbc2604f996651f9e; tt_webid=7292591854275954191; _ga=GA1.1.1405581095.1709174487; _ga_QEHZPBE5HH=GS1.1.1709177699.2.0.1709177699.0.0.0; msToken=zECchOLDTdSkSyvJ-6MQgqIdq-GjiS9F1v4nHw47MTdSIQCbP7zGM4GP7NiLqJoVF_KcuOnkCnwjmPISDObZmkO8mCu1Xx54ei6tuOk=; tt_anti_token=MHEJ0S8l-2903a09f3b5854bb69e7668c9ca032659c8ae3f8bcd78f7b38b3bf8132664c59; tt_scid=cbi8tsGlD0Bz4G9yqW5X2neHJAVKXYSYEkHXv87lsCrXE2l39YNxO.Krfg1QrcSq2e8b'
    ,'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36'
}

# 设置请求头信息:字典包含了HTTP请求的头部信息，包括Cookie和User-Agent

def getHeaders():
    # with open('headers.json', 'r') as file:
    #     headers_data = json.load(file)
    # headers = {
    #     'Cookie': headers_data['Cookie'],
    #     'User-Agent': headers_data['User-Agent']
    # }

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


# 获得特殊字典内容要求下的url
import ahocorasick
class specialContent_tool:
    def __init__(self):
        super(specialContent_tool,self).__init__()
        self.AC_tree = None
        self.ac_tree_init()

    def ac_tree_init(self):
        with open('特殊内容字典.json', 'r', encoding='utf-8') as file:
            json_data = json.load(file)
        self.AC_tree = ahocorasick.Automaton()
        for word in json_data:
            self.AC_tree.add_word(word, word)
        self.AC_tree.make_automaton()

    def get_specialNum(self,titles):
        specialNums = []
        i = 0
        for title in titles:
            matches = [match[1] for match in self.AC_tree.iter(title)]
            if matches:
                specialNums.append(i)
            i += 1
        return specialNums

def geturl_specialContent():
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
    spt = specialContent_tool()
    specialNums = spt.get_specialNum(titles)

    for index in specialNums:
        if index < len(links):
            urls.append(links[index])

    return urls


def getData():
    #for date, link in zip(geturl()[1], geturl()[0]):
    urls = geturl()
    print('urls:',urls)
    #for date, link in zip(urls[1], urls[0]):
    print('getData()')
    for link in urls:
        print('getlink：',link)
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


def getData_continuous():
    urls = geturl()
    if len(urls) > 0:
        print('urls:', urls)
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
                    if True:
                        df = pd.DataFrame(data)
                        df.to_csv(article_save_name, index=False, encoding='utf-8-sig')
                        break
                    df = pd.DataFrame(data)
                    df.to_csv(article_save_name, index=False, encoding='utf-8-sig')
                    continue

        df = pd.DataFrame(data)
        df.to_csv(article_save_name, index=False, encoding='utf-8-sig')
    else:
        pass

def getData_specialContent():
    urls = geturl_specialContent()
    if len(urls) > 0:
        print('urls:', urls)
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
                    if True:
                        df = pd.DataFrame(data)
                        df.to_csv(article_save_name, index=False, encoding='utf-8-sig')
                        break
                    df = pd.DataFrame(data)
                    df.to_csv(article_save_name, index=False, encoding='utf-8-sig')
                    continue

        df = pd.DataFrame(data)
        df.to_csv(article_save_name, index=False, encoding='utf-8-sig')
    else:
        pass


def test():
    A = [1, 2, 5]
    B = ["a", "b", "c", "d", "e", "f"]
    C = []

    for index in A:
        if index < len(B):
            C.append(B[index])

    print(C)
    pass


if __name__ == '__main__':
    getData()
    #test()
    pass