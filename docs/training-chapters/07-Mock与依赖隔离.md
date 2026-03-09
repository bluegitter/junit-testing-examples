# 第07章 Mock与依赖隔离

## 本章导读
单元测试的边界在于“只验证当前对象职责”。当方法依赖数据库、消息或外部服务时，需要通过 Mock 做隔离。

## 学习目标
- 理解 Mock 在单元测试中的定位。
- 掌握交互验证、参数捕获与顺序校验。

## 7.1 Mock 的基本原则
- Mock 外部依赖，不 Mock 被测对象本身。
- 验证关键业务交互，不验证无意义细节。

## 7.2 常用技术
- `@Mock`、`@InjectMocks`
- `when(...).thenReturn(...)`
- `verify(...)`、`never()`
- `ArgumentCaptor`
- `InOrder`

## 7.3 案例解析
- `src/test/java/com/company/training/service/UserServiceMockitoTest.java`
- `src/test/java/com/company/training/service/UserServiceArgumentCaptorTest.java`
- `src/test/java/com/company/training/service/UserServiceInteractionOrderTest.java`

建议关注三类验证：
- 结果是否正确。
- 关键依赖是否被调用。
- 调用参数与调用顺序是否符合业务约束。

## 7.4 关键语句逐行拆解
以下代码来自 `UserServiceArgumentCaptorTest`，用于验证“保存用户时传入的数据是否正确”。

```java
when(userRepository.findByEmail("new@company.com")).thenReturn(null);
when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class)))
        .thenAnswer(invocation -> {
            User input = invocation.getArgument(0);
            return new User(200L, input.getEmail(), input.getName());
        });

userService.register("new@company.com", "New Guy");

ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
verify(userRepository).save(userCaptor.capture());
User captured = userCaptor.getValue();
assertEquals("new@company.com", captured.getEmail());
assertEquals("New Guy", captured.getName());
```

### 7.4.1 `when(...).thenReturn(...)`
`when(userRepository.findByEmail("new@company.com")).thenReturn(null);`

这行是静态桩（stub）设置，表示当代码查询这个邮箱时，仓储返回 `null`，即“用户不存在”。  
这样被测逻辑会进入“注册新用户”的分支。

### 7.4.2 `when(...).thenAnswer(...)`
`thenAnswer` 适用于返回值依赖入参的场景。  
在本例中，`save(...)` 被调用时会拿到传入的 `User`，再基于该对象构造一个“已分配 ID 的用户”返回。

这模拟了数据库插入后的典型行为：  
- 入库前对象无主键。  
- 入库后对象获得主键（如 `200L`）。

相比 `thenReturn(固定对象)`，`thenAnswer` 更灵活，尤其适合“返回值与调用参数关联”的业务场景。

### 7.4.3 `ArgumentCaptor` 捕获参数
```java
ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
verify(userRepository).save(userCaptor.capture());
User captured = userCaptor.getValue();
```

这三行做了两件事：  
- `verify(...)`：确认 `save(...)` 确实被调用。  
- `capture()`：把这次调用传入的 `User` 实参抓出来。

拿到 `captured` 后，测试可继续断言关键字段是否符合预期（邮箱、姓名等）。

### 7.4.4 为什么不用 `any(User.class)` 直接断言
`any(User.class)` 只能说明“传了一个 `User`”，不能说明“这个 `User` 的字段值正确”。  
`ArgumentCaptor` 则可以验证对象内部数据，适合关键业务参数校验。

### 7.4.5 使用建议
- 优先在关键交互上使用 `ArgumentCaptor`，避免无意义捕获。  
- `thenAnswer` 只在确实需要基于入参动态返回时使用。  
- 如果固定返回即可表达业务，优先 `thenReturn`，保持测试简洁。

## 7.5 `verify` 常见写法对照表
在 Mockito 中，`verify` 用于校验 mock 交互是否符合预期。以下是最常用的三种次数校验方式。

| 写法 | 含义 | 典型场景 |
| --- | --- | --- |
| `verify(mock, times(1)).method(...)` | 方法恰好调用 1 次 | 关键流程中必须且仅执行一次 |
| `verify(mock, never()).method(...)` | 方法从未被调用 | 异常分支下不应触发某动作 |
| `verify(mock, atLeastOnce()).method(...)` | 方法至少调用 1 次 | 循环/重试场景下只关心“是否触发过” |

