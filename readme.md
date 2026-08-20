# cudrania

`cudrania` 是面向 Java 17 的单模块工具库，Maven 坐标为 `io.github.nianien:cudrania`

它不是对 JDK、Guava、Apache Commons、Spring JDBC 或 ORM 框架的全面替代。当前代码中最有辨识度的能力是：

- `Pipeline`：命名中间结果、按名重新取参、逐步骤降级的类型安全流水线 DSL
- `SqlStatement`：与执行器解耦、支持条件片段和集合展开的参数化 SQL 构建器
- `Fn`：1 至 10 元可序列化函数、逐参数绑定和方法引用信息提取
- `CompositeClassLoader` / `FileClassLoader`：动态组合多个类路径以及 child-first 文件类加载
- `LoserTree`、`MultiBit`、`TextAnalyzer`、`TreeBuilder`、`TrieTree` 等针对特定问题的数据结构

`ArrayUtils`、`CollectionUtils`、`StringUtils`、`Files` 等基础工具主要提供项目内统一的便利 API，其中不少能力与 JDK 或常见工具库重叠

本文以当前 `pom.xml` 和 `src/main/java` 为 API 事实来源，并参考 [CLASS-ANALYSIS.md](CLASS-ANALYSIS.md) 中的逐类定位与取舍分析。分析文档同时记录了历史清理过程，因此其中的历史类数量、已删除类和改造前结构不应当作为当前 API 清单

## 环境与依赖

- JDK 17
- Maven 3.x
- 当前 POM 版本 `1.2.7`
- 公共包名前缀 `com.nianien.cudrania`

```xml
<dependency>
    <groupId>io.github.nianien</groupId>
    <artifactId>cudrania</artifactId>
    <version>1.2.7</version>
</dependency>
```

Jackson Databind 是普通依赖。以下集成依赖在 POM 中标记为 `optional`，使用对应功能的应用需要自行显式引入：

| 功能 | 对应类 | 依赖 |
| --- | --- | --- |
| 邮件发送 | `EmailSender` | `javax.mail:mail` |
| 从字节码读取方法参数名 | `Methods.getParameterNames` | `org.javassist:javassist` |
| Logback 调用位置转换器 | `CmlConfigurator`、`FQCNConverter` | `ch.qos.logback:logback-classic` |
| 预留依赖，当前主源码无直接引用 | — | `org.codehaus.janino:janino` |

Lombok 的作用域为 `provided`，主要用于编译本项目源码

## API 地图

