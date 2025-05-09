import ahocorasick
import json


class titleEntity_tool:
    def __init__(self,dictionary_path):
        super(titleEntity_tool,self).__init__()
        self.AC_tree = None
        self.dict_path = dictionary_path
        self.ac_tree_init()


    def ac_tree_init(self):
        with open(self.dict_path, 'r', encoding='utf-8') as file:
            json_data = json.load(file)
        self.AC_tree = ahocorasick.Automaton()
        for word in json_data:
            self.AC_tree.add_word(word, word)
        self.AC_tree.make_automaton()


    def get_titleEntity(self,title):
        matches = [match[1] for match in self.AC_tree.iter(title)]
        return matches

    def get_entity_type(self, entity):
        if entity in self.AC_tree:
            return True
        else:
            return False




def test():
    tet = titleEntity_tool('特殊内容字典.json')
    e = tet.get_titleEntity('脑中风的发病竟然和这种心脏疾病有关！您知道吗？')
    print(e)
    from export import entities_change
    import csv
    entities_name = []
    entities_id = []
    with open('./export_entities.csv', 'r', encoding='utf-8') as file:
        reader = csv.reader(file)
        for row in reader:
            entities_name.append(row[0])
            entities_id.append(row[1])
    e2 = entities_change(entities_name,entities_id,e)
    print(e2)



def test2():
    path = '../知识图谱相似实体审查/foods.json'
    tet = titleEntity_tool(path)
    bool1 = tet.get_entity_type('鸭肉')
    bool2 = tet.get_entity_type('1213232')
    print(bool1)
    print(bool2)




if __name__ == '__main__':

    test()

    pass


