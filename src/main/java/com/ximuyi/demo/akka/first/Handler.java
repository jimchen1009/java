package com.ximuyi.demo.akka.first;

import akka.actor.UntypedActor;
import akka.io.Tcp;
import akka.io.TcpMessage;
import akka.util.ByteString;

/**
 * Created by chenjingjun on 2017-12-29.
 */
public class Handler extends UntypedActor {

    @Override
    public void onReceive(Object o) throws Exception {
        System.out.println("Handler receice:" + o);
        if (o instanceof Tcp.Received){
            final ByteString data = ((Tcp.Received)o).data();
            getSender().tell(TcpMessage.write(data),getSelf());
        }
        else if (o instanceof Tcp.ConnectionClosed){
            getContext().stop(getSelf());
        }
    }
}
