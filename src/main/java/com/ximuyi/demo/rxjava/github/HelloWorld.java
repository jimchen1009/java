package com.ximuyi.demo.rxjava.github;

import rx.*;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by chenjingjun on 2017-12-19.
 */
public class HelloWorld {
    private static Observable<String> observable = Observable.create(elem->{
        elem.onNext("Hello RxJava - observable");
    }, Emitter.BackpressureMode.NONE);

    private static Subscriber<String> subscriber = new Subscriber<String>() {
        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable throwable) {

        }

        @Override
        public void onNext(String s) {
            System.out.println(s);
        }
    };

    private static Action1<String> action = new Action1<String>() {
        @Override
        public void call(String s) {
            System.out.println("Action1::onCompleted " + s);
        }
    };

    public static void main(String[] args) {
        Observable.from(new String[]{"url1", "url2", "url3"}).filter( s-> s != null).take(1).doOnNext(s -> s.length()).
                subscribe(url -> System.out.println(url));

        observable = observable.observeOn(Schedulers.io()).map( s -> Thread.currentThread().getName() + "：" + s);
        Subscription subscription = observable.subscribe(action);
        System.out.println("isUnsubscribed = " + subscription.isUnsubscribed());
        observable.subscribe(subscriber);
    }
}
