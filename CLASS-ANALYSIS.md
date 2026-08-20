# cudrania 逐类分析(按包)

> 说明:本文档**每一条都基于我亲自通读该类源码**得出。
> 格式:**功能**(实现了什么)/ **价值**(独特性、与 JDK17·主流库的关系)/ **判断**(留 / 删 / 改 + 依据)。
> 这是工具库,类是给外部用的——判断只看"实现本身是否有价值 / 是否被 JDK17·主流库直替",不看内部引用。

---

## 📌 本轮变更总览(相对原始代码)

> 下方各包的逐类分析是**判断依据**;本节汇总落地结果——**当前代码库 104 个 main 类**。

### ⚠️ 更正:`collection/wrapper/*`(7 类)已恢复
之前误删。逐字重读后确认它是**独特设计**:基于动态代理生成的对象**本身 implements List/Set/Map**(是活集合),又叠加 `$add/$put/...` 返回 this 的链式方法(`@Delegate` 转发)+ `IterSupplier/MapSupplier` 的 `map/groupBy/grouped/invert/doBatch` mixin。JDK/Guava 均无此组合(`List.of` 不可变、Guava Builder 产物不可变)。按"看实现是否独特"的标准应**留**。已连同 `TestWrapper`(6 测试)一起恢复至新包。

### ⚠️ 更正:`collection/map/CounterMap` 已恢复
之前误删且未经授权。重读后确认它是**带计数语义的具名 `Map<T,Integer>`**:`increase/decrease/add(key,n)`、计数 ≤0 自动删 key、`get` 未命中返回 0。`map.merge` 无"自动删除 / 缺省返回 0"语义,且不是可传递的具名类型。应**留**,已恢复至新包。

### ⚠️ 二次复核:逐字重读全部已删类后的更正
- **恢复 `io/ClassScanner`**:classpath 包扫描(file+jar),JDK 无等价(仅 Spring/Guava 有)。恢复时修复递归子包漏点 bug(`+""`→`+"."`)、移除 `System.out.println` 噪音。**留**。
- **`algorithm/Calculator` 维持删除**:虽自包含且 JDK 无等价,但比较/逻辑运算符声明了却未实现(用到即抛),是半成品;真正的表达式解析会上 ANTLR/JavaCC 或成熟库,无人会依赖它。**独特 ≠ 有人用**,删。
- 仍**删**但需说明:
  - `xml/XMLDocument`:依赖 **dom4j(已按你要求移除)**,原样无法恢复,须改写到 JAXP。
  - `xml/XMLNode`:XMLDocument 的产物类型,单独无用(w3c DOM 可替)。
  - `xml/XMLBuilder`:bean→XML(自包含),但**不转义**(正确性隐患)且依赖的 `BeanProperty.getAlias()` 已改私有,需适配+修转义才能恢复。可按需处理。
  - `utils/Zipper`:依赖 **ant(已移除)**,须改写到 `java.util.zip`。

### 已删除(16 类)
- **有 JDK17 标准直替(8)**:`text/Encryptor`(→已重命名 Encoder,功能保留)、`json/serializer/SerEncryptor`(→SerMasker)、`json/serializer/SimpleSerEncryptor`(→SimpleSerMasker)、`bits/ByteUtils`(→`HexFormat`+`ByteBuffer`+`Byte.toUnsignedInt`)、`collection/map/Pair`(→`AbstractMap.SimpleEntry`,且原 setValue 有 bug)、`comparator/EntryComparator`(→`Map.Entry.comparingByKey/Value`+`reversed/thenComparing`)、`collection/set/CaseInsensitiveSet`(→`TreeSet(CASE_INSENSITIVE_ORDER)`,且原 contains 有 bug)、`io/Console`(→`Scanner`)
- **低价值/半成品(4)**:`io/FileSearcher`(→`Files.walk/find`)、`io/ObjectSerializer`(≈Commons `SerializationUtils`,JDK 无一行版)、`log/LoggerFactory`(≈JUL 平凡包装)、`algorithm/Calculator`(半成品求值器,真解析用 ANTLR)
- **依赖已移除、须改写才能留(4)**:`xml/XMLDocument`(dom4j)、`xml/XMLNode`、`xml/XMLBuilder`(+转义/适配)、`utils/Zipper`(ant,曾是 ant 依赖的唯一用户)

### 重命名(旧名已删,新名保留)
- `text/Encryptor` → **`text/Encoder`**(名副其实:可逆编码非加密)
- `json/serializer/SerEncryptor` → **`SerMasker`**;`SimpleSerEncryptor` → **`SimpleSerMasker`**(脱敏而非加密)

### 现代化/修复(保留但改进)
- `date/DateFormatter` **已迁 `java.time.DateTimeFormatter`**(保持 Date 公开 API,+TestDateFormat 断言)
- `reflection/Methods` caller 部分已迁 `StackWalker`(去魔数偏移)
- `utils/Enums` 去 `System.err`/`printStackTrace` 噪音
- `utils/TimeCounter` 改用 `System.nanoTime`(单调)
- 修复 bug:`MultiBitSet.cardinality`、`ResourceLoader.getFile`(jar 资源)、`ProxyFactory`(after 移入 finally),以及早前多处(见 CLEANUP.md)

### 依赖瘦身
- 移除:**dom4j、ant**;运行时仅剩 jackson-databind(强)+ mail/javassist/logback/janino(optional)+ lombok(provided)

### 🅲 未删(库内双实现,可选精简)
- `json/serializer/SecurityPropertyWriter`、`PbPropertyWriter`(各自 Filter 版更全,保留 Filter 即可)

---

## algorithm

### ConsistentHash
- **功能**:一致性哈希环。每个节点用 MD5 链生成虚拟节点(默认 512)填入 `TreeMap<Long,节点>`;`getTarget` 用 `ceilingKey` 顺时针命中。(已修:空环返 null、取 MD5 前 8 字节做 64 位哈希、固定 UTF-8)
- **价值**:环形虚拟节点(Ketama 语义)。Guava `Hashing.consistentHash` 是 Jump(返回桶编号)语义,**不支持任意节点对象 + 虚拟节点环**,不能直替。
- **判断**:✅ **留**。

