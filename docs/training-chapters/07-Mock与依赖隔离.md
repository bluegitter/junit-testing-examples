# 第07章 Mock 与依赖隔离

## 本章导读
当一个业务类依赖数据库、消息、邮件、缓存或第三方服务时，单元测试很容易失去“快速、稳定、可控”的特征。此时，测试的重点不再只是验证返回值，还要回答另一个关键问题：被测对象与外部依赖之间的协作是否符合预期。Mock 的意义，就在于隔离这些不可控依赖，让测试聚焦于当前对象的职责。本章围绕 Mockito 的基本能力展开，逐步说明如何在服务层测试中控制依赖、验证交互并保持测试边界清晰。

## 学习目标
- 理解 Mock 在单元测试中的定位与边界。
- 掌握 `@Mock`、`@InjectMocks`、`when`、`verify` 等常用能力。
- 理解静态桩、动态桩、参数捕获和顺序验证的适用场景。
- 能用 Mockito 为服务层编写结果验证与行为验证并重的测试。

## 7.1 为什么需要 Mock
单元测试的一个核心原则是：  
测试应尽量只验证当前对象的行为，而不是把整个系统一起拉进来执行。

如果一个类依赖以下资源：
- 数据库
- 邮件网关
- HTTP 接口
- 消息队列
- 系统时间或随机数

那么直接运行真实依赖通常会带来几个问题：
- 测试速度慢
- 环境准备复杂
- 结果不稳定
- 失败原因难定位

Mock 的作用并不是“伪造一切”，而是把不可控依赖替换成可控对象，从而让测试只关注当前类的业务逻辑。

## 7.2 什么该 Mock，什么不该 Mock
判断是否应使用 Mock，可以遵循一个简单原则：

- 当前对象的外部依赖，应优先考虑 Mock。
- 当前对象本身的核心业务逻辑，不应被 Mock。
- 值对象、简单数据对象通常不需要 Mock。

例如在本项目中：
- `UserService` 依赖 `UserRepository` 和 `EmailSender`
  - 这两个依赖适合 Mock。
- `UserService` 本身的注册逻辑
  - 这是被测重点，不应 Mock。
- `User` 只是一个普通数据对象
  - 通常直接 new 即可，不需要 Mock。

如果一个测试把所有对象都 Mock 掉，最后得到的往往不是单元测试，而是“用 Mock 拼出来的脚本化流程”，失去真实业务验证意义。

## 7.3 Mockito 在服务层测试中的基本结构
一个典型的 Mockito 服务层测试通常包含四步：
1. 创建或注入 mock 依赖。
2. 为依赖预置返回行为。
3. 调用被测方法。
4. 验证结果与关键交互。

从结构上看，它仍然符合 AAA：
- Arrange：准备 mock 和桩行为。
- Act：执行被测方法。
- Assert：验证返回值、异常、交互、参数或顺序。

## 7.4 核心注解说明
### 7.4.1 `@ExtendWith(MockitoExtension.class)`
用于启用 Mockito 与 JUnit 5 的集成，使 `@Mock`、`@InjectMocks` 等注解能够自动生效。

### 7.4.2 `@Mock`
用于声明一个模拟依赖对象。  
该对象不会执行真实逻辑，默认返回空值、默认值或空集合，除非显式设置桩行为。

### 7.4.3 `@InjectMocks`
用于创建被测对象，并将当前测试类中声明的 mock 自动注入进去。  
这要求被测类本身具备较好的依赖注入设计，例如构造器注入。

本项目中的 `UserService` 就是可测试设计的典型例子：
- 依赖通过构造器传入
- 不在方法中直接 `new` 外部对象
- 测试中可直接使用 Mockito 替换依赖

## 7.5 桩行为：`when(...).thenReturn(...)`
桩（stub）指的是：为 mock 方法指定固定行为。

典型写法如下：

```java
when(userRepository.findByEmail("demo@company.com")).thenReturn(null);
```

这行代码的含义是：  
当被测代码调用 `findByEmail("demo@company.com")` 时，不执行真实仓储逻辑，而直接返回 `null`。

