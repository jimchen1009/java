package com.ximuyi.demo.guava;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalListeners;
import com.google.common.cache.RemovalNotification;
import com.google.common.cache.Weigher;
import com.google.common.collect.BiMap;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ForwardingList;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.PeekingIterator;
import com.google.common.util.concurrent.AbstractIdleService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BasicUtils {

    private static final Random ramdon = new Random();

    public static void main(String[] args) throws Exception {
//        optioanal();
//        predictions();
//        objects();
//        comparator();
//        immutable();
        cache();
//        future();
//        service();
//        string();
    }

    private static void string(){
        Joiner joiner = Joiner.on("; ").skipNulls();
        System.out.println(joiner.join(Arrays.asList("A", null, "V")));
        Iterable<String> strings = Splitter.on(", ").trimResults().limit(2).split("20,56, 50, 55");
        System.out.println(strings);
    }

    private static void service(){
        AbstractIdleService service = new AbstractIdleService() {
            @Override
            protected void startUp() throws Exception {
                //名字被强制修改过
                System.out.println(Thread.currentThread().getName());
            }

            @Override
            protected void shutDown() throws Exception {
                //名字被强制修改过
                System.out.println(Thread.currentThread().getName());
            }
        };
        service.startAsync().awaitRunning();
        service.stopAsync().awaitTerminated();
    }

    private static void future(){

    }

    private static void cache() throws Exception {
        int mapCount = 5;
        RemovalListener<Integer, String> removalListener = new RemovalListener<Integer, String>() {
            @Override
            public void onRemoval(RemovalNotification<Integer, String> notification) {
                System.out.println(Thread.currentThread().getName() + " " + notification.getCause());
            }
        };
        Weigher<Integer, String> weigher = new Weigher<Integer, String>() {
            @Override
            public int weigh(Integer key, String value) {
                return key % mapCount;
            }
        };
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        removalListener = RemovalListeners.asynchronous(removalListener,  executor);
        LoadingCache<Integer, String> cache = CacheBuilder.newBuilder()
//                .maximumSize(5)
                .expireAfterAccess(2, TimeUnit.MINUTES)
                .maximumWeight(mapCount)
                .weigher(weigher)
                .removalListener(removalListener)
                .recordStats()
                .build(new CacheLoader<Integer, String>() {
                    @Override
                    public String load(Integer key) throws Exception {
                        return key + "_" + ramdon.nextInt(50);
                    }
                });

        for (int i = 0; i < mapCount; i++) {
            cache.get(i);
        }
        cache.get(11, new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "11_" + ramdon.nextInt(50);
            }
        });
        cache.invalidate(11);
        ImmutableMap<Integer, String> cacheAll = cache.getAll(Arrays.asList(1, 2, 3, 4));
        System.out.println(cacheAll);
        ConcurrentMap<Integer, String> asMap = cache.asMap();
        asMap.put(11, "Okay");
        System.out.println(asMap);
        System.out.println(cache.stats());
        System.in.read();
    }

    private static void collections(){
        //装饰者
        ForwardingList<Integer> forwardingList = new ForwardingList<Integer>() {
            private final List<Integer> valueList = new ArrayList<>();
            @Override
            protected List<Integer> delegate() {
                return valueList;
            }
        };
        ArrayList<Integer> arrayList = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            forwardingList.add(i);
            arrayList.add(i);
        }
        PeekingIterator<Integer> peekingIterator = Iterators.peekingIterator(arrayList.iterator());
    }

    private static void immutable(){
        ImmutableSet<Integer> immutableSet = ImmutableSet.of(1, 2, 3,5);
        List<Integer> valueList = Arrays.asList(1, 5, 20, 3);
        immutableSet = ImmutableSet.copyOf(valueList);

        HashMultiset<Integer> multiset = HashMultiset.create();
        for (int i = 0; i < 50; i++) {
            multiset.add(ramdon.nextInt(50));
        }
        System.out.println(multiset.toString());
        HashBasedTable<Integer, Integer, Integer> basedTable = HashBasedTable.create();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                basedTable.put(i, j, 1);
            }
        }
        System.out.println(basedTable);
        BiMap<String, Integer> userId = HashBiMap.create();
        userId.put("Jim", 100);
        System.out.println(userId.inverse().get(100));
    }

    private static void comparator(){
        ArrayList<BasicCls> basicCls = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            basicCls.add(new BasicCls(new Random().nextInt(10), String.valueOf(i)));
        }
        Ordering<BasicCls> ordering = Ordering.from(BasicCls.comparator).compound(BasicCls.comparator);
        basicCls.sort(ordering.reverse());
        System.out.println(basicCls);
        basicCls.sort(ordering);
        System.out.println(basicCls);
    }

    private static final void objects(){
        BasicCls cls0 = new BasicCls(0, "0");
        BasicCls cls00 = new BasicCls(0, "0");
        BasicCls cls1 = new BasicCls(1, "1");
        System.out.println( Objects.equal(cls0, cls00));
        System.out.println( Objects.equal(cls0, cls1));
        System.out.println( Objects.equal("b", "a"));
        System.out.println( Objects.equal(null, null));
    }

    private static final void optioanal(){
        Integer value = null;
        Optional<Integer> optional = Optional.fromNullable(value);
        System.out.println(optional.or(1));
        System.out.println(optional.isPresent());
        System.out.println(optional.orNull());

        List<Integer> valueList0 = Arrays.asList(1, 2, null, 3);
        List<Optional<Integer>> optionals = valueList0.stream().map(v -> Optional.fromNullable(v)).collect(Collectors.toList());
        Iterable<Integer> integers = Optional.presentInstances(optionals);
        StringBuilder builder = new StringBuilder();
        for (Integer integer : integers) {
            builder.append(integer).append(" ");
        }
        System.out.println(builder.toString());
    }

    private static final void predictions(){
        Object object = new Object();
        Preconditions.checkArgument(true, "");
        Preconditions.checkState(true, "%s", "error");
        Preconditions.checkNotNull(object, "%s", 1); //template must use symbol %s
        List<Integer> valueList1= Arrays.asList(1, 2, 3);
        Preconditions.checkPositionIndex(0, valueList1.size());
        Preconditions.checkPositionIndexes(0, 2, valueList1.size());
    }

    private static class BasicCls {
        public static final Comparator<BasicCls> comparator = new Comparator<BasicCls>() {
            @Override
            public int compare(BasicCls o1, BasicCls o2) {
                return ComparisonChain.start().compare(o1.id, o2.id).compare(o1.name, o2.name).result();
            }
        };
        public final int id;
        public final String name;

        public BasicCls(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BasicCls basicCls = (BasicCls) o;
            return Objects.equal(id, basicCls.id) && Objects.equal(name, basicCls.name);
        }

        @Override
        public int hashCode() {
//            return Objects.hashCode(this);// 这个写法很有问题
            return Objects.hashCode(id, name);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this).add("id", id).add("name", name).toString();
        }
    }
}
