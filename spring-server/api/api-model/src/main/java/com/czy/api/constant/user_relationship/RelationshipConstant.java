package com.czy.api.constant.user_relationship;

/**
 * @author 13225
 * @date 2025/4/4 23:36
 */
public class RelationshipConstant {

    public static final String serviceName = "user-relationship-service";
    public static final String serviceRoute = "/" + serviceName;
    public static final String Relationship_CONTROLLER = "/relation";


    public static final String Search_User_ByAccount = "/searchUser";
    public static final String Search_User_ByName = "/searchUserByName";
    public static final String Get_Add_Me_Request_List = "/getAddMeRequestList";
    public static final String Get_Handle_My_Add_User_Response_List = "/getHandleMyAddUserResponseList";
    public static final String Get_My_Friend_List = "/getMyFriendList";
    public static final String Get_My_Friend_Apply_List = "/getMyFriendApplyList";

    public static final String serviceUri = "lb://" + serviceName;

}
