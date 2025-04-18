package exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 13225
 * @date 2025/1/3 17:04
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class AppException extends RuntimeException{

    /**
     * 错误码
     */
    private String errCode;
    /**
     * 状态码 [不赋值默认200]
     */
    private int statusCode = 200;

    /**
     * 构造方法
     * @param errMsg    错误信息
     */
    public AppException(String errMsg) {
        super(errMsg);
    }

    public AppException(String errMsg, Throwable e) {
        super(errMsg, e);
    }

    /**
     * 构造方法
     * @param errCode       错误码
     * @param errMsg        错误信息
     */
    public AppException(String errCode, String errMsg) {
        super(errMsg);
        this.errCode = errCode;
    }

    /**
     * 构造方法
     * @param errCode       错误码
     * @param statusCode    状态码
     * @param errMsg        错误信息
     */
    public AppException(String errCode, int statusCode, String errMsg) {
        super(errMsg);
        this.errCode = errCode;
        this.statusCode = statusCode;
    }

    /**
     * 构造方法
     * @param errCode       错误码
     * @param statusCode    状态码
     * @param errMsg        错误信息
     * @param cause         异常
     */
    public AppException(String errCode, int statusCode, String errMsg, Throwable cause) {
        super(errMsg, cause);
        this.errCode = errCode;
        this.statusCode = statusCode;
    }

}
