package com.example.chattest.User;

import java.util.HashSet;
import java.util.Set;

public class User {
    public static boolean Logged = false;
    public static byte[] User_image;
    public static String Name;
    public static String account;
    public static int user_id;
    public static String password;
    public static Set<Integer> user_collection_Article = new HashSet<>();
    //通过账户来获得user_id
    public static void get_user_id_by_account(){

    }



}
