package com.atguigu.guli.service.order.controller.api;

import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.common.base.util.JwtUtils;
import com.atguigu.guli.service.order.entity.Order;
import com.atguigu.guli.service.order.service.OrderService;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
@RequestMapping("/api/order")
@Api(description = "网站订单管理")
@Slf4j
public class ApiOrderController {

    @Autowired
    private OrderService orderService;

    @ApiOperation(value = "新增订单")
    @PostMapping("auth/save/{courseId}")
    public R save(@PathVariable String courseId, HttpServletRequest request){
        String token = request.getHeader("token");
        Claims claims = JwtUtils.parseJWT(token);
        String memberId = (String) claims.get("id");
        String orderId = orderService.saveOrder(courseId, memberId);
        return R.ok().data("orderId",orderId);
    }

    @ApiOperation(value = "判断课程是否购买")
    @GetMapping("auth/is-buy/{courseId}")
    public R isBuyByCourseId(@PathVariable String courseId, HttpServletRequest request) {

        String jwtToken = request.getHeader("token");
        Claims claims = JwtUtils.parseJWT(jwtToken);
        String memberId = (String)claims.get("id");
        Boolean isBuy = orderService.isBuyByCourseId(memberId, courseId);
        return R.ok().data("isBuy", isBuy);
    }

    @ApiOperation(value = "获取订单")
    @GetMapping("auth/get/{orderId}")
    public R get(@PathVariable String orderId, HttpServletRequest request){
        String token = request.getHeader("token");
        Claims claims = JwtUtils.parseJWT(token);
        String memberId = (String)claims.get("id");
        Order order = orderService.getByOrderId(orderId, memberId);
        return R.ok().data("order",order);
    }
}
