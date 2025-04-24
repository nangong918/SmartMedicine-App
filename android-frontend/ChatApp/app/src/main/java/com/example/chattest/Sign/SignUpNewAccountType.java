package com.example.chattest.Sign;

import java.nio.file.Path;

public class SignUpNewAccountType {

    public byte[] NewUser_imageBytes;

    public String Name;
    public String Account;
    public String Password;

    public SignUpNewAccountType() {
    }

    public SignUpNewAccountType(byte[] newUser_imageBytes, String name, String account, String password) {
        NewUser_imageBytes = newUser_imageBytes;
        Name = name;
        Account = account;
        Password = password;
    }
}
