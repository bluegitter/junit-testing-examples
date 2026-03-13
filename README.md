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

## 什么是“单元测试指标透出”

单元测试指标透出，是指把原本只存在于测试报告里的结果，例如：
- 总用例数
- 通过/失败/跳过数
- 执行耗时
- 覆盖率

转换成 Prometheus 可抓取的监控指标，再在 Grafana 中可视化展示。  
这类做法常用于 CI 质量看板、培训演示环境、测试平台和质量门禁。

本项目已经补充了一个最小示例：
- 启动 Spring Boot 应用后，可从 `target/surefire-reports/*.xml` 和 `target/site/jacoco/jacoco.xml` 读取测试结果
- 通过 `/actuator/prometheus` 暴露为 Prometheus 指标
- 通过 `/api/test-metrics/summary` 查看当前聚合结果
- 通过 `/api/test-metrics/refresh` 手工刷新一次指标快照

主要指标包括：
- `training_unit_test_cases_total{status="total|passed|failed|error|skipped"}`
- `training_unit_test_suites_total`
- `training_unit_test_duration_seconds`
- `training_unit_test_success_rate`
- `training_unit_test_last_run_timestamp_seconds`
- `training_unit_test_coverage_ratio{counter="line|branch|instruction|method|class|complexity"}`

## 启动指标示例

先生成测试与覆盖率报告：

```bash
mvn verify
```

再启动应用：

```bash
mvn spring-boot:run
```

如果想在执行 `mvn verify` 后自动刷新一次指标快照，可直接执行：

```bash
./scripts/verify-and-refresh-metrics.sh
```

如果指标服务不在本机 `8080`，可通过环境变量指定：

```bash
TEST_METRICS_BASE_URL=http://192.168.64.20:8080 ./scripts/verify-and-refresh-metrics.sh
```

验证接口：

```bash
curl http://localhost:8080/api/test-metrics/summary
curl -X POST http://localhost:8080/api/test-metrics/refresh
curl http://localhost:8080/actuator/prometheus | grep training_unit_test
```

Prometheus 抓取示例见：
- `monitoring/prometheus/unit-test-metrics-scrape.yml`

Grafana Dashboard JSON 见：
- `monitoring/grafana/junit-unit-test-metrics-dashboard.json`

注意：
- 这个脚本只会刷新应用暴露出来的最新测试指标
- 不会直接写入 Prometheus 数据库
- 只有当 Prometheus 已经配置抓取该应用，并在下一次 scrape 发生后，指标才会进入 Prometheus 时序库

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
