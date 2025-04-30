package com.example.chattest.URL;

import com.example.chattest.Test.MyDebug;
import com.example.chattest.User.User;

import java.util.Objects;

public class UrlUtil {

    //-------------------------IP:localhost

    public static String IpAddress = "172.18.28.66";

    //-------------------------Url

    private static final String recommend_url = "/user_recommend";
    private static final String Sign_url = "/sign";
    private static final String SignIn_url = "/signIn";
    private static final String checkAccount_url = "/checkAccount";
    private static final String signUp_url = "/signUp";
    private static final String UserOnlineService_url = "/user_online";
    private static final String POST_heartbeat = "/post_heartbeat";
    private static final String GET_heartbeat = "/heartbeat";
    private static final String recommend_not_logged = "/ResponseNotLogged";
    private static final String recommend_logged = "/response_logged";
    private static final String dialogAI_url = ":30004/reply";
    private static final String article_url = "/article";
    private static final String get_article_list_url = "/get_article_list";
    private static final String search_list_url = "/search";
    private static final String collection_list_url = "/collection";
    private static final String medical_predict_url = ":30003/medical_predict";


    //-------------------------方法

    //getIp
    public static String GetIP(){
        return IpAddress;
    }

    public static String Get_SpringBoot_url(String IP){
        if(Objects.equals(IP, "")){
            IP = "localhost";
        }
        return "http://" + IP + ":8080";
    }

    public static String Get_IP_url(String IP){
        if(Objects.equals(IP, "")){
            IP = "localhost";
        }
        return "http://" + IP;
    }

    public static String GetDialogAI_url(){
        String IP = IpAddress;
        if(Objects.equals(IpAddress, "")){
            IP = "localhost";
        }
        return "http://" + IP + dialogAI_url;
    }

    public static String GetRecommendList(){
        String url = Get_SpringBoot_url(GetIP()) + recommend_url;
        if(!User.Logged){
            url = url + recommend_not_logged;
        }
        else{
            url = url + recommend_logged + "/" + User.user_id;
        }
        MyDebug.Print("请求推荐的url："+url);
        return url;
    }

    public static String GetSignInUrl(){
        return Get_SpringBoot_url(GetIP()) + Sign_url + SignIn_url;
    }

    public static String Get_GET_heartbeatUrl(int user_id){
        String url = Get_SpringBoot_url(GetIP()) + UserOnlineService_url + GET_heartbeat + "/";
        url = url + user_id;
        return url;
    }

    public static String Get_GET_heartbeat_Post_Url(){
        return Get_SpringBoot_url(GetIP()) + UserOnlineService_url + POST_heartbeat;
    }

    public static  String Get_checkAccount_url(String account){
        String url = Get_SpringBoot_url(GetIP()) + Sign_url + checkAccount_url;
        url = url + "/" + account;
        return url;
    }

    public static String Get_SignUp_url(){
        return Get_SpringBoot_url(GetIP()) + Sign_url + signUp_url;
    }

    public static String Get_articleList(){
        return Get_SpringBoot_url(GetIP()) + article_url + get_article_list_url;
    }
    public static String Get_search_url(String data){
        String url = Get_SpringBoot_url(GetIP()) + article_url  + search_list_url;
        url = url + "/" + data;
        return url;
    }
    public static String Get_collection_url(int user_id){
        String url = Get_SpringBoot_url(GetIP()) + article_url  + collection_list_url;
        url = url + "/" + user_id;
        return url;
    }
    public static String Get_medical_predict_Url(){
        return Get_IP_url(GetIP()) + medical_predict_url;
    }
}
