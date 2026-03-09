# Java JUnit 单元测试培训大纲

## 阶段 A：基础入门

## 1. 培训目标
- 明确单元测试价值：防回归、重构保障、提升交付质量。
- 建立“可讲、可写、可落地”的测试思维。
- 本次覆盖：JUnit 5 + Mockito + Spring Boot + MyBatis。

## 2. 环境与项目结构
- JDK：1.8.0
- 构建：Maven（`pom.xml`）
- 测试框架：JUnit Jupiter（JUnit 5）
- Mock：Mockito
- 运行命令：`mvn test`

## 3. JUnit 核心写法
- AAA 模式（Arrange / Act / Assert）
- 测试命名规范（Given-When-Then）
- 常用断言：`assertEquals`、`assertThrows`、`assertTimeout`
- 演示文件：
  - `src/test/java/com/company/training/core/CalculatorTest.java`
  - `src/test/java/com/company/training/core/TimeoutAndDisabledTest.java`

## 4. 生命周期与测试组织
- `@BeforeEach` / `@AfterEach`
- `@Nested` 场景分组
- `@DisplayName` 提升可读性
- 演示文件：
  - `src/test/java/com/company/training/core/LifecycleAndNestedTest.java`

## 阶段 B：JUnit 进阶（提升覆盖率与可维护性）

## 5. 参数化测试基础
- 为什么参数化：减少重复、提高覆盖
- `@CsvSource`、`@ValueSource`、`@NullSource`
- 演示文件：
  - `src/test/java/com/company/training/core/StringNormalizerParameterizedTest.java`

## 6. 参数化测试进阶
- `@MethodSource` 组织复杂测试数据
- 业务规则类测试写法（折扣、阈值）
- 演示文件：
  - `src/test/java/com/company/training/core/DiscountCalculatorMethodSourceTest.java`
  - `src/main/java/com/company/training/core/DiscountCalculator.java`

## 7. Mock 与依赖隔离
- `@Mock`、`@InjectMocks`、`when`、`verify`、`never`
- 参数捕获：`ArgumentCaptor`
- 调用顺序：`InOrder`
- 演示文件：
  - `src/test/java/com/company/training/service/UserServiceMockitoTest.java`
  - `src/test/java/com/company/training/service/UserServiceArgumentCaptorTest.java`
  - `src/test/java/com/company/training/service/UserServiceInteractionOrderTest.java`

## 8. 高级技巧
- `assertAll`、`assertDoesNotThrow`
- `assumeTrue`（条件跳过）
- `@RepeatedTest`、`@TestFactory`
- 演示文件：
  - `src/test/java/com/company/training/core/AssertionsAndAssumptionsTest.java`
  - `src/test/java/com/company/training/core/RepeatedAndDynamicTest.java`

## 阶段 C：框架实战（Spring Boot + MyBatis）

## 9. Spring Boot 测试分层（30 分钟）
- Service 纯单测（不启 Spring）
- Controller 切片测试：`@WebMvcTest` + `MockMvc` + `@MockBean`
- 何时使用 `@SpringBootTest`
- 演示文件：
  - `src/test/java/com/company/training/spring/service/GreetingServiceTest.java`
  - `src/test/java/com/company/training/spring/controller/GreetingControllerWebMvcTest.java`

## 10. MyBatis Mapper 层 CRUD 测试
- `@MybatisTest` 验证 SQL 映射
- 增删查改（insert/update/delete/select）
- 演示文件：
  - `src/main/java/com/company/training/spring/mapper/EmployeeMapper.java`
  - `src/test/java/com/company/training/spring/mapper/EmployeeMapperMybatisTest.java`

## 11. MyBatis Service 集成测试
- `@SpringBootTest` 验证业务层 + Mapper 协作
- `@Sql` + H2 内存库保证测试隔离
- 演示文件：
  - `src/main/java/com/company/training/spring/service/EmployeeService.java`
  - `src/test/java/com/company/training/spring/service/EmployeeServiceSpringBootTest.java`
  - `src/test/resources/sql/employee-schema.sql`

## 阶段 D：最佳实践

## 12. 最佳实践与反模式
- 最佳实践：单测只验证一个行为、断言聚焦结果、测试数据最小化
- 反模式：测试里写复杂逻辑、过度 Mock、不稳定测试、只写不跑

## 13. 测试报告与覆盖率报告生成
- 一键命令：`mvn clean verify`
- 单测报告：`target/surefire-reports/`、`target/site/surefire-report.html`
- 覆盖率报告：`target/site/jacoco/index.html`、`target/site/jacoco/jacoco.xml`
- CI 集成：报告上传与覆盖率阈值校验
