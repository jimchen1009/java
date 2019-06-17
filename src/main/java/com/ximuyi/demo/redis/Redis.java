package com.ximuyi.demo.redis;

import org.redisson.misc.URIBuilder;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Transaction;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by chenjingjun on 2018-01-31.
 */
public class Redis {
	private static final URI SINGLE = URIBuilder.create("redis://127.0.0.1:7008");
	private static final Set<HostAndPort> CLUSTER = new HashSet<>();
	static {
		for (int port = 7000; port < 7008; port++){
			CLUSTER.add(new HostAndPort("127.0.0.1", port));
		}
	}

    /**
     * @param args
     */
    public static void main(String[] args) throws InterruptedException, IOException {
	    setValue();
        //cluster();
    }

    private static Jedis newSingleJedis(){
    	return new Jedis(SINGLE);
    }

    private static void setValue() throws InterruptedException {
	    Jedis jedis = newSingleJedis();
	    int count = 0;
	    while(count++ < 1000){
		    String name = "key-"+count;
		    String value = "Redis-"+count;
		    jedis.set(name, value);
		    TimeUnit.SECONDS.sleep(5);
	    }
    }

    public static void subscibe() throws IOException {
	    Jedis jedis0 = newSingleJedis();
	    Jedis jedis1 = newSingleJedis();
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
        JedisCluster cluster = new JedisCluster(CLUSTER);
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
