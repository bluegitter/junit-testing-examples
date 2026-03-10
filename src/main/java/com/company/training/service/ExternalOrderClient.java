package com.company.training.service;

import java.io.IOException;

public interface ExternalOrderClient {

    OrderStatusResponse queryStatus(String orderNo) throws IOException;
}
