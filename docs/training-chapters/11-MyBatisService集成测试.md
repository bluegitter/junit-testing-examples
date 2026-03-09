# 第11章 MyBatis Service集成测试

## 本章导读
当业务逻辑跨越 Service 与 Mapper 时，需要通过集成测试验证协作是否正确。

## 学习目标
- 使用 `@SpringBootTest` 验证完整业务流程。
- 理解集成测试与 Mapper 测试的职责差异。

## 11.1 测试边界
- Mapper 测试关注 SQL 正确性。
- Service 集成测试关注业务流程编排与数据一致性。

## 11.2 案例解析
- Service：`src/main/java/com/company/training/spring/service/EmployeeService.java`
- 测试：`src/test/java/com/company/training/spring/service/EmployeeServiceSpringBootTest.java`
- 初始化脚本：`src/test/resources/sql/employee-schema.sql`

案例展示了完整 CRUD 流程：创建、查询、更新、删除。

## 11.3 隔离与稳定性
通过 `@Sql` 在每个测试前重置数据环境，保证结果可重复、可预测。

## 本章小结
集成测试为跨层行为提供验证依据，是系统级质量保障的重要组成部分。

## 思考与练习
- 为 `EmployeeService` 增加“批量创建”方法并补测试。
- 思考哪些场景适合继续下沉为纯单测。
