package exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

/**
 * @author 13225
 * @date 2025/7/29 9:46
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NettyException extends RuntimeException {

    private Long receiverId;
    private String code;
    private String message;

    private ExceptionEnums exceptionEnums;

    public NettyException(@NonNull ExceptionEnums exceptionEnums, @NonNull Long receiverId){
        super(exceptionEnums.getMessage());
        this.exceptionEnums = exceptionEnums;
        this.code = exceptionEnums.getCode();
        this.message = exceptionEnums.getMessage();
        this.receiverId = receiverId;
    }

    public NettyException(@NonNull ExceptionEnums exceptionEnums, @NonNull Long receiverId, Exception e){
        super(e);
        this.exceptionEnums = exceptionEnums;
        this.code = exceptionEnums.getCode();
        this.message = exceptionEnums.getMessage() + "\n" + e.getMessage();
        this.receiverId = receiverId;
    }

    public NettyException(Exception e, @NonNull Long receiverId){
        super(e);
        this.code = "600";
        this.message = e.getMessage();
        this.receiverId = receiverId;
    }
}