| 包 | 主要类型 | 定位 |
| --- | --- | --- |
| `algorithm` | `ConsistentHash`、`LoserTree`、`MassSorter`、`PriorityHeap` | 一致性哈希、稳定 k 路归并、外部文本排序、固定容量 Top-N |
| `core.bits` | `BitValue`、`MultiBit`、`MultiBitSet` | 位值访问、定宽状态打包和多位宽 BitSet |
| `core.functions` | `Fn`、`Fluent`、`Param`、`Params` | 多元函数、绑定、链式对象操作和条件参数 |
| `core.pipeline` | `Pipeline`、`Pipeline2`、`Pipeline3`、`Pipelines`、`Named` | 1 至 3 个初始参数的命名流水线 DSL |
| `core.text` | `Expression`、`TextAnalyzer`、`HighLighter`、`Wildcard`、`Encoder` | 递归变量表达式、文本分片重建、关键词高亮和通配匹配 |
| `core.tree` | `TreeNode`、`TreeBuilder`、`TrieTree` | 孩子兄弟树、扁平数据建树、剪枝和前缀推荐 |
| `core.loader` | `CompositeClassLoader`、`FileClassLoader`、`ResourceLoader`、`PropertiesLoader` | 动态类路径、文件类加载和 classpath 资源读取 |
| `core.json` | `JsonParser`、`SerMasker`、`SecurityPropertyFilter`、`PbPropertyFilter` | Jackson 门面、无注解字段脱敏和序列化扩展 |
| `core.reflection` | `Reflections`、`Generics`、`Methods`、`BeanProperty` | 属性模型、重载解析、类型转换和泛型实参解析 |
| `core.math`、`core.date` | `SmartDecimal`、`DateFormatter`、`DatePattern` | 自动缩放的小数运算和基于 `DateTimeFormatter` 的 `Date` 门面 |
| `core.collection`、`core.arrays` | `CollectionUtils`、`ArrayUtils`、`CaseInsensitiveMap`、`DuoMap`、`ThreadLocalMap` | 集合与数组便利方法以及少量专用 Map |
| `core.collection.wrapper` | `Wrappers`、`ListWrapper`、`SetWrapper`、`MapWrapper` | 基于 JDK Proxy 的可链式集合包装器和分组、分批适配器 |
| `core.proxy` | `ProxyFactory`、`ProxyHandler`、`Interceptor` | 基于 JDK 动态代理的轻量接口代理 |
| `core.io`、`core.utils` | `Files`、`Closer`、`StringUtils`、`Enums`、`RadixUtils`、`TimeCounter` | 通用 I/O、字符串、枚举、进制和计时工具 |
| `core.log.logback` | `CmlConfigurator`、`FQCNConverter` | 跳过 Logger 包装层并输出真实调用位置 |
| `jdbc.sql` | `SqlStatement`、`SqlGenerator`、`DataField`、`SqlOperator` | 参数化 SQL 拼装和实体 SQL 生成 |
| `jdbc.table` | `DataTable`、`DataTableFactory`、`@Table`、`@Column`、`@Id` | JDBC 实体元数据和字段映射 |
| `jdbc.query` | `SqlQuery`、`SqlQueryBuilder`、`ResultSetAdapter` | SQL 执行和 ResultSet 到 Map 或 Bean 的映射 |
| `jdbc.datasource` | `DataSourceBuilder` | 默认配置继承、命名配置合并和 DataSource 反射构建 |

## Pipeline

### 适用场景

普通的 `Function.andThen` 只适合线性传值。Pipeline 适合下面这些情况：

- 后续步骤需要重新使用初始参数或较早的中间结果
- 一个步骤需要从上下文选择 1、2 或 3 个输入
- 每个步骤需要独立的异常降级逻辑
- 希望在编译期约束当前步骤的入参数量和类型

Pipeline 是本库的核心能力之一。当前实现由 `Pipeline`、`Pipeline2`、`Pipeline3` 描述三种执行入口，由一个统一的 `PipelineStep` 实现所有 DSL 状态，而不是每种入口各维护一份 Step 实现

### 完整示例

```java
import com.nianien.cudrania.core.pipeline.Pipeline2;
import com.nianien.cudrania.core.pipeline.Pipelines;

public class PipelineExample {

    interface Inputs {
        String text();
        Integer multiplier();
    }

    interface Results {
        Integer number();
        Integer product();
    }

    public static void main(String[] args) {
        Pipeline2<String, Integer, String> pipeline = Pipelines
                .of(Inputs::text, Inputs::multiplier)
                .with(Inputs::text)
                .and(Integer::parseInt)
                .failOver(error -> 0)
                .as(Results::number)
                .with(Results::number, Inputs::multiplier)
                .and((number, multiplier) -> number * multiplier)
                .as(Results::product)
                .and(product -> "result=" + product)
                .end();

        String success = pipeline.eval("21", 2);
        String fallback = pipeline.eval("invalid", 3);

        assert success.equals("result=42");
        assert fallback.equals("result=0");
    }
}
```

### DSL 语义

