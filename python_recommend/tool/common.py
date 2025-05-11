import tensorflow as tf
from tool.topK import TopK_data,topK_evaluate





# 评分函数
def get_score_fn(model):

    # tensorflow GPU 加速
    @tf.function(experimental_relax_shapes=True)
    def accelerate_model(ui):
        # 调用model评价并用squeeze除去多余维度
        return tf.squeeze(model(ui))

    def score_fn(ui):
        # 字典中的v转化为张量    k仍然是字符串
        ui = {k: tf.constant(v, dtype=tf.int32) for k, v in ui.items()}
        return accelerate_model(ui).numpy()

    return score_fn


# 日志输出
def log(epoch, train_loss, train_auc, train_precision, train_recall, test_loss, test_auc, test_precision, test_recall):
    "F1 = 2 * (精确度 * 召回率) / (精确度 + 召回率)"
    train_f1 = 2. * train_precision * train_recall / pr if (pr := train_precision + train_recall) else 0
    test_f1 = 2. * test_precision * test_recall / pr if (pr := test_precision + test_recall) else 0
    print('[Epoch = %d, 训练 Loss = %.5f, 训练 AUC = %.5f, 训练 F1 = %.5f, 测试 Loss = %.5f, 测试 AUC = %.5f, 测试 F1 = %.5f]' %
          (epoch + 1, train_loss, train_auc, train_f1, test_loss, test_auc, test_f1))


def topK_out(topK_data: TopK_data, score_fn, K_list = [10, 36, 100]):
    precisions, recalls = topK_evaluate(topK_data, score_fn, K_list)
    for k, precision, recall in zip(K_list, precisions, recalls):
        f1 = 2. * precision * recall / pr if (pr := precision + recall) else 0
        print('[k = %d, precision = %.3f%%, recall = %.3f%%, F1 = %.3f%%]' %
              (k, 100. * precision, 100. * recall, 100. * f1), end='')













