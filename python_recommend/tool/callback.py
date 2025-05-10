import tensorflow as tf
from tool.topK import TopK_data
from tool.common import log,topK_out




class Evaluate_callback(tf.keras.callbacks.Callback):
    def __init__(self,topK_data: TopK_data,score_fn):
        super(Evaluate_callback,self).__init__()
        self.topK_data = topK_data
        self.score_fn = score_fn

    def on_epoch_end(self,epoch,logs=None):
        log(epoch, logs['loss'], logs['auc'], logs['precision'], logs['recall'],
            logs['val_loss'], logs['val_auc'], logs['val_precision'], logs['val_recall'])

        topK_out(self.topK_data,self.score_fn)


