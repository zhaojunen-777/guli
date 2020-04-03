package com.atguigu.guli.service.order.service;

import com.atguigu.guli.service.order.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 订单 服务类
 * </p>
 *
 * @author en
 * @since 2020-03-29
 */
public interface OrderService extends IService<Order> {

    String saveOrder(String courseId, String memberId);

    Boolean isBuyByCourseId(String memberId, String courseId);

    Order getByOrderId(String orderId, String memberId);

    Order getOrderByOrderNo(String orderNo);

    void updateOrderStatus(Map<String, String> map);
}
