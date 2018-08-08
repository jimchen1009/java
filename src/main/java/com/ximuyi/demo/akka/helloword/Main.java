package com.ximuyi.demo.akka.helloword;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

/**
 * Created by chenjingjun on 2018-04-01.
 */
public class Main {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("Hello");
        ActorRef a = system.actorOf(Props.create(HelloWorld.class), "helloWorld");
        System.out.println(a.path());
    }
}
