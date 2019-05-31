package com.ximuyi.demo.redission;

import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RScheduledExecutorService;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class RedissonMain {
    public static void main(String[] args) throws InterruptedException {
        Config config = new Config();
        config.setTransportMode(TransportMode.NIO);
        config.setThreads(5);
        config.useSingleServer().setAddress("redis://127.0.0.1:7008");
//        config.useClusterServers().setScanInterval(2000)
//                .addNodeAddress("redis://127.0.0.1:7001")
//                .addNodeAddress("redis://127.0.0.1:7002")
//                .addNodeAddress("redis://127.0.0.1:7003")
//                .addNodeAddress("redis://127.0.0.1:7004")
//                .addNodeAddress("redis://127.0.0.1:7005")
//                .addNodeAddress("redis://127.0.0.1:7006")
//                .addNodeAddress("redis://127.0.0.1:7007");

        RedissonClient redisson = Redisson.create(config);
        int count = 10000;
        while (count-- > 0){
            String key = "name" + count;
            RedisValue value = new RedisValue("Jim-"+count);
            RBucket<Object> bucket = redisson.getBucket(key);
            bucket.set(value);
            bucket.setAsync(value).onComplete( ( v, t)->{

            });
            RScheduledExecutorService service = redisson.getExecutorService(key);
            TimeUnit.SECONDS.sleep(1);
        }
    }

    private static class RedisValue implements Serializable {
        private String name;

        public RedisValue(String name) {
            this.name = name;
        }
    }
}
