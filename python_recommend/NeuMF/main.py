from train import train_main
from data import data_loader,data_process
from data.sql_dataset import data_loader as data_loader_for_article



'''

NeuMF 算法：

    通用结构：
        Input层：输入
            稀疏的向量   （one-hot编码的二值化稀疏向量）
        Embedding层：特征向量     （全连接层，用于将输入层的稀疏表示映射成一个稠密向量）
            用户特征向量u-vec
            物品特征向量i-vec
        Neural CF层：神经写通过过滤
            将潜在特征向量映射成预测分数
            loss函数：均方差误差（预测值-真实值的平方）
        Output层：输出
            用激活函数预测分数（sigmoid）

    模型
        GMF广义矩阵分解
            Pi(*)Qu     其中(*)表示逐行相乘
            Yui = Aout(W(Pi(*)Qu))  其中Aout表示激活函数，W表示权重
        MLP多层感知机data_loader.py
            Z1 = %(Pi,Qu)
            %(Zn) = Aout(Wn*Z1 + bn)
            Yui = sigmoid(H*%(Zn))
        NCF张量拼接
            GMF + MLP
'''





if __name__ == '__main__':

    # 数据提取  (协同过滤)
    # n_user, n_item, train_data, test_data, topK_data = data_process.pack(data_loader.read_ml100k)
    n_user,n_item,train_data,test_data,topK_data = data_process.pack(data_loader_for_article.read_sql_dataset)

    # 模型参数  (embedding维度，多层感知机n，正则化)
    gmf_dim, mlp_dim, layers, l2 = 8, 16, [32, 16, 8], 1e-4

    # 模型训练
    train_main(n_user,n_item,train_data,test_data,topK_data,gmf_dim,mlp_dim,layers,l2)
