# cudrania 代码瘦身分析与删除建议

> 原则:这是**发布到 Maven 中央仓库的工具库**,每个类都是给外部使用的公开 API。
> 判断依据是**代码实现本身的独特性 / 是否与 JDK 17·主流库重复**,**不看内部是否被引用**。
> 所有"建议删除"都给出了具体替代 API。

---

## 一、状态总览

| 档位 | 含义 | 状态 |
|------|------|------|
| 🅰 | 有 JDK17 标准替代 / 库内自我重复(高置信) | ✅ **已删除** |
| 🅱 | 有替代但涉及依赖取向 / 本身有 bug | ⏳ 待你拍板 |
| 🅲 | 库内"双实现重叠",可精简 | ⏳ 待你拍板 |
| ✅保留 | 独特、别处不好找 | 保留 |
| 🔧改造 | 被 core 依赖,不能删,建议现代化 | 待安排 |

---

## 二、🅰 已删除(9 个文件 + 2 处迁移)

| 已删类 | 替代 | 说明 |
|--------|------|------|
| `core/text/Encryptor` | `core/text/Encoder` | 重命名遗留,与 Encoder 逐字节重复;名为"加密"实为可逆编码 |
| `core/json/serializer/SerEncryptor` | `SerMasker` | 重命名遗留死码 |
| `core/json/serializer/SimpleSerEncryptor` | `SimpleSerMasker` | 重命名遗留死码 |
| `core/bits/ByteUtils` | `java.util.HexFormat` + `ByteBuffer` + `Byte.toUnsignedInt` | 每个方法都有 JDK17 直替 |
| `core/collection/map/Pair` | `Map.entry` / `AbstractMap.SimpleEntry` | JDK9+;原 hashCode 只用 key 违反 Entry 契约 |
| `core/comparator/EntryComparator` | `Map.Entry.comparingByKey/Value` + `reversed`/`thenComparing` | 8 枚举全是 JDK 组合子排列 |
| `core/collection/set/CaseInsensitiveSet` | `TreeSet(String.CASE_INSENSITIVE_ORDER)` | 原 remove 是 O(n) |
| `core/io/Console` | `java.io.Console` / `Scanner` | 平凡包装 |
| `core/io/FileSearcher` | `java.nio.file.Files.walk/find` | NIO 直替 |

**顺手迁移(保留类,因用到了被删类)**:
- `core/text/MessageDigests`:`ByteUtils.byte2Hex(x)` → `HexFormat.of().formatHex(x)`(小写一致)
- `core/utils/Zipper`:`FileSearcher.find` → 内联私有 `findFiles`(忠实复刻递归语义)

---

## 三、🅱 —— ✅ 已执行(TimeCounter 保留并修复,其余全删)

> 本轮已删:`wrapper/`(7)、`xml/`(XMLBuilder+XMLNode+**XMLDocument**)、`Calculator`、`ObjectSerializer`、`LoggerFactory`、`CounterMap`。
> **连带处理**:
> - `XMLDocument` 本不在列表,但它整个建立在 `XMLNode` 上,删 XMLNode 必然连它一起删 → xml 包整体移除。
> - `LoggerFactory` 被 `DataTableImpl` 用 → 已改为 `java.util.logging.Logger.getLogger(...)` 直用。
> - **`dom4j` 依赖现已无人使用**(xml 包没了),可从 pom 移除(未动,待你确认)。
> - **`TimeCounter` 保留并已修复**:改用 `System.nanoTime()` 测时长(单调),`timePassed` 换算成毫秒,`start/stop` 返回墙钟时间戳。
> - 测试未动(按你要求),最后统一修+验证。

> 标注:✅=我已亲自读代码核实