### 7.5.1 示例代码
```java
verify(userRepository, times(1)).save(any(User.class));
verify(emailSender, never()).sendWelcomeEmail(any(String.class));
verify(auditLogger, atLeastOnce()).record(anyString());
```

### 7.5.2 选择建议
- 对核心副作用优先使用 `times(1)`，约束更明确。  
- 只有在明确“不应发生”时使用 `never()`。  
- `atLeastOnce()` 适合调用次数不固定但必须发生的场景。

## 7.6 调用顺序校验：`InOrder` 与 `verifyNoMoreInteractions`
当业务流程对执行顺序有严格要求时，仅校验“是否调用”并不充分，还需要校验“调用顺序是否正确”。

### 7.6.1 典型场景
- 必须先完成数据落库，再发送通知。  
- 必须先完成权限校验，再执行写操作。  
- 需要避免流程中出现额外副作用调用。

### 7.6.2 示例代码
以下代码来自 `UserServiceInteractionOrderTest`：

```java
InOrder inOrder = inOrder(userRepository, emailSender);
inOrder.verify(userRepository).findByEmail("order@company.com");
inOrder.verify(userRepository).save(any(User.class));
inOrder.verify(emailSender).sendWelcomeEmail("order@company.com");
verifyNoMoreInteractions(userRepository, emailSender);
```

### 7.6.3 逐句说明
- `inOrder(userRepository, emailSender)`：创建跨多个 mock 的顺序验证器。  
- `inOrder.verify(...).findByEmail(...)`：第一步必须是查重。  
- `inOrder.verify(...).save(...)`：第二步必须是保存。  
- `inOrder.verify(...).sendWelcomeEmail(...)`：第三步才允许发邮件。  
- `verifyNoMoreInteractions(...)`：确保没有未预期的额外交互。

### 7.6.4 使用建议
- 对有明确业务时序约束的流程使用 `InOrder`。  
- `verifyNoMoreInteractions` 适合关键路径，避免无关调用被忽略。  
- 若业务不关心顺序，不建议强行加顺序校验，以免测试过度收紧。

## 7.7 `UserServiceMockitoTest` 结构化解读
`UserServiceMockitoTest` 展示了 Mockito 在服务层单元测试中的基础套路：  
“准备依赖桩 -> 调用被测方法 -> 断言结果与副作用”。

### 7.7.1 初始化相关注解
- `@ExtendWith(MockitoExtension.class)`：启用 Mockito 与 JUnit 5 的集成。  
- `@Mock`：创建依赖的 mock 实例（如仓储、邮件发送器）。  
- `@InjectMocks`：将 mock 注入 `UserService`，构造被测对象。

### 7.7.2 成功路径测试要点
对应方法：`shouldRegisterSuccessfully()`

关键步骤：  
- `when(findByEmail).thenReturn(null)`：声明“邮箱不存在”。  
- `when(save).thenReturn(带ID用户)`：模拟保存成功返回。  
- 调用 `register(...)` 后断言：  
  - 返回对象字段正确。  
  - `save(...)` 被调用。  
  - `sendWelcomeEmail(...)` 被调用。

该方法体现了两类验证：  
- 结果验证（返回值）。  
- 行为验证（依赖交互）。

### 7.7.3 失败路径测试要点
对应方法：`shouldFailWhenUserExists()`

关键步骤：  
- `when(findByEmail).thenReturn(existingUser)`：声明“邮箱已存在”。  
- `assertThrows(...)`：验证进入异常分支。  
- `verify(..., never())`：验证失败分支没有产生副作用。

这里 `never()` 的核心价值是：  
不仅验证“抛异常”，还验证“没有继续执行不该执行的动作”。

### 7.7.4 实践建议
- 对核心业务方法应至少覆盖一条成功路径和一条失败路径。  
- 失败路径建议搭配 `never()`，防止异常后仍发生副作用。  
- 优先断言业务意义明确的交互，避免对实现细节过度约束。

## 本章小结
Mock 的价值不是“把所有依赖替换掉”，而是精准隔离不可控依赖，突出业务逻辑验证。

## 思考与练习
- 为 `UserService` 新增一个失败场景并补充交互断言。
- 思考哪些 `verify` 可以删除而不损失业务价值。
