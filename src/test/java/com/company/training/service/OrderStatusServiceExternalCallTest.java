package com.company.training.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("外部接口调用 Mock 示例")
class OrderStatusServiceExternalCallTest {

    // 模拟第三方订单接口，避免测试时真实发起网络请求
    @Mock
    private ExternalOrderClient externalOrderClient;

    // 将外部接口 mock 注入被测服务
    @InjectMocks
    private OrderStatusService orderStatusService;

    @Test
    @DisplayName("外部接口正常返回：直接返回订单状态")
    void shouldReturnStatusWhenRemoteCallSucceeds() throws Exception {
        // 模拟第三方接口正常响应
        when(externalOrderClient.queryStatus("ORD-1001"))
                .thenReturn(new OrderStatusResponse("ORD-1001", "DELIVERED"));

        String result = orderStatusService.queryStatusLabel("ORD-1001");

        assertEquals("DELIVERED", result);
        verify(externalOrderClient).queryStatus("ORD-1001");
    }

    @Test
    @DisplayName("订单号为空：直接抛出参数异常且不调用外部接口")
    void shouldRejectBlankOrderNoBeforeRemoteCall() throws Exception {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> orderStatusService.queryStatusLabel(" ")
        );

        assertEquals("Order number must not be blank", ex.getMessage());
        // 参数不合法时，应在本地快速失败，避免无意义的远端调用
        verify(externalOrderClient, never()).queryStatus(" ");
    }

    @Test
    @DisplayName("外部接口超时：返回降级状态")
    void shouldReturnFallbackStatusWhenRemoteCallTimesOut() throws Exception {
        // 连续两次超时，说明重试后仍失败，最终返回降级状态
        when(externalOrderClient.queryStatus("ORD-1002"))
                .thenThrow(new SocketTimeoutException("Read timed out"));

        String result = orderStatusService.queryStatusLabel("ORD-1002");

        assertEquals("QUERY_TIMEOUT", result);
        verify(externalOrderClient, times(2)).queryStatus("ORD-1002");
    }

    @Test
    @DisplayName("外部接口首次超时第二次成功：重试后返回成功结果")
    void shouldRetryOnceWhenFirstRemoteCallTimesOut() throws Exception {
        // 使用连续桩行为模拟“第一次超时，第二次成功”
        when(externalOrderClient.queryStatus("ORD-1006"))
                .thenThrow(new SocketTimeoutException("Read timed out"))
                .thenReturn(new OrderStatusResponse("ORD-1006", "PAID"));

        String result = orderStatusService.queryStatusLabel("ORD-1006");

        assertEquals("PAID", result);
        // 这里验证了服务层确实发生过一次重试
        verify(externalOrderClient, times(2)).queryStatus("ORD-1006");
    }

    @Test
    @DisplayName("外部接口返回空响应：返回空响应兜底状态")
    void shouldReturnEmptyResponseStatusWhenRemoteCallReturnsNull() throws Exception {
        // 模拟第三方接口返回空响应对象
        when(externalOrderClient.queryStatus("ORD-1004")).thenReturn(null);

        String result = orderStatusService.queryStatusLabel("ORD-1004");

        assertEquals("EMPTY_RESPONSE", result);
        verify(externalOrderClient).queryStatus("ORD-1004");
    }

    @Test
    @DisplayName("外部接口返回未知状态：返回未知状态兜底")
    void shouldReturnUnknownStatusWhenRemoteCallReturnsUnexpectedValue() throws Exception {
        // 模拟第三方接口返回系统未识别的状态值
        when(externalOrderClient.queryStatus("ORD-1005"))
                .thenReturn(new OrderStatusResponse("ORD-1005", "WAITING_MANUAL_CHECK"));

        String result = orderStatusService.queryStatusLabel("ORD-1005");

        assertEquals("UNKNOWN_STATUS", result);
        verify(externalOrderClient).queryStatus("ORD-1005");
    }

    @Test
    @DisplayName("外部接口连接失败：返回连接异常兜底状态")
    void shouldReturnConnectionErrorStatusWhenRemoteConnectionFails() throws Exception {
        // 连接失败属于传输层异常，与超时不同，通常表示根本未连通远端服务
        when(externalOrderClient.queryStatus("ORD-1007"))
                .thenThrow(new ConnectException("Connection refused"));

        String result = orderStatusService.queryStatusLabel("ORD-1007");

        assertEquals("QUERY_CONNECTION_ERROR", result);
        verify(externalOrderClient).queryStatus("ORD-1007");
    }

    @Test
    @DisplayName("外部接口返回业务错误码：返回业务错误状态")
    void shouldReturnBusinessErrorStatusWhenRemoteResponseContainsErrorCode() throws Exception {
        // 远端接口可能 HTTP 正常，但业务层返回错误码，例如订单不存在或状态非法
        when(externalOrderClient.queryStatus("ORD-1008"))
                .thenReturn(new OrderStatusResponse("ORD-1008", "FAILED", "ORDER_NOT_FOUND"));

        String result = orderStatusService.queryStatusLabel("ORD-1008");

        assertEquals("REMOTE_BUSINESS_ERROR", result);
        verify(externalOrderClient).queryStatus("ORD-1008");
    }

    @Test
    @DisplayName("外部接口不可用：抛出业务异常")
    void shouldThrowBusinessExceptionWhenRemoteServiceIsUnavailable() throws Exception {
        // 模拟第三方服务不可用，如网关返回 503 或服务熔断
        when(externalOrderClient.queryStatus("ORD-1003"))
                .thenThrow(new RemoteServiceUnavailableException("503 Service Unavailable"));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> orderStatusService.queryStatusLabel("ORD-1003")
        );

        assertEquals("Order service is unavailable", ex.getMessage());
        verify(externalOrderClient).queryStatus("ORD-1003");
    }

    @Test
    @DisplayName("外部接口发生 I/O 异常：抛出输入输出错误异常")
    void shouldWrapIOExceptionWhenRemoteCallFailsDueToIoError() throws Exception {
        when(externalOrderClient.queryStatus("ORD-1009"))
                .thenThrow(new IOException("Broken pipe"));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> orderStatusService.queryStatusLabel("ORD-1009")
        );

        assertEquals("Order query failed due to I/O error", ex.getMessage());
        assertEquals(IOException.class, ex.getCause().getClass());
        verify(externalOrderClient).queryStatus("ORD-1009");
    }

    @Test
    @DisplayName("外部接口发生非预期运行时异常：保留原始异常并抛出统一异常")
    void shouldWrapUnexpectedRuntimeExceptionWhenRemoteCallFailsUnexpectedly() throws Exception {
        RuntimeException remoteBug = new RuntimeException("JSON parse failed");
        when(externalOrderClient.queryStatus("ORD-1010")).thenThrow(remoteBug);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> orderStatusService.queryStatusLabel("ORD-1010")
        );

        assertEquals("Unexpected error while querying order status", ex.getMessage());
        assertSame(remoteBug, ex.getCause());
        verify(externalOrderClient).queryStatus("ORD-1010");
    }

    @Test
    @DisplayName("批量查询：部分成功部分失败时返回聚合结果且不中断整批处理")
    void shouldAggregateMixedBatchResultsWithoutStoppingOnSingleFailure() throws Exception {
        when(externalOrderClient.queryStatus("ORD-2001"))
                .thenReturn(new OrderStatusResponse("ORD-2001", "DELIVERED"));
        // 这个订单连续两次超时，用于验证循环中的单项失败不会影响后续项
        when(externalOrderClient.queryStatus("ORD-2002"))
                .thenThrow(new SocketTimeoutException("Read timed out"));
        when(externalOrderClient.queryStatus("ORD-2003"))
                .thenThrow(new RemoteServiceUnavailableException("503 Service Unavailable"));
        when(externalOrderClient.queryStatus("ORD-2004"))
                .thenReturn(new OrderStatusResponse("ORD-2004", "FAILED", "ORDER_NOT_FOUND"));

        Map<String, String> result = orderStatusService.queryBatchStatusLabels(
                Arrays.asList("ORD-2001", " ", "ORD-2002", "ORD-2003", "ORD-2004")
        );

        assertEquals("DELIVERED", result.get("ORD-2001"));
        assertEquals("INVALID_ORDER_NO", result.get(" "));
        assertEquals("QUERY_TIMEOUT", result.get("ORD-2002"));
        assertEquals("BATCH_QUERY_FAILED", result.get("ORD-2003"));
        assertEquals("REMOTE_BUSINESS_ERROR", result.get("ORD-2004"));
        assertEquals(5, result.size());

        verify(externalOrderClient).queryStatus("ORD-2001");
        verify(externalOrderClient, times(2)).queryStatus("ORD-2002");
        verify(externalOrderClient).queryStatus("ORD-2003");
        verify(externalOrderClient).queryStatus("ORD-2004");
        verify(externalOrderClient, never()).queryStatus(" ");
    }
}
