import tensorflow as tf
from MediclePrediction.data_tool import disease_list

def get_model(lr = 1e-4):
    input_shape_1 = len(disease_list)

    # 5. 构建模型
    model = tf.keras.Sequential([
        tf.keras.layers.Dense(1, activation='sigmoid', input_shape=(input_shape_1,))
    ])

    # 6. 编译模型
    optimizer = tf.keras.optimizers.Adam(learning_rate=lr)  # 设置学习率
    model.compile(optimizer=optimizer, loss='binary_crossentropy', metrics=['accuracy'])
    # model.compile(optimizer=optimizer, loss='mean_squared_error')

    return model

def get_model_attention(lr = 1e-4):
    input_shape_1 = len(disease_list)

    # 5. 构建模型
    inputs = tf.keras.layers.Input(shape=(input_shape_1,))
    x = tf.keras.layers.Dense(64, activation='relu')(inputs)
    attention_scores = tf.keras.layers.Dense(1, activation='sigmoid')(x)
    attended_features = tf.keras.layers.Multiply()([x, attention_scores])
    x = tf.keras.layers.Dense(32, activation='relu')(attended_features)
    outputs = tf.keras.layers.Dense(1, activation='sigmoid')(x)
    model = tf.keras.Model(inputs=inputs, outputs=outputs)


    # 6. 编译模型
    optimizer = tf.keras.optimizers.Adam(learning_rate=lr)  # 设置学习率
    model.compile(optimizer=optimizer, loss='binary_crossentropy', metrics=['accuracy'])
    # model.compile(optimizer=optimizer, loss='mean_squared_error')

    return model

def get_model_Feature_Interaction(lr = 1e-4):
    input_shape_1 = len(disease_list)

    # 5. 构建模型
    inputs = tf.keras.layers.Input(shape=(input_shape_1,))
    x1 = tf.keras.layers.Dense(64, activation='relu')(inputs)
    x2 = tf.keras.layers.Dense(64, activation='relu')(inputs)
    interaction_features = tf.keras.layers.Multiply()([x1, x2])
    x = tf.keras.layers.Dense(32, activation='relu')(interaction_features)
    outputs = tf.keras.layers.Dense(1, activation='sigmoid')(x)
    model = tf.keras.Model(inputs=inputs, outputs=outputs)

    # 6. 编译模型
    optimizer = tf.keras.optimizers.Adam(learning_rate=lr)  # 设置学习率
    model.compile(optimizer=optimizer, loss='binary_crossentropy', metrics=['accuracy'])
    # model.compile(optimizer=optimizer, loss='mean_squared_error')

    return model


def get_model_MultiFullyConnectedLayer(lr = 1e-4):
    input_shape_1 = len(disease_list)

    # 5. 构建模型
    model = tf.keras.Sequential([
        tf.keras.layers.Dense(64, activation='relu', input_shape=(input_shape_1,)),
        tf.keras.layers.Dense(32, activation='relu'),
        tf.keras.layers.Dense(16, activation='relu'),
        tf.keras.layers.Dense(1, activation='sigmoid')
    ])

    # 6. 编译模型
    optimizer = tf.keras.optimizers.Adam(learning_rate=lr)  # 设置学习率
    model.compile(optimizer=optimizer, loss='binary_crossentropy', metrics=['accuracy'])

    return model

def get_model_MSE(lr = 1e-4):
    input_shape_1 = len(disease_list)

    # 5. 构建模型
    model = tf.keras.Sequential([
        tf.keras.layers.Dense(1, activation='sigmoid', input_shape=(input_shape_1,))
    ])

    # 6. 编译模型
    optimizer = tf.keras.optimizers.Adam(learning_rate=lr)  # 设置学习率
    model.compile(optimizer=optimizer, loss='mean_squared_error', metrics=['accuracy'])

    return model