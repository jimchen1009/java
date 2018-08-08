package com.ximuyi.demo.redis;

import redis.clients.jedis.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by chenjingjun on 2018-01-31.
 */
public class Redis {

    /**
     *
     * @param args
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        Jedis jedis0 = new Jedis("10.18.19.84", 6372);
        Jedis jedis1 = new Jedis("10.18.19.84", 6372);
//        subscibe(jedis0, jedis1);
        cluster();
    }

    public static void subscibe(Jedis jedis0, Jedis jedis1) throws IOException {
        JedisPubSub sub = new JedisPubSub(){
            @Override
            public void onMessage(String channel, String message) {
                System.out.println("channel:" + channel + "receives message :" + message);
            }

            @Override
            public void onSubscribe(String channel, int subscribedChannels) {
                System.out.println("channel:" + channel + "is been subscribed:" + subscribedChannels);
            }

            @Override
            public void onUnsubscribe(String channel, int subscribedChannels) {
                System.out.println("channel:" + channel + "is been unsubscribed:" + subscribedChannels);
            }
        };
        Runnable runnable = ()->{
            int i = 0;
            while (true){
                try {
                    jedis1.publish("hello", "--" + i++);
                    TimeUnit.SECONDS.sleep(30);
                } catch (InterruptedException e) {
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        jedis0.subscribe(sub, "hello");
        System.in.read();
    }

    public static void cluster() throws InterruptedException {
        Set<HostAndPort> address = new HashSet<>();
        for (int port = 7000; port <= 7000; port++){
            address.add(new HostAndPort("127.0.0.1", port));
        }
        JedisCluster cluster = new JedisCluster(address);
        for (int i = 10000; i < 1000000; i++){
            String key = String.valueOf(i);
            String value = "key" + String.valueOf(i);
            cluster.set(key, value);
        }
    }

    public static void mutol(Jedis jedis){
        Transaction tx = jedis.multi();
        for (int i = 0; i < 10000; i++){
            tx.incrBy("pipeline", 1);
        }
        List<Object> results = tx.exec();
    }

    /**
     *
     */
    public static void pipeline(Jedis jedis){
        Pipeline pipeline = jedis.pipelined();
        for (int i = 0; i < 100l; i++){
            pipeline.incrBy("pipeline", 1);
        }
        List<Object> results =  pipeline.syncAndReturnAll();
        results.clear();
    }
}
