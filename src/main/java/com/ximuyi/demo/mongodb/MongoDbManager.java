package com.ximuyi.demo.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class MongoDbManager {

	private static final String DB = "demo";

	private final MongoClient client;
	private final Map<String, MongoDatabase> dbs;
	private final Map<String, Map<String, MongoCollection<Document>>> documents;

	public MongoDbManager() {
		ServerAddress address = new ServerAddress("localhost", 27017);
		MongoCredential credential = MongoCredential.createScramSha1Credential("root", DB, "000000".toCharArray());
		//通过连接认证获取MongoDB连接
		MongoClientOptions.Builder builder = MongoClientOptions.builder();
		MongoClientOptions options = builder.sslEnabled(false).serverSelectionTimeout(1000).build();
		this.client = new MongoClient(address, options);
		this.dbs = new ConcurrentHashMap<>();
		this.documents = new ConcurrentHashMap<>();
	}


	public MongoDatabase defaultDb(){
		return getDb(DB);
	}

	public MongoCollection<Document> defaultDocument(String collection){
		return getDocument(DB, collection);
	}

	public MongoDatabase getDb(String name){
		return dbs.computeIfAbsent(name, client::getDatabase);
	}

	public MongoCollection<Document> getDocument(String dbName, String collection){
		MongoDatabase db = Objects.requireNonNull(getDb(dbName));
		Map<String, MongoCollection<Document>> documents = this.documents.computeIfAbsent(dbName, key -> new ConcurrentHashMap<>());
		return documents.computeIfAbsent(collection, db::getCollection);
	}
}
