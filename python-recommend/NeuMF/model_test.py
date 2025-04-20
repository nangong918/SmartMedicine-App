import tensorflow as tf
import numpy as np

h5_path = 'h5/neumf_model.h5'

movie_h5_path = 'movie_h5/neumf_model.h5'

# 加载模型
neumf_model = tf.keras.models.load_model(movie_h5_path)

# 构造输入数据
user_id = np.array([276])  # 用户ID
item_id = np.array([54])  # 物品ID


neumf_output = neumf_model.predict([user_id, item_id])

print(neumf_output)


