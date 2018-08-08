package com.ximuyi.demo.akka.remote;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class RemoteActorMain {

    public static void main(String args[]) throws IOException {

        // 不使用默认的配置，而是选择加载选定的remote actor配置
        Config config = ConfigFactory.load("akka.remote/calculator");
        final ActorSystem system = ActorSystem.create( "caculatorSystem", config);

        // remote actor的ref
        final ActorRef calculatorActor = system .actorOf(Props.create(CalculatorActor. class ), "calculator" );

        final Timeout timeout = new Timeout(Duration.create(5, TimeUnit.SECONDS));
        Future<Object> addFuture = Patterns.ask( calculatorActor, new Op.Add(1, 2), timeout );
        Future<Object> subtractFuture = Patterns.ask( calculatorActor, new Op.Subtract(1, 2), timeout );
        Future<Object> multiplyFuture = Patterns.ask( calculatorActor, new Op.Multiply(1, 2), timeout );
        Future<Object> divideFuture = Patterns.ask( calculatorActor, new Op.Divide(1, 2), timeout );

        Iterable<Future<Object>> futures = Arrays.asList (addFuture , subtractFuture , multiplyFuture, divideFuture );

//        futures.forEach( future->{
//            try {
//                Op.MathResult r = (Op.MathResult)Await.result( future, timeout .duration());
//                if (r instanceof Op.AddResult) {
//                    System. out .println("add result=" + ((Op.AddResult) r ).getResult());
//                } else if (r instanceof Op.SubtractResult) {
//                    System. out .println("subtract result=" + ((Op.SubtractResult) r ).getResult());
//                } else if (r instanceof Op.MultiplicationResult) {
//                    System. out .println("multiply result=" + ((Op.MultiplicationResult) r ).getResult());
//                } else if (r instanceof Op.DivisionResult) {
//                    System. out .println("divide result=" + ((Op.DivisionResult) r ).getResult());
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//        Future<Iterable<Op.MathResult>> results = Futures.traverse( futures, param -> Futures.future(() -> (Op.MathResult) Await.result( param, timeout .duration()), system.dispatcher())
//                , system.dispatcher());
//        results.onSuccess( new OnSuccess<Iterable<Op.MathResult>>() {
//            @Override
//            public void onSuccess(Iterable<Op.MathResult> result ) {
//                for (Op.MathResult r : result ) {
//                    if (r instanceof Op.AddResult) {
//                        System. out .println("add result=" + ((Op.AddResult) r ).getResult());
//                    } else if (r instanceof Op.SubtractResult) {
//                        System. out .println("subtract result=" + ((Op.SubtractResult) r ).getResult());
//                    } else if (r instanceof Op.MultiplicationResult) {
//                        System. out .println("multiply result=" + ((Op.MultiplicationResult) r ).getResult());
//                    } else if (r instanceof Op.DivisionResult) {
//                        System. out .println("divide result=" + ((Op.DivisionResult) r ).getResult());
//                    }
//                }
//            }
//        }, system.dispatcher());
    }
}
