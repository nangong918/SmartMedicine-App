import pymysql

# 全局变量，用于存储数据库连接实例
global database_connection

def initialize_database(host = 'localhost',
    port = 3306,
    user = 'root',
    password = '123456',
    database = 'medical_articles'
                        ):
    global database_connection
    # 创建MySQL连接
    database_connection = pymysql.connect(host=host, port=port, user=user, password=password, database=database)

def disconnect_database():
    global database_connection
    if database_connection is not None:
        database_connection.close()
        database_connection = None


def execute_query(sql, params=None):
    global database_connection

    if database_connection is None:
        raise Exception("Database connection is not initialized.")

    cursor = database_connection.cursor()
    cursor.execute(sql, params)
    result = cursor.fetchall()
    cursor.close()
    return result



