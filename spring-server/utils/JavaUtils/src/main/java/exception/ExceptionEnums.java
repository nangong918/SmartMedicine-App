package exception;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 13225
 * @date 2025/6/26 17:39
 */
public interface ExceptionEnums {

    String getCode();
    String getMessage();

    default Map<String, String> getDataMap(){
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("code", getCode());
        dataMap.put("message", getMessage());
        return dataMap;
    }
}
