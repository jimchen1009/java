package com.ximuyi.demo.akka.lifecycle;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * Created by chenjingjun on 2018-04-01.
 */
public class MyWork extends UntypedActor {
    LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    public static enum Msg{
        WORKING, DONE, CLOSE;
    }

    @Override
    public void onReceive(Object o)  throws Exception {
        try {
            if(o == Msg.WORKING){
                logger.info("i am  working");
            }else if(o == Msg.DONE){
                logger.info("stop  working");
            }else if(o == Msg.CLOSE){
                logger.info("stop  close");
                getSender().tell(Msg.CLOSE, getSelf());
                getContext().stop(getSelf());
            }else {
                unhandled(o);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void preStart() {
        logger.info("myWork starting.");
    }

    @Override
    public void postStop() throws Exception {
        logger.info("myWork stoping..");
    }
}