| 类 | 替代 / 理由 | 核实 |
|----|-------------|------|
| `core/collection/wrapper/*`(7 类:Wrappers/Delegate/ListWrapper/SetWrapper/MapWrapper/IterSupplier/MapSupplier) | 反射代理 + `@Delegate` 字符串委托,**脆弱且慢**(每调用一次反射查找;签名重构会运行时才炸;吞掉返回值)。建议用**具体自返回子类**重写,或删 | ✅ 早前 @Delegate 深挖时逐文件读过 |
| `algorithm/Calculator` | enum 声明了 `! < <= > >= == != && \|\| = ,`,但 `doOperate` switch(215–244)只实现算术/位运算,其余全 `default→UnsupportedOperationException`。`1<2`/`a&&b` 能解析、求值即抛异常——误导 API。要么补全要么删声明 | ✅ 已读 enum+switch 核实 |
| `core/xml/XMLBuilder` | 第171行 `sb.append(bean)`、第280行 `append(attrValue)` 直接拼值,**全程无 `<&"` 转义** → 字段含这些字符即产生非法 XML。用 Jackson `XmlMapper`/JAXB(自动转义) | ✅ 已读拼接逻辑核实 |
| `core/collection/map/CounterMap` | **JDK `map.merge`**(同为 Map<K,Integer>,`merge(k,-1,(a,b)->a+b<=0?null:a+b)` 复刻"≤0 删 key")。**不等于 Guava `Multiset`**(Multiset 是 Collection,契约不同) | ✅ 已读全文核实(原"= Multiset"是错的,已改) |
| `core/utils/TimeCounter` | 替代是 **`System.nanoTime()` 差值**。注意它当前用 `System.currentTimeMillis()`(墙钟、非单调,会被改时间/NTP 影响)——测时长本身是**缺陷**;非"= Guava Stopwatch"(Stopwatch 用 nanoTime 单调) | ✅ 已读全文核实(原"Guava Stopwatch"表述不准,已改) |
| `core/xml/XMLNode` | dom4j `Element` / W3C DOM | ✅ 已读:纯 POJO 树节点(name/text/attributes/parent/children),dom4j Element 的贫化重造,仅为 XMLDocument 输出 |
| `core/io/ObjectSerializer` | 裸 `ObjectStream` / Commons `SerializationUtils`;原生序列化不推荐 | ✅ 已读:就是 ObjectOutputStream/ObjectInputStream 样板 + RAF 截断技巧 |
| `core/log/LoggerFactory` | JUL 直用 / SLF4J(名字还与 `org.slf4j.LoggerFactory` 撞名) | ✅ 已读:getLogger 全是 `Logger.getLogger(...)` 透传,仅静态块的"配置文件加载优先级"算增量 |

---

## 四、🅲 库内"双实现重叠"可精简(保留 Filter 版)

| 可删 | 保留 | 理由 | 核实 |
|------|------|------|------|
| `core/json/serializer/SecurityPropertyWriter` | `SecurityPropertyFilter` | Writer 只处理 POJO 字段;Filter 还覆盖 Map 条目(serializeAsField/serializeAsElement)。Writer 的 javadoc 示例代码还写错了构造签名 | ✅ 已读两者核实 |
| `core/json/serializer/PbPropertyWriter` | `PbPropertyFilter` | Writer 只走 BeanPropertyWriter(POJO);Filter 同时处理 `BeanPropertyWriter` 和 `MapProperty`(第42/50行),更全 | ✅ 已读两者核实 |

---

## 五、🔧 改造而非删(被 core 依赖)

| 类 | 建议 |
|----|------|
| `core/date/DateFormatter` | 还在用 `SimpleDateFormat`+`ThreadLocal`,迁 `java.time.DateTimeFormatter`(会影响 JsonParser/jdbc 的日期行为,需回归测试) |

---

## 六、✅ 明确保留(独特、别处不好找)

