package com.jim.demo.mongodb;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.event.CommandFailedEvent;
import com.mongodb.event.CommandListener;
import com.mongodb.event.CommandStartedEvent;
import com.mongodb.event.CommandSucceededEvent;
import com.mongodb.event.ConnectionAddedEvent;
import com.mongodb.event.ConnectionCheckedInEvent;
import com.mongodb.event.ConnectionCheckedOutEvent;
import com.mongodb.event.ConnectionPoolClosedEvent;
import com.mongodb.event.ConnectionPoolListener;
import com.mongodb.event.ConnectionPoolOpenedEvent;
import com.mongodb.event.ConnectionPoolWaitQueueEnteredEvent;
import com.mongodb.event.ConnectionPoolWaitQueueExitedEvent;
import com.mongodb.event.ConnectionRemovedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MongoDBManager implements IMongoDBManager {

	private static final Logger logger = LoggerFactory.getLogger(MongoDBManager.class);

	private static final String DEFAULT_DB = "demo";

	private final MongoClient client;

	public MongoDBManager(List<ServerAddress> addressList) {
		MongoCredential credential = MongoCredential.createScramSha1Credential("root", DEFAULT_DB, "000000".toCharArray());

		// 新版本的连接
		MongoClientSettings settings = MongoClientSettings.builder()
				.readPreference(ReadPreference.secondary())
				.addCommandListener(new MyCommandListener())
				.applicationName("mongodb")
				.applyToSslSettings( builder -> builder.enabled(false))
				.applyToSocketSettings(builder -> builder.connectTimeout(1, TimeUnit.SECONDS).readTimeout(1, TimeUnit.SECONDS))
				.applyToConnectionPoolSettings(builder -> builder.addConnectionPoolListener(new MyConnectionPoolListener()).maxSize(10))
				.applyToClusterSettings(builder -> builder.hosts(addressList).build())
				.build();
		this.client = MongoClients.create(settings);
	}


	public MongoDatabase getDb(String name){
		return client.getDatabase(name);
	}

	private final class MyCommandListener implements CommandListener{

		@Override
		public void commandStarted(CommandStartedEvent event) {
			logger.info("{} {} {} ", event.getCommandName(), event.getRequestId(), event.getDatabaseName());
		}

		@Override
		public void commandSucceeded(CommandSucceededEvent event) {
			logger.info("{} {} {}(ms) ", event.getCommandName(), event.getRequestId(), event.getElapsedTime(TimeUnit.MILLISECONDS));
		}

		@Override
		public void commandFailed(CommandFailedEvent event) {
			logger.error("{} {}", event.getCommandName(), event.getRequestId(), event.getThrowable());
		}
	}


	private final class MyConnectionPoolListener implements ConnectionPoolListener{

		@Override
		public void connectionPoolOpened(ConnectionPoolOpenedEvent event) {

		}

		@Override
		public void connectionPoolClosed(ConnectionPoolClosedEvent event) {

		}

		@Override
		public void connectionCheckedOut(ConnectionCheckedOutEvent event) {

		}

		@Override
		public void connectionCheckedIn(ConnectionCheckedInEvent event) {

		}

		@Override
		public void waitQueueEntered(ConnectionPoolWaitQueueEnteredEvent event) {

		}

		@Override
		public void waitQueueExited(ConnectionPoolWaitQueueExitedEvent event) {

		}

		@Override
		public void connectionAdded(ConnectionAddedEvent event) {

		}

		@Override
		public void connectionRemoved(ConnectionRemovedEvent event) {

		}
	}

}
