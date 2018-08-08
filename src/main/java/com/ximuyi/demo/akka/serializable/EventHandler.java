package com.ximuyi.demo.akka.serializable;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.alibaba.fastjson.JSON;

/**
 * Created by chenjingjun on 2018-04-08.
 */
public class EventHandler extends AbstractActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);


    @Override
    public Receive createReceive() {
        return receiveBuilder().match(Evt.class, (msg)-> log.info( "Handled Event: " + JSON.toJSONString(msg)))
                .build();
    }

    @Override
    public void unhandled(Object message) {
        super.unhandled(message);
    }


}
