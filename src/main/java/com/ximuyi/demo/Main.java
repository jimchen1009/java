    package com.ximuyi.demo;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by chenjingjun on 2018-04-04.
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static abstract class Cls<T>{
        private Class<T> cls ;

        public Cls(Class<T> cls) {
            this.cls = cls;
        }

        public void function(Object object){
            if (object.getClass().isAssignableFrom(cls)){
                print(cls.cast(object));
            }
        }

        public abstract void print(T value);
    }

    private static class Obj extends Cls<Integer> {

        public Obj() {
            super(Integer.class);
        }

        @Override
        public void print(Integer value) {

        }
    }

    public static void main(String[] args) throws InterruptedException {
       System.out.println( Long.toBinaryString(-1L));
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        System.out.println(calendar.getTime());
        for (int i = 1; i <= 7; i++) {
            calendar.set(Calendar.DAY_OF_WEEK, i);
            System.out.println(calendar.getTime());
        }
        BasicThreadFactory factory = new BasicThreadFactory.Builder()
                .namingPattern("CacheScheduler-%d")
                .uncaughtExceptionHandler((t, e) -> logger.error("", e))
                .daemon(true)
                .build();
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, factory);
        executor.scheduleWithFixedDelay(()-> {
            logger.debug("UnsupportedOperationException");
        }, 2, 1, TimeUnit.MILLISECONDS);

        Thread.sleep(TimeUnit.HOURS.toMillis(10));

        valueOf("zookeeper://root:000000@10.17.2.68:2181,10.17.2.68:2182,10.17.2.68:2183");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            builder.append(String.format("TRUNCATE `guild_name%d`;", i)).append("\n");
        }
        System.out.println(builder.toString());
        String[] allWords = new String[]{
                "game", "magic", "puzzle", "cube", "knife", "pliers", "screwdriver", "wrench", "axe", "saw",
                "scissors", "chisel", "hammer", "brush", "jersey", "jacket", "skirt", "robe", "tights", "shorts",
                "pants", "skorts", "dress", "hoodie", "T-shirt", "shirt", "bra", "boots", "flats", "pumps",
                "sandals", "slippers", "flip", "flops", "boat", "shoe", "heels", "oxfords", "loafer", "watch",
                "quartz", "mechanical", "digital", "sports", "dual", "display", "cover", "tree", "shoelace", "cutting",
                "crossbody", "bag", "backpack", "wallet", "travel", "briefcase", "pack", "waist", "shoulder", "clutches",
                "mat", "letter", "opener", "utility", "glue", "stick", "liquid", "adhesive", "tape", "dispenser",
                "wristlet", "cosmetic", "staple", "stapler", "glue", "clips", "chair", "computer", "laptop", "conference",
                "magazine", "rack", "sofa", "reception", "bookend", "card", "file", "tray", "mobile", "phone",
                "cable", "screen", "protector", "power", "bank", "dust", "plug", "adapter", "case", "coffee",
                "maker", "blender", "curling", "iron", "humidifier", "hair", "trimmer", "eye", "shadow", "eyebrow",
                "enhancer", "eyeliner", "eyelash", "lipstick", "bedding", "pillow", "towel", "cushion", "carpet", "rug",
        };

        Set<Character> allLetters = new HashSet<>();
        for (String allWord : allWords) {
            for (int i = 0; i < allWord.length(); i++) {
                allLetters.add(allWord.charAt(i));
            }
        }
        ArrayList<Character> allCharacters = new ArrayList<>(allLetters);
        allCharacters.sort(Comparator.comparingInt(c0 -> c0));

        Set<Character> abcdefLetters = new HashSet<>();
        abcdefLetters.addAll(Arrays.asList('a', 'b', 'c', 'e', 'e', 'f'));
        Map<Integer, Set<Character>> groupLetter = new HashMap<>();
        for (int i = 0; i < allCharacters.size(); i++) {
            char ch = allCharacters.get(i);
            if (ch == 'T'){
                ch = 't';
            }
            if (abcdefLetters.contains(ch)){
                continue;
            }
            int key = i % 5;
            Set<Character> letters = groupLetter.computeIfAbsent(key, (k) -> new HashSet<>());
            letters.add(ch);
        }
        Map<Integer, Set<String>> worldMap = new HashMap<>();

        for (Map.Entry<Integer, Set<Character>> entry : groupLetter.entrySet()) {
            ArrayList<Character> characters = new ArrayList<>(entry.getValue());
            characters.addAll(abcdefLetters);
            characters.sort(Comparator.comparingInt(c0 -> c0));
            builder = new StringBuilder('\n');
            for (Character character : characters) {
                builder.append(character).append(" ");
            }
            builder.append("\n");
            Set<String> worlds = worldMap.computeIfAbsent(entry.getKey(), (k) -> new HashSet<>());
            for (String eachWorld : allWords) {
                int count = 0;
                for (int i = 0; i < eachWorld.length(); i++) {
                    char ch = eachWorld.charAt(i);
                    if (ch == 'T'){
                        ch = 't';
                    }
                    if (abcdefLetters.contains(ch) || entry.getValue().contains(ch)){
                        count++;
                    }
                    if (count == 3){
                        worlds.add(eachWorld);
                        break;
                    }
                }
            }
            ArrayList<String> worldList = new ArrayList<>(worldMap.get(entry.getKey()));
            Collections.sort(worldList, Collator.getInstance(Locale.ENGLISH));
            for (String world : worldList) {
                builder.append(world).append(", ");
            }
            System.out.println(builder.toString());
        }


        int intValue = 1;
        Set<Long> longValues = Collections.singleton(1L);
        if (longValues.contains(intValue)) {
            System.out.println("yes");
        }
        String key = "ALTER TABLE `guild_name%d` CHANGE COLUMN `Name` `Name` VARCHAR(15) NOT NULL COLLATE 'utf8mb4_unicode_ci';";
        for (int i = 0; i < 100; i++) {
            System.out.println(String.format(key, i));
        }

        HashMap<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            map.put(i, i);
        }
        for (Iterator<Map.Entry<Integer, Integer>> iterator = map.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<Integer, Integer> entry = iterator.next();
            if (entry.getKey() == 2){
                iterator.remove();
            }
        }
        /***
         * -XX:CompileThreshold=5000
         */
        final int count = 500000000;
        long current = 0;
        Object value = new Obj();


        int times = 2;
        while (times-- > 0) {
            current = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                if (value.getClass().isAssignableFrom(Obj.class)) {
                }
            }
            System.out.println("第" + times + "次isAssignableFrom耗时：" + (System.currentTimeMillis() - current));

            current = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                if (value instanceof Obj) {
                }
            }
            System.out.println("第" + times + "次instanceof耗时：" + (System.currentTimeMillis() - current));
        }
    }

    public static void valueOf(String url) {
        if (url != null && (url = url.trim()).length() != 0) {
            String protocol = null;
            String username = null;
            String password = null;
            String host = null;
            int port = 0;
            String path = null;
            Map<String, String> parameters = null;
            int i = url.indexOf("?");
            if (i >= 0) {
                String[] parts = url.substring(i + 1).split("\\&");
                parameters = new HashMap();
                String[] var10 = parts;
                int var11 = parts.length;

                for(int var12 = 0; var12 < var11; ++var12) {
                    String part = var10[var12];
                    part = part.trim();
                    if (part.length() > 0) {
                        int j = part.indexOf(61);
                        if (j >= 0) {
                            parameters.put(part.substring(0, j), part.substring(j + 1));
                        } else {
                            parameters.put(part, part);
                        }
                    }
                }

                url = url.substring(0, i);
            }

            i = url.indexOf("://");
            if (i >= 0) {
                if (i == 0) {
                    throw new IllegalStateException("url missing protocol: \"" + url + "\"");
                }

                protocol = url.substring(0, i);
                url = url.substring(i + 3);
            } else {
                i = url.indexOf(":/");
                if (i >= 0) {
                    if (i == 0) {
                        throw new IllegalStateException("url missing protocol: \"" + url + "\"");
                    }

                    protocol = url.substring(0, i);
                    url = url.substring(i + 1);
                }
            }

            i = url.indexOf("/");
            if (i >= 0) {
                path = url.substring(i + 1);
                url = url.substring(0, i);
            }

            i = url.indexOf("@");
            if (i >= 0) {
                username = url.substring(0, i);
                int j = username.indexOf(":");
                if (j >= 0) {
                    password = username.substring(j + 1);
                    username = username.substring(0, j);
                }

                url = url.substring(i + 1);
            }

            if (!url.contains(",")) {
                i = url.indexOf(":");
                if (i >= 0 && i < url.length() - 1) {
                    port = Integer.parseInt(url.substring(i + 1));
                    url = url.substring(0, i);
                }
            }

            if (url.length() > 0) {
                host = url;
            }
        } else {
            throw new IllegalArgumentException("url == null");
        }
    }
}