| 方法 | 作用 |
| --- | --- |
| `Pipelines.of(...)` | 创建 1、2 或 3 入参流水线，`Named` 重载同时把初始值写入命名上下文 |
| `and(function)` | 追加一个执行步骤，未调用 `with` 时默认接收上一步结果 |
| `as(name)` | 给当前步骤结果命名，不会新建步骤 |
| `with(name...)` | 为紧随其后的一个 `and` 选择命名输入，用过后即清除 |
| `failOver(handler)` | 给当前步骤设置降级函数，替代失败结果后继续执行后续步骤 |
| `end()` | 结束构建并返回 `Pipeline`、`Pipeline2` 或 `Pipeline3` |

`Named` 通常写成无参属性方法引用，例如 `Inputs::text`。运行时键为“声明类型全名 + `#` + 方法名”，方法本身不会被调用

需要注意：

- `Pipelines.of(String.class)` 一类的 `Class` 重载只辅助泛型推断，不给初始参数命名，也不做运行时类型校验
- 使用字符串版 `with` 时由调用方负责类型正确，缺失的名称会在执行到该步骤时抛出 `IllegalStateException`
- Pipeline 当前支持 1 至 3 个初始参数，每个步骤也只执行一元、二元或三元函数，虽然 `Fn` 本身支持到 10 元
- `failOver` 只属于它前面的当前步骤，不是整条流水线的统一异常处理器
- 每次 `eval` 都创建独立上下文，同一个流水线可以重复或并发执行，但步骤函数捕获的外部状态仍需由调用方保证线程安全
- 推荐只从 `Pipelines.of(...)` 进入 DSL，不直接依赖 `PipelineStep.begin(...)` 这类实现入口

## Fn、Fluent 与 Params

### Fn

`Fn` 提供一元到十元的 `Function` 和 `Consumer`。这些接口可序列化，并且每次 `bind` 都固定第一个参数，返回少一元的函数

```java
import com.nianien.cudrania.core.functions.Fn;

Fn.TriFunction<Integer, Integer, Integer, Integer> sum =
        Fn.of((a, b, c) -> a + b + c);

int result = sum.bind(1).bind(2).apply(3);
assert result == 6;
```

方法引用可以反解出目标方法名，这也是 `Named` 的实现基础

```java
Fn.Function<String, Integer> length = Fn.of(String::length);
assert length.name().equals("length");
```

需要方法信息时应优先传方法引用。普通 lambda 反解到的通常是编译器生成的方法名，不适合作为稳定业务名称

### Fluent

`Fluent.apply` 执行变换并持有新结果，`Fluent.accept` 执行副作用后继续持有原对象。目标为 `null` 时，两类调用都会跳过函数执行并继续传递 `null`

```java
import com.nianien.cudrania.core.functions.Fluent;
import com.nianien.cudrania.core.functions.Params;

String normalized = Fluent.of("  value  ")
        .apply(String::trim)
        .apply(String::toUpperCase)
        .get();

String prefix = "id=";
String text = Fluent.of(new StringBuilder())
        .<String>consumer(StringBuilder::append)
        .acceptIf(Params.notEmpty(prefix))
        .accept("42")
        .get()
        .toString();

assert normalized.equals("VALUE");
assert text.equals("id=42");
```

`Params.with(value).when(predicate).then(mapper)` 把值、惰性条件和转换组合为一个 `Param<T>`。`Param.get()` 仅在值非空且全部条件通过时返回非空 `Optional`，它同时被 `Fluent` 和 `SqlStatement` 用作条件执行协议

## 集合包装器

`Wrappers.list`、`set`、`map` 使用 JDK 动态代理，让目标集合同时实现原始集合接口和增强接口。以 `$` 开头的修改方法会忽略原方法返回值并返回包装器，因此可以继续链式调用

