# Triones Commons i18n

国际化辅助模块：生成语言（Locale）和时区（TimeZone）的可读名称列表，用于前端的语言选择、时区选择等下拉框。时区名称基于 [ICU4J](https://unicode-org.github.io/icu/userguide/icu4j/) 生成。

## 引入依赖

```xml
<dependency>
    <groupId>com.trionesdev.commons</groupId>
    <artifactId>commons-i18n</artifactId>
    <version>0.0.3-SNAPSHOT</version>
</dependency>
```

## 语言列表：LocaleUtils

`LocaleUtils` 的方法是静态方法：

```java
// 用简体中文显示：英语、中文(中国)、中文(台湾)
List<LocaleInfo> locales = LocaleUtils.getDisplayNamesByLanguageTags(
        Set.of("zh-CN", "zh-TW", "en-US"), Locale.SIMPLIFIED_CHINESE);

// 传 null：每个语言用其自身显示（English、中文(中国)……），适合语言切换菜单
List<LocaleInfo> natives = LocaleUtils.getDisplayNamesByLanguageTags(Set.of("zh-CN", "en-US"), null);
```

返回 `LocaleInfo`：

| 字段 | 说明 | 示例 |
| --- | --- | --- |
| `code` | BCP 47 语言标签 | `zh-CN` |
| `language` | 语言代码 | `zh` |
| `country` | 国家/地区代码 | `CN` |
| `displayName` | 显示名称 | `中文(中国)` |

`displayName` 的生成规则：

- 用第二个参数 `displayLocale` 指定的语言显示语言名；为 `null` 时用该语言自身显示。
- 同一语言在列表中出现多次时，在名称后追加国家/地区名称，例如 `中文(中国)`、`中文(台湾)`。
- 返回列表按 `code` 排序；传入空集合或 `null` 时返回空列表。

也可以直接传 `Locale` 集合：`getDisplayNames(Set<Locale>, Locale)`。

## 时区列表：TimeZoneUtils

`TimeZoneUtils` 的方法是静态方法：

```java
// 指定时区 ID
List<TimeZoneInfo> zones = TimeZoneUtils.getDisplayNames(
        Set.of("Asia/Shanghai", "America/Los_Angeles", "America/New_York"), Locale.SIMPLIFIED_CHINESE);

// JDK 支持的全部时区 ID
List<TimeZoneInfo> all = TimeZoneUtils.getDisplayNames(Locale.SIMPLIFIED_CHINESE);
```

返回 `TimeZoneInfo`：

| 字段 | 说明 |
| --- | --- |
| `code` | 时区 ID，例如 `Asia/Shanghai` |
| `region` | 所属国家/地区代码，例如 `CN` |
| `regionName` | 按 `locale` 显示的地区名称 |
| `timeZoneName` | 按 `locale` 显示的时区名称 |
| `displayName` | 用于展示的组合名称 |

`displayName` 的生成规则：

- 一般格式为 `地区名(偏移量)`，例如 `中国(+08:00)`。
- 同一地区有多个时区时，格式为 `地区名:时区名(偏移量)`，用来区分美国、俄罗斯等多时区国家。
- `Etc/` 开头的时区直接使用时区名称。
- 列表按 `displayName` **倒序**排列；传入空集合时返回空列表。

## 注意事项

- 偏移量取的是时区的**标准偏移**（`rawOffset`），不包含夏令时。
- `getDisplayNames(Locale)` 会包含 `TimeZone.getAvailableIDs()` 返回的全部 ID，其中有不少历史别名，例如 `PRC`、`US/Pacific`。面向用户展示时，建议传入经过筛选的时区 ID 集合。
