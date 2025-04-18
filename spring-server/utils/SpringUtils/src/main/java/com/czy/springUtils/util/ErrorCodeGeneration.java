package com.czy.springUtils.util;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.security.SecureRandom;
import java.util.Random;

/**
 * @author 13225
 * @date 2024/9/6 15:22
 */
public class ErrorCodeGeneration {

    public static void errorCodeGenerate(ObjectNode responseJson){
        int errorCodeNum = generateRandomNumber();
        for(int i = 0; i < errorCodeNum; i++){
            String key = generateRandomString(generateRandomNumber());
            String message = generateRandomString(generateRandomNumber());

            responseJson.put(key,message);
        }
    }

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();

    private static String generateRandomString(int length) {
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            stringBuilder.append(CHARACTERS.charAt(index));
        }
        return stringBuilder.toString();
    }

    private static int generateRandomNumber() {
        Random random = new Random();
        // 生成一个在 6 到 20 之间的随机整数，包括 6 和 20
        return random.nextInt(15) + 6;
    }

}
