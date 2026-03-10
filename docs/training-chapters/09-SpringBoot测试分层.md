# 第09章 Spring Boot 测试分层

## 本章导读
进入 Spring Boot 项目后，单元测试不再只是“调用一个普通 Java 类的方法并断言结果”。框架的引入带来了 Controller、Service、配置、序列化、依赖注入和上下文装配等复杂度。如果对所有场景都统一使用 `@SpringBootTest`，虽然看起来“一次性测全”，但测试执行成本高、定位速度慢、反馈周期长。要让 Spring Boot 测试真正发挥价值，必须建立“按目标选择测试层级”的意识。本章围绕 Spring Boot 常见测试分层展开，说明不同测试方式分别解决什么问题，以及在项目中如何做取舍。

## 学习目标
- 理解纯单元测试、切片测试、全上下文测试的差异。
- 掌握 `@WebMvcTest` 与 `@SpringBootTest` 的定位。
- 能根据测试目标选择合适的测试范围。
- 结合当前项目示例理解 Spring Boot 测试分层的实践方式。

## 9.1 为什么要做测试分层
Spring Boot 的强大之处在于自动装配和框架集成，但这也意味着不同层级的测试成本差异明显。

如果不做分层，常见问题包括：
- 所有测试都启动完整 Spring 上下文，执行过慢。
- 某个 HTTP 层问题却要在全项目启动后才能发现。
- 一个简单业务逻辑失败，需要从控制器、服务、数据层一路排查。
- 测试职责不清，导致“该快的不快、该全的不全”。

测试分层的核心目标只有一句话：  
以尽可能小的测试范围，获取足够大的质量信心。

## 9.2 三种常见测试层级
在 Spring Boot 项目中，最常见的测试层级可以概括为三类。

### 9.2.1 纯单元测试
特点：
- 不启动 Spring 上下文
- 不依赖容器
- 只验证当前类的业务逻辑
- 执行速度最快

适用于：
- 规则判断
- 字符串处理
- 参数校验
- 无需框架参与的 Service 逻辑

### 9.2.2 切片测试
特点：
- 只加载某一层相关组件
- 不启动整个应用
- 适合验证某一类框架行为

典型形式：
- `@WebMvcTest`：只加载 Web 层
- `@MybatisTest`：只加载 MyBatis 相关能力

适用于：
- Controller 的请求映射、参数绑定、JSON 返回
- Mapper 的 SQL 映射与数据访问

### 9.2.3 全上下文测试
特点：
- 启动完整 Spring Boot 应用上下文
- 验证跨层协作和配置装配
- 执行成本最高，但验证范围最完整

典型形式：
- `@SpringBootTest`

适用于：
- Service + Mapper 协作
- 自动装配验证
- 配置与依赖注入链路验证

## 9.3 纯单元测试：最快的反馈方式
对应示例：
- `src/test/java/com/company/training/spring/service/GreetingServiceTest.java`

该测试没有使用任何 Spring 注解，只是直接创建 `GreetingService` 实例并执行方法断言。  
这类写法的优点非常明确：
- 启动快
- 失败定位直接
- 对环境依赖极低

这也是为什么在 Spring Boot 项目中，依然应优先让尽可能多的业务逻辑保持“可脱离容器测试”。

如果一个 Service 的逻辑只是字符串处理、参数判断或规则计算，那么没有必要为了“统一风格”强行加 `@SpringBootTest`。

## 9.4 `@WebMvcTest`：只测 Web 层
对应示例：
- `src/test/java/com/company/training/spring/controller/GreetingControllerWebMvcTest.java`

这个测试展示了 Spring Boot 中非常重要的一类测试：Controller 切片测试。

### 9.4.1 它解决什么问题
Controller 层通常负责：
- URL 映射
- 请求参数接收
- HTTP 状态码返回
- JSON 输出结构

如果这类问题都通过 `@SpringBootTest` 来测，成本过高。  
而 `@WebMvcTest` 只加载 Web 层相关组件，能更快地验证接口契约是否正确。

### 9.4.2 示例中的关键点
测试类中使用了：
- `@WebMvcTest(controllers = GreetingController.class)`
- `@Autowired MockMvc`
- `@MockBean GreetingService`

