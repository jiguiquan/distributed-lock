package com.jiguiquan.lock.redislock;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RedisLockApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisLockApplication.class, args);
    }

    /**
     * 注意，此处的Redisson并不是为了替代RedisTemplate，是独立的，不冲突，我们只用Redisson处理分布式锁问题
     * 其它的，redis使用，我们仍是使用RedisTemplate,底层仍是使用Jedis；
     * 因为Redisson有自己的缺点，就是在处理Redis的api上面不够丰富（术业有专攻）
     * @return
     */
    @Bean
    public Redisson redisson(){
        //此为单机模式
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.174.141:6379").setDatabase(0);
        return (Redisson)Redisson.create(config);
    }
}