### LoserTree
- **功能**:败者树,对多个有序数据源(`List<Iterator<T>>`)k 路归并。`tree[]` 非叶存数据源索引、`nodes[]` 叶存各源当前值;`beat()` 在值相等时比较分支索引以**保证排序稳定性**;提供 `merge/pop/top`。
- **价值**:JDK/Guava 无败者树。Guava `Iterators.mergeSorted` 是基于堆(胜者逻辑)且不保证稳定性,算法不同。
- **判断**:✅ **留**(独特)。

### MassSorter
- **功能**:海量文本外部归并排序:分块加载→块内 `Collections.sort`→落临时文件→两两归并成序。(已修:默认比较器、归并队列 LinkedList、write 去 `remove(0)`)
- **价值**:JDK/Guava 无开箱外部排序。
- **判断**:✅ **留**。(实现是"两两成对归并",可用同包 LoserTree 优化成 k 路,但功能完整)

### PriorityHeap
- **功能**:定容小顶堆(顶=优先级最低)。`add` 满容时仅当新元素优先级更高才替换顶、返回被淘汰者;`popAll` 出堆得优先级降序数组;用 `Class<T>` + 反射建泛型数组。
- **价值**:JDK `PriorityQueue` 无"定容自动淘汰 TopN"语义;不过 `PriorityQueue`+size 判断几行也能实现。差异点是"有界淘汰 + 返回被淘汰者 + 类型化数组"。
- **判断**:🟡 **留(轻量差异)** —— 有小差异化 API,价值有限。

---

## core/arrays

### ArrayUtils
- **功能**:大型数组工具(~1300 行):`contains`/`intersect`/`subtract`/`union`/`toString`/`isEmpty`/基本类型数组与包装数组互转/`forceToArray`/`toObjectArray`。(已修:`subtract` 空第二数组、`contains` 二分→线性)
- **价值**:与 Guava/Commons 数组工具重叠;但纯 JDK、是本库 `StringUtils`/`Params`/jdbc 的内部地基。
- **判断**:✅ **留**(地基)。

---

## core/bits

### MultiBit
- **功能**:把多个 N-bit 小值打包进**一个 long**(`oct()`=3bit、`hex()`=4bit);维护 `trueCode`+`maskCode`;`get/set/unset` 按槽位;独有 `apply(long)` 把打包值合并进外部 long、`apply("field")` 生成 SQL 表达式串 `field=field & ~mask | true`。(已修:移位溢出)
- **价值**:**JDK/Guava/Commons 都没有**"把 K 个小枚举打包进一个 long + 可掩码合并"。掩码合并 + 表达式生成是独特能力。
- **判断**:✅ **留**(独特)。

### MultiBitSet
- **功能**:同"每槽 N-bit"思路,但底层是可无限增长的 `java.util.BitSet`;加 `set(begin,end,value)` 区间填充、`get`、`cardinality`、`nextSetBit`(已修 -1)、`length`/`size`(均以槽为单位)。
- **价值**:JDK `BitSet` 只 1bit/位;这是它上面的多 bit 宽视图,JDK 无等价。
- **判断**:✅ **留**(独特)。(已修:`cardinality` 内层改 `j<i+bitNum`,不再漏算每组最高位)

### BitValue
- **功能**:把一个 `long` 当可寻址 bit 数组读写(get/set 单 bit),并支持大端/小端二进制串的解析与输出。(已修:index 边界)
- **价值**:核心位操作与 `Long`(`1L<<i`、`Long.toBinaryString`/`parseLong(s,2)`)、`java.util.BitSet` 重叠;**唯一独特**的是大小端串往返。
- **判断**:🟡 **视情况**(偏可删)—— 只有大小端串是增量,其余 JDK 位运算直替。

---

## core/collection

### CollectionUtils
- **功能**:`isEmpty/isNotEmpty`、`doBatch`/`grouped`(分批)、`enumeration`/`list`(Enumeration↔Iterator↔List↔数组 各种转换)、`map`/`groupBy`(集合→Map)、`remove(list,from,to)`、`remove(pred)`、`removeNull`、`arrayToList`。
- **价值**:诚实说——**大量是 JDK 一行的重贴**:`list(Enumeration)`=`Collections.list`;`remove(list,from,to)` 方法体就是 `list.subList(from,to).clear()`;`map`/`groupBy`=`Collectors.toMap`/`groupingBy`;`removeNull`=`removeIf(Objects::isNull)`;`enumeration` 就是包装迭代器。真正有点边际价值的是 `isEmpty`/`doBatch`/`grouped`。
- **判断**:🟡 **视情况**(可瘦身留几个独特方法)—— 纯 JDK,且被 `Reflections.populate`/`IterSupplier` 等内部用,删要迁移。

---

## core/collection/map

### CaseInsensitiveMap
- **功能**:大小写不敏感的 String 键、但保留原始大小写键;side `keyMap`(lower→original)维护;重写 get/put/remove/containsKey/putAll/getOrDefault/clear。(已修:putAll/getOrDefault)
- **价值**:`new TreeMap<>(String.CASE_INSENSITIVE_ORDER)` 是直替(也保留存入的原键)。泛型 `<K>` 是幌子——只对 String 键特殊处理。区别是 O(1) 哈希 vs O(log n)。
- **判断**:🟡 **视情况** —— 有 JDK 直替;但被 jdbc 列名映射用(`DataTableImpl`/`ResultSetAdapter`),留着。

### DuoMap
- **功能**:`extends HashMap<K, DuoValue<V1,V2>>`,一个 key 挂两个值;`getValue1/2`、`put(k,v1,v2)`、`updateValue1/2`(已修返回旧值)。`getValue1(key)` = `get(key).getValue1()`,**key 不存在直接 NPE**。
- **价值**:本质 `Map<K, 可变二元组>`。JDK **没有**可变元组;Commons `MutablePair`+HashMap 近似,但丢类型化访问器。
- **判断**:🟡 **视情况** —— niche,无纯 JDK 直替。

### DuoValue
- **功能**:`@Data @AllArgsConstructor` 的可变二元组,作 DuoMap 的 value。
- **价值**:Commons `MutablePair` 等价;JDK/Guava 无可变 pair。
- **判断**:🟡 **视情况**(DuoMap 的伴生)。

