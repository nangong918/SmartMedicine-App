from py2neo import Graph
import os
from tqdm import tqdm
import json
import threading
import re
import codecs

# 如果操作失误，执行 MATCH (n) DETACH DELETE n

#正则表达式清洗语句，防止cql出问题
def delete_special_symbols(text):
    # 输入类型保护
    if not isinstance(text, str):
        return text

    special_symbols = [
        r"\<=",     # <=
        r"<",       # <
        r">=",      # >=
        r">",       # >
        r"\<>",     # <>
        r"=~",      # ~=
        r"!",       # !
        r"!=",      # !=
        r"%",       # %
        r"\*",      # *
        r"\+",      # +
        r"-",       # -
        r"/",       # /
        r"::",      # ::
        r"=",       # =
        r"AND",
        r"CALL",
        r"CONTAINS",
        r"CREATE",
        r"DELETE",
        r"DETACH",
        r"ENDS",
        r"FOREACH",
        r"IN",
        r"IS",
        r"LOAD",
        r"MATCH",
        r"MERGE",
        r"OPTIONAL",
        r"OR",
        r"REMOVE",
        r"RETURN",
        r"SET",
        r"STARTS",
        r"UNION",
        r"UNWIND",
        r"USE",
        r"WITH",
        r"XOR",
        r"\^",      # ^
        r"'",       # 单引号
    ]

    # 使用 re.escape 避免特殊字符干扰，并按长度排序以保证长匹配优先
    escaped_symbols = [re.escape(sym) for sym in special_symbols]
    escaped_symbols.sort(key=lambda x: -len(x))  # 长度从高到低排序
    pattern = "|".join(escaped_symbols)

    # 编译一次，提升性能
    regex = re.compile(pattern)
    cleaned_text = regex.sub("", text)
    return cleaned_text


def replace_special_symbols(text):
    special_symbols = {
        "'":"''"
    }

    for symbol, replacement in special_symbols.items():
        text = text.replace(symbol, replacement)

    return text

