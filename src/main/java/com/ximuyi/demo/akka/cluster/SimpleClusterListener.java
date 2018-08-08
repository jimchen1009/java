package com.ximuyi.demo.akka.cluster;

import akka.actor.AbstractActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class SimpleClusterListener extends AbstractActor {
    LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    Cluster cluster = Cluster.get(getContext().getSystem());


    @Override
    public void preStart() {
        cluster.subscribe(getSelf(), ClusterEvent.initialStateAsEvents(),
                ClusterEvent.MemberEvent.class, ClusterEvent.UnreachableMember.class);
    }

    //re-subscribe when restart
    @Override
    public void postStop() {
        cluster.unsubscribe(getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(ClusterEvent.MemberUp.class, mUp -> {
                log.info("Member is Up: {}", mUp.member());
            })
            .match(ClusterEvent.UnreachableMember.class, mUnreachable -> {
                log.info("Member detected as unreachable: {}", mUnreachable.member());
            })
            .match(ClusterEvent.MemberRemoved.class, mRemoved -> {
                log.info("Member is Removed: {}", mRemoved.member());
            })
            .match(ClusterEvent.MemberEvent.class, message -> {
                // ignore
            })
            .build();
    }
}
