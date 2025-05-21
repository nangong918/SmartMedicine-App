import csv
import pymysql


# MySQL数据库连接配置
host = 'localhost'
port = 3306
user = 'root'
password = '123456'
database = 'medical_articles'


# 创建MySQL连接
conn = pymysql.connect(host=host, port=port, user=user, password=password, database=database)


def execute_query(sql):
    cursor = conn.cursor()
    cursor.execute(sql)
    result = cursor.fetchall()
    cursor.close()
    return result


def export_to_csv(filename):
    sql = "SELECT score, user_id, article_id FROM user_article_evaluate"
    result = execute_query(sql)

    with open(filename, 'w', newline='', encoding='utf-8-sig') as csvfile:
        writer = csv.writer(csvfile)
        writer.writerow(['score', 'user_id', 'article_id'])  # 写入表头

        for row in result:
            writer.writerow(row)  # 写入除了id的后3列数据


if __name__ == '__main__':
    filename = 'user_article_evaluate.csv'
    export_to_csv(filename)



