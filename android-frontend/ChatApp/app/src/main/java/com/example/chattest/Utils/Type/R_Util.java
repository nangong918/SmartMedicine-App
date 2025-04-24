package com.example.chattest.Utils.Type;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.chattest.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class R_Util {

    // 用<T> 泛型来代替，用于将元素处理为list
    public static <T> List<T> getListFromRData(String keyWord, R_dataType r, TypeReference<List<T>> typeReference) {
        if (r.getFlag()) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode jsonData = objectMapper.readTree(objectMapper.writeValueAsBytes(r.getData()));
                JsonNode listNode = jsonData.get(keyWord);
                List<T> resultList = objectMapper.convertValue(listNode, typeReference);
                System.out.println("Result List: " + resultList);
                return resultList;
            } catch (IOException e) {
                // 处理异常
                e.printStackTrace();
                // 可以选择抛出自定义异常或返回默认值等操作
            }
        }
        return null;
    }

    public static String getStringFromRData(String keyWord,R_dataType r){
        if(r.getFlag()){
            ObjectMapper objectMapper = new ObjectMapper();
            try{
                JsonNode jsonData = objectMapper.readTree(objectMapper.writeValueAsBytes(r.getData()));
                JsonNode StringNode = jsonData.get(keyWord);
                if (StringNode != null) {
                    return StringNode.asText();
                } else {
                    // 如果关键字不存在，可以选择抛出自定义异常或返回默认值等操作
                    throw new IllegalArgumentException("Keyword not found: " + keyWord);
                }
            } catch (IOException e) {
                // 处理异常
                e.printStackTrace();
                // 可以选择抛出自定义异常或返回默认值等操作
            }
            return "";
        }
        return "";
    }

    public static class R_JsonUtils {
        public static <T> T parseData(Object data, Class<T> targetType) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.convertValue(data, targetType);
        }

        public static String toJson(R_dataType rDataType){
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.writeValueAsString(rDataType);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null; // 或者根据需要返回其他默认值或错误处理
            }
        }
    }

    public static Object get_DoubleR_data(R_dataType DoubleR){
        Object singleR = DoubleR.getData();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String JsonData = objectMapper.writeValueAsString(singleR);
            // 解析 JSON 字符串
            JsonNode jsonNode = objectMapper.readTree(JsonData);
            // 获取 "data" 关键字的内容
            JsonNode dataNode = jsonNode.get("data");
            return objectMapper.treeToValue(dataNode, Object.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null; // 或者根据需要返回其他默认值或错误处理
        }
    }

    public static JSONArray get_JsonArray(String keyWord,R_dataType rDataType){
        Object data = rDataType.getData();
        JSONObject ReceivedJson = (JSONObject)data;
        return ReceivedJson.getJSONArray(keyWord);
    }

    public static Object get_dataByJsonObject(String keyWord,R_dataType rDataType){
        Object data = rDataType.getData();
        JSONObject ReceivedJson = (JSONObject)data;
        return ReceivedJson.get(keyWord);
    }
}
