package com.ximuyi.demo.akka.remote;

import akka.actor.AbstractActor;

public class CalculatorActor extends AbstractActor {
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Op.Add.class,  message->{
                    System. out .println("Calculating " + message.getN1() + " + " + message.getN2());
                    response(new Op.AddResult(message.getN1(), message.getN2(), message.getN1() + message.getN2()));
                })
                .match(Op.Subtract.class, message->{
                    System. out .println("Calculating " + message.getN1() + " - " + message.getN2());
                    response(new Op.SubtractResult(message.getN1(), message.getN2(), message.getN1() - message.getN2()));
                })
                .match(Op.Multiply.class, message->{
                    System. out .println("Calculating " + message.getN1() + " * " + message.getN2());
                    response(new Op.MultiplicationResult(message.getN1(), message.getN2(), message.getN1() * message.getN2()));
                })
                .match(Op.Divide.class, message->{
                    System. out .println("Calculating " + message.getN1() + " / " + message.getN2());
                    response(new Op.DivisionResult(message.getN1(), message.getN2(), message.getN1() / message.getN2()));
                })
                .build();
    }

    /**
     * @param result
     */
    private void response(Object result){
        getSender().tell( result, getSelf());
    }
}
