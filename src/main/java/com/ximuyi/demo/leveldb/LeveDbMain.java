package com.ximuyi.demo.leveldb;

import com.ximuyi.common.PoolThreadFactory;
import org.apache.commons.lang3.RandomUtils;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class LeveDbMain {

	private static final Logger logger = LoggerFactory.getLogger(LeveDbMain.class);

	public static void main(String[] args) throws IOException, InterruptedException {
		File parent = new File("ignoredata");
		File file = new File(parent, "leveldb");
		logger.info("leveldb location:{}", file.getAbsoluteFile());
		DB db = Iq80DBFactory.factory.open(file, new Options().createIfMissing(true));
		PoolThreadFactory factory = new PoolThreadFactory("Redis", false);
		int readWriteThreadCount = 5;
		for (int i = 0; i < readWriteThreadCount; i++) {
			String unqiueId = "thread" + i;
			Thread writeThread = factory.newThread(() -> {
				loopHandleCommon(unqiueId, 1000, dbKeyValue -> {
					db.put(dbKeyValue.key(), dbKeyValue.value());
				});
			});
			writeThread.start();
			Thread readThread = factory.newThread(() -> {
				loopHandleCommon(unqiueId, 2000, dbKeyValue -> {
					byte[] bytes = db.get(dbKeyValue.key());
					if (bytes == null ||bytes.length == 0){
						return;
					}
					logger.info("{}", new DbKeyValue(dbKeyValue.key(), bytes));
				});
			});
			readThread.start();
			TimeUnit.MILLISECONDS.sleep(RandomUtils.nextInt(0, 1000));
		}
	}

	private static void loopHandleCommon(String uniqueId, long sleepTime, Consumer<DbKeyValue> consumer){
		while(true){
			long count = 0;
			while (count++ < 1000){
				String key = uniqueId + (count );
				String string = "value" + (count);
				DbKeyValue dbKeyValue = new DbKeyValue(key, string);
				try {
					consumer.accept(dbKeyValue);
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

	private static final class DbKeyValue{
		private final byte[] key;
		private final byte[] value;

		public DbKeyValue(String key, String value) {
			this(key.getBytes(), value.getBytes());
		}

		public DbKeyValue(byte[] key, byte[] value) {
			this.key = key;
			this.value = value;
		}

		public byte[] key() {
			return key;
		}

		public byte[] value() {
			return value;
		}

		@Override
		public String toString() {
			return "{" +
					"key='" + new String(key) + '\'' +
					", value='" + new String(value) + '\'' +
					'}';
		}
	}
}
