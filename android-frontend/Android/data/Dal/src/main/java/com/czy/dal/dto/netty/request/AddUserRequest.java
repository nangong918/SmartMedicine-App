package com.czy.dal.dto.netty.request;

import com.czy.baseUtilsLib.json.BaseBean;
import com.czy.dal.constant.newUserGroup.ApplyStatusEnum;
import com.czy.dal.dto.http.request.BaseNettyRequest;
import com.czy.dal.dto.netty.base.BaseTransferData;

public class AddUserRequest extends BaseTransferData implements BaseBean {
    public String addUserAccount;
    public String myAccount;
    public String myName;
    public String addContent;
    public Integer source;
    public Integer applyType = ApplyStatusEnum.NOT_APPLY.code;
}
