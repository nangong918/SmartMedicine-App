package com.czy.appcore.utils.password;

public class PasswordTextUtil {

    // 检查密码是否合法：6-16位，包含数字、大小写字母、特殊字符、不允许有ASCII码之外的字符
    public static boolean isPasswordLegal(String password) {
        return password != null
                && password.length() >= 6
                && password.length() <= 16
                && password.matches(".*[a-z].*") // 包含小写字母
/*                && password.matches(".*[A-Z].*") // 包含大写字母
                && password.matches(".*\\d.*") // 包含数字
                && password.matches(".*[^a-zA-Z\\d].*") // 包含特殊字符
                && !password.matches(".*[\\s].*") // 不允许空格
                && password.matches("\\A\\p{ASCII}*\\z") // 只包含 ASCII 字符*/
                ;
    }

}
