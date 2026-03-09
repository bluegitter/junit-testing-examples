package com.company.training.service;

import com.company.training.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Mockito ArgumentCaptor 示例")
class UserServiceArgumentCaptorTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailSender emailSender;

    @InjectMocks
    private UserService userService;

    /**
     * 测试用例：注册用户时，保存用户信息
     */
    @Test
    void shouldCaptureSavedUserFields() {
        // 模拟“邮箱未注册”，让流程进入创建用户分支
        when(userRepository.findByEmail("new@company.com")).thenReturn(null);
        // 模拟仓储保存后返回带主键的用户，同时保留入参中的业务字段
        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class)))
                .thenAnswer(invocation -> {
                    User input = invocation.getArgument(0);
                    return new User(200L, input.getEmail(), input.getName());
                });

        userService.register("new@company.com", "New Guy");

        // 捕获 save(...) 的真实入参，校验传递给仓储层的数据是否正确
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User captured = userCaptor.getValue();
        assertEquals("new@company.com", captured.getEmail());
        assertEquals("New Guy", captured.getName());
    }
}