```java
import com.nianien.cudrania.core.collection.wrapper.ListWrapper;
import com.nianien.cudrania.core.collection.wrapper.MapWrapper;
import com.nianien.cudrania.core.collection.wrapper.Wrappers;

import java.util.List;
import java.util.Map;

ListWrapper<String> names = Wrappers.<String>list()
        .$add("Ada")
        .$add("Bob")
        .$remove("Bob");

List<String> rawNames = names.get();
List<List<String>> batches = names.grouped(1);

MapWrapper<String, Integer> scores = Wrappers.<String, Integer>map()
        .$put("Ada", 10)
        .$put("Bob", 20);

Map<Integer, String> namesByScore = scores.invertUnique();

assert rawNames.equals(List.of("Ada"));
assert batches.equals(List.of(List.of("Ada")));
assert namesByScore.get(20).equals("Bob");
```

普通的 `add`、`put`、`remove` 等方法仍保留 JDK 集合的原始返回类型，只有 `$add`、`$put`、`$remove`、`$clear` 用于链式修改。List 和 Set 的无参 `get()` 返回被包装集合，Map 中无参 `get()` 返回目标 Map，带 key 的 `get(key)` 仍是普通查询

`Wrappers.of(Iterable)` 和 `Wrappers.of(Map)` 不创建完整集合代理，只提供 `grouped`、`groupBy`、`doBatch`、`invert` 等适配能力。包装器与传入的目标集合共享数据，不提供快照、不可变性或并发保护；代理内部通过反射转发调用，异常形态也可能与直接调用目标集合不同

## SqlStatement

`SqlStatement` 只负责生成预编译 SQL 和参数，不依赖 `DataSource`，也不会自行访问数据库

### 动态查询示例

```java
import com.nianien.cudrania.core.functions.Params;
import com.nianien.cudrania.jdbc.sql.SqlStatement;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

String name = "";
List<Long> ids = List.of(10L, 20L);

SqlStatement statement = new SqlStatement("select * from users where 1=1")
        .append("and status=:status", Map.of("status", "ACTIVE"))
        .append("and name=:0", Params.notEmpty(name))
        .append("and id in :0", ids);

assert statement.preparedSql().equals(
        "select * from users where 1=1 and status=? and id in (?,?)");
assert Arrays.equals(
        statement.preparedRawParameters(),
        new Object[]{"ACTIVE", 10L, 20L});
```

`name` 为空，因此对应片段没有追加。`ids` 是集合，因此一个占位符被展开为两个 JDBC 占位符

### 参数形式

| 形式 | 参数来源 | 示例 |
| --- | --- | --- |
| `?` | 本次调用中的位置参数 | `append("id=?", 10)` |
| `:0`、`:1` | 本次调用中的指定位置参数 | `append("id=:0 or parent_id=:0", 10)` |
| `:name` | `Map` 中的命名参数 | `append("name=:name", Map.of("name", "Ada"))` |

数组和集合会递归展开，因此二维数组可生成行值表达式，例如 `((?,?),(?,?))`。空数组或空集合展开为 `(null)`，可避免非法的 `in ()`

解析器会跳过字符串、双引号标识符、单行注释和块注释中的占位符，也会跳过 PostgreSQL 的 `::` 类型转换。为了避免位置计数产生歧义，建议同一个 `append` 片段只使用一种占位符风格

### 自动条件

`append(Collection<DataField>)` 和 `append(Map<String, ?>)` 按下面的规则生成条件：

- `null` 生成 `is null`
- 数组或集合生成 `in (...)`
- 其他值生成 `= ?`

Map 的遍历顺序会决定 SQL 条件和参数顺序，需要稳定结果时使用 `LinkedHashMap`

### 执行与日志

```java
String sql = statement.preparedSql();
Object[] parameters = statement.preparedRawParameters();
String logOnly = statement.renderSql();
```

- 执行必须使用 `preparedSql()` 和 `preparedParameters()` 或 `preparedRawParameters()`
- `renderSql()` 只用于日志和调试，即使字符串值会转义单引号，也不能把它当作可执行 SQL
- 表名、列名和其他 SQL 结构片段不会自动引用或校验，不能直接拼接不可信输入

### 实体 SQL 与 JDBC 执行层

