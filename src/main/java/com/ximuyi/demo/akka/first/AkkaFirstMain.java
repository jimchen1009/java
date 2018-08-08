package com.ximuyi.demo.akka.first;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.io.Tcp;

/**
 * Created by chenjingjun on 2017-12-29.
 */
public class AkkaFirstMain {

    public static void main(String[] args){
        demo0();
//        demo1();
    }

    private static void demo0(){
        ActorSystem mySystem = ActorSystem.create("mySystem");
        ActorRef myActor = mySystem.actorOf(Props.create(MyActor.class), "myActor");
        myActor = mySystem.actorOf(Props.create(MyActor.class), "myActor");
        myActor.tell("Hello, World!", ActorRef.noSender());
        mySystem.terminate();
    }

    private static void demo1(){
        ActorSystem mySystem = ActorSystem.create("mySystem");
        ActorRef tcpManager = Tcp.get(mySystem).getManager();
        ActorRef accepter = mySystem.actorOf(Props.create(AccepterActor.class, tcpManager), "accepter");
        //端口 12345 绑定、
        accepter.tell(12345, ActorRef.noSender());
    }
}
