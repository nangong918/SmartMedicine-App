package com.czy.dal.dto.netty.request;


import com.czy.baseUtilsLib.json.BaseBean;
import com.czy.dal.dto.netty.base.BaseRequestData;

/**
 * @author 13225
 * @date 2025/2/8 18:18
 * 思考文件的相通性检查，相同的文件发送了两次就不要存储了
 */
public class DeleteAllMessageRequest extends BaseRequestData implements BaseBean {
}