class MedicalExtractor(object):
    #构造器
    def __init__(self):
        super(MedicalExtractor,self).__init__()

        #Neo4j图数据库建立连接
        self.graph = Graph("bolt://localhost:7687",auth=("neo4j","12345678"))

        # 8类节点
        self.drugs = [] # 药品
        self.recipes = [] #菜谱
        self.foods = [] #　食物
        self.checks = [] # 检查
        self.departments = [] #科室
        self.producers = [] #药企
        self.diseases = [] #疾病
        self.symptoms = []#症状

        #疾病信息
        self.disease_infos = []

        #构建节点和实体的关系
        self.rels_department = [] #　科室－科室关系
        self.rels_noteat = [] # 疾病－忌吃食物关系
        self.rels_doeat = [] # 疾病－宜吃食物关系
        self.rels_recommandeat = [] # 疾病－推荐吃食物关系
        self.rels_commonddrug = [] # 疾病－通用药品关系
        self.rels_recommanddrug = [] # 疾病－热门药品关系
        self.rels_check = [] # 疾病－检查关系
        self.rels_drug_producer = [] # 厂商－药物关系

        self.rels_symptom = [] #疾病症状关系
        self.rels_acompany = [] # 疾病并发关系
        self.rels_category = [] #　疾病与科室之间的关系

    #三元组提取器
    def extract_triples(self,medical_data_path):
        print("从json文件中转换抽取三元组：节点，节点-实体关系，疾病-x关系")
        with open(medical_data_path,'r',encoding='utf8') as f:
            for line in tqdm(f.readlines(),ncols=80):
                #json逐行读取
                data_json = json.loads(line)
                #初始化疾病字典
                disease_dict = {'name': '',
                                'desc': '',
                                'prevent': '',
                                'cause': '',
                                'easy_get': '',
                                'cure_department': '',
                                'cure_way': '',
                                'cure_lasttime': '',
                                'symptom': '',
                                'cured_prob': ''}

                if 'name' in data_json:
                    disease = data_json['name']
                    self.diseases.append(disease)
                    disease_dict['name'] = disease

                if 'symptom' in data_json:
                    #症状记录
                    self.symptoms +=data_json['symptom']
                    for symptom in data_json['symptom']:
                        #关系记录
                        self.rels_symptom.append([disease,'has_symptom',symptom])

                if 'acompany' in data_json:
                    for acompany in data_json['acompany']:
                        self.rels_acompany.append([disease,'acompany_with', acompany])
                        self.diseases.append(acompany)

                if 'desc' in data_json:
                    disease_dict['desc'] = data_json['desc']

                if 'prevent' in data_json:
                    disease_dict['prevent'] = data_json['prevent']

                if 'cause' in data_json:
                    disease_dict['cause'] = data_json['cause']

                if 'get_prob' in data_json:
                    disease_dict['get_prob'] = data_json['get_prob']

                if 'easy_get' in data_json:
                    disease_dict['easy_get'] = data_json['easy_get']

                if 'cure_department' in data_json:
                    cure_department = data_json['cure_department']
                    # 直属科   和  部门-科
                    if len(cure_department) == 1:
                         self.rels_category.append([disease, 'cure_department',cure_department[0]])
                    if len(cure_department) == 2:
                        big = cure_department[0]
                        small = cure_department[1]
                        self.rels_department.append([small,'belongs_to', big])
                        self.rels_category.append([disease,'cure_department', small])

                    disease_dict['cure_department'] = cure_department
                    self.departments += cure_department

                if 'cure_way' in data_json:
                    disease_dict['cure_way'] = data_json['cure_way']

                if  'cure_lasttime' in data_json:
                    disease_dict['cure_lasttime'] = data_json['cure_lasttime']

                if 'cured_prob' in data_json:
                    disease_dict['cured_prob'] = data_json['cured_prob']

                if 'common_drug' in data_json:
                    common_drug = data_json['common_drug']
                    for drug in common_drug:
                        self.rels_commonddrug.append([disease,'has_common_drug', drug])
                    self.drugs += common_drug

                if 'recommand_drug' in data_json:
                    recommand_drug = data_json['recommand_drug']
                    self.drugs += recommand_drug
                    for drug in recommand_drug:
                        self.rels_recommanddrug.append([disease,'recommand_drug', drug])

                if 'not_eat' in data_json:
                    not_eat = data_json['not_eat']
                    for _not in not_eat:
                        self.rels_noteat.append([disease,'not_eat', _not])

                    self.foods += not_eat
                    do_eat = data_json['do_eat']
                    for _do in do_eat:
                        self.rels_doeat.append([disease,'do_eat', _do])

                    self.foods += do_eat

                if 'recommand_eat' in data_json:
                    recommand_eat = data_json['recommand_eat']
                    for _recommand in recommand_eat:
                        self.rels_recommandeat.append([disease,'recommand_recipes', _recommand])
                    self.recipes += recommand_eat

                if 'check' in data_json:
                    check = data_json['check']
                    for _check in check:
                        self.rels_check.append([disease, 'need_check', _check])
                    self.checks += check

                if 'drug_detail' in data_json:
                    for det in data_json['drug_detail']:
                        det_spilt = det.split('(')
                        if len(det_spilt) == 2:
                            p,d = det_spilt
                            d = d.rstrip(')')
                            if p.find(d) > 0:
                                p = p.rstrip(d)
                            self.producers.append(p)
                            self.drugs.append(d)
                            self.rels_drug_producer.append([p,'production',d])
                        else:
                            d = det_spilt[0]
                            self.drugs.append(d)

                self.disease_infos.append(disease_dict)

    # 写入neo4j图节点：MERGE (n:Person {name: 'Alice', age: 30, city: 'New York'})
    #写入图的节点
    def write_nodes(self,entities,entity_type):
        node_count = 0  # 记录写入的节点数量
        print("写入 {0} 实体".format(entity_type))
        # set除去重复内容
        for node in tqdm(set(entities), ncols=80):
            # MERGE: 这是CQL语句的关键字，用于合并节点。
            # (n:{label}: 这部分定义了一个节点变量 n 和节点的标签 label
            # {{name:'{entity_name}'}}: 这部分定义了节点的属性，其中 name 是属性名
            # replace("'","") CQL 语句中，单引号被用作引号字符，替换实体名称中的单引号，防止 CQL 语句的语法错误。
            cql = ("""MERGE(n:{label}{{name:'{entity_name}'}})"""
                   .format(
                label=entity_type,
                entity_name=node
                .replace("'","")))
            try:
                if node_count % 1000 == 0:
                    print("cql抽查：写入 {0} 实体 {1} 条；cql语句：{2}".format(entity_type, node_count, cql))
                self.graph.run(cql)
                node_count += 1
            except Exception as e:
                print("nodes异常：",e)
                print(cql)
                node_count += 1
                # 如果出现错误，其他的仍然需要导入
                continue

    #写入图的边
    def write_edges(self, triples, head_type, tail_type):
        print("写入 {0} 关系".format(triples[0][1]))
        # set(map(tuple, triples)除去重复内容
        for head, relation, tail in tqdm(set(map(tuple, triples)), ncols=80):
            head = delete_special_symbols(head)
            tail = delete_special_symbols(tail)
            head_type = delete_special_symbols(head_type)
            tail_type = delete_special_symbols(tail_type)
            cql = """MATCH(p:{head_type}),(q:{tail_type})
                    WHERE p.name='{head}' AND q.name='{tail}'
                    MERGE (p)-[r:{relation}]->(q)""".format(
                head_type=head_type,
                tail_type=tail_type,
                head=head,
                tail=tail,
                relation=relation
            )
            try:
                self.graph.run(cql)
            except Exception as e:
                print("Edge异常：", e)
                print(cql)
                continue  # 添加 continue 语句

    #设置属性
    def set_attributes(self,entity_infos,e_type):
        print("写入 {0} 实体的属性".format(e_type))
        #前892个数据并不好，有全空情况
        for e_dict in tqdm(entity_infos[892:], ncols=80):
            name = e_dict['name']
            name = delete_special_symbols(name)
            for k,v in e_dict.items():
                #k是其中之一 :则不跳过\n     v使用''装起来
                if k in ['cure_department','cure_way']:
                    cql = """MATCH(n:{label})
                    WHERE n.name='{name}'
                    set n.{k}={v}""".format(label=e_type,name=name.replace("'",""),k=k,v=v)
                #跳过\n
                else:
                    cql = """MATCH(n:{label})
                    WHERE n.name='{name}'
                    set n.{k}='{v}'""".format(label=e_type,name=name.replace("'",""),k=k,v=v.replace("\n","").replace("'",""))
                try:
                    self.graph.run(cql)
                except Exception as e:
                    print("异常：设置属性",e)
                    print(cql)

    #创建实体
    def create_entities(self):
        self.write_nodes(self.drugs, '药品')
        self.write_nodes(self.recipes, '菜谱')
        self.write_nodes(self.foods, '食物')
        self.write_nodes(self.checks, '检查')
        self.write_nodes(self.departments, '科室')
        self.write_nodes(self.producers, '药企')
        self.write_nodes(self.diseases, '疾病')
        self.write_nodes(self.symptoms, '症状')

    #创建关系
    def create_relations(self):
        self.write_edges(self.rels_department, '科室', '科室')
        self.write_edges(self.rels_noteat, '疾病', '食物')
        self.write_edges(self.rels_doeat, '疾病', '食物')
        self.write_edges(self.rels_recommandeat, '疾病', '菜谱')
        self.write_edges(self.rels_commonddrug, '疾病', '药品')
        self.write_edges(self.rels_recommanddrug, '疾病', '药品')
        self.write_edges(self.rels_check, '疾病', '检查')
        self.write_edges(self.rels_drug_producer, '药企', '药品')
        self.write_edges(self.rels_symptom, '疾病', '症状')
        self.write_edges(self.rels_acompany, '疾病', '疾病')
        self.write_edges(self.rels_category, '疾病', '科室')

    #设置疾病属性
    def set_diseases_attributes(self):
        t = threading.Thread(target=self.set_attributes, args=(self.disease_infos, "疾病"))
        # 设置非守护线程：主线程会在该线程结束之后再结束线程
        t.setDaemon(False)
        t.start()

    #导出数据
    def export_data(self,data,path):
        if isinstance(data[0],str):
            data = sorted([d.strip("...") for d in set(data)])
        with codecs.open(path, 'w', encoding='utf-8') as f:
            json.dump(data, f, indent=4, ensure_ascii=False)

    #将medical的全部数据导出来
    def export_entities_relations(self):
        script_dir = os.path.dirname(__file__)
        self.export_data(self.drugs, os.path.join(script_dir, "graph_data/drugs.json"))
        self.export_data(self.recipes, os.path.join(script_dir, 'graph_data/recipes.json'))
        self.export_data(self.foods, os.path.join(script_dir, 'graph_data/foods.json'))
        self.export_data(self.checks, os.path.join(script_dir, 'graph_data/checks.json'))
        self.export_data(self.departments, os.path.join(script_dir, 'graph_data/departments.json'))
        self.export_data(self.producers, os.path.join(script_dir, 'graph_data/producers.json'))
        self.export_data(self.diseases, os.path.join(script_dir, 'graph_data/diseases.json'))
        self.export_data(self.symptoms, os.path.join(script_dir, 'graph_data/symptoms.json'))

        self.export_data(self.rels_department, os.path.join(script_dir, 'graph_data/rels_department.json'))
        self.export_data(self.rels_noteat, os.path.join(script_dir, 'graph_data/rels_noteat.json'))
        self.export_data(self.rels_doeat, os.path.join(script_dir, 'graph_data/rels_doeat.json'))
        self.export_data(self.rels_recommandeat, os.path.join(script_dir, 'graph_data/rels_recommandeat.json'))
        self.export_data(self.rels_commonddrug, os.path.join(script_dir, 'graph_data/rels_commonddrug.json'))
        self.export_data(self.rels_recommanddrug, os.path.join(script_dir, 'graph_data/rels_recommanddrug.json'))
        self.export_data(self.rels_check, os.path.join(script_dir, 'graph_data/rels_check.json'))
        self.export_data(self.rels_drug_producer, os.path.join(script_dir, 'graph_data/rels_drug_producer.json'))
        self.export_data(self.rels_symptom, os.path.join(script_dir, 'graph_data/rels_symptom.json'))
        self.export_data(self.rels_acompany, os.path.join(script_dir, 'graph_data/rels_acompany.json'))
        self.export_data(self.rels_category, os.path.join(script_dir, 'graph_data/rels_category.json'))



