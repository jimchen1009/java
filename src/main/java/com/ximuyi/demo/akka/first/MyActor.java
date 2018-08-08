package com.ximuyi.demo.akka.first;

import akka.actor.UntypedActor;

/**
 * Created by chenjingjun on 2017-12-29.
 *
 * 据说不能用嵌套类
 * java.lang.IllegalArgumentException: no matching constructor found on class com.ximuyi.demo.akka.first.AkkaFirstMain$MyActor for arguments []
 */
public class MyActor extends UntypedActor {

    public MyActor() {
    }

    @Override
    public void onReceive(Object o) throws Exception {
        System.out.println(o.toString());
    }
}