package com.company.training.service;

public class OrderStatusResponse {

    private final String orderNo;
    private final String status;
    private final String errorCode;

    public OrderStatusResponse(String orderNo, String status) {
        this(orderNo, status, null);
    }

    public OrderStatusResponse(String orderNo, String status, String errorCode) {
        this.orderNo = orderNo;
        this.status = status;
        this.errorCode = errorCode;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
