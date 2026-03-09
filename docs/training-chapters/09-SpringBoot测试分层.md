# 第09章 SpringBoot测试分层

## 本章导读
Spring Boot 项目中，测试成本随加载范围增大而上升。本章强调“按目标选择最小测试范围”。

## 学习目标
- 理解纯单测、切片测试、全上下文测试的差异。
- 能根据问题类型选择合适测试手段。

## 9.1 分层策略
- 纯单元测试：仅验证业务方法，不启动容器。
- Web 切片测试：仅验证控制器层契约。
- 全上下文测试：验证跨层协作与配置装配。

## 9.2 案例解析
- Service 单测：`src/test/java/com/company/training/spring/service/GreetingServiceTest.java`
- Controller 切片：`src/test/java/com/company/training/spring/controller/GreetingControllerWebMvcTest.java`

## 9.3 选择建议
- 只测业务逻辑时优先纯单测。
- 只测 HTTP 协议与序列化时优先 `@WebMvcTest`。
- 涉及跨层协作时使用 `@SpringBootTest`。

## 本章小结
测试分层的本质是“以最小成本获取足够信心”。

## 思考与练习
- 将一个可由 `@WebMvcTest` 覆盖的场景从 `@SpringBootTest` 改写为切片测试。
- 比较两种方式的执行速度与失败定位差异。
