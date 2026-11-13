# Triones Commons Exception

统一业务异常模块。异常只携带错误码 `code` 和参数，消息文本从资源文件按语言加载，以支持错误信息国际化；也可以不配置资源文件，直接在代码里给出默认消息。

## 引入依赖

```xml
<dependency>
    <groupId>com.trionesdev.commons</groupId>
    <artifactId>commons-exception</artifactId>
    <version>0.0.3-SNAPSHOT</version>
</dependency>
```

## 异常类型

所有异常均继承 `TrionesException`（`RuntimeException`），可通过 `getCode()` 取错误码、`getMessage()` 取解析后的消息。

| 异常 | 建议场景 |
| --- | --- |
| `TrionesException` | 基类 |
| `BusinessException` | 业务规则不满足 |
| `ValidationException` | 参数校验失败 |
| `NotFoundException` | 资源不存在 |
| `DuplicatedException` | 资源重复 |
| `PermissionDeniedException` | 无权限 |
| `InternalRequestException` | 调用内部服务失败（`commons-feign` 使用） |

每个异常都提供相同的一组构造器：

```java
new BusinessException(String code, ArgumentValue... params)
new BusinessException(String code, String defaultMessage, ArgumentValue... params)
new BusinessException(Throwable cause, String code, ArgumentValue... params)
new BusinessException(TrionesError error)
```

`ErrorResponse`（`code` + `message`）可作为接口返回的错误体，供全局异常处理器使用。

## 消息解析规则

构造异常时，消息按以下顺序确定：

1. 按 `code` 查找 `Locale.getDefault()` 下的文本：先查代码注册的 `codeMap`，再查资源文件。找到后用参数做 `MessageFormat` 格式化。
2. 找不到时，用 `defaultMessage` 做 `MessageFormat` 格式化。
3. 两者都没有时，`getMessage()` 为 `null`。

消息解析**不会抛出异常**：资源文件不存在时跳过该路径并记录 `WARNING` 日志（通过 `System.Logger` 输出），模板格式非法时返回原文。这样原本要抛出的业务异常不会被掩盖。

## 使用方式

### 1. 通过资源文件

在 `src/main/resources/i18n/` 下按 `ResourceBundle` 规范放置资源文件：

![资源文件目录](images/img.png)

```properties
# i18n/error_zh_CN.properties
USER_NOT_FOUND=用户不存在
USER_LOCKED=用户 {0} 已被锁定，请 {1} 分钟后重试
```

应用启动时注册资源路径（可传多个，支持 `classpath:` 前缀）：

```java
ExceptionResourceProperties.setResourcePaths("i18n/error");

throw new NotFoundException("USER_NOT_FOUND");
throw new BusinessException("USER_LOCKED", ArgumentValue.of("alice", 30));
```

### 2. 通过代码注册

```java
Map<String, String> messages = new HashMap<>();
messages.put("USER_NOT_FOUND", "用户不存在");
ExceptionResourceProperties.codeMap.put(Locale.SIMPLIFIED_CHINESE, messages);
```

### 3. 直接给出默认消息

不配置资源文件时，第二个参数作为默认消息：

```java
throw new BusinessException("SYSTEM_ERROR", "测试 {0} {1}", ArgumentValue.of("111", "222"));
// getMessage() -> "测试 111 222"
```

### 按请求语言返回消息

异常构造时使用 `Locale.getDefault()`。如需按请求语言返回，可在全局异常处理器中调用 `getMessage(Locale)`，它会连同参数一起按指定语言重新解析：

```java
@ExceptionHandler(TrionesException.class)
public ErrorResponse handle(TrionesException ex, Locale locale) {
    return new ErrorResponse(ex.getCode(), ex.getMessage(locale));
}
```

该语言下没有对应资源时返回 `getMessage()`。参数值可以通过 `getArguments()` 获取。

## 注意事项

- **缓存**：资源文件中的消息按 `Locale` 懒加载并缓存，调用 `setResourcePaths` 会清空这部分缓存。代码注册的 `codeMap` 不受影响。
- `codeMap` 是线程安全的 `ConcurrentHashMap`，key 和 value 不能为 `null`。
- **`MessageFormat` 转义**：单引号 `'` 在 `MessageFormat` 中是转义符，消息中要输出单引号需写成 `''`。数字参数会按本地格式输出，例如 `1,000`。
