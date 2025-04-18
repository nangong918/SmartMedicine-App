package com.czy.springUtils.util;

import org.junit.jupiter.api.Test;


/**
 * @author 13225
 * @date 2025/4/10 17:34
 */
class EncryptUtilTest {

    @Test
    void bcryptVerify() {
        String password = EncryptUtil.bcryptEncrypt("123456");
        String password2 = EncryptUtil.bcryptEncrypt("1234567");
        System.out.println(password);
        boolean result = EncryptUtil.bcryptVerify("123456", password);
        System.out.println(result);
        boolean result2 = EncryptUtil.bcryptVerify("123456", password2);
        System.out.println(result2);
    }
}