# Triones Commons Core

Triones Commons 的核心模块，提供 JSON 转换、分页模型、JWT 签发与验证、通用工具及业务模型。基于 Java 17；本模块不负责 HTTP 登录接口或 Spring Security 安全链配置。

## 引入依赖

```xml
<dependency>
    <groupId>com.trionesdev.commons</groupId>
    <artifactId>commons-core</artifactId>
    <version>0.0.3-SNAPSHOT</version>
</dependency>
```

版本应与项目使用的 Triones Commons 版本保持一致。

## 功能索引

| 包 | 主要内容 |
| --- | --- |
| `com.trionesdev.commons.core.util` | JSON、Jackson、分页、树结构、文件路径、时间、金额、HTML、标识及编号工具 |
| `com.trionesdev.commons.core.page` | `PageInfo`、`PageQuery`、`PageCriteria` 等统一分页模型 |
| `com.trionesdev.commons.core.jwt` | `JwtConfig`、`JwtFacade`、`JwtClaims`、`JwtUtils` |
| `com.trionesdev.commons.core.jackson` | `Instant` 序列化与反序列化、`Long` 序列化组件 |
| `com.trionesdev.commons.core.constant` | 标识相关常量 |
| `com.trionesdev.commons.model` | `ActorProfile`、`Attachment`、`EnumOption`、`Operator` 等模型 |

## JSON 转换

```java
import com.fasterxml.jackson.core.type.TypeReference;
import com.trionesdev.commons.core.util.JsonUtils;
import java.util.Map;

String json = JsonUtils.toJsonString(Map.of("name", "Alice"));
Map<String, String> result = JsonUtils.parse(
        json, new TypeReference<Map<String, String>>() {});
```

默认映射器忽略未知字段、忽略空值属性，并为 `Instant` 注册转换组件。空白 JSON 输入通过 `parse` 返回 `null`，转换失败抛出 `JsonException`。

可通过 `JsonUtils.setObjectMapper(...)` 替换共享映射器，建议在应用初始化时完成。该设置影响所有使用 `JsonUtils` 的调用。

## 分页模型

`PageInfo<T>` 用于统一不同 ORM 的分页返回结构：

| 字段 | 含义 |
| --- | --- |
| `pageNum` | 页码 |
| `pageSize` | 每页条数 |
| `total` | 总记录数 |
| `pages` | 总页数 |
| `rows` | 当前页数据 |

该类是数据容器；查询和分页统计由业务或 ORM 集成层完成。

## JWT

### 配置

| 属性 | 说明 |
| --- | --- |
| `algorithm` | 必填，例如 `JWSAlgorithm.HS256` 或 `JWSAlgorithm.RS256` |
| `secret` | HMAC 密钥字符串，按 UTF-8 转为字节；RSA/EC 不使用此字段生成固定密钥 |
| `expiration` | 有效期，单位为秒，使用按 subject 签发的接口时必须大于零 |

算法族由 `algorithm` 推导，无需配置 `family`。`JwtConfig` 没有提供算法和有效期的默认值，构建配置时应显式设置。

### HMAC 签发与验签

```java
import com.nimbusds.jose.JWSAlgorithm;
import com.trionesdev.commons.core.jwt.JwtConfig;
import com.trionesdev.commons.core.jwt.JwtFacade;
import java.util.Map;

// 从环境变量或密钥管理服务读取，部署前确保已配置。
String secret = System.getenv("JWT_SECRET");
if (secret == null || secret.isBlank()) {
    throw new IllegalStateException("JWT_SECRET is required");
}

JwtConfig config = JwtConfig.builder()
        .algorithm(JWSAlgorithm.HS256)
        .secret(secret)
        .expiration(3600)
        .build();
JwtFacade facade = new JwtFacade(config);

String token = facade.generate("user-123", Map.of("role", "USER"));
String subject = facade.parse(token).getSubject();
Map<String, Object> claims = facade.parseClaims(token);
```

复用 `JwtFacade` 实例。HMAC 签名与验签使用同一密钥，跨实例使用时必须保持算法及 secret 一致。

当前实现将 HMAC secret 补零至算法对应长度，并拒绝超过该长度的输入：HS256 为 32 字节、HS384 为 48 字节、HS512 为 64 字节。这里是 UTF-8 字节数，不是字符数；补零不会增加密钥熵，应提供足够随机的密钥。未提供 secret 时会生成临时随机密钥，重启后不能复用。

### RSA/EC 与固定密钥

仅传入配置时，RSA/EC 会生成临时密钥。需要重启后或多实例间继续验签时，使用持久化私钥 JWK：

```java
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.RSAKey;
import com.trionesdev.commons.core.jwt.JwtConfig;
import com.trionesdev.commons.core.jwt.JwtFacade;

// privateJwkJson 是从安全配置读取的完整 RSA 私钥 JWK JSON。
RSAKey key = RSAKey.parse(privateJwkJson);
JwtFacade facade = new JwtFacade(
        JwtConfig.builder()
                .algorithm(JWSAlgorithm.RS256)
                .expiration(3600)
                .build(),
        key);
```

`RSAKey.parse` 的解析异常需由调用方处理。私钥应包含签名所需参数；构造时会检查签名密钥与算法的兼容性。私钥不要提交到仓库或写入日志。

当前自动生成密钥支持 RSA 算法族，以及 EC 的 ES256、ES384、ES512 和 HMAC 的 HS256、HS384、HS512。EC 分别使用 P-256、P-384、P-521 曲线。

### 验证行为与边界

- `parse` 固定校验配置的算法和签名，并要求 `exp` 存在且未过期；存在 `nbf` 时检查生效时间。
- 当前未自动校验 issuer、audience，也未配置时钟容差；业务应按需要补充校验。
- 解析、签名校验或时间校验失败时，抛出 `IllegalArgumentException`。
- `generate(JWTClaimsSet)` 使用调用方提供的完整 claims，不自动补充有效期；调用方应自行设置 `exp`。
- `getPublicJwkSet()` 当前返回单个公钥 JWK 对象，而非含 `keys` 数组的标准 JWK Set；HMAC 调用此方法会抛出 `UnsupportedOperationException`。

JWT 只做签名，不会加密载荷，不应在 claims 中放入密码或密钥。