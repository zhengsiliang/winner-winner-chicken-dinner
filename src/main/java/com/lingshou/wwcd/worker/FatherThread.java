package com.lingshou.wwcd.worker;

import java.util.concurrent.CountDownLatch;

public class FatherThread extends Thread{

    /**
     * 标识
     */
    protected String identity;

    /**
     * 公用的打印流
     */
    protected StringBuffer stringBuffer;

    /**
     * 最终完成告知器
     */
    protected CountDownLatch countDownLatch;

}
