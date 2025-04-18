package jwt;

import json.BaseBean;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/1/15 15:38
 * Dubbo远程调用传输的对象需要进行序列化
 */
@Data
public abstract class BaseJwtPayloadAo implements BaseBean, Serializable {
    /**
     * 标记当前唯一性:当前时间
     */
    protected String currentData = String.valueOf(System.currentTimeMillis());

    /**
     * 子类需要实现的：获取JWT的Subject
     * @return  JWT的Subject
     */
    public abstract String getSubject();
}
