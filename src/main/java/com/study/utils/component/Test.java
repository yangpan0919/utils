package com.study.utils.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 让代码按照优先级来执行
 */
public class Test {

    public static void main(String[] args) {

    }

    List<Integer> list = new ArrayList<>();

    AtomicInteger max = new AtomicInteger();
    AtomicInteger count = new AtomicInteger();

    AtomicBoolean aBoolean = new AtomicBoolean(false);

    public List<Integer> getList() {
        return list;
    }

    public void setList(List<Integer> list) {
        this.list = list;
    }

    public AtomicInteger getMax() {
        return max;
    }

    public void setMax(AtomicInteger max) {
        this.max = max;
    }

    public AtomicInteger getCount() {
        return count;
    }

    public void setCount(AtomicInteger count) {
        this.count = count;
    }

    public AtomicBoolean getaBoolean() {
        return aBoolean;
    }

    public void setaBoolean(AtomicBoolean aBoolean) {
        this.aBoolean = aBoolean;
    }

    public List<LockObject> getTemp() {
        return temp;
    }

    public void setTemp(List<LockObject> temp) {
        this.temp = temp;
    }

    public void test() {

        int count = 10;
        CountDownLatch countDownLatch = new CountDownLatch(count);

        for (int i = 0; i < count; i++) {

            int finalI = i;
            new Thread(() -> {

                countDownLatch.countDown();
                if (getPassport(new LockObject(finalI))) {
                    System.out.println(System.currentTimeMillis() + "执行的ID为:" + finalI);
                    try {
                        Thread.sleep(10L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    returnPassport();
                } else {
                    System.out.println("获取失败的ID为:" + finalI);
                }


            }).start();
        }


    }


    public void returnPassport() {
        if (!aBoolean.compareAndSet(true, false)) {
            System.out.println("异常的返回");
        }
    }

    /**
     * 进行排序，同时设置最大的level
     *
     * @param i
     */
    private synchronized void sort(int i) {
        if (i == -1) {
            Collections.sort(list);
            list.remove(0);
            if (list.size() > 0) {
                max.set(list.get(list.size() - 1));
            }
            return;
        }
        list.add(i);
        Collections.sort(list);
        max.set(list.get(list.size() - 1));
    }


    private boolean getPassport(int level, long time) {
        while (true) {
            if (max.compareAndSet(level, -1)) {
                while (true) {
                    if (aBoolean.compareAndSet(false, true)) {
                        System.out.println("-----------------------------------" + level + "-----------------------------------");
                        sort(-1);
                        return true;
                    }
                }

            }
        }
    }


    List<LockObject> temp = new ArrayList<>();
    ConcurrentLinkedQueue<LockObject> queue = new ConcurrentLinkedQueue<>();

    public void returnPassport(LockObject lock) {
        if (!aBoolean.compareAndSet(true, false)) {
            System.out.println("异常的返回");
        }
    }


    public boolean getPassport(LockObject lock) {
        if (aBoolean.compareAndSet(false, true)) {
            if (temp.contains(lock)) {
                opList(lock, false);
            }
            return true;
        } else {
            if (!temp.contains(lock)) {
                opList(lock, true);
            }
            return lock.getPassport(this);
        }
    }

    private synchronized void opList(LockObject lock, boolean add) {
        if (add) {
            temp.add(lock);

        } else {
            temp.remove(0);
        }
    }

    private synchronized int addList(int level) {
        int index = list.size();
        list.add(level);
        return index;
    }
}