这类写法适合：
- 返回结果固定的场景
- 为成功路径或失败路径建立前置条件
- 快速构造业务分支入口

## 7.6 动态桩行为：`when(...).thenAnswer(...)`
有些场景下，返回值并不是固定常量，而是和入参有关。  
这时适合使用 `thenAnswer(...)`。

示例（来自 `UserServiceArgumentCaptorTest`）：

```java
when(userRepository.save(any(User.class)))
        .thenAnswer(invocation -> {
            User input = invocation.getArgument(0);
            return new User(200L, input.getEmail(), input.getName());
        });
```

这段逻辑模拟了一个常见数据库场景：
- 保存前对象没有主键
- 保存后返回一个已分配主键的对象
- 其余字段保持与入参一致

与 `thenReturn` 相比，`thenAnswer` 更适合：
- 需要基于入参构造返回值
- 需要模拟更贴近真实依赖的行为
- 需要验证“数据流”而不仅是固定结果

## 7.7 交互验证：`verify(...)`
返回值正确，并不意味着业务过程正确。  
很多服务层方法除了返回结果，还会触发关键副作用，例如：
- 保存数据
- 发送通知
- 记录日志
- 更新状态

`verify(...)` 的作用，就是验证这些关键交互是否发生。

示例：

```java
verify(userRepository).save(any(User.class));
verify(emailSender).sendWelcomeEmail("demo@company.com");
```

这说明测试不仅关心 `register(...)` 的返回值，也关心它是否真的做了“保存用户”和“发送邮件”这两件事。

## 7.8 `verify` 常见写法
在 Mockito 中，次数校验非常常见。最常用写法如下：

| 写法 | 含义 | 典型场景 |
| --- | --- | --- |
| `verify(mock, times(1)).method(...)` | 方法恰好调用 1 次 | 关键流程必须且仅执行一次 |
| `verify(mock, never()).method(...)` | 方法从未被调用 | 异常分支不应触发副作用 |
| `verify(mock, atLeastOnce()).method(...)` | 方法至少调用 1 次 | 调用次数不固定但必须发生 |

其中，`never()` 在失败路径测试中特别有价值，因为它能确认“异常发生后，没有继续执行后续动作”。

## 7.9 参数捕获：`ArgumentCaptor`
有时只验证“方法被调用了”还不够，还需要验证“调用时传入的参数是否正确”。  
此时可以使用 `ArgumentCaptor`。

示例：

```java
ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
verify(userRepository).save(userCaptor.capture());
User captured = userCaptor.getValue();
```

这段逻辑包含两个动作：
- `verify(...)`：确认 `save(...)` 被调用。
- `capture()`：抓取本次调用传入的参数。

之后即可对 `captured` 进一步断言：

```java
assertEquals("new@company.com", captured.getEmail());
assertEquals("New Guy", captured.getName());
```

这类能力适合用于：
- 校验对象字段是否正确传递
- 验证组装逻辑是否符合预期
- 确认服务层是否把业务参数正确下发到依赖层

## 7.10 调用顺序验证：`InOrder`
有些业务流程不仅关心“是否调用”，还关心“调用顺序是否正确”。  
例如：
- 必须先查重，再保存
- 必须先落库，再发邮件
- 必须先校验权限，再执行写操作

此时可使用 `InOrder`：

```java
InOrder inOrder = inOrder(userRepository, emailSender);
inOrder.verify(userRepository).findByEmail("order@company.com");
inOrder.verify(userRepository).save(any(User.class));
inOrder.verify(emailSender).sendWelcomeEmail("order@company.com");
```

这表示：  
查重必须先发生，保存其次，发送邮件最后。

如果业务时序很重要，`InOrder` 能有效防止“流程结果看起来对，但过程顺序有问题”的情况。

## 7.11 限定额外交互：`verifyNoMoreInteractions`
当希望进一步收紧测试边界，防止出现多余副作用时，可以使用：

