package com.ximuyi.demo.akka.strategy;

import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * Created by chenjingjun on 2018-04-01.
 */
public class SuperVisor extends UntypedActor {
    @Override
    public SupervisorStrategy supervisorStrategy(){
        akka.japi.Function<Throwable, SupervisorStrategy.Directive> function = throwable -> {
            if(throwable instanceof ArithmeticException){//ArithmeticException是出现异常的运算条件时，抛出此异常。例如，一个整数“除以零”时，抛出此类的一个实例。
                System.out.println("meet ArithmeticException ,just resume.");
                return  SupervisorStrategy.resume();//继续; 重新开始; 恢复职位;
            }else if(throwable instanceof NullPointerException){
                System.out.println("meet NullPointerException , restart.");
                return SupervisorStrategy.restart();
            }else if(throwable instanceof IllegalArgumentException){
                System.out.println("meet IllegalArgumentException ,stop.");
                return SupervisorStrategy.stop();
            }else{
                System.out.println("escalate.");
                return SupervisorStrategy.escalate();//使逐步升级; 使逐步上升; 乘自动梯上升;也就是交给更上层的actor处理。抛出异常
            }
        };
        //一分钟内重试3次，超过则kill掉actor
        return new OneForOneStrategy(3, Duration.create(1, TimeUnit.MINUTES), function);
    }

    @Override
    public void onReceive(Object o) throws Exception{
        if(o instanceof Props){
            getContext().actorOf((Props)o , "restartActor");
        }else{
            unhandled(o);
        }
    }
}
