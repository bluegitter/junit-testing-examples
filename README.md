# junit-testing-examples

Java JUnit 培训示例工程（JDK 1.8 + Maven）。

## 环境准备

- JDK：1.8
- Maven：3.8+ 或 3.9+

确认 JDK 已安装：

```bash
java -version
```

## 安装 Maven

### macOS

使用 Homebrew：

```bash
brew install maven
```

### Linux

Ubuntu / Debian：

```bash
sudo apt update
sudo apt install maven
```

CentOS / RHEL：

```bash
sudo yum install maven
```

### Windows

可通过以下方式之一安装：
- 安装 Chocolatey 后执行：

```bash
choco install maven
```

- 或从 Apache Maven 官方网站下载二进制压缩包，解压后配置 `MAVEN_HOME` 和 `PATH`

### 验证 Maven 是否安装成功

```bash
mvn -v
```

如果输出 Maven 版本、Java 版本和本机系统信息，说明安装成功。

## 快速开始

```bash
mvn test
```

## 执行单个测试用例

执行单个测试类：

```bash
mvn -Dtest=CalculatorTest test
```

执行单个测试方法：

```bash
mvn -Dtest=CalculatorTest#shouldAddTwoNumbers test
```

执行本项目中的其他示例类，也可以采用同样写法，例如：

```bash
mvn -Dtest=OrderStatusServiceExternalCallTest test
mvn -Dtest=OrderStatusServiceExternalCallTest#shouldRetryOnceWhenFirstRemoteCallTimesOut test
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