### ThreadLocalMap
- **功能**:`extends ThreadLocal<Map<K,V>> implements Map<K,V>`,把整套 `Map` API 转发到"当前线程的 HashMap",让一个对象既是 ThreadLocal 又能当 Map 用。
- **价值**:JDK/Guava 无"既是 ThreadLocal 又是 Map"的类型,**确实独特**;但设计可疑——`equals/hashCode/toString` 都转发到"当前线程的 map",对象身份很诡异,常规写法是 `ThreadLocal.withInitial(HashMap::new)`。
- **判断**:🟡 **视情况**(独特但设计怪)。

---

## core/comparator

### StringComparator
- **功能**:枚举,4 种排序——字典升/降(实为 `compareToIgnoreCase`,与"字典"措辞略不符)、长度升/降;全部通过 `nullLast` 助手让 null 恒排最后。(已修:null 契约)
- **价值**:`Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)` / `nullsLast(comparingInt(String::length))(.reversed())` 逐个可替;打包成"null 恒后 + 4 预设枚举"是小便利。
- **判断**:🟡 **视情况** —— 单个是 JDK 一行组合子;但被 `TextAnalyzer`/`HighLighter` 用,留着。

---

## core/functions(Fn/Fluent 见下方单独补)

### Param
- **功能**:单方法接口 `Optional<T> get()`,表示"条件参数"——`get()` 返回 present 时参数才可用。
- **价值**:本质是 `Supplier<Optional<T>>`;但它是 `Params`、`Fluent.applyIf`、`SqlStatement` 条件片段共同的**语义锚点**。
- **判断**:✅ **留**(体系契约,删了整套条件机制散架)。

### Params
- **功能**:静态工厂,产 `ImmutableParam`:`notNull`/`notEmpty(String/Collection/Map/Array)`/`eq/ne/gt/ge/lt/le`(及 `*0` 变体);`ImmutableParam` 支持 `when`(and 谓词链)/`negate`/`then`(map 转换)/`get`(返回 `Optional`,仅当值非 null 且谓词通过)。
- **价值**:判空能力等价 Guava `Strings`/Commons;但"**值 + 惰性谓词链 + 转换 + 返回 Optional**"的组合链 Guava/Commons 没有,且是 `SqlStatement` 条件拼装(`append(with(x).when(...))`)的 DSL 门面。
- **判断**:✅ **留**(核心资产配套)。

---

## core/date(DateFormatter 见下方单独补)

### DatePattern
- **功能**:日期格式串常量(`"yyyy-MM-dd HH:mm:ss E"` 等)+ `Default`。
- **价值**:纯常量,无重复问题(串大多兼容 SimpleDateFormat/DateTimeFormatter;`E`/`a` 在 DateTimeFormatter 下语义略不同)。
- **判断**:✅ **留**(若 DateFormatter 迁 java.time,可一起整理)。

---

## core/exception

### ExceptionChecker
- **功能**:`throwException(Throwable)` 用泛型擦除把受检异常当非受检抛出(sneaky-throw);`throwException(String)`;`throwIf`/`throwIfNull` 断言;`skip()` 修剪栈顶帧使异常指向调用者。
- **价值**:guard 那半 Guava `Preconditions` 覆盖;但 **sneaky-throw + 栈帧修剪**Guava/Commons 无(Lombok `@SneakyThrows` 最接近)。全库都在用。
- **判断**:✅ **留**(地基 + 独特)。

---

## core/proxy

### Interceptor
- **功能**:`before/after/exception` 三钩子接口(exception 返回兜底结果)。
- **价值**:概念同 Spring AOP `MethodInterceptor`,但**零依赖 3 方法接口**;弱于 AOP Alliance(无 around/proceed,只能异常时改返回值)。
- **判断**:🟡 **视情况/留**(零依赖迷你 AOP 契约)。

### ProxyHandler
- **功能**:函数式接口 `proxy(proxy, target, method, args)`——就是把 target 显式传入的 `InvocationHandler`。
- **价值**:近似 JDK `InvocationHandler`。
- **判断**:🟡 **视情况**(近重复,仅作 proxy 迷你 API 一环)。

### ProxyFactory
- **功能**:包 JDK `Proxy.newProxyInstance`(**仅能代理接口**);`Interceptor` 重载:before→`method.invoke`→异常解包 `InvocationTargetException`→`exception`→after。(已修:异常解包)
- **价值**:JDK Proxy 的薄糖;只能代理接口(要代理具体类得 ByteBuddy/CGLIB)。
- **判断**:🟡 **视情况/留**(零依赖糖)。(已修:`after()` 移入 finally,异常路径也会执行)

---

## core/log/logback

### CmlConfigurator
- **功能**:Logback `Configurator` SPI 实现;实例初始化块把转换词 `CML` 注册进 `PatternLayout.DEFAULT_CONVERTER_MAP` → `FQCNConverter`;`configure` 返回 NEUTRAL,其余状态方法 no-op;经 `META-INF/services` 自动加载。
- **价值**:Logback 专用胶水,JDK/Guava 无从替;等价 logback.xml 里的 `<conversionRule>`,但**程序化注册,免改 XML**。
- **判断**:✅ **留**(与 FQCNConverter 配套)。

### FQCNConverter
- **功能**:Logback `NamedConverter`,打印调用栈中**第一个类名不以后缀(默认 "Logger")结尾**的帧,格式 `class.method:line`。
- **价值**:解决"三方库封装了自定义 Logger 时,内置 `%class`/`%line` 取到包装 Logger 帧、FQCN 打错"的问题;Logback 无内置"跳过后缀帧"的转换器,真填空。
- **判断**:✅ **留**(Logback 扩展,无直替)。

---

### Fn(functions)
- **功能**:定义 1~10 参的函数式接口——`Supplier`/`Consumer`(Bi/Tri/Qua/Qui/Hex/Hep/Oct/Non/Dec)/`Function`(同)。每个接口都 `extends Lambda`(可序列化,支持方法名反解)+ `Binder`(柯里化:`bind(p1)` 返回少一参的版本,如 `TriConsumer.bind` → `BiConsumer`);`of()` 做类型推断。
- **价值**:**别处没有**。JDK 只到 `BiFunction`(2 参);3 参以上接口 + 柯里化 + **可序列化 lambda 方法名反解**(JDK lambda 非 Serializable,拿不到方法名)——是 pipeline"方法引用即参数名"的地基。
- **判断**:✅ **留**(本库最独特资产)。

