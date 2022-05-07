//package com.jim.demo.akka.stm;
//
//import akka.actor.ActorRef;
//import akka.actor.ActorSystem;
//import akka.actor.Props;
//import akka.pattern.Patterns;
//import akka.transactor.Coordinated;
//import akka.util.Timeout;
//import com.typesafe.config.ConfigFactory;
//import scala.concurrent.Await;
//
//import java.util.concurrent.TimeUnit;
//
///**
// * Created by chenjingjun on 2018-04-02.
// */
//public class STMMain {
//    public static ActorRef companyActor = null;
//    public static ActorRef employeeActor = null;
//
//
//    public static void main(String [] args) throws Exception {
//        ActorSystem system = ActorSystem.create("stm", ConfigFactory.load("akka.conf"));
//        companyActor = system.actorOf(Props.create(CompanyActor.class), "CompanyActor");
//        employeeActor = system.actorOf(Props.create(EmployeeActor.class), "EmployeeActor");
//
//        Timeout timeout = new Timeout(1, TimeUnit.SECONDS);
//
//        for(int i = 0 ; i < 23; i ++){
//            companyActor.tell(new Coordinated(i, timeout), ActorRef.noSender());
//
//            Thread.sleep(200);
//
//            int companyCount = (int) Await.result(Patterns.ask(companyActor, "getCount", timeout), timeout.duration());
//            int employeeCount = (int) Await.result(Patterns.ask(employeeActor, "getCount", timeout), timeout.duration());
//
//            System.out.println("companyCount = " + companyCount + ";employeeCount = " + employeeCount);
//            System.out.println("-----------------------");
//        }
//
//    }
//}
