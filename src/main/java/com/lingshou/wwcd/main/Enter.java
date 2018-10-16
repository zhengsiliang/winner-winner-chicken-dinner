package com.lingshou.wwcd.main;

import com.lingshou.wwcd.builder.MyThreadBuilder;
import com.lingshou.wwcd.worker.MyThread;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Enter {

    private static int threadCnt = 5;
    private static int printTimes = 100000;

    private static long maxWaitTime = printTimes / 10;
    private static int testTimes = 10;
    private static String needStr;

    public static void main(String[] args) {
        needStr = initNeedStr();
        long sumTime = 0L;
        for (int i = 0; i < testTimes; i++) {
            sumTime += handleAndMetric(() -> execOneTime(threadCnt, printTimes, maxWaitTime), i);
        }

        System.out.println(String.format("threadCnt:%s, printTimes:%s, testTimes:%s, timeSum:%s", threadCnt, printTimes, testTimes, sumTime));
    }

    private static <T> T handleAndMetric(Supplier<T> supplier, int testRounds) {
        long start = System.currentTimeMillis();
        System.out.println(String.format("testRounds:%s, time start:%s", testRounds, start));
        T t = supplier.get();
        System.out.println(String.format("testRounds:%s, end,  last:%s ms", testRounds, (System.currentTimeMillis() - start)));
        return t;
    }

    private static long execOneTime(int threadCnt, int printTimes, long maxWaitTime) {

        StringBuffer stringBuffer = new StringBuffer();

        CountDownLatch countDownLatch = new CountDownLatch(threadCnt);

        MyThread[] threads = MyThreadBuilder.build(threadCnt, stringBuffer, printTimes, countDownLatch);

        if (threads == null || threads.length != threadCnt) {
            throw new RuntimeException("线程构造不正确!");
        }

        long start = System.currentTimeMillis();

        Stream.of(threads).forEach(Thread::start);

        try {
            countDownLatch.await(maxWaitTime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException("等待超时!");
        }
        System.out.println("执行耗时" + (System.currentTimeMillis() - start) + "ms");
        doCheck(threadCnt, printTimes, stringBuffer);

        return System.currentTimeMillis() - start;
    }

    private static void doCheck(int threadCnt, int printTimes, StringBuffer stringBuffer) {
        long start = System.currentTimeMillis();

        String targetStr = stringBuffer.toString();

//        String needStr = "";
//        for (int i = 0; i < printTimes; i++) {
//            needStr += getStrByThreadCnt(threadCnt);
//        }

        if (!Objects.equals(targetStr, needStr)) {
            System.out.println(targetStr);
            System.out.println(needStr);
            throw new RuntimeException("结果不正确!" + targetStr);
        }
//        System.out.println("check耗时"+(System.currentTimeMillis()-start)+"ms");
    }

    private static String initNeedStr() {
        StringBuffer needStr = new StringBuffer();
        String strItem = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(0, threadCnt);
        for (int i = 0; i < printTimes; i++) {
            needStr.append(strItem);
        }
        return needStr.toString();
    }

    private static String getStrByThreadCnt(int threadCnt) {
        if (threadCnt <= 0 || threadCnt > 26) throw new RuntimeException("invalid:" + threadCnt);

        String str = "";
        for (int i = 0; i < threadCnt; i++) {
            str += getCharByCnt(i);
        }

        return str;
    }

    private static char getCharByCnt(int cnt) {
        return (char) ('A' + cnt % 26);
    }
}
