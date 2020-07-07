package com.jiguiquan.lock.redislock.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

    @GetMapping("/lock/deduct/old")
    public String deductStock(){
        String lockKey = "product_001";   //一般来说，我们得锁肯定是为了某一个具体商品设置的，防止超卖
        String clientId = UUID.randomUUID().toString();   //使用UUID生成当前线程调用此方法时的唯一标识

        try{
            while (!(redisTemplate.opsForValue().setIfAbsent(lockKey, clientId, 30, TimeUnit.SECONDS))){
                //做了个while自旋，但是要根据业务，也可以获取不到锁时，执行其他操作
//                try {
//                    TimeUnit.SECONDS.sleep(1);   //为了防止while自旋太过频繁，我设置了1s自旋一次，但是后来发现效率太满了
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
            //获取到锁之后，才进行后面的操作
            int stock = Integer.parseInt(redisTemplate.opsForValue().get("stock"));
            if (stock > 0){
                int realStock = stock - 1;
                redisTemplate.opsForValue().set("stock", String.valueOf(realStock));
                System.out.println(port + "扣减成功，剩余库存：" + realStock);
            }else {
                System.out.println(port + "扣减失败， 库存不足");
            }
        }finally {
            //使用try_finally是为了防止异常，最后没有删除锁
            if (clientId.equals(redisTemplate.opsForValue().get(lockKey))){
                redisTemplate.delete(lockKey);
            }
        }

        return port + "--end";
    }
}
