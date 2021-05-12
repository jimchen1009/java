package com.ximuyi.demo.rxjava;


import com.ximuyi.common.Log4jUtil;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import jodd.util.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * https://github.com/ReactiveX/RxJava
 * https://github.com/ReactiveX/RxJava/wiki/Creating-Observables
 *
 *
 *
 * 1. Observable 是RxJava描述的事件流，在链式调用中非常清晰，事件从创建到加工处理再到被订阅者接收到，就是一个接一个的Observable形成的一个事件流。
 *
 *
 * 2. ObservableOnSubscribe 是这个事件流的源头，下面我们称之为事件源，一般由我们自己创建并传入。
 *
 *
 * 3. Observer 是整个事件流的订阅者，也就是说，它将会订阅前面事件创建，加工以后的最终结果。它也是由我们创建的，我们将要重写它的onNext()，onCompleted()，onError()和onSubscribe()
 *
 *
 * subscribe 调用链:
 * 1.
 * onSubscribe 调用栈:
 *
 * 1. subscribeActual 触发 new RangeSubscription(s, start, end)
 * 1. 从publisher开始传递到subscriber [downstream 是下一个subscriber, 一般同时也是subscription]
 */
public class RxJavaObservables {


    private static final Logger logger = LoggerFactory.getLogger(RxJavaObservables.class);

    private static final Observer<String> Observer = new Observer<String>() {
        @Override
        public void onSubscribe(Disposable d) {
            logger.debug("");
        }

        @Override
        public void onNext(String s) {
            logger.debug("{}", s);
        }

        @Override
        public void onError(Throwable e) {
            logger.error("", e);
        }

        @Override
        public void onComplete() {
            logger.debug("");
        }
    };

    private static void action(){
        AtomicInteger increaseCounter = new AtomicInteger(0);
        Action action = () -> logger.debug("Jim-fromAction {}", increaseCounter.incrementAndGet());
        Completable completable = Completable.fromAction(action);
        completable.subscribe(new CompletableObserver(){

            @Override
            public void onSubscribe(Disposable d) {
                logger.debug("{}", increaseCounter.incrementAndGet());
            }

            @Override
            public void onComplete() {
                logger.debug("{}", increaseCounter.incrementAndGet());
            }

            @Override
            public void onError(Throwable e) {
                logger.debug("{}", increaseCounter.get(), e);
            }
        });
    }

    private static void fromFuture(){
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Future<String> future = executor.schedule(() -> "Jim-fromFuture", 1, TimeUnit.SECONDS);
        Observable<String> observable = Observable.fromFuture(future);
        observable.safeSubscribe(Observer);
    }

    private static void generate(){
        int startValue = 1;
        Flowable<Integer> flowable = Flowable.generate(() -> startValue, (s, emitter) -> {
            ThreadUtil.sleep(TimeUnit.SECONDS.toMillis(1));
            int nextValue = s + 1;
            emitter.onNext(nextValue);
            return nextValue;
        });
        flowable.subscribe(value -> logger.debug("generate:{}", value));
    }

    private static void create(){
        /***
         * Observable最原始的创建方式，创建出一个最简单的事件流，可以使用发射器发射特定的数据类型
         */
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        ObservableOnSubscribe<String> handler = emitter -> {
            Future<Object> future = executor.schedule(() -> {
                emitter.onNext("Jim-1");
                emitter.onNext("Jim-2");
                emitter.onComplete();
                return null;
            }, 1, TimeUnit.SECONDS);

            emitter.setCancellable(() -> future.cancel(false));
        };

        Observable<String> observable = Observable.create(handler);

        observable.subscribe(Observer);

        ThreadUtil.sleep(TimeUnit.SECONDS.toMillis(5));
        executor.shutdown();
    }

    /**
     * do not create the Observable until the observer subscribes, and create a fresh Observable for each observer
     */
    private static void defer(){
        Observable<Long> observable = Observable.defer(() -> {
            long time = System.currentTimeMillis();
            return Observable.just(time);
        });
        observable.subscribe(time -> logger.debug("{}", time));
        ThreadUtil.sleep(TimeUnit.SECONDS.toMillis(1));
        observable.subscribe(time -> logger.debug("{}", time));
    }

    /**
     * Concurrency within a flow
     * However, the lambda v -> v * v doesn't run in parallel for this flow; it receives the values 1 to 10 on the same computation thread one after the other.
     */
    private static void blockingSubscribe(){
        Flowable.range(1, 10)
                .observeOn(Schedulers.computation(), false, 2)
                .map(v -> Thread.currentThread().getName() + ". " + v)
                .blockingSubscribe( value -> logger.debug("{}", value), 2);
    }

    /**
     * Parallel processing
     * Practically, parallelism in RxJava means running independent flows and merging their results back into a single flow.
     * The operator flatMap does this by first mapping each number from 1 to 10 into its own individual Flowable, runs them and merges the computed squares.
     */
    private static void blockingSubscribeV2(){
        Flowable.range(1, 10)
                .flatMap( v -> Flowable.just(v)
                        .subscribeOn(Schedulers.computation())
                        .map(w -> Thread.currentThread().getName() + ". " + w)
                , false,  2 , 2)
                .blockingSubscribe( value -> logger.debug("{}", value), 2);
    }

    public static void main(String[] args) throws IOException {
        Log4jUtil.initilizeV2();
        create();
    }
}
