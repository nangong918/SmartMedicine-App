package utils;

import com.alibaba.fastjson.JSON;
import com.czy.api.domain.dto.base.BaseResponse;
import com.czy.api.exception.ExceptionEnums;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author 13225
 * @date 2025/4/29 10:07
 */
public class ResponseUtils {

    public static Mono<Void> setErrorResponse(ServerWebExchange exchange, String errorMessage) {
        BaseResponse<Void> responseJson = new BaseResponse<>(HttpStatus.FORBIDDEN.getReasonPhrase(), errorMessage, null);
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String jsonResponse = JSON.toJSONString(responseJson);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(jsonResponse.getBytes())));
    }

    public static Mono<Void> setErrorResponse(ServerWebExchange exchange, ExceptionEnums exceptionEnums) {
        BaseResponse<Void> responseJson = BaseResponse.LogBackError(exceptionEnums);
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String jsonResponse = JSON.toJSONString(responseJson);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(jsonResponse.getBytes())));
    }

}
