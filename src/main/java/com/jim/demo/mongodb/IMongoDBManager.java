package com.jim.demo.mongodb;

import java.util.Objects;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public interface IMongoDBManager {

    String DEFAULT_DB = "demo";


    default MongoDatabase defaultDb(){
        return getDb(DEFAULT_DB);
    }

    MongoDatabase getDb(String name);

    default MongoCollection<Document> defaultDocument(String collection){
        return getDocument(DEFAULT_DB, collection);
    }

    default MongoCollection<Document> getDocument(String dbName, String collection){
        MongoDatabase db = Objects.requireNonNull(getDb(dbName));
        return db.getCollection(collection);
    }
}