`SqlGenerator` 可根据 `@Table`、`@Column`、`@Id`、`@Property` 和 `@Ignore` 生成简单的 select、insert、update、delete。条件能力限定为 `=`、`is null` 和 `in`

```java
import com.nianien.cudrania.jdbc.query.SqlQueryBuilder;
import com.nianien.cudrania.jdbc.sql.SqlStatement;

SqlQueryBuilder queries = new SqlQueryBuilder(dataSource);
int changed = queries
        .build(new SqlStatement("update users set name=? where id=?", "Ada", 10L))
        .executeUpdate();
```

`SqlQuery` 每次操作从 `DataSource` 获取连接并在操作后关闭。它没有事务边界、连接复用、分页方言、乐观锁或完整 ORM 能力，需要这些能力时应使用 Spring JDBC、MyBatis、jOOQ 或其他成熟框架

## 动态类加载

`FileClassLoader` 和 `CompositeClassLoader` 的优先级容易混淆，两者需要分开理解：

- `FileClassLoader` 自身是 child-first，先查给定目录或 jar，再回退到父加载器
- `FileClassLoader` 的排除项是类名正则，含义是禁止匹配类回退到父加载器
- `CompositeClassLoader` 使用 parent-first 包装链，顺序为 bootstrap → rootLoader → 已附加 loader
- `insert` 只是在附加 loader 中放到最前，仍低于 rootLoader
- `add`、`insert`、`remove` 对下一次查找生效，但不会卸载已经定义的类

```java
import com.nianien.cudrania.core.loader.CompositeClassLoader;
import com.nianien.cudrania.core.loader.FileClassLoader;

import java.io.File;

ClassLoader applicationLoader = Thread.currentThread().getContextClassLoader();
FileClassLoader pluginLoader = new FileClassLoader(
        new File("plugins/example.jar"),
        applicationLoader);

CompositeClassLoader loader = new CompositeClassLoader(applicationLoader)
        .add(pluginLoader);

try {
    Class<?> pluginType = Class.forName(
            "com.example.plugin.ExamplePlugin",
            true,
            loader);
    Object plugin = pluginType.getDeclaredConstructor().newInstance();
} finally {
    loader.remove(pluginLoader);
    pluginLoader.close();
}
```

共享的 SPI 或 API 类型应由 rootLoader 提供，避免同名接口被不同加载器定义后出现 `ClassCastException`。移除 loader 只改变后续路由，`URLClassLoader` 的关闭和其他生命周期仍由调用方负责

`ResourceLoader.getFile` 只对 `file:` 资源返回 `File`。jar 内资源没有独立磁盘文件，此时应使用 `getInputStream` 或 `findResource`

## MultiBit 与 MultiBitSet

`MultiBit` 把多个固定宽度状态槽打包到一个 `long`，同时记录“值”和“本次要修改哪些槽”的掩码

```java
import com.nianien.cudrania.core.bits.MultiBit;

long original = 0x1234L;
MultiBit patch = MultiBit.hex()
        .set(0, 0xA)
        .set(2, 0xB);

long updated = patch.apply(original);
assert updated == 0x1B3AL;
```

这里每个槽占 4 bit，索引从低位开始。`set(index, 0)` 表示把该槽明确清零，`unset(index)` 表示把该槽移出补丁，应用时保留原值。这两个操作语义不同

`apply(String field)` 返回形如 `field=field & mask | value` 的 SQL 赋值片段，只适合受控字段名，方法本身不做 SQL 标识符转义

`MultiBitSet` 使用 `BitSet` 提供可增长版本：

```java
import com.nianien.cudrania.core.bits.MultiBitSet;

MultiBitSet states = new MultiBitSet(3);
states.set(100, 5);
states.set(200, 7);

assert states.get(100) == 5;
assert states.cardinality() == 2;
assert states.nextSetBit(101) == 200;
```

