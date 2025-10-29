package com.brezze.share.utils.common.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolUtil {

    /**
     * 业务处理异步线程池，线程池参数可以根据您的业务特点调整；或者您也可以用其他异步方式处理接收到的消息
     */
    private final static ExecutorService executorService = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() * 2, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(50000));

    public static ExecutorService getExecutorService() {
        return executorService;
    }

    public static void main(String[] args) {
        System.out.print("最大内存： ");
        System.out.println(Runtime.getRuntime().maxMemory() / 1024 / 1024 + "MB");
        System.out.print("可用内存： ");
        System.out.println(Runtime.getRuntime().freeMemory() / 1024 / 1024 + "MB");
        System.out.print("已使用内存： ");
        System.out.println(Runtime.getRuntime().totalMemory() / 1024 / 1024 + "MB");
        for (int i = 0; i < 10; i++) {
            byte[] b = new byte[1 * 1024 * 1024];
        }
    }
}
