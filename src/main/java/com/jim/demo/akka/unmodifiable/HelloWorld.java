package com.jim.demo.akka.unmodifiable;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.alibaba.fastjson.JSONObject;

import java.util.Arrays;

/**
 * Created by chenjingjun on 2018-04-01.
 */
public class HelloWorld extends UntypedActor {

    ActorRef greeter;

    @Override
    public void preStart() throws Exception {
        super.preStart();
        // create the greeter actor
        greeter = getContext().actorOf(Props.create(Greeter.class), "greeter");
        System.out.println("Greeter actor path：" + greeter.path());
        // tell it to perform the greeting
        greeter.tell(new Message(2, Arrays.asList("2", "dsf")), getSelf());
    }

    @Override
    public void onReceive(Object o) throws Exception {
        try {
            System.out.println("HelloWorld收到的数据为：" + JSONObject.toJSONString(o));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
