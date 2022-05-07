package com.jim.demo.akka.future;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * Created by chenjingjun on 2018-04-01.
 */
public class PrintActor extends UntypedActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    @Override
    public void onReceive(Object o) throws Exception {
        log.info("akka.future.PrintActor.onReceive:" + o);
        if (o instanceof Integer) {
            log.info("print:" + o);
        } else {
            unhandled(o);
        }
    }
}
