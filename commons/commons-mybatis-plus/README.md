# Triones Commons MyBatis-Plus

MyBatis-Plus 扩展模块，提供：

- 实体基类（审计字段、逻辑删除）
- 自定义 ID 生成器
- 支持同时查询多个租户的多租户插件
- JSON 集合字段 TypeHandler、忽略大小写的枚举 TypeHandler
- `IPage` 与统一分页模型 `PageInfo` 的互转

## 引入依赖

```xml
<dependency>
    <groupId>com.trionesdev.commons</groupId>
    <artifactId>commons-mybatis-plus</artifactId>
    <version>0.0.3-SNAPSHOT</version>
</dependency>

<!-- 本模块中 mybatis-plus、jackson 为 optional，需由业务项目引入；编译版本 mybatis-plus 3.5.6 -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
</dependency>
```

## 实体基类

| 类 | 字段 |
| --- | --- |
| `BasePO` | `createdAt`、`updatedAt` |
| `BaseLogicDelPO` | `BasePO` + `deleted` |
| `BaseLogicPO` | `BasePO` + `deleted`、`createdBy`、`updatedBy` |
| `BaseEntity` / `BaseLogicEntity` | 已废弃，请改用 `BasePO` / `BaseLogicPO` |

```java
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user")
public class UserPO extends BaseLogicPO {
    @TableId
    private Long id;
    private String name;
}
```

- `deleted` 映射到 `is_deleted` 列，`false` 表示未删除、`true` 表示已删除。删除方法会自动改为逻辑删除，查询会自动过滤已删除的数据。
- 审计字段使用 `@TableField(fill = ...)` 声明，**需要业务项目提供 `MetaObjectHandler`** 才会实际填充：

```java
@Component
public class AuditMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        Instant now = Instant.now();
        strictInsertFill(metaObject, "createdAt", Instant.class, now);
        strictInsertFill(metaObject, "updatedAt", Instant.class, now);
        strictInsertFill(metaObject, "deleted", Boolean.class, false);
        strictInsertFill(metaObject, "createdBy", String.class, currentUserId());
        strictInsertFill(metaObject, "updatedBy", String.class, currentUserId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        strictUpdateFill(metaObject, "updatedAt", Instant.class, Instant.now());
        strictUpdateFill(metaObject, "updatedBy", String.class, currentUserId());
    }
}
```

其中 `currentUserId()` 可以基于 [commons-context](../commons-context) 的 `ActorContext` 实现。

## ID 生成器

`TrionesIdentifierGenerator` 基于 MyBatis-Plus 的 `Sequence`（雪花算法），用于 `@TableId(type = IdType.ASSIGN_ID)` 和 `IdType.ASSIGN_UUID`：

```java
@Bean
public IdentifierGenerator identifierGenerator() {
    return new TrionesIdentifierGenerator(workerId, dataCenterId);
}
```

| 构造器 | 说明 |
| --- | --- |
| `TrionesIdentifierGenerator()` | 根据本机 IP 推算 workerId / dataCenterId |
| `TrionesIdentifierGenerator(InetAddress)` | 根据指定网卡地址推算 |
| `TrionesIdentifierGenerator(long workerId, long dataCenterId)` | 显式指定，**多实例部署推荐** |
| `TrionesIdentifierGenerator(Sequence)` | 使用自定义的 `Sequence` |

使用 IP 推算时，容器环境下不同实例可能得到相同的 workerId，导致 ID 冲突。`nextUUID` 返回 UUID 格式的随机字符串，没有设置 UUID 版本位，不是标准的 v4 UUID。

## 多租户插件（支持多租户同时查询）

`TenantLineMultiInnerInterceptor` 在 MyBatis-Plus `TenantLineInnerInterceptor` 的基础上，允许一次查询多个租户的数据：

`enableTenantMulti()` 返回 `true` 时，按 `getTenantIds()` 的返回值生成条件：

| `getTenantIds()` 返回 | 生成的条件 |
| --- | --- |
| 包含多个值的 `ExpressionList` | `tenant_id IN ('t1', 't2')`，括号由插件自动添加 |
| 只有一个值的 `ExpressionList` | `tenant_id = 't1'` |
| 子查询 `Select` | `tenant_id IN (SELECT ...)` |
| 空列表或 `null` | 退回 `tenant_id = getTenantId()` |

`enableTenantMulti()` 返回 `false` 时与原插件一致，生成 `tenant_id = getTenantId()`。

