# 第11章 MyBatis Service集成测试

## 本章导读
在前一章中，Mapper 层测试解决的是“SQL 执行与结果映射是否正确”的问题。但在真实业务中，数据访问往往不是直接从测试代码调用 Mapper，而是通过 Service 层完成业务编排、参数组装和调用协调。因此，仅有 Mapper 测试还不够，还需要进一步验证 Service 与 Mapper 之间的协作是否正常、Spring 容器是否正确装配、数据库环境是否可用。本章聚焦 Service 层集成测试，说明它与 Mapper 测试的区别、使用场景以及当前项目中的完整示例。

## 学习目标
- 理解 Service 集成测试与 Mapper 单测的职责边界。
- 掌握 `@SpringBootTest` 在数据访问协作场景中的用法。
- 理解 `@Sql` 在集成测试中的隔离价值。
- 能为 Service + Mapper 协作流程编写完整集成测试。

## 11.1 为什么 Mapper 测试之后还需要 Service 集成测试
Mapper 测试关注的是：
- SQL 是否正确
- 映射是否正确
- 单条数据库操作是否正确

但 Service 层还承担了另外一些职责，例如：
- 组装业务对象
- 协调多个 Mapper 或依赖调用
- 控制业务流程顺序
- 对数据库操作结果进行进一步处理

因此，即使 Mapper 层测试都通过，也不能完全说明：
- Spring 是否正确注入 Service 和 Mapper
- Service 是否正确调用 Mapper
- 业务流程在完整上下文中是否可运行

Service 集成测试的意义，正是在于验证这类跨层协作。

## 11.2 什么是集成测试
本章所说的集成测试，指的是：
- 启动 Spring 上下文
- 使用真实的 Service Bean
- 使用真实的 Mapper Bean
- 使用测试数据库环境
- 验证完整的业务调用链

与纯单元测试相比，集成测试的特点是：
- 范围更大
- 依赖更多
- 执行成本更高
- 但验证链路更完整

因此，集成测试不是为了替代单元测试，而是作为更高层级的补充。

## 11.3 为什么使用 `@SpringBootTest`
`@SpringBootTest` 的作用是启动完整 Spring Boot 应用上下文。  
在本项目中，这意味着：
- `EmployeeService` 会作为真实 Bean 被注入
- `EmployeeMapper` 会由 MyBatis 自动装配
- 数据源、Mapper 扫描、Spring 配置会一起参与启动

这类测试适合验证以下问题：
- Bean 是否能被正确注入
- Service 与 Mapper 是否能协作
- 配置、扫描路径和自动装配是否正常
- 实际业务流程是否在完整上下文中成立

如果测试目标仅仅是验证一个简单规则，那么 `@SpringBootTest` 通常过重；  
但如果目标是验证“Service + 数据访问链路”，它就是合适选择。

## 11.4 `@Sql` 在集成测试中的作用
集成测试最大的风险之一，是数据环境不稳定。  
如果不在每个测试前重建环境，常见问题包括：
- 上一个测试写入的数据污染下一个测试
- 本地多次执行结果不一致
- CI 环境与本地环境表现不同

本项目通过：

```java
@Sql(scripts = "/sql/employee-schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
```

实现了每个测试前重建表结构。  
这样做带来的收益非常明确：
- 测试环境一致
- 数据状态可预测
- 每个测试可独立执行

## 11.5 当前示例结构
对应 Service：
- `src/main/java/com/company/training/spring/service/EmployeeService.java`

对应测试：
- `src/test/java/com/company/training/spring/service/EmployeeServiceSpringBootTest.java`

对应初始化脚本：
- `src/test/resources/sql/employee-schema.sql`

其中 `EmployeeService` 本身很简单，但这正好适合作为培训示例，因为可以把注意力集中在“集成测试在验证什么”上，而不是被复杂业务细节干扰。

## 11.6 `EmployeeService` 测试验证了什么
测试方法 `shouldCompleteCrudFlow()` 展示的是一个完整业务链路：
1. 创建员工
2. 根据 ID 查询员工
3. 更新员工信息
4. 查询所有员工
5. 删除员工
6. 确认删除结果

从表面上看，这是一个 CRUD 流程；  
从测试设计角度看，它实际验证了以下内容：
- Service Bean 是否成功注入
- Mapper 是否已正常装配
- 插入、查询、更新、删除链路是否贯通
- 数据库脚本是否正确初始化
- 业务方法在完整上下文中是否可运行

这就是集成测试与 Mapper 单测的最大差异：  
它关注的是“链路是否可用”，而不仅仅是“某条 SQL 是否正确”。

## 11.7 为什么这里不使用 Mock
在 Service 的纯单元测试中，通常会把依赖层 mock 掉。  
但本章的测试目标并不是隔离 Mapper，而是要验证 Service 与 Mapper 的真实协作。

因此，在这里：
- 不应该 mock `EmployeeMapper`
- 不应该 mock 数据源
- 不应该只验证单独某个方法调用

而应让真实 Spring 上下文与真实数据库测试环境参与执行。

这说明一个重要原则：  
是否使用 Mock，取决于测试目标，而不是取决于个人习惯。

## 11.8 Service 集成测试与 Mapper 测试的职责边界
可以用下面的方式理解两者差异：

### 11.8.1 Mapper 测试
重点验证：
- SQL 正确性
- 字段映射正确性
- CRUD 单操作是否正确

### 11.8.2 Service 集成测试
重点验证：
- Service 与 Mapper 的协作
- Spring 注入是否正确
- 完整业务流程是否走通
- 数据状态是否按预期变化

两者并不是“二选一”，而是互补关系：
- Mapper 测试偏底层、偏精细
- Service 集成测试偏链路、偏整体

## 11.9 常见误区
### 11.9.1 用集成测试替代所有单元测试
这会导致测试体系过重，反馈变慢，失败定位困难。

### 11.9.2 认为 Mapper 测试已经足够，不需要 Service 测试
这会遗漏装配问题、业务编排问题和多层协作问题。

### 11.9.3 集成测试不做环境重置
如果没有 `@Sql` 或等价机制，测试间污染会迅速出现。

### 11.9.4 在集成测试里只验证“没有报错”
集成测试也必须有明确断言，验证数据变化和业务结果，而不是只看“程序跑完了”。

## 11.10 实践建议
- 对关键 Service 流程，至少建立一条完整集成测试链路。
- 集成测试优先覆盖“多层协作”而不是“单纯算法逻辑”。
- 每个测试前重置数据库环境，保证稳定性。
- 集成测试要断言最终状态，而不仅是中间过程。
- 不要因为 `@SpringBootTest` 好用，就把所有测试都做成全上下文测试。

## 本章小结
Service 集成测试的核心目标，是验证业务层与数据访问层在真实 Spring 上下文中的协作是否正常。它补足了 Mapper 测试无法覆盖的装配、流程和链路问题。通过 `@SpringBootTest`、`@Sql` 和内存数据库，可以建立稳定、可重复的集成测试环境，为关键业务流程提供更完整的质量保障。

## 思考与练习
- 为 `EmployeeService` 增加一个“批量创建”方法，并补充集成测试。
- 比较 Mapper 测试和 Service 集成测试的执行成本与定位能力差异。
- 思考：如果一个 Service 方法本身没有业务逻辑，只是简单转调 Mapper，是否仍然值得写完整集成测试？
