package com.ximuyi.demo.redission;

import com.ximuyi.common.PoolThreadFactory;
import org.apache.commons.lang3.RandomUtils;
import org.apache.ibatis.io.Resources;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RFuture;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;
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
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class RedissonMain {
	private static final Logger logger = LoggerFactory.getLogger(RedissonMain.class);

	private static final String PASSWORD = "123456";
	private static final URI MASTER = URIBuilder.create("redis://127.0.0.1:7008");
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
		    Thread writeThread = factory.newThread(() -> {
			    handleRedisWrite(redisson, unqiueId, 10);
		    });
		    writeThread.start();
		    Thread readThread = factory.newThread(() -> {
			    handleRedisRead(redisson, unqiueId, 20);
		    });
		    readThread.start();
	    }
    }

    private static void handleRedisRead(RedissonClient redisson, String uniqueId, long sleepTime){
	    loopHandleRBucket(redisson, uniqueId, sleepTime, (bucket, value) -> {
		    RFuture<RedisValue> future = bucket.getAsync();
		    future.onComplete((redisValue, exception) ->{
			    if (exception != null) {
				    logger.error("", exception);
			    }
			    else {
				    logger.info("getAsync() is invoked successfully: {}", redisValue);
			    }
		    });
	    });
    }
    private static void handleRedisWrite(RedissonClient redisson, String uniqueId, long sleepTime){
	    loopHandleRBucket(redisson, uniqueId, sleepTime, (rBucket, value) -> {
		    rBucket.set(value);
		    int nextInt = RandomUtils.nextInt(0, 10);
		    if (nextInt == 0){
			    rBucket.expire(20, TimeUnit.DAYS);
		    }
		    else if (nextInt == 1){
			    rBucket.delete();
		    }
		    else if (nextInt == 2){
			    rBucket.clearExpire();
		    }
		    else if (nextInt == 3){
			    rBucket.isExists();
		    }
		    else if (nextInt == 4){
			    rBucket.touch();
		    }
		    else if (nextInt == 5){
			    rBucket.remainTimeToLive();
		    }
		    else {
			    rBucket.move(nextInt);
		    }
	    });
    }


    private static void loopHandleRBucket(RedissonClient redisson, String uniqueId, long sleepTime, BiConsumer<RBucket<RedisValue>, RedisValue> consumer){
	    while(true){
		    long count = 0;
		    while (count++ < 1000){
			    String key = uniqueId + (count );
			    String string = "value" + (count);
			    RedisValue value = new RedisValue(string);
			    RBucket<RedisValue> bucket = redisson.getBucket(key);
			    try {
				    consumer.accept(bucket, value);
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
}
