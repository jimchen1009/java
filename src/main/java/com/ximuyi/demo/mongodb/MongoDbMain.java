package com.ximuyi.demo.mongodb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.mongodb.ReadPreference;
import com.mongodb.Tag;
import com.mongodb.TagSet;
import com.mongodb.TaggableReadPreference;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.ximuyi.common.PoolThreadFactory;
import org.apache.commons.lang3.RandomUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoDbMain {

    static final Logger logger = LoggerFactory.getLogger(MongoDbMain.class);

//	private static final MongoDBManager dbManager = new MongoDBManager(MongoDBConfig.STANDALONE_ADDRESS);
	private static final MongoDBManager dbManager = new MongoDBManager(MongoDBConfig.REPLICA_ADDRESS);


	public static void main(String[] args) throws IOException {
//        addFile2GridFS(1);
//        readReference();
	}

	private static void readReference(){
        MongoCollection<Document> collection = dbManager.defaultDocument("employees");
        List<Tag> tagList = Arrays.asList(new Tag("name", "C"));
        TaggableReadPreference readPreference = ReadPreference.primaryPreferred(new TagSet(tagList), 5000L, TimeUnit.MILLISECONDS);
        MongoCollection<Document> withReadPreference = collection.withReadPreference(readPreference);
        FindIterable<Document> iterable = withReadPreference.find(new Document());
        for (Document document : iterable) {
            logger.info("document:{}", document);
        }
    }

	private void firstDemo(){
        MongoCollection<Document> collection = dbManager.defaultDocument("users");
        FindIterable<Document> iterable = collection.find();
        for (Document document : iterable) {
            logger.debug("document:{}", document.toJson());
        }
        PoolThreadFactory factory = new PoolThreadFactory("mongo", false);
        for (int i = 0; i < 5; i++) {
            Thread thread = factory.newLoopThread((index) -> {
                for (int userId = 1; userId <= 10; userId++) {
                    createOrLogin(userId, collection);
                }
            }, TimeUnit.MILLISECONDS, RandomUtils.nextInt(50, 100), 0);
            thread.start();
        }
    }

	private static void createOrLogin(long userId, MongoCollection<Document> collection){
		Date current = new Date();
		Document uniqueKey = new Document("userId", userId);
		Document document = collection.find(uniqueKey).first();
		Document updated = new Document("updated", current);
		if (document == null){
			String account = UUID.randomUUID().toString();
			document = new Document(uniqueKey).append("account", account).append("created", current);
			document.putAll(updated);
			collection.insertOne(document);
		}
		else {
			collection.updateOne(uniqueKey, new Document("$set", updated));
		}
	}

	private static void addFile2GridFS(int index) throws IOException {
        MongoDatabase db = dbManager.defaultDb();
        GridFSBucket bucket = GridFSBuckets.create(db, "driver");
        String fileName = "java_" + index;
        GridFSFindIterable files = bucket.find(new Document("filename", fileName));
        ObjectId objectId;
        if (files.first() == null){
            String content = "JAVA实例，增加文件" + fileName + ", 增加时间："+System.currentTimeMillis();
            byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            objectId = bucket.uploadFromStream(fileName, inputStream);
            inputStream.close();
        }
        else {
            objectId = files.first().getObjectId();
        }
        files = bucket.find(new Document("_id", objectId));
        for (GridFSFile file : files) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bucket.downloadToStream(file.getFilename(), outputStream);
            String message = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
            logger.info("最新的文件:{} 内容：{}", file.getFilename(), message);
        }
    }
}
