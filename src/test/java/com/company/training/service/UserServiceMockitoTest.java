package com.company.training.service;

import com.company.training.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService + Mockito 示例")
class UserServiceMockitoTest {

    // 模拟用户仓储依赖，避免连接真实数据库
    @Mock
    private UserRepository userRepository;

    // 模拟邮件发送依赖，避免外部副作用
    @Mock
    private EmailSender emailSender;

    // 将上面的 mock 自动注入被测对象
    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("注册成功：保存用户并发送欢迎邮件")
    void shouldRegisterSuccessfully() {
        // 预置：邮箱未被注册
        when(userRepository.findByEmail("demo@company.com")).thenReturn(null);
        // 预置：保存成功后返回带主键的用户
        when(userRepository.save(any(User.class))).thenReturn(new User(100L, "demo@company.com", "Demo"));

        User result = userService.register("demo@company.com", "Demo");

        // 断言：返回结果正确
        assertEquals(Long.valueOf(100L), result.getId());
        assertEquals("demo@company.com", result.getEmail());
        // 断言：关键副作用发生（保存 + 发欢迎邮件）
        verify(userRepository).save(any(User.class));
        verify(emailSender).sendWelcomeEmail("demo@company.com");
    }

    @Test
    @DisplayName("注册失败：邮箱已存在")
    void shouldFailWhenUserExists() {
        // 预置：邮箱已存在，触发异常分支
        when(userRepository.findByEmail("exists@company.com"))
                .thenReturn(new User(1L, "exists@company.com", "Old"));

        assertThrows(IllegalStateException.class,
                () -> userService.register("exists@company.com", "New"));

        // 断言：失败分支不应产生保存和发邮件副作用
        verify(userRepository, never()).save(any(User.class));
        verify(emailSender, never()).sendWelcomeEmail(any(String.class));
    }
}