`length`、`size`、`cardinality` 和 `nextSetBit` 都以状态槽索引为单位，而不是底层 bit 索引

## 文本表达式与分片

### Expression

`Expression` 支持位置变量、命名变量、函数解析器、递归展开和循环引用检测

```java
import com.nianien.cudrania.core.text.Expression;

import java.util.Map;

Expression expression = new Expression("${", "}");
Map<String, String> variables = Map.of(
        "host", "example.com",
        "base", "https://${host}",
        "path", "docs");

String url = expression.eval("${base}/${path}", variables);
assert url.equals("https://example.com/docs");
```

默认会保留未知变量。构造函数的第三个参数设为 `false` 时，当前实现会把未知变量写成文本 `null`。变量之间形成循环引用时会抛出 `IllegalArgumentException`

### TextAnalyzer 与 HighLighter

`TextAnalyzer` 不复制每个片段内容，而是用 `Fragment(begin, end, matched)` 保存原文偏移，再通过 `rebuild` 统一重建文本。这适合需要区分匹配段和非匹配段、同时保留原始位置的处理

```java
import com.nianien.cudrania.core.text.HighLighter;

HighLighter highLighter = new HighLighter(
        new String[]{"Java 17", "Java"},
        true);

String html = highLighter.highLightHtml(
        "<a href='/java'>Java 17</a>");

assert html.equals("<a href='/java'><em>Java 17</em></a>");
```

关键词按长度降序匹配，因此包含关系中较长关键词优先。`highLightHtml` 会跳过标签本身，但它基于正则 `<.*?>` 分段，不是完整 HTML 解析器

## 算法与树结构

### LoserTree 与 PriorityHeap

`LoserTree` 的每个输入迭代器必须已经按照同一个比较器排序，归并会消费这些迭代器。值相同时按分支索引保持稳定顺序

```java
import com.nianien.cudrania.algorithm.LoserTree;
import com.nianien.cudrania.algorithm.PriorityHeap;

import java.util.Iterator;
import java.util.List;

List<Iterator<Integer>> branches = List.of(
        List.of(1, 4, 7).iterator(),
        List.of(2, 4, 8).iterator(),
        List.of(3, 5, 9).iterator());

List<Integer> merged = new LoserTree<>(branches).merge();
assert merged.equals(List.of(1, 2, 3, 4, 4, 5, 7, 8, 9));

PriorityHeap<Integer> top3 = PriorityHeap.of(3, Integer.class);
List.of(1, 9, 5, 7).forEach(top3::add);
assert List.of(top3.popAll()).equals(List.of(9, 7, 5));
```

`MassSorter` 按行分块排序到临时文件，再两两归并到目标文件。传入的 `Reader` 和合并方法的 `Writer` 会被关闭，调用方不应在排序后继续复用

`ConsistentHash` 使用带虚拟节点的哈希环，节点可以是任意对象；默认每个节点创建 512 个虚拟节点。节点位置和输入散列都基于 `toString()`，该字符串应保持稳定。空节点集合的 `getTarget` 返回 `null`

### TreeBuilder 与 TrieTree

`TreeBuilder` 从扁平数据构建孩子兄弟树，返回的根节点是 `data == null` 的虚拟根。`minimize` 会原地删除所有不通向命中节点的分支

```java
import com.nianien.cudrania.core.tree.TreeBuilder;
import com.nianien.cudrania.core.tree.TreeNode;

import java.util.List;

record Menu(Integer id, Integer parentId, String name) {}

List<Menu> menus = List.of(
        new Menu(1, null, "root"),
        new Menu(2, 1, "users"),
        new Menu(3, 1, "orders"));

@SuppressWarnings("unchecked")
TreeNode<Menu> root = (TreeNode<Menu>) TreeBuilder.buildTree(
        menus,
        Menu::id,
        Menu::parentId);

TreeBuilder.minimize(
        root,
        node -> node.data() != null && node.data().id() == 2);
```

