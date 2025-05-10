from getData import getData_continuous,geturl_specialContent,getData_specialContent
from formatSave import download_and_FormatData

def crawl_All():
    getData_continuous()
    download_and_FormatData()

def crawl_specialContent():
    getData_specialContent()
    download_and_FormatData()

if __name__ == '__main__':
    # 全部爬取
    #crawl_All()
    # 特殊疾病爬取
    crawl_specialContent()
    pass