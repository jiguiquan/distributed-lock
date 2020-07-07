package com.jiguiquan.lock.redislock.controller;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class RedissonLockController {
    @Autowired
    private Redisson redisson;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${server.port}")
    private String port;

    @GetMapping("/lock/deduct")
    public String deductStock(){
        String lockKey = "product_001";   //一般来说，我们得锁肯定是为了某一个具体商品设置的，防止超卖

        RLock redissonLock = redisson.getLock(lockKey);  //这只是拿到锁对象，并没有真正开始加锁
        try{
            boolean isLock = redissonLock.tryLock(30000, 15000, TimeUnit.MILLISECONDS);
            if (isLock){
                //获取到锁之后，才进行后面的操作
                int stock = Integer.parseInt(redisTemplate.opsForValue().get("stock"));
                if (stock > 0){
                    int realStock = stock - 1;
                    redisTemplate.opsForValue().set("stock", String.valueOf(realStock));
                    System.out.println(port + "扣减成功，剩余库存：" + realStock);
                }else {
                    System.out.println(port + "扣减失败， 库存不足");
                }
            }
        }catch (Exception e){

        }finally {
            //使用try_finally是为了防止异常，最后没有释放锁
            redissonLock.unlock();
        }

        return port + "--end";
    }
}
