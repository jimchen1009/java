package com.jim.demo.util;

import java.util.*;

/**
 * Created by chenjingjun on 2018-02-06.
 */
public class Util {

    public static final Random random = new Random();

    public static void main(String[] args) {
        linkHashMap();
        sortedTree();
        sortedMap();
    }

    private static void linkHashMap(){
        LinkedHashMap<Integer, SortItem> linkHashMap = new LinkedHashMap<>();
        linkHashMap.put(800, SortItem.get(800, "name" + 800));
        for (int i = 0; i < 100; i ++){
            linkHashMap.put(i, SortItem.get(i, "name" + i));
        }
        int count = 0;
        Iterator<Map.Entry<Integer, SortItem>> interator = linkHashMap.entrySet().iterator();
        while(interator.hasNext()){
            System.out.println(interator.next());
            count++;
        }
        System.out.println("总共数量：" + count);
    }

    private static void sortedTree(){
        SortedSet<SortItem> sortedSet = new TreeSet<>((o1, o2) -> o1.id - o2.id);
        for (int i = 0; i < 100; i ++){
            int index = random.nextInt(50);
            sortedSet.add(SortItem.get(index, "name" + index));
        }
        int count = 0;
        System.out.println("第一个元素：" + sortedSet.first());
        System.out.println("后一个元素：" + sortedSet.last());
        Iterator<SortItem> interator = sortedSet.iterator();
        while(interator.hasNext()){
            System.out.println(interator.next());
            count++;
        }
        System.out.println("总共数量：" + count);
    }

    private static void sortedMap(){
        TreeMap<Integer, SortItem> sortedMap = new TreeMap<>((o1, o2) -> o1 - o2);
        sortedMap.subMap(1, 10);
        sortedMap.put(800, SortItem.get(800, "name" + 800));
        for (int i = 0; i < 100; i ++){
            int index = random.nextInt(50);
            sortedMap.put(index, SortItem.get(index, "name" + index));
        }
        int count = 0;
        System.out.println("第一个元素：" + sortedMap.firstEntry());
        System.out.println("后一个元素：" + sortedMap.lastEntry());
        Iterator<Map.Entry<Integer, SortItem>> interator = sortedMap.entrySet().iterator();
        while(interator.hasNext()){
            System.out.println(interator.next());
            count++;
        }
        System.out.println("总共数量：" + count);
    }


    public static class SortItem{
        public final int id;
        public final String name;

        public SortItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public static SortItem get(int id, String name){
            return new SortItem(id, name);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SortItem sortItem = (SortItem) o;

            if (id != sortItem.id) return false;
            return !(name != null ? !name.equals(sortItem.name) : sortItem.name != null);
        }

        @Override
        public int hashCode() {
            int result = id;
            result = 31 * result + (name != null ? name.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