### Fluent(functions)
- **功能**:包裹一个 target 对象做链式操作:`apply(Function)` 变换并持有新结果;`accept(Consumer)` 副作用后**返回自身**继续链;多参重载 `apply/accept(BiFunction/TriFunction..., p1,p2,..)` 直到 5 参;`applyIf/acceptIf(fn, Param)` 按 `Param` 谓词条件执行;`consumer(...)`/`function(...)` 返回可后续绑参的 F* 子类。
- **价值**:**不是 Optional**。Optional 的 `ifPresent` 返回 void(不能链)、无多参、无按谓词条件执行。Fluent 是 Java 版 Kotlin `apply`/`also`(tap/scope 组合子)+ 多参 + 条件赋值,和 `Fn`/`Param` 一套体系。
- **判断**:🟡 **视情况**(独特但库内闲置;要不要"Java 版 apply 语义"你定,**非"与 Optional 重复"**)。

---

### DateFormatter(date)
- **功能**:**已迁 `java.time.DateTimeFormatter`**(按"模式@Locale"缓存于 `ConcurrentHashMap`,不可变线程安全,去掉 `SimpleDateFormat`+`ThreadLocal`+每次 `applyPattern`);公开 API 仍为 `Date`+模式串(不破坏 JsonParser/Reflections/jdbc);解析用 `TemporalQueries` 提取日期/时间、缺失补默认(纯日期→午夜)复现宽松语义;`parseDate(source, patterns...)` 多格式回退;`setLocale`/新增 `setZone`。
- **价值**:现已是现代 java.time 栈;新增 `TestDateFormat` 4 条断言(round-trip / 纯日期补午夜 / 多模式 / 12 小时制)验证行为。
- **判断**:✅ **留(已现代化)**。

---

## core/math

### SmartDecimal
- **功能**:`extends BigDecimal`,记住 `scale`+`RoundingMode`,对**每个运算**(add/subtract/multiply/divide/pow/abs/remainder/…)的结果自动重设精度并返回 `SmartDecimal`;`divide(divisor)` 用记住的 scale 自动四舍五入(**不再抛非终止小数异常**);`equals` 按 `compareTo`(忽略 scale),`hashCode` 与之一致(已修:用普通 BigDecimal 重建避免递归)。
- **价值**:**JDK/Guava/Commons 无等价**——原生 BigDecimal 每次运算都要显式给 scale/rounding、非终止 `divide` 抛 `ArithmeticException`。这真去掉了样板。
- **判断**:✅ **留**(独特)。⚠️ 注:继承 BigDecimal(未设计为可继承)、equals 有意偏离 scale 敏感契约(已文档说明)。

---

## core/reflection

### Reflections
- **功能**:反射大工具箱——方法/字段缓存查找(`ConcurrentHashMap`)、getter/setter 识别、`beanProperties`、`populate`(装箱兼容的 bean→bean 拷贝)、按兼容度评分的重载/构造器解析(`invoke`/`newInstance` 走 `typeMatched`/`argsMatched`)、`doConvert`(String/Number→原始/枚举/Date/LocalDate 的宽松转换)、泛型/实例判断。(本轮修过一堆 bug)
- **价值**:取值/拷贝与 Commons `BeanUtils`/Spring 重叠;但 `doConvert` 宽松转换 + 评分式重载解析 + **零第三方依赖**的组合是独特的。
- **判断**:✅ **留**(地基)。

### Generics
- **功能**:`find(clazz, genericClass, index)` 递归解析某类在指定泛型父类/接口上**实际绑定的类型实参**(处理 ParameterizedType/TypeVariable 传递)。
- **价值**:等价 Spring `GenericTypeResolver.resolveTypeArguments(clazz, genericIfc)[index]`(真等价,更完整);Guava 也能——但方法是 `TypeToken.of(clazz).getSupertype(genericClass)` 再读 `getActualTypeArguments()[index]`(**不是 `resolveType`,此处早前笔误已更正**)。**纯 JDK 无一行替代**。
- **判断**:✅ **留**(除非愿依赖 Spring/Guava)。

### Methods
- **功能**:① `getCallerClassName/MethodName(depth)`——**已改为 JDK9+ `StackWalker`**(`frameAt(n)`=`StackWalker.getInstance().walk(s->s.skip(n+2).findFirst())`,替代原 `getStackTrace()[n+3]` 魔数偏移,语义不变);② 用 **javassist 读 `LocalVariableTable`** 拿方法真实参数名。
- **价值**:① caller 部分已用纯 JDK `StackWalker`(零依赖、不靠魔数,更健壮);② 参数名反解独特(不需 `-parameters` 重编译)。
- **判断**:✅ **留**(caller 已现代化;参数名反解是无 JDK 直替的独特能力)。

### BeanProperty
- **功能**:封装单个 bean 属性(name/alias/getter/setter/field/ignore),`getValue/setValue`(getter 优先,回退 field),聚合 `@Property` 别名 / `@Ignore`。(已修:setValue 用 else if)
- **价值**:类似 `java.beans.PropertyDescriptor`,但 **含 field + 自定义注解别名/ignore**,PropertyDescriptor 没有。
- **判断**:✅ **留**。

### Classes
- **功能**:获取类的 `.class` URL、类文件磁盘路径、代码源根目录(`getClassRoot` 用 `ProtectionDomain.getCodeSource().getLocation()`)。
- **价值**:都是标准片段,但无单一 API 一次到位。依赖 `io/Files.urlToPath`。
- **判断**:✅ **留**(小而实用)。

### Primitive
- **功能**:8 种基本类型的枚举,`name`("int")↔`type`(int.class)↔`clazz`(Integer.class) 三向映射与查找。
- **价值**:Guava `com.google.common.primitives.Primitives` 提供同等能力;作为零依赖内部件也合理。
- **判断**:🟡 **视情况**(Guava Primitives 替代 / 或留零依赖)。

### Property(注解)
- **功能**:`@Property("别名")`,作用于 field/method,给属性起别名。
- **价值**:库自定义,配合 BeanProperty/映射。
- **判断**:✅ **留**。

### Ignore(注解)
- **功能**:`@Ignore`(默认 true),标记属性忽略。
- **价值**:库自定义;Jackson `@JsonIgnore` 等可替但会引入耦合。
- **判断**:✅ **留**。

