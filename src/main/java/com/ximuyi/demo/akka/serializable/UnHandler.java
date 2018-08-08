package com.ximuyi.demo.akka.serializable;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.alibaba.fastjson.JSON;

/**
 * Created by chenjingjun on 2018-04-08.
 */
public class UnHandler extends AbstractActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);


    @Override
    public Receive createReceive() {
        return receiveBuilder().matchAny((msg)-> log.info( getSender().toString() +" UnHandler : " + JSON.toJSONString(msg)))
                .build();
    }

    @Override
    public void unhandled(Object message) {
        super.unhandled(message);
    }


}