- **函数式/编排**:`Fn`(2~10 参函数接口 + 可序列化 lambda 方法名反解)、`Named`、整套 `pipeline`+`step`(方法引用命名 + 按名取参 + 类型安全 DSL + 每步 failOver)、`Params`(谓词链条件参数)、`Fluent`(Java 版 Kotlin `apply`/`also`,多参+条件执行,非 Optional)、`Param`
- **反射**:`Reflections`、`Generics`(泛型实参解析)、`BeanProperty`、`Classes`、`@Property`/`@Ignore`;`Primitive`(可选,Guava `Primitives` 替代);`Methods`(参数名反解独特;caller 部分建议改 `StackWalker`)
- **类加载**:`CompositeClassLoader`(动态聚合 + 保持双亲委派)、`FileClassLoader`(child-first + 排除规则)——**JDK 无等价,标杆资产**
- **算法**:`LoserTree`(败者树 k 路归并)、`MassSorter`(外部归并排序)、`ConsistentHash`(环形虚拟节点,与 Guava Jump 语义不同)、`PriorityHeap`(有界淘汰堆)
- **文本**:`Wildcard`(字符串 + Ant `**` 通配)、`Expression`(递归命名模板 + 循环检测)、`TextAnalyzer`(片段切分模型)、`HighLighter`、`RadixUtils`(radix>36 + 自定义字符集)、`Characters`、`Encoder`
- **树**:`TrieTree`(前缀补全 + 软删 + 压缩)、`TreeBuilder`(扁平表→树 + 剪枝)、`TreeNode`
- **bits**:`MultiBit`/`MultiBitSet`(多值打包进 long/BitSet,JDK 无)、`BitValue`(视情况)
- **JSON**:`JsonParser`(Jackson 门面)、`SerMasker`/`SimpleSerMasker`(无注解按名脱敏且能改值)、`SecurityPropertyFilter`、`PbPropertyFilter`、`DescriptionPropertyWriter`(把 `@JsonPropertyDescription` 写进输出)
- **jdbc**:`SqlStatement`(★核心:条件片段+命名参数+集合展开+执行解耦)、`DataField`、`SqlOperator`、`SqlTypeConverter`、`DataSourceBuilder`(零 Spring 自适应数据源)
- **数学**:`SmartDecimal`(自动保精度/舍入的 BigDecimal,JDK 无)
- **异常**:`ExceptionChecker`(sneaky-throw + 栈帧修剪)
- **日志扩展**:`FQCNConverter` + `CmlConfigurator`(Logback 跳过包装 Logger 帧,SPI 自注册)
- **其他**:`CollectionUtils`、`CaseInsensitiveMap`、`RegexUtils`、`StringUtils`(可瘦身但地基)、`Enums`(字段值反查独特)、`Files`/`Closer`(视情况)、`ResourceLoader`/`PropertiesLoader`(视情况)、`Interceptor`/`ProxyFactory`/`ProxyHandler`(零依赖迷你 AOP)

> **jdbc 整栈**(SqlGenerator/ResultSetAdapter/SqlQuery*/DataTable*/@Column·@Id·@Table):对标 MyBatis/JdbcTemplate/JPA 是"轻量重复",但作为**零 Spring 依赖的自带栈**成立,且你已定调"DataTable 为完整性、非重点"——**不删,按'够用且正确'保留**。

---

## 七、读代码时发现的 Bug(与删除无关,建议单独修)

| 位置 | 问题 |
|------|------|
| `bits/MultiBitSet.cardinality` | 内层 `j < i+bitNum-1` 漏算每组最高位,只置最高位的值被误算为 0 |
| `io/ClassScanner` | 每扫一个类就 `System.out.println` —— 库里不该有 |
| `loader/ResourceLoader.getFile` | `new File(url.toURI())` 对 jar 内资源失效(footgun) |
| `proxy/ProxyFactory` | `after()` 不在 finally,方法抛异常且 `exception()` 重抛时被跳过 |
| `utils/Zipper.unzip` | 无 zip-slip 路径穿越防护(`new File(destDir, name)`) |
| `xml/XMLBuilder` | 输出不转义(见 🅱,本身即删除理由) |
| `text/MessageDigests` | 默认 MD5/`"SHA"`(=SHA-1)弱算法,校验和可以,安全场景不行 |

---

## 八、依赖现状(重依赖已在早前评估;当前保留原样)

- 运行时:jackson-databind(强)、dom4j / ant / javax.mail / logback / janino / javassist(后几个 optional 或按功能)
- 说明:早前一轮曾删 xml/mail/ant 及其依赖,后按你要求**全部回滚恢复**;当前依赖与原始一致。若将来要瘦身依赖,dom4j(xml)、ant(Zipper)、javax.mail(EmailSender)是三个可议的重依赖点。

---

*本文档随清理进度更新。🅰 已完成;🅱/🅲 待你确认后执行,测试统一在最后修复+验证。*