`TrieTree` 是字符串集合而不是 key-value Map，删除操作只是把节点标记为不存在，`optimize` 会压缩没有分支和有效数据的纵向路径

```java
import com.nianien.cudrania.core.tree.TrieTree;

TrieTree trie = new TrieTree();
trie.insert("java");
trie.insert("javascript");
trie.insert("javadoc");

assert trie.find("java");
assert trie.suggest("java").contains("javascript");

trie.delete("java");
assert !trie.find("java");
```

## JSON 序列化扩展

`JsonParser` 是可配置的 Jackson `ObjectMapper` 门面。默认配置允许无引号字段名、单引号和数字前导零，忽略未知属性，不输出 `null` 字段，并开启重复字段检测

字段脱敏推荐使用 `withSerMasker`，它通过 `SecurityPropertyFilter` 同时处理 POJO 属性和 Map 条目：

```java
import com.nianien.cudrania.core.json.JsonParser;
import com.nianien.cudrania.core.json.serializer.SimpleSerMasker;

import java.util.LinkedHashMap;
import java.util.Map;

JsonParser parser = new JsonParser().withSerMasker(
        new SimpleSerMasker(
                "(?i).*(password|phone|id_?card).*",
                "****"));

Map<String, Object> account = new LinkedHashMap<>();
account.put("name", "Ada");
account.put("password", "secret");

String json = parser.toJson(account);
assert json.equals("{\"name\":\"Ada\",\"password\":\"****\"}");
```

脱敏只是输出值替换，不是加密，也不能用于密码存储。`JsonParser` 的配置方法会修改内部 `ObjectMapper`，应当在共享实例并发使用之前完成全部配置

`PbPropertyFilter` 可为集合、数组和 Map 额外输出 `xxxList`、`xxxMap` 风格字段，`DescriptionPropertyWriter` 可把 `@JsonPropertyDescription` 写成附加的 `xxx_desc` 字段。Filter 版本可以处理 Map，Writer 版本只处理 POJO 属性

## 日期、反射与数值边界

- `DateFormatter` 的公开契约仍是 `java.util.Date` 和模式字符串，内部使用不可变的 `DateTimeFormatter` 缓存
- `DateFormatter.setLocale` 和 `setZone` 修改全局静态配置，应在应用初始化阶段设置，不适合按请求切换
- `Methods` 的调用栈查询基于 `StackWalker`，只有参数名读取依赖 Javassist 和 class 文件中的本地变量表
- `Reflections.populate(target, source)` 的参数顺序是目标在前、来源在后，来源可以是 Bean 或 Map
- `ProxyFactory` 只基于 JDK 动态代理，因此只能代理接口
- `SmartDecimal` 重写多数 `BigDecimal` 运算以继续返回相同类型，并在重建结果时使用保存的 scale 和舍入配置，但当前构造路径会经过 `Number.doubleValue()`，精确财务计算应优先使用原生 `BigDecimal`
- `MessageDigests.md5` 和默认的 `SHA` 适合兼容或校验场景，不适合密码、签名等安全用途
- `ThreadLocalMap` 的 `equals`、`hashCode` 和 `toString` 都取决于当前线程中的 Map，不应把它当作具有稳定对象身份的普通 Map 键

## 构建、测试与发布

```bash
mvn compile
mvn test
mvn install
mvn clean deploy -Possrh
```

POM 已为编译器配置：

```text
--add-exports=java.base/jdk.internal.loader=ALL-UNNAMED
```

`install-jar` profile 在存在 `src/main/java` 时自动激活，并使用下面的版本格式额外安装构建产物：

```text
${project.version}.${git.commit.time}-${git.commit.id.abbrev}
```

因此本地安装后的时间戳版本不同于 POM 中声明的 `1.2.7`。发布中央仓库需要本机已配置 OSSRH 凭据和 GPG 签名

## License

Apache License 2.0
