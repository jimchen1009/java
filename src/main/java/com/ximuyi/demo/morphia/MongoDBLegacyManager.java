package com.ximuyi.demo.morphia;

import java.util.List;

import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.ximuyi.demo.mongodb.IMongoDBManager;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoDBLegacyManager implements IMongoDBManager {

	private static final Logger logger = LoggerFactory.getLogger(MongoDBLegacyManager.class);


	private final com.mongodb.MongoClient legacyClient;

	public MongoDBLegacyManager(List<ServerAddress> addressList) {
		// 旧版本的连接
		MongoClientOptions.Builder builder = MongoClientOptions.builder();
		MongoClientOptions options = builder.sslEnabled(false).serverSelectionTimeout(1000).build();
		this.legacyClient = new com.mongodb.MongoClient(addressList, options);
	}

	public MongoDatabase getDb(String name){
		return legacyClient.getDatabase(name);
	}

	public Datastore createDefaultDataStore(String packageName){
		return createDataStore(DEFAULT_DB, packageName);
	}

	public Datastore createDataStore(String dbName, String packageName){
		final Morphia morphia = new Morphia();
		morphia.mapPackage(packageName);
		Datastore datastore = morphia.createDatastore(legacyClient, dbName);
		return datastore;
	}
}