```java
verifyNoMoreInteractions(userRepository, emailSender);
```

其作用是：  
在已经验证过若干交互后，断言这些 mock 不应再发生其他未预期调用。

它适合用于：
- 关键流程
- 副作用敏感流程
- 需要严格控制调用范围的服务层逻辑

但它也应谨慎使用，因为过度使用会让测试对实现细节过于敏感。

## 7.12 外部接口调用 Mock 示例
除了数据库、消息和邮件依赖，另一个非常常见的场景是：  
服务层需要调用第三方 HTTP 接口或远程服务。

这类依赖尤其适合 Mock，原因包括：
- 真实网络调用会让测试变慢。
- 第三方接口可能不稳定或不可用。
- 培训环境和 CI 环境不适合依赖外部系统。

本项目新增了一个专门示例：
- 接口定义：`src/main/java/com/company/training/service/ExternalOrderClient.java`
- 返回对象：`src/main/java/com/company/training/service/OrderStatusResponse.java`
- 异常类型：`src/main/java/com/company/training/service/RemoteServiceUnavailableException.java`
- 服务实现：`src/main/java/com/company/training/service/OrderStatusService.java`
- 测试类：`src/test/java/com/company/training/service/OrderStatusServiceExternalCallTest.java`

### 7.12.1 正常返回场景
```java
when(externalOrderClient.queryStatus("ORD-1001"))
        .thenReturn(new OrderStatusResponse("ORD-1001", "DELIVERED"));
```

该写法模拟第三方接口正常响应。  
测试重点是：
- 服务层能否拿到远程返回结果
- 是否能将外部数据正确转换为业务结果

### 7.12.2 接口超时场景
```java
when(externalOrderClient.queryStatus("ORD-1002"))
        .thenThrow(new SocketTimeoutException("Read timed out"));
```

该写法模拟网络超时。  
在示例中，`OrderStatusService` 选择将超时映射为降级状态 `QUERY_TIMEOUT`，这体现了一种常见的容错处理策略：
- 对可恢复问题不直接抛出异常
- 给上层返回一个可识别的降级结果

如果要进一步演示“瞬时失败重试”，Mockito 还支持连续桩行为：

```java
when(externalOrderClient.queryStatus("ORD-1006"))
        .thenThrow(new SocketTimeoutException("Read timed out"))
        .thenReturn(new OrderStatusResponse("ORD-1006", "PAID"));
```

这段代码表示：
- 第一次调用超时
- 第二次调用恢复正常

它非常适合说明“为什么超时往往要区别于永久失败”。  
在本项目中，`OrderStatusService` 对超时进行了单次重试，因此该测试既能讲 Mockito 的连续桩行为，也能讲服务层的重试策略。

### 7.12.3 接口不可用场景
```java
when(externalOrderClient.queryStatus("ORD-1003"))
        .thenThrow(new RemoteServiceUnavailableException("503 Service Unavailable"));
```

该写法模拟第三方服务不可用。  
在示例中，服务层选择抛出业务异常：

```java
throw new IllegalStateException("Order service is unavailable", ex);
```

这类处理方式适用于：
- 业务流程无法在当前条件下继续
- 需要把远程系统异常转换为本系统可识别异常

### 7.12.4 异常分类处理场景
外部接口失败并不是单一概念。  
在工程实践中，至少要区分以下几类情况：

- 超时：通常表示请求已发出，但在规定时间内没有等到响应
- 连接失败：通常表示网络未连通，或者远端端口不可访问
- 服务不可用：通常表示远端服务本身异常，例如网关返回 503
- 业务错误响应：接口调用成功，但返回的业务结果表示失败
- 非预期运行时异常：例如解析错误、空指针、第三方 SDK 缺陷

这些场景不应统一处理，因为它们的恢复策略不同。

连接异常示例：

```java
when(externalOrderClient.queryStatus("ORD-1007"))
        .thenThrow(new ConnectException("Connection refused"));
```

在示例中，这类异常被映射为 `QUERY_CONNECTION_ERROR`，强调它和超时并不是同一种问题。

