package com.ximuyi.demo.java.skip;

import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicLong;

public class SkipMain {

    
    public static void main(String[] args){
        /***
         * Comparator<SkipValue> comparator 比较的值一定是唯一的，不能重复~
         */
        Comparator<SkipValue> comparator = Comparator.comparingLong(SkipValue::index);
        ConcurrentSkipListSet<SkipValue> skipListSet = new ConcurrentSkipListSet<>(comparator);
        int count = 10;
        for (int i = 0; i < count; i++) {
            SkipValue skipValue = new SkipValue(0);
            if (i < count / 2){
                boolean success = skipListSet.add(skipValue);
                System.out.println("add " + i + " :" + success);
            }
            else {
                boolean success = skipListSet.remove(skipValue);
                System.out.println("remove " + i + " :" + success);
            }
        }
    }

    private static final class SkipValue{

        private static final AtomicLong aLong = new AtomicLong();

        private final int index;
        private final long uniqueId;

        public SkipValue(int index) {
            this.index = index;
            this.uniqueId = aLong.incrementAndGet();
        }

        public int index() {
            return index;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SkipValue skipValue = (SkipValue) o;
            return index == skipValue.index && uniqueId == skipValue.uniqueId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(index, uniqueId);
        }
    }
}
