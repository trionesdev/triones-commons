# Triones Commons Feign

OpenFeign 扩展：把内部服务调用失败的 HTTP 响应统一转换为 `InternalRequestException`（来自 `commons-exception`），并提供读取 Feign 响应的工具方法。

## 引入依赖

```xml
<dependency>
    <groupId>com.trionesdev.commons</groupId>
    <artifactId>commons-feign</artifactId>
    <version>0.0.3-SNAPSHOT</version>
</dependency>
```

会传递引入 `feign-core`、`fastjson2`、`guava` 和 `commons-exception`。

## DefaultErrorDecoder

### 注册

原生 Feign：

```java
UserClient client = Feign.builder()
        .errorDecoder(new DefaultErrorDecoder())
        .target(UserClient.class, "http://user-service");
```

Spring Cloud OpenFeign，全局生效：

```java
@Configuration
public class FeignConfig {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new DefaultErrorDecoder();
    }
}
```

### 转换规则

Feign 只会对非 2xx 响应调用 `ErrorDecoder`。返回的异常类型均为 `InternalRequestException`：

1. **透传下游错误**：无论状态码是多少，只要响应体是带 `code` 的 `ErrorResponse` JSON（如 `{"code":"USER_LOCKED","message":"..."}`），异常的 `code` 和 `message` 就直接取下游的值。消息原样保留，不会被当作 `MessageFormat` 模板处理。
2. **兜底**：响应体为空、不是 JSON 或没有 `code` 时，`code` 为 `DefaultErrorDecoder.DEFAULT_CODE`（`INTERNAL_REQUEST_EXCEPTION`），消息按状态码给出：

| 响应状态 | 默认消息 |
| --- | --- |
| 401 | `Internal Request Not Authentication` |
| 404 | `Internal Request Not Found` |
| 500 | `Internal Request Internal Exception` |
| 其他 | `Internal Request Unknown Exception` |

两种情况都会记录 `error` 日志，内容包括状态码、URL、Feign 方法，以及下游错误码或响应体（截断到 1000 字符）。

```java
try {
    userClient.getUser(id);
} catch (InternalRequestException e) {
    if ("USER_LOCKED".equals(e.getCode())) {
        // 处理下游业务错误
    }
}
```

### 注意事项

- **本地资源文件优先**：如果 `commons-exception` 的资源文件中也定义了下游的 `code` 或 `INTERNAL_REQUEST_EXCEPTION`，异常消息会以本地资源为准，见 [commons-exception](../commons-exception)。
- HTTP 状态码不会保存在异常上。如果需要按状态码区分处理，请自定义 `ErrorDecoder`。

## FeignUtils

| 方法 | 说明 |
| --- | --- |
| `requestUrl(Response)` | 返回原始请求的 URL |
| `requestBody(Response)` | 以 UTF-8 读取响应体，无响应体时返回 `null` |

响应体是流，只能读取一次。