业务错误响应示例：

```java
when(externalOrderClient.queryStatus("ORD-1008"))
        .thenReturn(new OrderStatusResponse("ORD-1008", "FAILED", "ORDER_NOT_FOUND"));
```

这里没有抛异常，说明传输层是成功的；但响应体中的 `errorCode` 表示业务失败。  
在示例中，这种情况被统一映射为 `REMOTE_BUSINESS_ERROR`。

非预期运行时异常示例：

```java
RuntimeException remoteBug = new RuntimeException("JSON parse failed");
when(externalOrderClient.queryStatus("ORD-1010")).thenThrow(remoteBug);
```

这一类异常通常不应直接透传给上层，而应包装为统一的业务异常，并保留原始 `cause` 便于排查。

### 7.12.5 请求参数非法场景
外部接口测试不应只覆盖远端行为，还应覆盖本地的前置校验逻辑。  
在 `OrderStatusService` 中，如果订单号为空或只包含空白字符，将直接抛出异常，而不会发起远程调用：

```java
assertThrows(IllegalArgumentException.class,
        () -> orderStatusService.queryStatusLabel(" "));
verify(externalOrderClient, never()).queryStatus(" ");
```

这类测试说明两件事：
- 输入校验应尽量在服务入口完成，尽早失败。
- 对非法输入，不应把错误流转给远端系统。

### 7.12.6 接口返回空响应场景
真实系统中，第三方接口并不一定总能返回结构完整的对象。  
如果远端返回 `null`，测试应验证服务层是否具备明确的兜底处理：

```java
when(externalOrderClient.queryStatus("ORD-1004")).thenReturn(null);
```

在本示例中，服务层将其统一映射为 `EMPTY_RESPONSE`。  
这种处理方式的价值在于：
- 避免 `NullPointerException`
- 让调用方拿到稳定、可识别的结果

### 7.12.7 接口返回未知状态场景
第三方接口的返回值并不总是严格受控。  
当远端返回了系统尚未识别的新状态值时，服务层需要决定是抛错、忽略，还是统一兜底。

```java
when(externalOrderClient.queryStatus("ORD-1005"))
        .thenReturn(new OrderStatusResponse("ORD-1005", "WAITING_MANUAL_CHECK"));
```

在本示例中，未知状态被映射为 `UNKNOWN_STATUS`。  
这一策略适用于：
- 外部枚举值可能扩展，但本系统尚未完成兼容
- 希望保留业务可用性，而不是因为新状态导致流程直接失败

### 7.12.8 批量查询与聚合结果场景
真实业务里，外部接口调用并不总是单笔发生。  
例如订单中心、对账任务、运营后台常常需要对一批订单逐个查询状态，然后汇总输出。

这一类测试的难点不在于单次调用，而在于：
- 循环中会发生多次远端调用
- 同一批数据里可能同时出现成功、超时、非法输入、远端异常
- 业务通常希望“尽量返回更多结果”，而不是一条失败就整批终止

本项目中，`OrderStatusService` 增加了批量方法 `queryBatchStatusLabels(List<String> orderNos)`，用于演示这种聚合处理。

示例测试会构造一组混合输入：
- `ORD-2001` 正常返回
- 空白订单号触发本地校验失败
- `ORD-2002` 连续两次超时
- `ORD-2003` 远端服务不可用
- `ORD-2004` 返回业务错误码

对应测试重点是：
- 结果集中每个订单号是否都拿到了明确结果
- 单项失败是否被隔离，没有中断后续处理
- 超时项是否确实发生了两次调用
- 非法输入是否被本地拦截，没有打到远端接口

这一场景非常适合在培训中讲“边界和异常混合测试”。  
因为它要求测试代码同时验证：
- 正常路径
- 局部失败
- 重试行为
- 聚合结果结构

### 7.12.9 这个示例在培训中的价值
这个外部接口示例补充了 Mockito 的另一类高频用法：
- 不只是 mock 仓储或本地依赖
- 还可以 mock 网络接口和第三方服务

