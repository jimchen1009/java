package com.ximuyi.demo.redis;

import com.ximuyi.common.PoolThreadFactory;
import org.redisson.misc.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.JedisMonitor;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.Transaction;
import redis.clients.util.Pool;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by chenjingjun on 2018-01-31.
 */
public class JedisMain {

	private static final URI SINGLE = URIBuilder.create("redis://password:123456@127.0.0.1:7208");
	private static final List<URI> SHARED = Arrays.asList(
		URIBuilder.create("redis://password:123456@127.0.0.1:7108"),
		URIBuilder.create("redis://password:123456@127.0.0.1:7109"),
		URIBuilder.create("redis://password:123456@127.0.0.1:7110")
	);
	private static final Set<HostAndPort> CLUSTER = new HashSet<>();
	static {
		for (int port = 7000; port < 7008; port++){
			CLUSTER.add(new HostAndPort("127.0.0.1", port));
		}
	}
	private static final Logger logger = LoggerFactory.getLogger(JedisMain.class);

	private static final MyCommonJedisPool commonPool = new MyCommonJedisPool(SINGLE);
	private static final MyShardedJedisPool sharedPool = new MyShardedJedisPool(SHARED.stream().map(JedisShardInfo::new).collect(Collectors.toList()));
	private static final PoolThreadFactory threadFactory = new PoolThreadFactory("redis");


    public static void main(String[] args){
    	//初始化数据
	    threadFactory.newThread(()-> initJedis(commonPool.getResource())).start();
	    threadFactory.newThread(()-> sharedPool.getResource(redis -> redis.getAllShards().forEach(JedisMain::initJedis))).start();
	    //cluster();
	    threadFactory.newThread(()-> channelSubscibe("channel")).start();
	    threadFactory.newThread(()-> channelPublic("channel")).start();
	    threadFactory.newThread(JedisMain::transactions).start();
    }

    private static void initJedis(Jedis jedis){
	    jedis.flushAll();
	    jedis.monitor(new JedisMonitor() {
		    public void onCommand(String command) {
			    logger.info("monitor command:{}", command);
		    }
	    });
    }

	private static void channelPublic(String channel) {
		commonPool.getResource(redis -> {
			while (true){
				try {
					UUID uuid = UUID.randomUUID();
					redis.publish(channel, uuid.toString());
					TimeUnit.SECONDS.sleep(10);
				} catch (InterruptedException ignored) {
				}
			}
		});
	}

	private static void channelSubscibe(String channel) {
	    JedisPubSub sub = new JedisPubSub(){
		    @Override
		    public void onMessage(String channel, String message) {
			    logger.debug("channel:" + channel + "received a message :" + message);
		    }

		    @Override
		    public void onSubscribe(String channel, int subscribedChannels) {
			    logger.debug("channel:" + channel + "is been subscribed:" + subscribedChannels);
		    }

		    @Override
		    public void onUnsubscribe(String channel, int subscribedChannels) {
			    logger.debug("channel:" + channel + "is been unsubscribed:" + subscribedChannels);
		    }
	    };
	    commonPool.getResource(redis -> {
		    /***
		     * 查看源码可以知道，订阅之后会一直while循环，直到别的线程取消订阅为止~
		     */
		    logger.debug("channel:{} is start subscribing...", channel);
		    redis.subscribe(sub, channel);
	    });
    }

	public static void transactions(){
		commonPool.getResource(redis -> {
			String operationKey = "transactions";
			while(true){
				Transaction transaction = redis.multi();
				Response<String> getResponse = transaction.get(operationKey);
				Response<String> setResponse = transaction.set(operationKey, "value");
				List<Object> objects = transaction.exec();
				/**
				 * 如果在exec执行之前调用response.get()，异常：
				 * redis.clients.jedis.exceptions.JedisDataException: Please close pipeline or multi block before calling this method.
				 *
				 * 在没有完成事务操作之前操作 redis.set(operationKey, "redis.set");
				 * redis.clients.jedis.exceptions.JedisDataException: Cannot use Jedis when in Multi. Please use Transation or reset jedis state.
				 * */
				String getString = getResponse.get();
				String setString = setResponse.get();
				logger.debug("transactions:  {} {} {}", getString, setString, objects);
				TimeUnit.SECONDS.sleep(15);
			}
		});
	}


	public static void cluster() throws InterruptedException {
        JedisCluster cluster = new JedisCluster(CLUSTER);
        for (int i = 10000; i < 1000000; i++){
            String key = String.valueOf(i);
            String value = "key" + String.valueOf(i);
            cluster.set(key, value);
        }
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


    private static final class MyCommonJedisPool extends MyJedisPool<Jedis> {

	    private final JedisPool pool;

	    public MyCommonJedisPool(URI uri) {
		    this.pool = new JedisPool(getConfig(), uri);
	    }

	    @Override
	    protected Pool<Jedis> getPool() {
		    return pool;
	    }

	    @Override
	    protected void returnResource(Jedis resource) {
		    resource.close();
	    }
    }


	private static final class MyShardedJedisPool extends MyJedisPool<ShardedJedis> {

		private final ShardedJedisPool  pool;

		public MyShardedJedisPool(List<JedisShardInfo> shardInfoList) {
			this.pool = new ShardedJedisPool(getConfig(), shardInfoList);
		}

		@Override
		protected Pool<ShardedJedis> getPool() {
			return pool;
		}

		@Override
		protected void returnResource(ShardedJedis resource) {
			resource.close();
		}
	}

	private static abstract class MyJedisPool<T extends JedisCommands> {

		public T getResource() {
			return getPool().getResource();
		}

		protected abstract Pool<T> getPool();

		protected abstract void returnResource(T resource);

		public void getResource(JedisConsumer<T> consumer){
			T resource = null;
			try {
				resource = getResource();
				consumer.accept(resource);
			}
			catch (Throwable throwable){
				logger.warn("", throwable);

			}finally {
				if (resource != null) {
					returnResource(resource);
				}
			}
		}

		public JedisPoolConfig getConfig(){
			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxTotal(10);
			return config;
		}
	}

    private interface JedisConsumer<T>{
    	void accept(T redis) throws Exception;
    }
}
