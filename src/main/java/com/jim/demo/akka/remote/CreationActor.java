package com.jim.demo.akka.remote;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

public class CreationActor extends AbstractActor {
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Op.MathOp.class, (message)->{
                        ActorRef calculator = getContext().actorOf(Props.create(CreationActor. class));
                        calculator.tell( message, getSelf());
                })
                .match(Op.MultiplicationResult.class, (message)->{
                    System. out .printf("Mul result: %d * %d = %d\n" , message .getN1(), message.getN2(), message.getResult());
                    getContext().stop(getSender());
                })
                .match(Op .DivisionResult.class, message->{
                    System. out .printf("Div result: %.0f / %d = %.2f\n" , message .getN1(), message.getN2(), message.getResult());
                    getContext().stop(getSender());
                })
                .build();
    }
}