```java
@Bean
public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    interceptor.addInnerInterceptor(new TenantLineMultiInnerInterceptor(new TenantLineMultiHandler() {
        @Override
        public Expression getTenantId() {
            return new StringValue(currentTenantId());
        }

        @Override
        public boolean enableTenantMulti() {
            return true;
        }

        @Override
        public Expression getTenantIds() {
            return new ExpressionList<>(currentTenantIds().stream().map(StringValue::new).toList());
        }

        @Override
        public boolean ignoreTable(String tableName) {
            return "t_tenant".equals(tableName);
        }
    }));
    interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
    return interceptor;
}
```

- 多租户 `IN` 条件只作用于查询、更新、删除的 WHERE 条件；插入时仍使用 `getTenantId()` 的单个值。
- 通过 MyBatis 配置文件的 `tenantLineValuesHandler` 属性指定 Handler 时，插入、忽略表等逻辑也会使用同一个 Handler。
- 和原插件一样，多租户插件应放在分页插件之前。

## TypeHandler

### JSON 集合字段

把集合以 JSON 字符串存入 `VARCHAR` / `JSON` 列。使用时实体需开启 `autoResultMap`：

```java
@TableName(value = "t_article", autoResultMap = true)
public class ArticlePO extends BasePO {
    @TableField(typeHandler = StringCollectionTypeHandler.class)
    private List<String> tags;

    @TableField(typeHandler = LongCollectionTypeHandler.class)
    private List<Long> categoryIds;
}
```

| Handler | 元素类型 | 反序列化结果 |
| --- | --- | --- |
| `StringCollectionTypeHandler` | `String` | `List` |
| `LongCollectionTypeHandler` | `Long` | `List` |
| `IntegerCollectionTypeHandler` | `Integer` | `List` |
| `DoubleCollectionTypeHandler` | `Double` | `List` |
| `FloatCollectionTypeHandler` | `Float` | `List` |
| `BooleanCollectionTypeHandler` | `Boolean` | `List` |
| `StringListTypeHandler` / `LongListTypeHandler` | — | 已废弃，请改用对应的 `XxxCollectionTypeHandler` |

以上内置 Handler 反序列化后都是 `List`，实体字段请声明为 `List` 或 `Collection`。

### 自定义元素类型

```java
// List<Address>
public class AddressListTypeHandler extends ListTypeHandler<Address> {
    public AddressListTypeHandler(Class<?> type) { super(type); }
    @Override
    protected Class<Address> specificType() { return Address.class; }
}

// Set<String>
public class StringSetTypeHandler extends SetTypeHandler<String> {
    public StringSetTypeHandler(Class<?> type) { super(type); }
    @Override
    protected Class<String> specificType() { return String.class; }
}

// 任意泛型结构
public class ScoreMapTypeHandler extends SpecificTypeHandler<Map<String, Integer>> {
    public ScoreMapTypeHandler(Class<?> type) { super(type); }
    @Override
    public TypeReference<Map<String, Integer>> typeReference() { return new TypeReference<>() {}; }
}
```

需要保留 `(Class<?> type)` 构造器，MyBatis-Plus 会通过它实例化 Handler。

### ObjectMapper

每个 Handler 类各自持有静态 `ObjectMapper`，默认是 `new ObjectMapper()`，不支持 `Instant` 等 Java 8 时间类型。需要时请在启动阶段替换：

```java
CollectionTypeHandler.setObjectMapper(objectMapper);  // 作用于所有 ListTypeHandler / SetTypeHandler 子类
StringCollectionTypeHandler.setObjectMapper(objectMapper);
```

`SpecificTypeHandler` 没有提供 setter，如需定制可在子类中覆写 `parse` / `toJson`。

### 枚举

`EnumTypeHandler` 以 `name()` 写入数据库，读取时**忽略大小写**匹配，无法匹配时返回 `null`，不会抛出异常：

```java
@TableField(typeHandler = EnumTypeHandler.class)
private OrderStatus status;
```

## 分页工具

```java
IPage<UserPO> page = userMapper.selectPage(MpPageUtils.page(criteria), wrapper);

PageInfo<UserPO> result = MpPageUtils.of(page);
PageInfo<UserDTO> dtoResult = MpPageUtils.of(page, toDtoList(page.getRecords()));
```

- `MpPageUtils.page(PageCriteria)`：把 `pageNum` / `pageSize` 转为 MyBatis-Plus 的 `Page`。
- `MpPageUtils.of(...)`：把 `IPage` 转为 [commons-core](../commons-core) 的 `PageInfo`。
- `PageUtils` 返回的是旧的 `MSPageInfo`，已废弃。
