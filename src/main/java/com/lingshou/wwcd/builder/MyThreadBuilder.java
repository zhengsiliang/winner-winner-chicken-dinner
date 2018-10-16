package com.lingshou.wwcd.builder;

import com.lingshou.wwcd.worker.MyThread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MyThreadBuilder {
    /**
     * 构建你的线程们
     *
     * @param threadCnt    线程总个数
     * @param stringBuffer 打印到此流中
     * @param printTimes   打印总次数
     * @return
     */
    public static MyThread[] build(int threadCnt, StringBuffer stringBuffer, int printTimes, CountDownLatch countDownLatch) {

        //todo create your own threads
        ReentrantLock lock = new ReentrantLock();
        Condition[] conditions = new Condition[threadCnt];
        for (int i = 0; i < threadCnt; i++) {
            conditions[i] = lock.newCondition();
        }

        MyThread[] threads = new MyThread[threadCnt];
        for (int i = 0; i < threadCnt; i++) {
            char printChar = (char) ('A' + i);
            threads[i] = new MyThread(printChar, i, threadCnt, printTimes, stringBuffer, countDownLatch, lock, conditions[i % threadCnt], conditions[(i + 1) % threadCnt]);
        }

        return threads;
    }


}
