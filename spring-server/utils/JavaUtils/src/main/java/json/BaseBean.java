package json;

import com.alibaba.fastjson.JSON;


/**
 * @author 13225
 * 只能测试时候调用toJsonString，运行时候禁止调用，因为这个本质是反射。
 */
public interface BaseBean {

    default String toJsonString() throws com.alibaba.fastjson.JSONException{
        return JSON.toJSONString(toJson(), true); // 使用格式化输出
    }

    default com.alibaba.fastjson.JSONObject toJson() {
        return (com.alibaba.fastjson.JSONObject) JSON.toJSON(this); // 将当前对象转换为 JSONObject
    }

}