---

## core/pipeline(+step)—— 一套"命名参数 + 类型安全流式 DSL"的编排框架

> 整体价值:JDK `Stream`/`Function.andThen` 只能线性串接、无命名中间结果、无按名取参、无逐步 failOver。这套用泛型状态机保证每步类型安全 + 用**方法引用当参数名**——是本库最独特的资产之一。

### Pipeline / Pipeline2 / Pipeline3
- **功能**:1/2/3 输入的流水线接口 + 内嵌 DSL 状态接口(`StepIn`/`StepIn2`/`StepIn3`/`StepOut`/`StepInit`),用 `with(Named/String...)` 按名取参、`and(fn)` 加步骤、`as(name)` 命名结果、`failOver(handler)` 逐步兜底、`end` 收尾。
- **判断**:✅ **留**(独特)。

### PipelineContext
- **功能**:执行期上下文——`Map<String,Object>` 存命名结果 + `first`/`last` 数组。(已修:每次 eval 独立创建 → 可重入)
- **判断**:✅ **留**(框架运行时载体)。

### Pipelines
- **功能**:三个 Pipeline 接口的统一实现 + `of(...)` 工厂;`eval0` 遍历 step 链,按 `inputNames` 从 context 取参,按 arity 分派到 `Fn.Function/BiFunction/TriFunction`,异常走 `failOver`。
- **判断**:✅ **留**(编排引擎核心)。

### Named
- **功能**:可序列化命名函数接口,`name()` 经 `SerializedLambda` 反解出 `类全名#方法名`,把方法引用当"参数名"。
- **判断**:✅ **留**(依赖可序列化 lambda,标准库无此能力,是 DSL 命名机制关键)。

### step/AbstractStep
- **功能**:步骤链表节点抽象——持 inputNames/outputNames/ability/nextStep/initStep/depth/errorHandler;`addAbility`/`inputNames`/`outputName` 构建链;`toStrings` 用 `Named::name` 把方法引用解析成参数名。
- **判断**:✅ **留**(框架内部件)。

### step/PipelineStep / Pipeline2Step / Pipeline3Step
- **功能**:分别实现对应 Pipeline 的全部 DSL 状态接口,把 `with/and/as/failOver/end/begin` 落成对 AbstractStep 的操作。三者结构几乎相同,只差泛型元数(S / S1,S2 / S1,S2,S3)——为支持 1/2/3 个流水线入参各写一份的样板。
- **判断**:✅ **留**(功能必需;三份重复是 Java 缺可变元数泛型的固有代价,可作未来精简点)。

---

## core/text

### Characters
- **功能**:`char[]` 常量(NUMBER/HEX/LETTER/UPPER/LOWER/NUMBER_LETTER…)。
- **价值**:radix/Encoder 的字母表底座;JDK 无这些命名常量。
- **判断**:✅ **留**(无害,是 radix API 的公开依赖)。

### Encoder
- **功能**:把每个 `char` 编成定宽 base-N 数字串(over 指定字符集),解码按定宽分组还原;另有数字编码 `encodeToNumber`/`decodeFromNumber`。
- **价值**:非标准的"逐字符 radix 再编码"(不是 Base64);真实需求(Base64/32)用 `java.util.Base64`/Guava `BaseEncoding` 更合适。类文档已注明"非加密"。
- **判断**:🟡 **视情况**(niche)。

### Expression
- **功能**:变量模板引擎,可配置边界符(`${}`/`{}`);支持位置参数、命名 `Map`、`Function<String,Object>` 解析器、**嵌套/递归展开 + 循环引用检测**、`keepUnknownVariable` 模式。
- **价值**:比 `MessageFormat`/`String.format` 强(命名 + 递归);Commons Text `StringSubstitutor` 是最接近的第三方替代。可插拔 resolver + 循环检测是真特性。
- **判断**:🟡 **视情况/留**(不引 Commons Text 就留)。

### HighLighter
- **功能**:把关键词包成 `<em>`(可改),**最长关键词优先**(`setKeywords` 用 `StringComparator.LengthDesc` 排序),可选忽略大小写,借 `TextAnalyzer` 正确跳过 HTML 标签。
- **价值**:JDK/Guava/Commons 无关键词高亮器。
- **判断**:✅ **留**(无标准替代)。

### MessageDigests
- **功能**:MD5/SHA over String/byte[]/File,`ThreadLocal<MessageDigest>` 缓存,hex 输出(已迁 `java.util.HexFormat`)。
- **价值**:Guava `Hashing`/`Files.asByteSource().hash()` 更全更稳。
- **判断**:🟡 **视情况**(Guava Hashing 替代 / 或留零依赖)。⚠️ 默认 MD5、`"SHA"`(=SHA-1)是弱算法,校验和可以、安全场景不行。

### TextAnalyzer
- **功能**:核心文本分片引擎——把文本按字符集/正则/字符串集切成 `Fragment(begin,end,matched)` 序列(可最长优先/首次出现),`rebuild` 用 handler 重组,`replace(text, map)`。
- **价值**:Fragment/handler 模型(匹配段 vs 非匹配段 + 保留原始偏移)**JDK regex / Guava `Splitter` / Commons 都没有**;是 HighLighter 和 `StringUtils.replace` 的底座。
- **判断**:✅ **留**(独特)。