同时它也演示了更多真实项目中的高频情况：
- 正常返回
- 首次超时、重试成功
- 超时降级
- 连接异常兜底
- 服务不可用并抛出业务异常
- 业务错误响应映射
- 非预期运行时异常包装
- 非法输入快速失败
- 空响应兜底处理
- 未知状态统一映射
- 批量查询中的部分成功、部分失败与聚合处理

## 7.13 案例串联：四个测试类分别解决什么问题
本项目用四个测试类覆盖了 Mockito 的不同关注点：

### 7.13.1 `UserServiceMockitoTest`
对应文件：
- `src/test/java/com/company/training/service/UserServiceMockitoTest.java`

关注点：
- 成功路径下，结果是否正确
- 失败路径下，是否抛出异常
- 成功/失败分支下，副作用是否符合预期

这是 Mockito 最基础、最常见的服务层测试模板。

### 7.13.2 `UserServiceArgumentCaptorTest`
对应文件：
- `src/test/java/com/company/training/service/UserServiceArgumentCaptorTest.java`

关注点：
- `save(...)` 的真实入参是什么
- 服务层组装出的对象字段是否正确

这是参数传递正确性的验证。

### 7.13.3 `UserServiceInteractionOrderTest`
对应文件：
- `src/test/java/com/company/training/service/UserServiceInteractionOrderTest.java`

关注点：
- 依赖调用顺序是否符合业务约束
- 是否存在额外交互

这是流程顺序正确性的验证。

### 7.13.4 `OrderStatusServiceExternalCallTest`
对应文件：
- `src/test/java/com/company/training/service/OrderStatusServiceExternalCallTest.java`

关注点：
- 外部接口正常返回时结果是否正确
- 接口超时时是否执行降级处理
- 接口不可用时是否转换为业务异常

四者结合后，服务层测试就能覆盖：
- 结果值
- 分支行为
- 参数正确性
- 时序正确性
- 外部接口异常处理

## 7.14 Mock 使用中的常见误区
### 7.14.1 过度 Mock
把值对象、简单工具类甚至被测类本身也 Mock 掉，会让测试失去真实业务意义。

### 7.14.2 只验证交互，不验证结果
如果只看 `verify(...)`，不看返回值或异常，测试容易变成“流程脚本”，而不是业务验证。

### 7.14.3 只验证结果，不验证关键副作用
对于服务层方法，这也不够。很多关键问题出现在“结果对了，但副作用错了”。

### 7.14.4 对实现细节约束过多
并不是每个内部调用都值得验证。  
应优先验证具有业务意义的交互，而不是把测试写成对实现步骤的逐行审查。

### 7.14.5 只测正常返回，不测外部依赖异常
对于外部接口调用，真正高风险的往往不是“返回成功”，而是：
- 超时
- 接口不可用
- 返回空值或异常结构

如果只覆盖正常场景，测试保护能力是不完整的。

## 7.15 实践建议
- 对服务层测试，至少覆盖一条成功路径和一条失败路径。
- 对有副作用的方法，同时验证结果与关键交互。
- 对关键参数传递使用 `ArgumentCaptor`，而不是只用 `any(...)`。
- 对存在明确业务时序约束的流程使用 `InOrder`。
- 对外部接口调用，至少覆盖正常返回、超时和不可用三类场景。
- Mock 的目标是隔离依赖，不是把整个系统抽空。

## 本章小结
Mock 与依赖隔离解决的是“如何在不依赖真实外部系统的前提下，准确测试当前对象的业务逻辑”。Mockito 提供了从桩行为、交互验证、参数捕获到顺序校验的一整套能力。真正高质量的服务层测试，不仅要验证结果是否正确，还要验证依赖协作是否符合业务预期。

## 思考与练习
- 为 `UserService` 新增一个失败场景，并补充对应的 `never()` 校验。
- 选择一个只验证了返回值的服务层测试，补上关键交互验证。
- 思考：如果某个方法只是简单转调下层接口，是否值得写完整 Mockito 单元测试？
