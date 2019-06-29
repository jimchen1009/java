package com.ximuyi.demo.redission;

import com.ximuyi.common.PoolThreadFactory;
import org.apache.commons.lang3.RandomUtils;
import org.apache.ibatis.io.Resources;
import org.redisson.Redisson;
import org.redisson.api.RBitSet;
import org.redisson.api.RBucket;
import org.redisson.api.RExpirable;
import org.redisson.api.RFuture;
import org.redisson.api.RGeo;
import org.redisson.api.RHyperLogLog;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.IntegerCodec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;
import org.redisson.config.TransportMode;
import org.redisson.misc.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class RedissonMain {
	private static final Logger logger = LoggerFactory.getLogger(RedissonMain.class);

	private static final String PASSWORD = "123456";
	private static final URI MASTER = URIBuilder.create("redis://127.0.0.1:7208");
	private static final Set<URI> SLAVES = new HashSet<>(Arrays.asList(
			URIBuilder.create("redis://127.0.0.1:7009"),
			URIBuilder.create("redis://127.0.0.1:7010"),
			URIBuilder.create("redis://127.0.0.1:7011")
	));

    public static void main(String[] args) throws InterruptedException, IOException {
	    Config config = loadConfig("redisson.master.slaves/redisson.yaml");
	    RedissonClient redisson = Redisson.create(config);
	    redisson.getKeys().flushall();
	    /***
	     * Window、Redis 版本3.2.1【坑爹不维护windows的高版本，只能去用linux吧~】
	     * rdbcompression yes: dump.rdb [71KB] appendonly.aof[93KB]
	     * rdbcompression no: dump.rdb [71KB] appendonly.aof[93KB]
	     *
	     * RedisValue 增加字段：
	     *         private String empty0 = "                        ";
	     *         private String empty1 = "                        ";
	     *         private String empty2 = "                        ";
	     * rdbcompression yes: dump.rdb [81KB] appendonly.aof[122KB]
	     * rdbcompression no: dump.rdb [101KB] appendonly.aof[122KB]
	     *
	     *
	     * # min-slaves-to-write 2
	     * # min-slaves-max-lag 5
	     * Exception in thread "main" org.redisson.client.RedisException:
	     * NOREPLICAS Not enough good slaves to write.. channel: [id: 0x847026fc, L:/127.0.0.1:64019 - R:127.0.0.1/127.0.0.1:7008]
	     *
	     */
	    PoolThreadFactory factory = new PoolThreadFactory("Redis", false);
	    int readWriteThreadCount = 5;
	    for (int i = 0; i < readWriteThreadCount; i++) {
		    String unqiueId = "thread" + i;
		    factory.newThread(() -> loopRedissionHandler(redisson, unqiueId, 1000, true)).start();
		    factory.newThread(() -> loopRedissionHandler(redisson, unqiueId, 2000, false)).start();
	    }
    }

    private static void loopRedissionHandler(RedissonClient redisson, String uniqueId, long sleepTime, boolean isWrite){
	    long loop = 0;
	    while(loop++ < Long.MAX_VALUE){
		    int count = 0;
		    while (count++ < 1000){
			    try {
				    RedisKey redisKey = new RedisKey(loop, count, uniqueId);
				    int index = count % HANDLERS.size();
				    RedissonHandler handler = HANDLERS.get(index);
				    if (isWrite){
				    	handler.writeInvoke(redisson, redisKey);
				    }
				    else {
					    handler.readInvoke(redisson, redisKey);
				    }
			    }
			    catch (Throwable throwable){
				    logger.error("{}", throwable);
			    }
			    if (sleepTime <= 0){
				    continue;
			    }
			    try {
				    TimeUnit.MILLISECONDS.sleep(sleepTime);
			    } catch (InterruptedException ignored) {
			    }
		    }
	    }
    }

	/**
	 * Declarative configuration
	 * @param resoure
	 * @return
	 * @throws IOException
	 */
	private static Config loadConfig(String resoure) throws IOException {
		URL url = Resources.getResourceURL(resoure);
		return Config.fromYAML(url);
	}

	/**
	 * Programmatic configuration
	 * @return
	 */
    private static Config getConfig(){
	    Config config = new Config();
	    config.setTransportMode(TransportMode.NIO);
	    config.setThreads(5);
	    config.setCodec(new JsonJacksonCodec());
	    config.useMasterSlaveServers()
			    .setReadMode(ReadMode.SLAVE)
			    .setMasterAddress(MASTER)
			    .setPassword(PASSWORD)
			    .setSlaveAddresses(SLAVES);
//        config.useClusterServers().setScanInterval(2000)
//                .addNodeAddress("redis://127.0.0.1:7001")
//                .addNodeAddress("redis://127.0.0.1:7002")
//                .addNodeAddress("redis://127.0.0.1:7003")
//                .addNodeAddress("redis://127.0.0.1:7004")
//                .addNodeAddress("redis://127.0.0.1:7005")
//                .addNodeAddress("redis://127.0.0.1:7006")
//                .addNodeAddress("redis://127.0.0.1:7007");
	    return config;
    }

    private static class RedisValue implements Serializable {
        private String name;
        private String empty0 = "                        ";
        private String empty1 = "                        ";
        private String empty2 = "                        ";

	    public RedisValue() {
	    }

	    public RedisValue(String name) {
            this.name = name;
        }

	    @Override
	    public String toString() {
		    return "{" +
				    "name='" + name + '\'' +
				    ", empty0='" + empty0 + '\'' +
				    ", empty1='" + empty1 + '\'' +
				    ", empty2='" + empty2 + '\'' +
				    '}';
	    }
    }

	private static final List<RedissonHandler> HANDLERS = Arrays.asList(
			new RedissonHandler() {

				@Override
				public RExpirable writeInvoke0(RedissonClient redisson, RedisKey redisKey) {
					RBucket<RedisValue> rBucket = redisson.getBucket(redisKey.key(false));
					rBucket.set(redisKey.objValue());
					return rBucket;
				}

				@Override
				public void readInvoke0(RedissonClient redisson, RedisKey redisKey, Consumer<Object> consumer) {
					RBucket<RedisValue> rBucket = redisson.getBucket(redisKey.key(false));
					RFuture<RedisValue> future = rBucket.getAsync();
					future.onComplete((redisValue, exception) ->{
						if (exception != null) {
							logger.error("", exception);
						}
						else {
							consumer.accept(redisValue);
						}
					});
				}
			},
			new RedissonHandler() {

				@Override
				public RExpirable writeInvoke0(RedissonClient redisson, RedisKey redisKey) {
					RBitSet bitSet = redisson.getBitSet(redisKey.key(false));
					bitSet.set(redisKey.randomIndex(100));
					bitSet.clear(redisKey.randomIndex(100));
					return bitSet;
				}

				@Override
				public void readInvoke0(RedissonClient redisson, RedisKey redisKey, Consumer<Object> consumer) {
					RBitSet bitSet = redisson.getBitSet(redisKey.key(false));
					consumer.accept(bitSet.isExists() ?  bitSet.asBitSet() : null );
				}
			},
			new RedissonHandler() {

				@Override
				public RExpirable writeInvoke0(RedissonClient redisson, RedisKey redisKey) {
					RList<Integer> objectRList = redisson.getList(redisKey.key(false), new IntegerCodec());
					objectRList.add(redisKey.count);
					return objectRList;
				}

				@Override
				public void readInvoke0(RedissonClient redisson, RedisKey redisKey, Consumer<Object> consumer) {
					RList<Integer> objectRList = redisson.getList(redisKey.key(false), new IntegerCodec());
					List<Integer> list = objectRList.readAll();
					consumer.accept(list);
					ramdomCmd(redisKey, objectRList);
				}
			},
			new RedissonHandler() {

				@Override
				public RExpirable writeInvoke0(RedissonClient redisson, RedisKey redisKey) {
					RMap<Integer, RedisValue> map = redisson.getMap(redisKey.key(false), new JsonJacksonCodec());
					map.put(redisKey.randomIndex(100), redisKey.objValue());
					map.remove(redisKey.randomIndex(100));
					return map;
				}

				@Override
				public void readInvoke0(RedissonClient redisson, RedisKey redisKey, Consumer<Object> consumer) {
					/***
					 * RMap<KEY, String>  泛型的KEY byte[] String Integer 都不会报错~
					 * 但是在运行时的时候，会报错的：例如RMap<byte[], String> map
					 * java.lang.ClassCastException: java.lang.String cannot be cast to [B
					 *                      ArrayList<byte[]> bytes1 = new ArrayList<>(bytes);
					 * 						byte[] bytes2 = bytes1.get(0);
					 *
					 */
					RMap<Integer, RedisValue> map = redisson.getMap(redisKey.key(false), new JsonJacksonCodec());
					Map<Integer, RedisValue> readAllMap = map.readAllMap();
					consumer.accept(readAllMap);
				}
			},
			new RedissonHandler() {

				@Override
				public RExpirable writeInvoke0(RedissonClient redisson, RedisKey redisKey) {
					RHyperLogLog<Integer> hyperLogLog = redisson.getHyperLogLog(redisKey.key(false));
					hyperLogLog.add(redisKey.randomIndex(1000));
					return hyperLogLog;
				}

				@Override
				public void readInvoke0(RedissonClient redisson, RedisKey redisKey, Consumer<Object> consumer) {
					RHyperLogLog<Integer> hyperLogLog = redisson.getHyperLogLog(redisKey.key(false));
					consumer.accept(hyperLogLog.count());
				}
			},
			new RedissonHandler() {

				@Override
				public RExpirable writeInvoke0(RedissonClient redisson, RedisKey redisKey) {
					RGeo<Long> redissonGeo = redisson.getGeo(redisKey.key(false));
					return redissonGeo;
				}

				@Override
				public void readInvoke0(RedissonClient redisson, RedisKey redisKey, Consumer<Object> consumer) {
					RGeo<Long> redissonGeo = redisson.getGeo(redisKey.key(false));
				}
			}
			,new RedissonHandler() {

				@Override
				public RExpirable writeInvoke0(RedissonClient redisson, RedisKey redisKey) {
					RLock lock = redisson.getLock(redisKey.key(false));
					lock.lock();
					try {
						TimeUnit.SECONDS.sleep(10);
					} catch (InterruptedException ignored) {
					}
					finally {
						lock.unlock();
					}
					return null;
				}

				@Override
				public void readInvoke0(RedissonClient redisson, RedisKey redisKey, Consumer<Object> consumer) {
					RLock lock = redisson.getLock(redisKey.key(false));
					lock.lock();
					try {
						TimeUnit.SECONDS.sleep(10);
					} catch (InterruptedException ignored) {
					}
					finally {
						lock.unlock();
					}
				}
			}
	);

	private static class RedisKey{
		private final long loop;
		private final int count;
		private final String uniqueKey;

		public RedisKey(long loop, int count, String uniqueKey) {
			this.loop = loop;
			this.count = count;
			this.uniqueKey = uniqueKey;
		}

		public String key(boolean loopUnique){
			String string = uniqueKey + count;
			if (loopUnique){
				string = loop + string;
			}
			return string;
		}

		public RedisValue objValue(){
			return new RedisValue(stringValue());
		}

		public String stringValue(){
			return loop + "-" + count;
		}

		public int modeIndex(int value){
			return count % value;
		}

		public int randomIndex(int value){
			return RandomUtils.nextInt(0, value);
		}

		public boolean random(int lessThanValue, int totalValue){
			if (lessThanValue >= totalValue){
				return true;
			}
			return RandomUtils.nextInt(0, totalValue) < lessThanValue;
		}
	}

	private abstract static class RedissonHandler {

		private final boolean isLogging;

		public RedissonHandler() {
			this(false);
		}

		public RedissonHandler(boolean isLogging) {
			this.isLogging = isLogging;
		}

		public final void writeInvoke(RedissonClient redisson, RedisKey redisKey){
		    RExpirable rExpirable = writeInvoke0(redisson, redisKey);
		    if (rExpirable == null){
		    	return;
		    }
		    ramdomCmd(redisKey, rExpirable);
	    }

	    public final void readInvoke(RedissonClient redisson, RedisKey redisKey){
		    readInvoke0(redisson, redisKey, object -> {
			    if (isLogging && object != null) {
				    logger.info("get is invoked successfully: {}", object);
			    }
		    });
	    }

	    protected void ramdomCmd(RedisKey redisKey, RExpirable rObject){
		    int nextInt = RandomUtils.nextInt(0, 15);
		    if (nextInt == 0){
			    rObject.delete();
		    }
		    else if (nextInt == 1){
			    rObject.touch();
		    }
		    else if (nextInt == 2){
			    rObject.expireAt(new Date());
		    }
		    else if (nextInt == 3){
			    rObject.isExists();
		    }
		    else if (nextInt == 4){
			    try {
				    rObject.rename(redisKey.key(true));
			    }
			    catch (Throwable ignored){
			    }
		    }
		    else if (nextInt == 5){
			    rObject.sizeInMemory();
		    }
		    else if (nextInt == 6){
		    	try {
				    rObject.renamenx(redisKey.key(true));
			    }
			    catch (Throwable ignored){

			    }
		    }
		    else if (nextInt == 7){
			    rObject.clearExpire();
		    }
		    else if (nextInt == 8){
			    rObject.remainTimeToLive();
		    }
		    else {
			    rObject.move(nextInt);
		    }
	    }

	    public abstract RExpirable writeInvoke0(RedissonClient redisson, RedisKey redisKey);

	    public abstract void readInvoke0(RedissonClient redisson, RedisKey redisKey, Consumer<Object> consumer);
    }
}
