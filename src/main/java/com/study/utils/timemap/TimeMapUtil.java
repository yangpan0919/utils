package com.study.utils.timemap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TimeMapUtil {
    //设计一个存放定时任务的节点管理器，对需要销毁的元素进行销毁
    /**
     * 基本策略，几个可以重复使用的定时器，,
     * 一个排序时间的集合 以秒为单位
     * 一个map 时间对应需要消除的key值
     */

    public static ExecutorService executorService = Executors.newScheduledThreadPool(2);

    private static LinkedList<Long> timeLink = new LinkedList<Long>();

    public static void main(String[] args) {

        timeLink.add(3L);
        timeLink.add(1L);
        timeLink.add(5L);
        timeLink.add(2L);
        timeLink.add(4L);
        timeLink.add(0L);
        timeLink.add(2, 100L);
//        Collections.sort(timeLink);

        System.out.println(timeLink);
    }

    public static void putTime(Long time) {


    }

    public static int findIndex(Long time, int start, int end) {
        if (start == end) {
            return end;
        }
        int i = ((start + end) / 2) + 1;
        timeLink.get(i);
        return 0;
    }


}
