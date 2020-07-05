package com.jiguiquan.lock.redislock.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jiguiquan
 * @create 2020-07-05 17:06
 */
@RestController
public class LockController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${server.port}")
    private String port;

    @GetMapping("/lock/deduct")
    public String deductStock(){
        synchronized (this){
            int stock = Integer.parseInt(redisTemplate.opsForValue().get("stock"));
            if (stock > 0){
                int realStock = stock - 1;
                redisTemplate.opsForValue().set("stock", String.valueOf(realStock));
                System.out.println(port + "扣减成功，剩余库存：" + realStock);
            }else {
                System.out.println(port + "扣减失败， 库存不足");
            }
        }

        return port + "--end";
    }
}
