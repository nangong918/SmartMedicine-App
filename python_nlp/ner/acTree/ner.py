import json
import ahocorasick
import os



# 由于Ner训练能力有限，部分专业属于+弱数据集交给dict处理
class NerBaseDict(object):
    def __init__(self, diseases_dict_path,symptoms_dict_path,recommend_dict_path):
        super(NerBaseDict,self).__init__()
        # 字典路径  ：知识图谱导出的diseases
        self.diseases_match_tree = self.build_AC_tree(self.load_path(diseases_dict_path))
        self.symptoms_match_tree = self.build_AC_tree(self.load_path(symptoms_dict_path))
        self.recommend_match_tree = self.build_AC_tree(self.load_path(recommend_dict_path))

    def load_path(self,path):
        with open(path,'r',encoding='utf8') as f:
            return json.load(f)

    # Aho-Corasick Automaton（AC自动机）逐行匹配
    def build_AC_tree(self,wordlist):
        AC_tree = ahocorasick.Automaton()
        # 枚举整个列表
        for index,word in enumerate(wordlist):
            # 与单词匹配，匹配成功后输出索引和单词
            AC_tree.add_word(word,(index,word))
        AC_tree.make_automaton()
        return AC_tree


    def get_entities(self,ac_tree,text,type_):
        region_wds = []
        # i的类型：(7, (5824, '老年人流行性感冒'))  第一个参数是字数-1，第二个数是在词典中的行数
        # 会匹配到词典中有的所有实体
        for i in ac_tree.iter(text):
            # 取出里面的词
            wd = i[1][1]
            region_wds.append(wd)
        stop_wds = []
        for wd1 in region_wds:
            for wd2 in region_wds:
                # 只选出最大的词汇
                if wd1 in wd2 and wd1 != wd2:
                    stop_wds.append(wd1)
        # 创建一个新列表 final_wds，其中包含在 region_wds 中但不在 stop_wds 中的词
        final_wds = [i for i in region_wds if i not in stop_wds]
        entities = [{"word":i,"type":type_,"recog_label":"dict"} for i in final_wds]
        return entities

    # 字典的识别实体方式
    def recognize(self, text,intent_type):
        # 存储识别结果 文本与识别出来的实体
        item = {"string": text, "entities": []}

        entity_list = []

        diseases_entities = self.get_entities(self.diseases_match_tree, text, "Disease")
        entity_list.extend(diseases_entities)
        if intent_type == "病因":
            symptoms_entities = self.get_entities(self.symptoms_match_tree,text,"symptom")
            entity_list.extend(symptoms_entities)
        elif intent_type == "推荐":
            recommend_entities = self.get_entities(self.recommend_match_tree, text, "recommend")
            entity_list.extend(recommend_entities)

        item["entities"] = entity_list

        return item


dict_path_diseases = 'dictionary\\diseases.json'
dict_path_recommend = 'dictionary\\recommend.json'
dict_path_symptoms = 'dictionary\\symptoms.json'

class MedicalNerAcTree(object):
    def __init__(self):
        super(MedicalNerAcTree, self).__init__()
        # 词汇pickle加载
        self.current_dir = os.path.dirname(os.path.abspath(__file__))  # 获取当前脚本文件的绝对路径
        print('self.current_dir:',self.current_dir)
        self.ner_dict = NerBaseDict(diseases_dict_path=os.path.join(self.current_dir, dict_path_diseases),
                                    recommend_dict_path=os.path.join(self.current_dir, dict_path_recommend),
                                    symptoms_dict_path=os.path.join(self.current_dir, dict_path_symptoms))

    def Predict(self, texts,Intent_type):
        res = []
        # 字典识别
        for text in texts:
            ents = self.ner_dict.recognize(text,intent_type=Intent_type)
            if ents["entities"]:
                res.append(ents)
        return res




if __name__ == '__main__':


    pass

