# Triones Commons Context

请求上下文模块：把当前执行人（`Actor`）放进线程上下文，在一次请求或任务的生命周期中随时取到用户 ID、租户 ID、角色、traceId 等信息，并同步写入日志 MDC。

底层使用 [TransmittableThreadLocal](https://github.com/alibaba/transmittable-thread-local)，可以跨线程池传递上下文。

## 引入依赖

```xml
<dependency>
    <groupId>com.trionesdev.commons</groupId>
    <artifactId>commons-context</artifactId>
    <version>0.0.3-SNAPSHOT</version>
</dependency>
```

## 核心类

| 类 | 作用 |
| --- | --- |
| `Actor` | 执行人信息载体 |
| `ActorContextHolder` | 静态方法读写当前线程的 `Actor`，并维护 MDC |
| `ActorContext` | `ActorContextHolder` 的实例化封装，提供空值安全的读取方法，适合注册为 Spring Bean 注入使用 |
| `ActorContextUtils` | `runAs` 的静态快捷入口 |
| `ActorRoleEnum` | 角色枚举：`PERSONAL_USER`、`TENANT_USER`、`TENANT_MEMBER`、`ADMIN_USER`、`AGENT_USER`、`BOSS_USER`（`USER` 已废弃） |
| `ActorConstants` | 跨服务透传用的 HTTP 请求头名称及 MDC 键名 |

### Actor 字段

| 字段 | 说明 |
| --- | --- |
| `traceId` | 链路追踪 ID |
| `role` | 角色，取值为 `ActorRoleEnum` 的名称 |
| `actorId` | 执行人 ID |
| `userId` | 用户 ID |
| `memberId` | 租户成员 ID |
| `tenantId` | 租户 ID |
| `time` | 请求时间 |
| `ipAddress`、`userAgent`、`clientOs` | 客户端信息 |
| `attributes` | 扩展属性 |
| `tenantMemberId` | 已废弃，使用 `memberId` |

## 使用方式

### 设置与清理

通常在 Web Filter / 拦截器中解析 token 或请求头后设置，并在 `finally` 中清理，避免线程复用造成上下文串号：

```java
Actor actor = Actor.builder()
        .traceId(traceId)
        .userId(userId)
        .tenantId(tenantId)
        .role(ActorRoleEnum.TENANT_MEMBER.name())
        .time(Instant.now())
        .build();
ActorContextHolder.setActor(actor);
try {
    chain.doFilter(request, response);
} finally {
    ActorContextHolder.resetActor();
}
```

### 读取

```java
ActorContext actorContext = new ActorContext(); // 或注册为 Bean 后注入

String userId = actorContext.getUserId();      // 无 Actor 时返回 null
String tenantId = actorContext.getTenantId();
Instant time = actorContext.getTime();         // 无 Actor 或未设置 time 时返回 Instant.now()
boolean anonymous = actorContext.isEmpty();    // actorId、userId、memberId 均为空时为 true
```

`oneself(operatorId)` 按角色判断某条数据是否属于当前执行人：

| 角色 | 判断方式 |
| --- | --- |
| `PERSONAL_USER` | 比较 `userId` |
| `TENANT_USER`、`TENANT_MEMBER` | 比较 `memberId` |
| `ADMIN_USER`、`AGENT_USER` | 始终为 `true` |
| 其他 | `false` |

### 临时切换身份

```java
// 以指定 Actor 执行，结束后恢复原 Actor
ActorContextUtils.runAs(systemActor, () -> doSomething());
String result = ActorContextUtils.runAs(systemActor, () -> query());

// 仅切换租户执行
actorContext.asTenant("tenant-1").run(() -> doSomething());
```

切换基于栈实现，可以嵌套使用。回调抛出异常时也会恢复原 Actor；当前没有 Actor 时同样可以调用，结束后会恢复为无 Actor 的状态。`runAs(null, ...)` 表示以无 Actor 的状态执行。

### 日志 MDC

设置 Actor 时会写入以下 MDC 键，可在日志 pattern 中直接使用，例如 `%X{traceId}`：

`traceId`、`actorId`、`userId`、`tenantId`、`memberId`、`role`

Actor 中值为空的字段会从 MDC 中移除，切换身份后不会残留上一个 Actor 的值。`resetActor()` 只移除以上这些键，不影响其他组件写入的 MDC。

### 跨服务透传

`ActorConstants` 定义了透传用的请求头名称，例如 `X-Request-TraceId`、`X-Request-UserId`、`X-Request-TenantId`、`X-Request-MemberId`、`X-Request-Role`。本模块只提供常量，读写请求头需由网关、Filter 或 Feign 拦截器自行实现。

## 注意事项

- **线程池传递**：TransmittableThreadLocal 只有在线程池被 `TtlExecutors.getTtlExecutorService(...)` 包装，或启用 TTL Java Agent 时，才能把上下文正确传递给池中的线程。
- `resetActor()` 会同时清空 `runAs` / `asTenant` 的切换栈，请在请求结束时调用。
- `getMemberId()` 在 `memberId` 为空时会退回到已废弃的 `tenantMemberId`。
- **`oneself` 要求角色有效**：`role` 为空或不在 `ActorRoleEnum` 中时，会抛出 `NullPointerException`。
- `getActorId(Class)` 只是对字符串 `actorId` 做强制类型转换，传入 `String.class` 以外的类型会抛出 `ClassCastException`。
- `actorOneself`、`userOneself`、`memberOneself`、`hasXxxPermission` 已废弃，请使用 `oneself`。