这说明：
- 当前测试只关心 Controller
- Service 逻辑不在本测试中展开
- 通过 `MockMvc` 模拟 HTTP 请求并断言返回结果

### 9.4.3 为什么这里要用 `@MockBean`
因为 Controller 依赖 `GreetingService`，但本测试的目标不是验证 Service 内部逻辑，而是验证：
- 请求是否被正确映射
- 参数是否被正确传入
- JSON 返回是否符合预期

因此，`GreetingService` 在这里被替换成 mock，避免把 Service 行为也一起混入测试范围。

## 9.5 `@SpringBootTest`：验证完整上下文
对应示例：
- `src/test/java/com/company/training/spring/service/EmployeeServiceSpringBootTest.java`

这类测试会启动完整应用上下文，适合验证：
- Bean 是否正确装配
- Service 与 Mapper 是否协作正常
- 数据库测试环境是否可用
- SQL 初始化与事务行为是否符合预期

在该示例中，`EmployeeService` 的测试不仅要验证业务流程，还要确保：
- Spring 能正确注入 `EmployeeService`
- `EmployeeService` 能正确调用 Mapper
- 测试数据库环境能被正常初始化

这类测试比纯单测更重，但也能覆盖更完整的真实链路。

## 9.6 如何选择测试层级
可以用一个简单判断方法：

### 9.6.1 如果只关心业务逻辑
优先选择纯单元测试。

### 9.6.2 如果只关心某一层框架行为
优先选择切片测试。  
例如：
- Web 层问题用 `@WebMvcTest`
- MyBatis 层问题用 `@MybatisTest`

### 9.6.3 如果关心多层协作
使用 `@SpringBootTest`。

换句话说，不应先问“我会不会用哪个注解”，而应先问：  
这次测试到底想验证什么。

## 9.7 当前项目中的分层映射
本项目已经提供了较清晰的示例映射关系：

- 纯单元测试：
  - `src/test/java/com/company/training/spring/service/GreetingServiceTest.java`
- Web 切片测试：
  - `src/test/java/com/company/training/spring/controller/GreetingControllerWebMvcTest.java`
- 全上下文协作测试：
  - `src/test/java/com/company/training/spring/service/EmployeeServiceSpringBootTest.java`

这样的安排有助于在培训中向团队说明：  
Spring Boot 测试不是只有一种写法，而是应根据验证目标进行分层设计。

## 9.8 常见误区
### 9.8.1 所有 Spring 测试都用 `@SpringBootTest`
这会导致测试过重、执行过慢、定位困难。

### 9.8.2 为了“看起来像 Spring 项目”，把纯逻辑也放进上下文测试
这会浪费测试资源，也掩盖代码本身是否具备可测试性。

### 9.8.3 Controller 测试顺带验证 Service 逻辑
这会让测试职责混乱。  
Controller 应优先验证接口层行为，而不是替代 Service 测试。

### 9.8.4 测试层级划分与代码设计脱节
如果代码难以进行纯单元测试，往往说明业务逻辑与框架耦合过深，需要从设计上优化。

## 9.9 实践建议
- 先从纯单元测试覆盖可脱离框架的业务逻辑。
- 对 HTTP 契约类问题使用 `@WebMvcTest`。
- 对多层协作和装配问题使用 `@SpringBootTest`。
- 不要把“能测”误解为“测得合理”，测试范围选择本身就是设计能力。

## 本章小结
Spring Boot 测试分层的本质是：  
在不同验证目标之间，选择最合适的测试成本。纯单测强调快速反馈，切片测试强调局部框架行为验证，全上下文测试强调真实协作链路。理解并合理使用这些层级，才能让测试体系既高效又可靠。

## 思考与练习
- 将一个当前使用 `@SpringBootTest` 的简单场景，改写为纯单元测试或切片测试。
- 比较 `GreetingServiceTest` 和 `GreetingControllerWebMvcTest` 的执行成本与验证重点。
- 思考：如果一个 Service 无法脱离 Spring 上下文测试，问题更可能出在测试代码还是业务设计？
