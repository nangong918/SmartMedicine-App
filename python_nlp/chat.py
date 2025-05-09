from modules import classifier,chat_robot,medical_robot
from chat_context_records.record_utils import *


def reply_to_user(message):
    user_intent = classifier(message)
    print(user_intent)
    if user_intent in ["greet","goodbye","deny","isbot"]:   # 积极，再见，否认，关于AI
        reply = chat_robot(user_intent)
    elif user_intent == "accept":# 肯定回答
        reply = load_user_dialogue_context('user')
        reply = reply.get("choice_answer")
    else:# diagnosis
        reply = medical_robot(message,'user')
        if reply["slot_values"]:
            reply_save_format = change_format(reply)
            record_user_dialogue_context('user',reply_save_format)
        reply = reply.get("replay_answer")

    return reply


if __name__ == '__main__':
    message = "胃癌的治疗时间是什么？"
    # reply = medical_robot(message, 'user')
    # reply = reply.get("slot_values")
    # print(reply)
    reply = reply_to_user(message)
    print(reply)