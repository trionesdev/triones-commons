# Triones Commons Lock

锁抽象模块：业务代码只依赖 `TrionesLockTemplate` / `TrionesLock` 接口，按部署形态切换实现。

- `RedisLockTemplate`：基于 Redisson 的分布式锁，适合多实例部署。
- `ThreadLockTemplate`：基于 `ReentrantLock` 的单 JVM 进程内锁，适合单机运行或测试。

## 引入依赖

```xml
<dependency>
    <groupId>com.trionesdev.commons</groupId>
    <artifactId>commons-lock</artifactId>
    <version>0.0.3-SNAPSHOT</version>
</dependency>

<!-- 使用 RedisLockTemplate 时需自行引入（本模块中为 optional，编译版本 3.35.0） -->
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson</artifactId>
</dependency>
```

## 接口

```java
public interface TrionesLockTemplate {
    TrionesLock getLock(String lockName);
}

public interface TrionesLock extends java.util.concurrent.locks.Lock {
    // 最多等待 waitTime；获取成功后 leaseTime 到期自动释放
    boolean tryLock(long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException;
}
```

`lockName` 相同即为同一把锁。

## 使用方式

### 注册

```java
@Bean
public TrionesLockTemplate lockTemplate(RedissonClient redissonClient) {
    return new RedisLockTemplate(redissonClient);
    // 单机 / 测试环境：return new ThreadLockTemplate();
}
```

### 加锁

```java
TrionesLock lock = lockTemplate.getLock("order:pay:" + orderId);
if (!lock.tryLock(3, 30, TimeUnit.SECONDS)) {
    throw new BusinessException("ORDER_PROCESSING");
}
try {
    // 业务逻辑
} finally {
    lock.unlock();
}
```

请始终在 `finally` 中释放锁。

## 实现差异

| | `RedisLockTemplate` | `ThreadLockTemplate` |
| --- | --- | --- |
| 作用范围 | 跨进程、跨实例 | 当前 JVM |
| 可重入 | 是 | 是 |
| `lock()` 自动续期 | 是（Redisson 看门狗） | 不适用 |
| `leaseTime` 自动释放 | 是 | 是 |
| `newCondition()` | 不支持 | 不支持 |

时间参数约定与 Redisson 一致：`waitTime < 0` 表示一直等待，`leaseTime <= 0` 表示不过期。

## 注意事项

### ThreadLockTemplate

- **租约到期后锁会被自动释放**：其他线程可以立即获取，原持有线程之后再调用 `unlock()` 不做任何操作。业务执行时间可能超过 `leaseTime` 时，请改用不带租约的 `lock()` / `tryLock(time, unit)`。
- 重入时如果指定了 `leaseTime`，会刷新到期时间，和 Redisson 的行为一致。
- **非持有线程调用 `unlock()` 会报错**：锁被其他线程持有时，调用 `unlock()` 会抛出 `IllegalMonitorStateException`。锁处于空闲状态时，`unlock()` 不做任何操作。
- **锁对象不会回收**：每个 `lockName` 对应的锁会一直保存在静态 Map 中。锁名中包含订单号这类无限增长的值时，内存会持续增长。
- 多实例部署时它**不能**提供互斥，请改用 `RedisLockTemplate`。

### RedisLockTemplate

- 行为与 Redisson `RLock` 一致：指定 `leaseTime` 时不会自动续期，业务执行时间超过 `leaseTime` 会导致锁提前释放；用 `lock()` 或不指定 `leaseTime` 时，由看门狗自动续期。
- 只有持有锁的线程才能 `unlock()`，否则 Redisson 会抛出 `IllegalMonitorStateException`。
