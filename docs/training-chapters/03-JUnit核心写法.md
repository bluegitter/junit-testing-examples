# 第03章 JUnit核心写法

## 本章导读
单元测试的核心不是注解数量，而是表达能力。本章围绕最常见场景，建立可复用的测试写作模板。

## 学习目标
- 掌握 AAA（Arrange-Act-Assert）结构。
- 能编写正常路径与异常路径测试。
- 理解断言与业务规则的一一对应关系。

## 3.1 AAA 写作模型
- Arrange：准备输入、依赖和前置条件。
- Act：执行被测方法。
- Assert：验证结果与行为。

AAA 的价值在于让测试读起来像业务说明书：输入是什么、发生了什么、期望是什么。

## 3.2 常用断言
- `assertEquals`：验证结果值。
- `assertThrows`：验证异常类型及信息。
- `assertTimeout`：验证执行时长边界。

## 3.3 案例解析
- `src/test/java/com/company/training/core/CalculatorTest.java`
  - 演示算术逻辑测试与异常断言。
- `src/test/java/com/company/training/core/TimeoutAndDisabledTest.java`
  - 演示时间约束与临时禁用用例。

## 3.4 实践建议
- 每个测试只验证一个明确行为。
- 测试方法名直接反映业务语义。
- 断言应尽可能接近业务规则，而非实现细节。

## 本章小结
高质量单元测试的第一步，是把业务规则准确、清晰地写成断言。

## 思考与练习
- 为 `Calculator` 再补一个边界值用例。
- 将一个过长测试拆分为两个更聚焦的测试方法。
