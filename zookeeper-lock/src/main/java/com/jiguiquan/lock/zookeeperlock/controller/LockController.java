package com.jiguiquan.lock.zookeeperlock.controller;

import com.jiguiquan.lock.zookeeperlock.lock.ZkDistributeLock;
import com.jiguiquan.lock.zookeeperlock.lock.ZkLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class LockController {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${server.port}")
    private String port;

    private static ExecutorService executorService = Executors.newFixedThreadPool(5);

    @GetMapping("/lock/deduct/old")
    public String deductStock(){

        executorService.execute(() -> {
            ZkLock zkLock = new ZkDistributeLock();

            zkLock.zkLock();
            //获取到锁之后，才进行后面的操作
            try{
                int stock = Integer.parseInt(redisTemplate.opsForValue().get("stock"));
                if (stock > 0){
                    int realStock = stock - 1;
                    redisTemplate.opsForValue().set("stock", String.valueOf(realStock));
                    System.out.println(port + "扣减成功，剩余库存：" + realStock);
                }else {
                    System.out.println(port + "扣减失败， 库存不足");
                }
            }finally {
                zkLock.zkUnLock();
            }
        });

        return port + "--end";
    }
}
