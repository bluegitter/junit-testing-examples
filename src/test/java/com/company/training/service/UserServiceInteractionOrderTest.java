package com.company.training.service;

import com.company.training.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Mockito 调用顺序验证示例")
class UserServiceInteractionOrderTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailSender emailSender;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCallSaveBeforeSendEmail() {
        // 模拟“邮箱不存在”，让注册流程进入保存+发邮件路径
        when(userRepository.findByEmail("order@company.com")).thenReturn(null);
        // 模拟保存成功后返回带主键的用户对象
        when(userRepository.save(any(User.class))).thenReturn(new User(300L, "order@company.com", "Order"));

        userService.register("order@company.com", "Order");

        // 按业务期望校验调用顺序：先查重 -> 再保存 -> 最后发欢迎邮件
        InOrder inOrder = inOrder(userRepository, emailSender);
        inOrder.verify(userRepository).findByEmail("order@company.com");
        inOrder.verify(userRepository).save(any(User.class));
        inOrder.verify(emailSender).sendWelcomeEmail("order@company.com");
        // 限定没有其他额外交互，避免隐藏副作用
        verifyNoMoreInteractions(userRepository, emailSender);
    }
}
