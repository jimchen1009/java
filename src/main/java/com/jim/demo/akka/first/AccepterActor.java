package com.jim.demo.akka.first;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.io.Tcp;
import akka.io.TcpMessage;

import java.net.InetSocketAddress;

/**
 * Created by chenjingjun on 2017-12-29.
 */
public class AccepterActor extends UntypedActor {

    private final ActorRef tcpManager;

    public AccepterActor(ActorRef tcpManager) {
        this.tcpManager = tcpManager;
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
    }

    @Override
    public void onReceive(Object o) throws Exception {
        System.out.println(o.toString());
        if (o instanceof Integer){
            final int port = (Integer)o;
            final InetSocketAddress endpoint = new InetSocketAddress("localhost", port);
            final Object cmd = TcpMessage.bind(getSelf(), endpoint, 100);
            tcpManager.tell(cmd, getSelf());
        }
        else if (o instanceof Tcp.Bound){
            tcpManager.tell(o, getSelf());
        }
        else if (o instanceof Tcp.CommandFailed){
            getContext().stop(getSelf());
        }
        else if (o instanceof Tcp.Connected){
            final Tcp.Connected conn = (Tcp.Connected)o;
            tcpManager.tell(conn, getSelf());
            final ActorRef handler = getContext().actorOf(Props.create(Handler.class));
            getSender().tell(TcpMessage.register(handler), getSelf());
        }
    }
}