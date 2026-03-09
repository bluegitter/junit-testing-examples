# 第10章 MyBatis Mapper层CRUD测试

## 本章导读
Mapper 层测试的核心是验证 SQL 映射是否正确，确保数据访问行为与预期一致。

## 学习目标
- 使用 `@MybatisTest` 验证 Mapper 行为。
- 覆盖增删查改四类基础操作。

## 10.1 测试策略
- 使用 H2 内存库，避免依赖外部数据库。
- 使用 `@Sql` 在测试前重建表结构，保证隔离。
- 直接对 Mapper 进行操作与断言。

## 10.2 案例解析
- Mapper：`src/main/java/com/company/training/spring/mapper/EmployeeMapper.java`
- 测试：`src/test/java/com/company/training/spring/mapper/EmployeeMapperMybatisTest.java`
- 脚本：`src/test/resources/sql/employee-schema.sql`

## 10.3 关注点
- `insert` 后主键是否回填。
- `update` 是否只影响目标记录。
- `delete` 后数据是否确实不可见。
- `selectAll` 返回顺序是否符合预期。

## 本章小结
Mapper 测试是数据访问层的“第一道防线”，对 SQL 变更尤为重要。

## 思考与练习
- 为 `EmployeeMapper` 增加按邮箱查询方法并补充测试。
- 在脚本中增加唯一约束，验证冲突场景测试。
