package com.atguigu.guli.service.sms.controller.api;

import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.common.base.util.RandomUtils;
import com.atguigu.guli.service.sms.service.SmsService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/sms")
@Api(description = "短信管理")
@CrossOrigin //跨域
@Slf4j
public class ApiSmsController {
    @Autowired
    private SmsService smsService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @GetMapping("send/{phone}")
    public R getCode(@PathVariable String phone){
        String code = RandomUtils.getFourBitRandom();
        Map<String, Object> param = new HashMap<>();
        param.put("code",code);
//        smsService.send(phone,param);

        redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);

        return R.ok().message("短信发送成功");

    }
}