#--------------------------------Test---------------------------------


def check_diseases(name):
    for e in extractor.disease_infos:
        if e['name'] == name:
            for k in e.keys():
                print(k)
                print(e[k])


def set_single_diseases_attribute(e_dict,e_type):
    print("写入 {0} 实体 {1} 的属性".format(e_type,e_dict['name']))
    name = e_dict['name']
    name = delete_special_symbols(name)
    for k, v in e_dict.items():
        # k是其中之一 :则不跳过\n     v使用''装起来
        if k in ['cure_department', 'cure_way']:
            cql = """MATCH(n:{label})
            WHERE n.name='{name}'
            set n.{k}={v}""".format(label=e_type, name=name.replace("'", ""), k=k, v=v)
        # 跳过\n
        else:
            cql = """MATCH(n:{label})
            WHERE n.name='{name}'
            set n.{k}='{v}'""".format(label=e_type, name=name.replace("'", ""), k=k,
                                      v=v.replace("\n", "").replace("'", ""))
        try:
            extractor.graph.run(cql)
        except Exception as e:
            print("异常：设置属性", e)
            print(cql)

def single_diseases_import_test(name):
    for e in extractor.disease_infos:
        if e['name'] == name:
            set_single_diseases_attribute(e,'疾病')
            break


#-------------------------------import-------------------------------

def import_data():
    extractor.create_entities()
    # extractor.create_relations()
    # extractor.set_diseases_attributes()
    # extractor.export_entities_relations()
    pass

if __name__ == '__main__':
    #路径合成
    script_dir = os.path.dirname(__file__)
    path = os.path.join(script_dir,"graph_data","medical.json")
    extractor = MedicalExtractor()
    extractor.extract_triples(path)

    import_data()

    #----------------------------Test

    # check_diseases("感冒")
    # single_diseases_import_test('感冒')




