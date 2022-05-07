package com.jim.demo.akka.remote;

import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.ConfigFactory;

public class RemoteActorSelection {

    public static class HandlerResult extends AbstractActor {

        @Override
        public void preStart() throws Exception {
            ActorSelection selection = this.getContext().actorSelection(
                    "akka.tcp://caculatorSystem@127.0.0.1:8989/user/calculator" );
            selection.tell( new Op.Add(1, 2), this.getSelf());
        }

        @Override
        public Receive createReceive() {
            return receiveBuilder()
                    .match(Op.AddResult.class,  message->{
                        System. out .println("add result=" + message.getResult());
                    })
                    .match(Op.SubtractResult.class, message->{
                        System. out .println("subtract result=" + message.getResult());
                    })
                    .match(Op.MultiplicationResult.class, message->{
                        System. out .println("multiply result=" + message.getResult());
                    })
                    .match(Op.DivisionResult.class, message->{
                        System. out .println("divide result=" + message.getResult());
                    })
                    .build();
        }
    }

    public static void main(String args[]) {
//        // 不使用默认的配置，而是选择加载选定的remote actor配置
//        final ActorSystem system = ActorSystem.create( "caculatorSystem", ConfigFactory.load( "akka.remote/calculator"));
//        // 初始化远程actor
//        system .actorOf(Props.create(CalculatorActor. class ),"calculator" );


        // 初始化本地的Actor
        final ActorSystem localSystem = ActorSystem.create( "localSystem", ConfigFactory.load( "akka.remote/remotelookup"));
        localSystem .actorOf(Props.create(HandlerResult. class ), "handlerResult" );
    }
}
