import random
import string

def generate_random_string(length, mode):
    characters = []

    if mode == 'pure_chinese':
        for _ in range(length):
            characters.append(chr(random.randint(0x4e00, 0x9fa5)))
    elif mode == 'pure_non_chinese':
        for _ in range(length):
            char_type = random.choice(['digit', 'letter', 'symbol'])
            if char_type == 'digit':
                characters.append(random.choice(string.digits))
            elif char_type == 'letter':
                characters.append(random.choice(string.ascii_letters))
            elif char_type == 'symbol':
                characters.append(random.choice(string.punctuation.replace(',', '')))  # 禁止生成逗号
    else:  # 混合模式
        for _ in range(length):
            char_type = random.choice(['chinese', 'digit', 'letter', 'symbol'])
            if char_type == 'chinese':
                characters.append(chr(random.randint(0x4e00, 0x9fa5)))
            elif char_type == 'digit':
                characters.append(random.choice(string.digits))
            elif char_type == 'letter':
                characters.append(random.choice(string.ascii_letters))
            elif char_type == 'symbol':
                characters.append(random.choice(string.punctuation.replace(',', '')))  # 禁止生成逗号

    return ''.join(characters)

def generate_sentence(num):
    result = []
    for _ in range(num):
        length = random.randint(15, 25)
        mode = random.choice(['pure_chinese', 'pure_non_chinese', 'mixed'])  # 随机选择模式
        random_string = generate_random_string(length, mode)
        result.append(random_string + ",n")  # 拼接 ",n"
    return result

# 生成伪数据并写入文件
def write_to_file(filename, num):
    result = generate_sentence(num)
    with open(filename, 'w', encoding='utf-8') as f:
        for line in result:
            f.write(line + '\n')  # 每行写入一个字符串

def transform_data(input_file, output_file):
    with open(input_file, 'r', encoding='utf-8') as infile, open(output_file, 'w', encoding='utf-8') as outfile:
        for line in infile:
            line = line.strip()
            last_comma_index = line.rfind(',')
            second_last_comma_index = line.rfind(',', 0, last_comma_index)

            if second_last_comma_index != -1:  # 确保存在倒数第二个逗号
                text = line[:second_last_comma_index]  # 获取文本部分
                # 将除了最后一个逗号之外的所有逗号替换为中文逗号
                text = text.replace(',', '，')
                # 输出格式：text,y
                outfile.write(f"{text},y\n")

# 调用函数，输入和输出文件名
# transform_data('input.txt', 'output.txt')




if __name__ == '__main__':
    # 调用函数，生成 100 行数据并写入 output.txt
    write_to_file('output_n.txt', 820)
    # 调用函数，输入和输出文件名
    transform_data('test.csv', 'output_y.txt')
    pass