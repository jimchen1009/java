package com.jim.demo.akka.helloword;

import akka.actor.UntypedActor;

/**
 * Created by chenjingjun on 2018-04-01.
 */
public class Greeter extends UntypedActor {

    public static enum Msg {
        GREET, DONE;
    }

    @Override
    public void onReceive(Object msg) throws InterruptedException {
        if (msg == Msg.GREET) {
            System.out.println("Hello World!");
            Thread.sleep(1000);
            getSender().tell(Msg.DONE, getSelf());
        } else
            unhandled(msg);
    }
}
