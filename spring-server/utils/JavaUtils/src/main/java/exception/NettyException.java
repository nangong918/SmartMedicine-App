package exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 13225
 * @date 2025/7/29 9:46
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NettyException extends RuntimeException {
    public NettyException(String message) {
        super(message);
    }

    private String code;
    private String message;

    private ExceptionEnums exceptionEnums;

    public NettyException(ExceptionEnums exceptionEnums){
        this.exceptionEnums = exceptionEnums;
        this.code = exceptionEnums.getCode();
        this.message = exceptionEnums.getMessage();
    }

    public NettyException(ExceptionEnums exceptionEnums, Exception e){
        this.exceptionEnums = exceptionEnums;
        this.code = exceptionEnums.getCode();
        this.message = exceptionEnums.getMessage() + "\n" + e.getMessage();
    }

    public NettyException(Exception e){
        this.code = "600";
        this.message = e.getMessage();
    }
}
