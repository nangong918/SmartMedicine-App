package com.czy.domain.vo.entity.message.message;

public class ContactItemVo {
    // 头像（支持网络 URL 或本地 URI）
    public String avatarUrl = "";

    // 名称
    public String name = "";
    // 备注
    public String remark = "";

    public ContactItemVo(){
    }

    public ContactItemVo(ContactItemVo vo){
        this.avatarUrl = vo.avatarUrl;
        this.name = vo.name;
        this.remark = vo.remark;
    }
}
