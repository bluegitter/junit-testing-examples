# junit-testing-examples

Java JUnit 培训示例工程（JDK 1.8 + Maven）。

## 快速开始

```bash
mvn test
```

## 生成测试报告与覆盖率报告

```bash
mvn clean verify
```

执行后可查看：
- 单元测试原始报告（XML/TXT）：`target/surefire-reports/`
- 单元测试 HTML 报告：`target/site/surefire-report.html`
- 覆盖率 HTML 报告：`target/site/jacoco/index.html`
- 覆盖率 XML 报告：`target/site/jacoco/jacoco.xml`

## 目录

- 培训大纲：`TRAINING_OUTLINE.md`
- 源码：`src/main/java/com/company/training`
- 测试：`src/test/java/com/company/training`

## 示例覆盖点

- 基础断言、异常断言、超时断言
- 参数化测试：`@CsvSource`、`@ValueSource`、`@NullSource`、`@MethodSource`
- 生命周期管理：`@BeforeEach`、`@AfterEach`、`@Nested`
- 重复测试和动态测试：`@RepeatedTest`、`@TestFactory`
- Mockito：`@Mock`、`@InjectMocks`、`verify`、`ArgumentCaptor`、`InOrder`
- Spring Boot：`@WebMvcTest`、`MockMvc`、`@SpringBootTest`
- MyBatis：`@MybatisTest` 下的增删查改（H2 内存数据库）