### Wildcard
- **功能**:字符串 `*`/`?`/`\`转义 匹配 + Ant 风格 `**` 深路径匹配(`matchPath`),递归实现。
- **价值**:JDK `FileSystem.getPathMatcher("glob:")` 覆盖 glob 但重且面向路径;**JDK/Guava 无纯字符串通配匹配器**(本质是 Jodd 的 Wildcard)。
- **判断**:✅ **留**(填空)。

---

## core/io

### Files
- **功能**:大型文件工具——move/copy(递归,NIO `FileChannel`)、递归删、read/readLines/write/writeLines(File/InputStream/Reader/Writer,charset 感知)、流对拷、路径助手(`urlToPath` 剥 jar `!/` 后缀、`getFileExt`/`getDirectory`/`getCurrentPath`/`getFile` 拼接)。(已修:getBytes 用 readAllBytes、write(Writer,Reader)/write(File,charset) 关流)
- **价值**:read/write/copy/delete 核心重复 `java.nio.file.Files`/Guava/Commons-IO;文件名助手重复 Commons-IO `FilenameUtils`;`NEW_LINE`=`System.lineSeparator()`。独特点很少(`urlToPath` 剥 jar 后缀)。
- **判断**:🟡 **视情况**(大部分可瘦身;但被 Classes/Zipper 等内部用)。

### Closer
- **功能**:空安全静默关闭 `AutoCloseable`;`Object` 重载对非 AutoCloseable **反射调 `close()`**(鸭子类型);varargs 批量关。
- **价值**:AutoCloseable 部分 = try-with-resources / Guava `Closeables` / Commons-IO;**反射鸭子关闭**独特。
- **判断**:🟡 **视情况**(留反射重载才有意义)。

### ClassScanner
- **功能**:扫描包(file:/jar: URL)加载全部 `.class`,应用 `Predicate<Class>` 过滤,返回匹配 `Class` 列表。(已修:子包分隔符)
- **价值**:Spring/Guava `ClassPath`/Reflections 库更全;本类零依赖。
- **判断**:🟡 **视情况**。⚠️ **每扫一个类就 `System.out.println`——库里不该有,需去掉**;`ClassNotFoundException` 会中断整个扫描。

---

## core/loader

### CompositeClassLoader
- **功能**:多类加载器聚合成一个,保持双亲委派,支持运行时 add/insert/remove;每次 `loadClass`/`getResource(s)` 按当前列表重建 wrapper 链(动态生效);wrapper "非定义型"(只重写 findClass)故重建无副作用;rootLoader 优先级最高(避免共享 SPI/契约类多份身份);资源查找只委托最外层(避免 Spring `classpath*:` 重复枚举)。
- **价值**:**JDK 无聚合类加载器**,Spring/Tomcat 的是不同问题、非直替。是本库标杆资产。
- **判断**:✅ **留**。

### FileClassLoader
- **功能**:单文件/目录/jar/zip 的 **child-first** `URLClassLoader` + `excludes` 正则(强制某些类始终走父);`classPathToURL` 用 `toURI().toURL()` 建 jar:/zip: URL。
- **价值**:JDK `URLClassLoader` 是 parent-first,无 child-first;是 CompositeClassLoader 的构建块。
- **判断**:✅ **留**。

### PropertiesLoader
- **功能**:从 File/资源名/InputStream/Reader(+charset)产 `Properties`,`loadXML`(`Properties.loadFromXML`)。(已修:loadXML 关流)
- **价值**:薄封装 `Properties.load`/`loadFromXML` + classpath 查找;Spring `PropertiesLoaderUtils` 等价。
- **判断**:🟡 **视情况**(偏可删)。

### ResourceLoader
- **功能**:classpath 资源查找(URL/File/InputStream/path + `packageResource` 包名→路径前缀)。
- **价值**:`ClassLoader.getResource(AsStream)` / Spring `ClassPathResource` / Guava `Resources` 等价。
- **判断**:🟡 **视情况**(偏可删)。(已修:`getFile` 对非 file 协议/jar 内资源返回 null,不再抛异常)

---

## core/json(+serializer)

### JsonParser
- **功能**:Jackson `ObjectMapper` 门面——预设宽松解析(无引号/单引号/前导零/忽略未知)、可见性、多日期格式;`config`/`registerModule`/`setVisibility`/`withDatePatterns`/`withPropertyFilter`/`withSerializerModifier`/`modifyPropertyWriter`/`modifySerializer`/`withSerMasker` 钩子;`toBean/toObject/toArray/toList/toMap/toJson`。
- **价值**:转换便捷方法重复 Jackson 原生;**独特在把宽松配置 + 多日期 + 脱敏/PB 扩展一站式串起来**,是 serializer 扩展的入口。
- **判断**:✅ **留**(JSON 门面)。

### SerMasker(接口)+ SimpleSerMasker
- **功能**:字段脱敏——`shouldMask(name)`+`mask(name,value)`;Simple 版用正则谓词 + BiFunction/固定替换。
- **价值**:Jackson `@JsonFilter` 只能 include/exclude **不能改值**;`@JsonSerialize` 需注解侵入。本方案**按字段名正则、无注解、能改值**。
- **判断**:✅ **留**。

### SecurityPropertyFilter
- **功能**:filter 路线脱敏,覆盖 POJO 字段 + Map 条目(serializeAsField/serializeAsElement)。
- **判断**:✅ **留**(`withSerMasker` 的落地)。

### SecurityPropertyWriter
- **功能**:writer 路线脱敏,**仅 POJO 字段**;是 SecurityPropertyFilter 的贫化双胞胎(javadoc 示例签名还写错了)。
- **判断**:🅲 **可删**(保留 Filter 版)。

### PbPropertyFilter
- **功能**:filter 路线 PB 兼容——集合/数组字段额外输出 `xxxList`、Map 输出 `xxxMap`,**同时覆盖 BeanPropertyWriter 和 MapProperty**。
- **判断**:✅ **留**。

### PbPropertyWriter
- **功能**:writer 路线 PB,**仅 POJO 字段**(BeanPropertyWriter);Filter 版的贫化双胞胎。
- **判断**:🅲 **可删**(保留 Filter 版)。

### DescriptionPropertyWriter
- **功能**:序列化时,若字段有 `@JsonPropertyDescription`,在原字段后额外输出 `xxx_desc` 带描述文本。
- **价值**:Jackson 的 `@JsonPropertyDescription` 只用于 JSON Schema 生成、**不出现在序列化输出**;此类把描述写进数据(自描述 JSON)。
- **判断**:✅ **留**(小而独特)。

---

## jdbc/sql

### SqlStatement ★核心
- **功能**:流式动态 SQL 构建器,**与执行解耦**(只产 `preparedSql()`+`preparedParameters()`)。三种参数:`?`(出现序位置)、`:n`(位置)、`:name`(命名 Map);解析跳过注释/引号;**集合/数组自动展开成 `(?,?,...)`**、空集降级 `(null)`、嵌套数组展开成行值 in;`append(Param.when/notNull)` 做**条件片段**;`append(Collection<DataField>)`/`append(Map)` 按 null→`is null`、集合→`in`、其余→`=` 自动生成 where;`renderSql` 仅日志(带转义警告)。
- **价值**:Spring `NamedParameterJdbcTemplate` 与执行强绑定、无条件短路 append;MyBatis 靠 XML;jOOQ 重且需代码生成。**"轻量 + 纯拼装 + 可 append + 执行无关 + 条件谓词"这个组合点主流库无直接等价**。
- **判断**:✅ **留(全库最有价值)**。

### SqlGenerator
- **功能**:由实体注解(`@Table/@Column`)或字段列表生成 select/insert/update/delete 的 `SqlStatement`,条件仅 `=`/`is null`/`in`。(已修:insert 空字段 throwIf、未知字段报错)
- **价值**:重复 MyBatis / MyBatis-Plus `BaseMapper` / Spring Data JDBC,且功能更弱(无分页/乐观锁)。
- **判断**:🟡 **视情况**(SqlStatement 的实体便捷层,非核心卖点)。

### DataField
- **功能**:不可变三元组 `(name, value, SQLType)`,SQL 参数载体,默认类型 `JAVA_OBJECT`。
- **判断**:✅ **留**(SqlStatement 必要支撑)。

### SqlOperator
- **功能**:SQL 运算符枚举 + 模板(`? = ?`、`? in ?`…),`toSQL(name)` 普通字符串替换首个 `?`。(已修:去 replaceFirst)
- **判断**:✅ **留**(SqlGenerator/SqlStatement 依赖)。

### SqlTypeConverter
- **功能**:把参数值转字符串用于 SQL 内联(日期格式化、数值精度、布尔→1/0、集合/数组→逗号串),仅 `renderSql`(日志)用。(已修:BigDecimal.valueOf)
- **判断**:✅ **留**(服务 renderSql 调试)。

### TypeConverter(接口)
- **功能**:泛型 `S→T` 的 `convert`。
- **价值**:与 JDK `Function`/Spring `Converter` 重复。
- **判断**:🟡 **视情况**(可用 `Function` 替,改动成本高故留)。

---

## jdbc/query

### ResultSetAdapter
- **功能**:`ResultSet`→List/Map/Bean/分页/列映射的静态转换器;`getBean` 借 `DataTable` 反射注入,`getFieldValue` 逐 JDBC 类型取值 + primitive `wasNull`。(已修:分页 break、可空包装列 NPE、大小写列名映射)
- **价值**:重复 Spring `BeanPropertyRowMapper`/`ColumnMapRowMapper`、Commons DbUtils、MyBatis 结果映射。
- **判断**:🟡 **视情况**(自带 ORM 映射)。

### SqlQuery(接口)/ SqlQueryImpl / SqlQueryBuilder / ResultSetHandler(接口)
- **功能**:轻量 JDBC 执行层——`SqlQueryBuilder(dataSource).build(sqlStatement)` → `SqlQueryImpl` 执行查询/更新/批处理,配合 ResultSetAdapter 出结果;`ResultSetHandler<T>` 是回调;每次开关连接。
- **价值**:重复 Spring `JdbcTemplate`+`ResultSetExtractor` 及 Commons DbUtils `QueryRunner`/`ResultSetHandler`(**连名字都撞 DbUtils**);不支持事务/连接复用。
- **判断**:🟡 **视情况**(与 SqlStatement 搭配是"零 Spring 自带执行器";单看是 JdbcTemplate 弱化重复)。

---

## jdbc/table

### DataTable(接口)/ DataTableImpl / DataTableFactory
- **功能**:实体元数据模型(表名 + 字段属性大小写不敏感 Map + 主键);Factory 用 `ConcurrentHashMap` 按 Class 缓存单例 + 运行时覆盖表名的匿名包装。(已修:computeIfAbsent、CaseInsensitiveMap、field-only 属性 NPE)
- **价值**:重复 MyBatis `TableInfo`/Hibernate/JPA 实体元模型。
- **判断**:✅ **留**(SqlGenerator/ResultSetAdapter 的必要支撑,内聚自洽)——但整个 jdbc 栈相对 MyBatis 是轻量重复实现。

### FieldProperty
- **功能**:字段元数据 `(name/type/sqlType/getter/setter)`,反射 get/set,`Comparable` 按名排序。
- **判断**:✅ **留**(基础件)。

### Column / Id / Table(注解)
- **功能**:分别标列名+sqlType、主键、表名。语义等同 JPA `@Column`/`@Id`/`@Table` 极简版。
- **价值**:JPA(`jakarta.persistence`)有**概念相似**的 `@Table/@Column/@Id`,但**非完全对应**:① 属性名不同(cudrania 用 `value`,JPA 用 `name`),源码不兼容;② cudrania 的 `@Column.sqlType()`(`java.sql.JDBCType`)**JPA `@Column` 没有对应属性**(JPA 用 `columnDefinition` 原始 DDL 或自动推断);③ JPA 注解要在 `@Entity`+Provider 上下文才生效。只有 `@Id` 算真接近。
- **判断**:🟡 **视情况**(为避免引 JPA 而自造;迁 JPA 需改代码,不是换个 import)。注:`Column.sqlType` 默认 `OTHER` vs 无注解回退 `JAVA_OBJECT` 的细微差异,已在 `SqlQueryImpl.prepare` 处理。

### TableHelper
- **功能**:从 `@Table`/类名(下划线化)推表名,从 `@Column`/字段名(下划线化)推列名与 sqlType,返回动态 `Column`。(已修:只设 sqlType 时不丢失)
- **判断**:✅ **留**(DataTableImpl 依赖)。

---

## jdbc/datasource

### DataSourceBuilder
- **功能**:通用数据源工厂——按 name 合并 `default`+命名配置(camelCase 归一化、嵌套 Map 转 `Properties`),反射 `Reflections.populate` 填充任意 `DataSource` 实现(Hikari/Druid…),支持按谓词批量构建。
- **价值**:Spring Boot `DataSourceBuilder`(**同名**)也做自适应构建;本类**独特在多命名配置 + default 继承合并 + 反射通用填充,且零 Spring Boot 依赖**。
- **判断**:✅ **留**(无 Spring Boot 场景实用)。

---

## core/utils

### StringUtils
- **功能**:大型字符串工具(~1300 行):`isEmpty/isBlank/isAnyEmpty/...`、`leftPad/rightPad`、`capitalize/decapitalize`、`defaultIfEmpty/defaultString`、`substringBefore/After/BeforeLast/AfterLast`、`containsAny/containsAll`、`count`、`indexOfAny/lastIndexOfAny`、`indexOfTimes/lastIndexOfTimes`(第 N 次)、`join`、`combine`、`fill`(位置填充)、`eliminate`、`trim(char[]/String)`、`camelCase/pascalCase/hyphenCase/underscoreCase`、`replace(text,Map)`(最长优先多替换,走 TextAnalyzer)、`transCode`、`parseInt/parseBoolean`。
- **价值**(诚实拆分):**大量重复** Commons Lang `StringUtils`(isEmpty/pad/capitalize/substring*/containsAny/count/indexOfAny/join)+ JDK 11(`String.repeat`/`isBlank`/`String.join`)+ Guava `CaseFormat`(4 个 case 转换)。**真正独特**的只有:`combine`(去重连接符)、位置 `fill`、`indexOfTimes`、Map 最长优先 `replace`、`transCode`。
- **判断**:🟡 **视情况**(大部分可被 Commons Lang+Guava+JDK11 替;但它是全库地基,删要迁移;可保留少数独特方法)。

### RegexUtils
- **功能**:正则助手——`find`/`find(left,right)`(边界提取)、`replace`(map/Function/fill)、`escape`(=`Pattern.quote`)、`matchRegex`、`matchWildcard`(转正则)、`regexForContains/Wildcard`。
- **价值**:`replace(source,regex,Function)` JDK9+ `Matcher.replaceAll(fn)` 部分覆盖;`find` 重叠 `Matcher.results()`;但 **map 驱动 replace + 边界提取**有价值;`matchWildcard` 弱重复 `Wildcard`。
- **判断**:🟡 **视情况**(几个方法有 JDK9+ 等价;map replace/边界提取独特)。

### Enums
- **功能**:`of(class,name,default)` 按名(带默认)查;`with(class,field,value)` 按**任意字段值反射反查**枚举;`except(...)` 列出除指定外的全部常量。
- **价值**:`of` 重复 Guava `Enums.getIfPresent`/Commons `EnumUtils`;但**字段值反查 + except**JDK/Guava/Commons 都没有。
- **判断**:✅ **留**(字段反查独特)。(已修:去掉 not-found 的 `System.err`/`printStackTrace` 噪音,用 `Objects.equals` 防 NPE)

### RadixUtils
- **功能**:base 2~62 编解码,over `Characters.NUMBER_LETTER` 或任意字符集。(已修:`== "0"` → equals)
- **价值**:JDK `Long.toString(n,radix)`/`parseLong` 只到 radix 36;**>36 + 自定义字符集**超出 JDK。
- **判断**:✅ **留**。

### EmailSender
- **功能**:流式 SMTP 发信 over `javax.mail`(JavaMail),支持附件、cc/bcc、auth。(已修:`Session.getInstance` 非 getDefaultInstance)
- **价值**:JavaMail 是外部件,JDK 无等价;流式封装是真便利。
- **判断**:✅ **留**。⚠️ 硬依赖 `javax.mail`/`javax.activation`(Jakarta 迁移待做)。

### TimeCounter
- **功能**:秒表,`start/stop/timePassed/timeOut`。(已修:改用单调 `System.nanoTime` 测时长、换算毫秒;`start/stop` 返回墙钟时间戳)
- **价值**:替代是 `System.nanoTime` 差值;修复后已消除"墙钟非单调"缺陷。
- **判断**:✅ **留(已修)**。

### Zipper
- **功能**:zip/unzip 工具 over **ant `org.apache.tools.zip`**,目录树压缩(保结构)、map/handler 命名、过滤压缩、解压、zip 内随机读。(已修:FileSearcher 依赖已内联为私有 `findFiles`)
- **价值**:Guava/JDK 无高层目录压缩;Commons Compress 是最接近的真替代。
- **判断**:✅ **留**(相对裸 `java.util.zip` 是真便利)。⚠️ 依赖 ant;`unzip` **无 zip-slip 路径穿越防护**(安全隐患,建议修)。

---

## core/tree

### TreeNode
- **功能**:孩子-兄弟链式节点(`firstChild`/`nextBrother`/`parent` + `exist` 标记 + `data`),`addChild`/`addBrother`/`first`/`last`,equals/hashCode 按 `data`。
- **价值**:TreeBuilder/TrieTree 的支撑结构;是这两者的专用节点模型。
- **判断**:✅ **留**(与 TreeBuilder/TrieTree 一体)。

### TreeBuilder
- **功能**:`buildTree(扁平Collection, idGen, parentIdGen)` → 虚拟根;`buildTree(Map<child,parent>)`;`minimize`(后序剪枝,只留通向 selector 命中节点的路径);`traversal`(层序遍历)。
- **价值**:JDK 无"扁平列表→树 + 剪枝";Guava `Traverser` 只遍历、不从扁平表构建。菜单/组织架构的真实需求。
- **判断**:✅ **留**(独特)。

### TrieTree
- **功能**:字符串压缩/radix 前缀树;`insert`/`find`/`delete`(软删,`exist=false`)/`suggest`(前缀自动补全)/`optimize`(纵向合并无数据的单孩子链)/`display`(调试渲染)。(已修:optimize 遍历所有子节点)
- **价值**:最接近的 Commons-Collections `PatriciaTrie` 是 key→value **Map**,而本类是带 `suggest`/软删/压缩的**字符串集**,无直替。
- **判断**:✅ **留**(独特)。

---

## ✅ 完成:全部 97 个类已逐个通读并分析

- **判断口径**:✅留(独特/地基)· 🟡视情况(有替代,取决于是否愿引依赖/是否要该能力)· 🅲双实现精简 · 🔧改造非删。
- **删除建议清单**见 `CLEANUP.md`(已执行 🅰🅱,含核实证据)。
- **读代码时发现、与删除无关的 bug**:MultiBitSet.cardinality 漏最高位、ClassScanner 打印、ResourceLoader.getFile 对 jar 失效、ProxyFactory after 不在 finally、Zipper.unzip 无 zip-slip、MessageDigests 弱算法默认——见 CLEANUP.md 第七节。

