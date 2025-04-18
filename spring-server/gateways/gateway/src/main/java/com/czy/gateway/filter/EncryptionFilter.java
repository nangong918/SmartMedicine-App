package com.czy.gateway.filter;

import com.czy.springUtils.util.TokenAesUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 13225
 * @date 2025/4/5 0:05
 */

@Slf4j
@RequiredArgsConstructor
@Component
public class EncryptionFilter extends AbstractGatewayFilterFactory<EncryptionFilter.Config> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public static final String HEADER_NAME = "PIKIXUX7w0Zvjde1";
    public static final String HEADER_VALUE_NAME = "9I6WQBHayw+sZ0O0";

    @Override
    public GatewayFilter apply(Config config) {
        return new OrderedGatewayFilter(
                (exchange, chain) -> {
                    ServerHttpRequest request = exchange.getRequest();

                    // 检查是否需要处理加密
                    if (!shouldHandleEncryption(request)) {
                        return chain.filter(exchange);
                    }

                    // 处理请求解密
                    return handleRequestDecryption(exchange, chain);
                },
                // 在ip和jwt之后就进行解密
                2
        );
    }

    /**
     * 检查是否需要处理加密
     * @param request   请求对象
     * @return          是否需要处理加密
     */
    private boolean shouldHandleEncryption(ServerHttpRequest request) {
        // 检查Content-Type
        String contentType = request.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
        if (contentType == null || !contentType.contains("application/json")) {
            return false;
        }

        // 检查加密头
        String headerValue = request.getHeaders().getFirst(HEADER_NAME);
        return HEADER_VALUE_NAME.equals(headerValue);
    }

    /**
     * 处理请求解密
     * @param exchange  请求对象
     * @param chain     过滤器链
     * @return          处理后的请求
     */
    private Mono<Void> handleRequestDecryption(ServerWebExchange exchange, GatewayFilterChain chain) {
        return DataBufferUtils.join(exchange.getRequest().getBody())
                .flatMap(dataBuffer ->
                        processRequestBody(dataBuffer, exchange, chain)
                )
                .onErrorResume(this::handleError);
    }

    /**
     * 处理请求体解密
     * @param dataBuffer        请求体
     * @param exchange          请求对象
     * @param chain             过滤器链
     * @return
     */
    private Mono<Void> processRequestBody(DataBuffer dataBuffer, ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            String originalBody = extractOriginalBody(dataBuffer);
            String modifiedBody = handleRequestBody(originalBody);

            ServerHttpRequestDecorator decoratedRequest = createDecoratedRequest(exchange, modifiedBody);
            ServerHttpResponseDecorator decoratedResponse = createDecoratedResponse(exchange);

            // 继续过滤器链
            return chain.filter(exchange.mutate()
                    .request(decoratedRequest)
                    .response(decoratedResponse)
                    .build());
        } catch (Exception e){
            return handleError(e);
        }
        finally {
            DataBufferUtils.release(dataBuffer);
        }
    }

    /**
     * 从DataBuffer中提取原始请求体
     * @param dataBuffer        请求体
     * @return                  原始请求体str
     */
    private String extractOriginalBody(DataBuffer dataBuffer) {
        byte[] bytes = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * 创建装饰后的请求
     * @param exchange          请求对象
     * @param modifiedBody      修改后的请求体
     * @return                  请求对象
     */
    private ServerHttpRequestDecorator createDecoratedRequest(ServerWebExchange exchange, String modifiedBody) {
        return new ServerHttpRequestDecorator(exchange.getRequest()) {
            @NotNull
            @Override
            public Flux<DataBuffer> getBody() {
                return Flux.just(
                        exchange.getResponse()
                                .bufferFactory()
                                .wrap(modifiedBody.getBytes(StandardCharsets.UTF_8))
                );
            }
        };
    }

    /**
     * 创建装饰后的响应
     * @param exchange  请求对象
     * @return          响应对象
     */
    private ServerHttpResponseDecorator createDecoratedResponse(ServerWebExchange exchange) {
        return new ServerHttpResponseDecorator(exchange.getResponse()) {
            @NotNull
            @Override
            public Mono<Void> writeWith(@NotNull Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                    return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                        DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
                        DataBuffer join = bufferFactory.join(dataBuffers);
                        byte[] content = new byte[join.readableByteCount()];
                        join.read(content);
                        DataBufferUtils.release(join);

                        String encryptedResponse = encryptResponseContent(content);
                        return bufferFactory.wrap(encryptedResponse.getBytes(StandardCharsets.UTF_8));
                    }));
                }
                return super.writeWith(body);
            }
        };
    }

    /**
     * 响应加密
     * @param content   响应内容
     * @return          加密后的响应内容
     */
    private String encryptResponseContent(byte[] content) {
        try {
            String responseBody = new String(content, StandardCharsets.UTF_8);
            return encryptResponse(responseBody);
        } catch (Exception e) {
            log.error("响应加密失败", e);
            return new String(content); // 返回原始内容，避免崩溃
        }
    }


    /**
     * 除去混淆 + 解密
     * <p>
     * {
     *     "data": "{\"userName\":\"JohnDoe\",\"userId\":\"123456\"}"
     * }
     *
     * @param originalBody      请求体String
     * @return                  解密后的JSON String
     */
    private String handleRequestBody(String originalBody) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(originalBody);
        JsonNode dataNode = jsonNode.get("data");
        String data = dataNode.asText();
        data = decrypt(data);

        // 解析解密后的JSON字符串
        JsonNode decryptedJsonNode = objectMapper.readTree(data);
        return decryptedJsonNode.toString();
    }

    /**
     * 解密
     * @param data
     * @return
     */
    private String decrypt(String data) {
        return TokenAesUtil.decryptCBC(data, TokenAesUtil.key);
    }

    private Mono<Void> handleError(Throwable error) {
        log.error("处理加密请求体失败", error);
        return Mono.error(error); // 或根据需求返回适当的响应
    }

    /**
     * 响应体加密处理
     */
    private String encryptResponse(String responseBody) {
        // 加密响应体
        String encryptedData = TokenAesUtil.encryptCBC(responseBody, TokenAesUtil.key);

        // 构建响应结构
        ObjectNode responseNode = objectMapper.createObjectNode();
        responseNode.put("data", encryptedData);

        // 添加错误码等信息（根据你的ErrorCodeGeneration类调整）
//        ErrorCodeGeneration.errorCodeGenerate(responseNode);

        return responseNode.toString();
    }


    public static class Config {
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return new ArrayList<>();
    }


}
