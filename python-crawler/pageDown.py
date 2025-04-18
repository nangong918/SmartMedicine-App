from selenium.webdriver.chrome.options import Options
from selenium import webdriver
import time


# 1.使用前，先在cmd输入命令打开chrome浏览器： "C:\Program Files\Google\Chrome\Application\chrome.exe" --remote-debugging-port=9222 --user-data-dir="C:\selenium\AutomationProfile"
# 2.在浏览器手动打开要爬取的文章列表， pageDown()--main()--download() 顺序运行

'''
    "C:\Program Files\Google\Chrome\Application\chrome.exe" 是 Chrome 浏览器的可执行文件路径
    --remote-debugging-port=9222 表示指定远程调试端口为 9222。远程调试端口是 Chrome 浏览器的一个特性，通过该端口可以与浏览器建立调试连接。
    --user-data-dir="C:\selenium\AutomationProfile" 是可选的，它指定了用户数据目录的路径。用户数据目录存储了 Chrome 浏览器的配置和个人数据，通过指定一个自定义的目录，可以避免与默认配置冲突
'''


def pageDown():
    # 操作已经打开的浏览器
    options = Options()
    # 连接到已经打开的Chrome浏览器的调试接口
    options.add_experimental_option("debuggerAddress", "127.0.0.1:9222")
    # 浏览器控制
    driver = webdriver.Chrome(options=options)
    # 最多等待数据10秒
    driver.implicitly_wait(10)

    # 尝试运行50轮次，不行再添加轮次
    for i in range(50):
        # 等待3秒，防止反爬识别
        time.sleep(3)
        # driver.execute_script方法执行JavaScript代码，将网页滚动到一个很大的值（100000），达到向下滚动网页的效果
        driver.execute_script("var q=document.documentElement.scrollTop=100000")



if __name__ == '__main__':
    pageDown()