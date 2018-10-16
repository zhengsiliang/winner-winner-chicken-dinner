package com.lingshou.wwcd.worker;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MyThread extends FatherThread {
    //todo 实现你的线程, 做完一切事情后, call notifyFinish();
    char printChar;
    int flag;
    int threadCnt;
    int printTimes;
    Condition selfCondition;
    Condition nextCondition;
    ReentrantLock lock;


    public MyThread(char printChar, int flag, int threadCnt, int printTimes, StringBuffer stringBuffer, CountDownLatch countDownLatch, ReentrantLock lock, Condition selfCondition, Condition nextCondition) {
        this.printChar = printChar;
        this.flag = flag;
        this.threadCnt = threadCnt;
        this.printTimes = printTimes;
        this.stringBuffer = stringBuffer;
        this.countDownLatch = countDownLatch;
        this.selfCondition = selfCondition;
        this.nextCondition = nextCondition;
        this.lock = lock;
    }


    private void notifyFinish() {
        countDownLatch.countDown();
    }

    static volatile int printThreadFlag = 0;

    @Override
    public void run() {
        int count = 0;
        while (count < printTimes) {
            lock.lock();
            try {
                while (printThreadFlag % threadCnt != flag) {
                    selfCondition.await();
                }
                stringBuffer.append(printChar);
                count++;
                printThreadFlag++;
                nextCondition.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
//        System.out.println(System.currentTimeMillis());
        notifyFinish();
    }
}
