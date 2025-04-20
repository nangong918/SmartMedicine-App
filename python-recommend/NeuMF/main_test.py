from train import train_main_test,train_main
from data import data_loader,data_process
from data.sql_dataset import data_loader as data_loader_for_article
import matplotlib.pyplot as plt


if __name__ == '__main__':

    # 数据提取  (协同过滤)
    # n_user, n_item, train_data, test_data, topK_data = data_process.pack(data_loader.read_ml100k)
    n_user,n_item,train_data,test_data,topK_data = data_process.pack(data_loader_for_article.read_sql_dataset)

    # 模型参数  (embedding维度，多层感知机n，正则化)
    dim, layers = 3, [6, 3]

    # 模型训练
    history1 = train_main_test(n_user,n_item,train_data,test_data,topK_data,dim,layers)

    # 模型参数  (embedding维度，多层感知机n，正则化)
    gmf_dim, mlp_dim, layers, l2 = 8, 16, [32, 16, 8], 1e-4

    history2 = train_main(n_user, n_item, train_data, test_data, topK_data, gmf_dim, mlp_dim, layers, l2)

    plt.figure(figsize=(8, 6))
    plt.plot(history1.history['val_loss'], label='old model val_Loss')
    plt.plot(history2.history['val_loss'], label='new model val_Loss')
    plt.xlabel('Epoch')
    plt.ylabel('Loss')
    plt.title('Model Loss')
    plt.legend()
    plt.show()

    plt.figure(figsize=(8, 6))
    plt.plot(history1.history['val_accuracy'], label='old model val_Accuracy')
    plt.plot(history2.history['val_accuracy'], label='new model val_Accuracy')
    plt.xlabel('Epoch')
    plt.ylabel('Accuracy')
    plt.title('Model Accuracy')
    plt.legend()
    plt.show()
