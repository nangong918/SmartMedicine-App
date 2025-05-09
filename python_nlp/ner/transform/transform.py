
from ahocorasick import Automaton




def transform_tool(text, entity, entity_type):
    ac = Automaton()
    ac.add_word(entity, entity_type)
    ac.make_automaton()

    result = []

    for char in text:
        result.append(char + " O")

    for end_index, _ in ac.iter(text):
        start_index = end_index - len(entity) + 1
        print("end_index",end_index)
        print("len(match)",len(entity))
        result[start_index] = text[start_index] + " B_" + entity_type

        for i in range(start_index + 1, end_index + 1):
            result[i] = text[i] + " I_" + entity_type
    result = "\n".join(result)
    return result



train_output_path = 'train.txt'
train_input_path = 'train.csv'
test_output_path = 'test.txt'
test_input_path = 'test.csv'
class NerTransformTool:
    def __init__(self,csv_path):
        super(NerTransformTool,self).__init__()
        self.csv_path = csv_path
        self.AC_tree = None
        self.ac_tree_init()

    def load_data(self):
        import pandas as pd
        # 从CSV文件中读取数据
        df = pd.read_csv(self.csv_path)

        # 将数据存储到text、entity和type列表中
        self.text_list = df['text'].tolist()
        self.entity_list = df['entity'].tolist()
        self.entity_type_list = df['type'].tolist()
        self.entities = set((e, t) for e, t in zip(self.entity_list, self.entity_type_list))

    def ac_tree_init(self):
        self.load_data()
        self.AC_tree = Automaton()
        for entity,entity_type in self.entities:
            self.AC_tree.add_word(entity, entity_type)
        self.AC_tree.make_automaton()

    def transform_tool(self, text, entity, entity_type):
        result = []

        for char in text:
            result.append(char + " O")

        for end_index, _ in self.AC_tree.iter(text):
            start_index = end_index - len(entity) + 1
            result[start_index] = text[start_index] + " B_" + entity_type

            for i in range(start_index + 1, end_index + 1):
                result[i] = text[i] + " I_" + entity_type
        result = "\n".join(result)
        return result

    def export_txt(self,output_path):
        export_list = []
        for text,e,t in zip(self.text_list,self.entity_list,self.entity_type_list):
            p = self.transform_tool(text,e,t) + "\n"
            export_list.append(p)

        # 将export_list的内容保存到文件中
        with open(output_path, 'w', encoding='utf-8') as file:
            file.write('\n'.join(export_list))

        print(f"导出完成，保存路径：{output_path}")





def test():
    # text = "胸闷呼吸困难是怎么回事阿，呼吸，胸闷"
    # entity = "胸闷呼吸困难"
    # entity_type = "symptom"
    #
    # output = transform_tool(text, entity, entity_type)
    # print(output,"===="*20)
    #
    # ntt = NerTransformTool(input_path)
    # print(ntt.transform_tool(text, entity, entity_type))
    # print(ntt.transform_tool("能推荐我两篇文章吗？","推荐",'v'))
    ntt = NerTransformTool(train_input_path)
    ntt.export_txt(train_output_path)
    pass


def export_data():
    ntt = NerTransformTool(test_input_path)
    ntt.export_txt(test_output_path)


if __name__ == '__main__':
    #test()
    export_data()