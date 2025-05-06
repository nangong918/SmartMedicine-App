import pymysql


# MySQL数据库连接配置
host = 'localhost'
port = 3306
user = 'root'
password = '123456'
database = 'medical_articles'


# 创建MySQL连接
conn = pymysql.connect(host=host, port=port, user=user, password=password, database=database,charset='utf8')


def execute_query(conn, sql, params=None):
    cursor = conn.cursor()
    cursor.execute(sql, params)
    result = cursor.fetchall()
    cursor.close()
    return result

def check_user_behavior_set(conn,user_id: int) -> set:
    sql = f"SELECT article_id FROM user_article_evaluate WHERE user_id = {user_id}"
    result = execute_query(conn,sql)
    article_ids = {row[0] for row in result}
    return article_ids


from typing import List
def check_articlesId_by_entitiesId(conn, entitiesId: List[int]) -> set:
    if entitiesId:
        article_ids = set()
        for entity_id in entitiesId:
            sql = f"SELECT article_id FROM article_entity WHERE entity_id = {entity_id}"
            result = execute_query(conn, sql)
            article_ids.update(row[0] for row in result)
        return article_ids
    else:
        return set()


def check_entitiesId_by_entitiesName(conn, entitiesName: List[str]) -> List[int]:
    if entitiesName:
        entitiesId = []
        for name in entitiesName:
            sql = f"SELECT id FROM entity WHERE name = '{name}'"
            result = execute_query(conn, sql.encode('utf-8'))
            if result:
                entitiesId.append(result[0][0])
        return entitiesId
    else:
        return None



def test():
    print(check_user_behavior_set(conn,1))
    print(check_articlesId_by_entitiesId(conn,[117, 54]))
    print(check_entitiesId_by_entitiesName(conn,['息肉','结节']))

if __name__ == '__main__':
    test()