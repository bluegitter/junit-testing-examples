package com.company.training.service;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OrderStatusService {

    private static final Set<String> KNOWN_STATUS = new HashSet<String>(
            Arrays.asList("CREATED", "PAID", "DELIVERED", "CANCELLED")
    );

    private final ExternalOrderClient externalOrderClient;

    public OrderStatusService(ExternalOrderClient externalOrderClient) {
        this.externalOrderClient = externalOrderClient;
    }

    public String queryStatusLabel(String orderNo) {
        if (orderNo == null || orderNo.trim().isEmpty()) {
            throw new IllegalArgumentException("Order number must not be blank");
        }

        SocketTimeoutException lastTimeout = null;
        for (int attempt = 1; attempt <= 2; attempt++) {
            try {
                OrderStatusResponse response = externalOrderClient.queryStatus(orderNo);
                if (response == null || response.getStatus() == null) {
                    return "EMPTY_RESPONSE";
                }
                if (response.getErrorCode() != null) {
                    return "REMOTE_BUSINESS_ERROR";
                }
                if (!KNOWN_STATUS.contains(response.getStatus())) {
                    return "UNKNOWN_STATUS";
                }
                return response.getStatus();
            } catch (SocketTimeoutException ex) {
                // A timeout can be transient, so retry once before returning fallback.
                lastTimeout = ex;
            } catch (ConnectException ex) {
                return "QUERY_CONNECTION_ERROR";
            } catch (RemoteServiceUnavailableException ex) {
                throw new IllegalStateException("Order service is unavailable", ex);
            } catch (IOException ex) {
                throw new IllegalStateException("Order query failed due to I/O error", ex);
            } catch (RuntimeException ex) {
                throw new IllegalStateException("Unexpected error while querying order status", ex);
            }
        }

        if (lastTimeout != null) {
            // Timeout is treated as a recoverable case and mapped to a fallback status.
            return "QUERY_TIMEOUT";
        }

        throw new IllegalStateException("Order query failed without a deterministic result");
    }

    public Map<String, String> queryBatchStatusLabels(List<String> orderNos) {
        Map<String, String> result = new LinkedHashMap<String, String>();
        if (orderNos == null || orderNos.isEmpty()) {
            return result;
        }

        for (String orderNo : orderNos) {
            try {
                // Reuse the single-query path so batch mode and single mode follow the same rules.
                result.put(orderNo, queryStatusLabel(orderNo));
            } catch (IllegalArgumentException ex) {
                // Invalid input should be isolated to the current item instead of failing the whole batch.
                result.put(orderNo, "INVALID_ORDER_NO");
            } catch (IllegalStateException ex) {
                // Batch aggregation should keep partial results, so remote failures are downgraded per item.
                result.put(orderNo, "BATCH_QUERY_FAILED");
            }
        }

        return result;
    }
}
