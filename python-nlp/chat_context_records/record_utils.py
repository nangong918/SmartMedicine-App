import os
import json


current_dir = os.path.dirname(os.path.abspath(__file__))

#------------------------------record

def record_user_dialogue_context(user,data):
    data_file = os.path.join(current_dir, '{}.json'.format(str(user)))
    with open(data_file, 'w', encoding='utf8') as f:
        f.write(json.dumps(data, sort_keys=True, indent=4,
                           separators=(', ', ': '), ensure_ascii=False))



#------------------------------load


def load_user_dialogue_context(user):
    data_file = os.path.join(current_dir,'{}.json'.format(str(user)))
    if not os.path.exists(data_file):
        return {"choice_answer": "hi，机器人小智很高心为您服务", "slot_values": None}
    else:
        with open(data_file, 'r', encoding='utf8') as f:
            data = f.read()
            return json.loads(data)



#------------------------------format


def change_format(reply):
    new_reply = {"ask_template": reply["ask_template"], "deny_response": reply["deny_response"],
                 "replay_answer": reply["replay_answer"], "reply_template": reply["reply_template"],
                 "slot_list": reply["slot_list"], "slot_values": reply["slot_values"]}
    if "choice_answer" in reply and reply["choice_answer"]:
        new_reply["choice_answer"] = reply["choice_answer"]
    return new_reply



