from modules import classifier,chat_robot,medical_robot
from chat_context_records.record_utils import *
from chat import *




def get_user_input():
    return input('请输入您的问题：')



if __name__ == '__main__':
    while True:
        message = get_user_input()
        if message.lower() == 'exit':
            break
        reply = reply_to_user(message)
        print('回复: ', reply)