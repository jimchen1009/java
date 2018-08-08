package com.ximuyi.demo.akka;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;
import com.typesafe.config.ConfigFactory;
import com.ximuyi.demo.akka.lifecycle.WatchActor;

/**
 * Created by chenjingjun on 2018-04-01.
 */
public class ProcedureTest extends UntypedActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public enum Msg{
        PLAY, SLEEP;
    }

    Procedure<Object> happy = new Procedure<Object>() {
        @Override
        public void apply(Object o) throws Exception {
            log.info("i am happy! " + o);
            if (o == Msg.PLAY) {
                getSender().tell("i am alrady happy!!", getSelf());
                log.info("i am alrady happy!!");
            } else if (o == Msg.SLEEP) {
                log.info("i do not like sleep!");
                getContext().become(angray);
            } else {
                unhandled(o);
            }
        }
    };

    Procedure<Object> angray = new Procedure<Object>() {
        @Override
        public void apply(Object o) throws Exception {
            log.info("i am angray! "+o);
            if(o ==Msg.SLEEP){
                getSender().tell("i am alrady angray!!", getSelf());
                log.info("i am alrady angray!!");
            } else if(o ==Msg.PLAY) {
                log.info("i like play.");
                getContext().become(happy);
            } else {
                unhandled(o);
            }
        }
    };
    @Override
    public void onReceive(Object o) throws Exception {
        //akka.ProcedureTest#onReceive这个方法只被调用一次
        log.info("onReceive msg: " + o);
        if(o == Msg.SLEEP){
            getContext().become(angray);
        }else if(o == Msg.PLAY){
            getContext().become(happy);
        }
        else {
            unhandled(o);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ActorSystem system = ActorSystem.create("strategy", ConfigFactory.load("akka.config"));
        ActorRef procedureTest = system.actorOf(Props.create(ProcedureTest.class), "ProcedureTest");
        system.actorOf(Props.create(WatchActor.class, procedureTest), "WatchActor");
        procedureTest.tell(Msg.PLAY, ActorRef.noSender());
        procedureTest.tell(Msg.SLEEP, ActorRef.noSender());
        procedureTest.tell(Msg.PLAY, ActorRef.noSender());
        procedureTest.tell(Msg.PLAY, ActorRef.noSender());

        procedureTest.tell(PoisonPill.getInstance(), ActorRef.noSender());
    }
}
