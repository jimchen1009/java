package com.jim.demo.akka.remote.e0;

import akka.actor.AbstractActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReceiveActor extends AbstractActor {
    private static final Logger logger = LoggerFactory.getLogger(ReceiveActor.class);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, message->{
                    logger.info("收到消息 msg:" + message.toString());
                    this.getSender().tell("Hello I'm " + this.getSelf().path().name(), getSelf());
                })
                .matchAny( message->{
                    logger.info(message.toString());
                })
                .build();
    }
}
