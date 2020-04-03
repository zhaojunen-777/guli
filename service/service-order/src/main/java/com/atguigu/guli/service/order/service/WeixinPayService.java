package com.atguigu.guli.service.order.service;

import java.util.Map;

public interface WeixinPayService {

    Map<String, Object> createNative(String orderNo, String remoteAddr);

    Map<String, String> queryPayStatus(String orderNo);
}
