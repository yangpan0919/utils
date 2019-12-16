package com.study.utils.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 锁机制，代码按照优先级执行
 */
public class Model {


    public static void main(String[] args) {
        Model model = new Model();
        model.test();
        model.test1();
        System.out.println(model.getaBoolean());
        System.out.println(model.getTemp());
        System.out.println(model.isFlag());
        model.test();
    }

    List<LockObject> temp = new ArrayList<>();

    AtomicBoolean aBoolean = new AtomicBoolean(false);

    long preTime;

    volatile boolean flag = true;

    public List<LockObject> getTemp() {
        return temp;
    }

    public AtomicBoolean getaBoolean() {
        return aBoolean;
    }

    public boolean isFlag() {
        return flag;
    }

    public void test1() {
        if (getPassport(new LockObject(-1))) {
            System.out.println(System.currentTimeMillis() + "执行的ID为:" + -1);
            try {
                Thread.sleep(2L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            returnPassport();
        } else {
            System.out.println("获取失败的ID为:" + -1);
        }
    }

    public void test() {

        int count = 100;
        CountDownLatch countDownLatch = new CountDownLatch(count);

        for (int i = 0; i < count; i++) {

            int finalI = i;
            new Thread(() -> {

                countDownLatch.countDown();
                if (finalI == 1) {
                    System.out.println(System.currentTimeMillis());
                }
                LockObject lockObject = new LockObject(finalI);
                if (getPassport(lockObject)) {
                    System.out.println(System.currentTimeMillis() + "执行的ID为:" + finalI);
                    try {
                        Thread.sleep(2L);
                        if (finalI == 4) {
                            return;
//                            Thread.sleep(3000L);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    returnPassport();
                } else {
                    System.out.println("获取失败的ID为:" + finalI);
                }
            }).start();
        }

        try {
            Thread.sleep(11000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    private synchronized void returnPassport() {
        if (aBoolean.get()) {
            if (temp.size() > 0) {
                flag = false;
                //从集合中获取，下一个对象
                if (aBoolean.compareAndSet(true, false)) {
                    temp.get(0).getCountDownLatch().countDown();
                    temp.remove(0);
                } else {
                    System.out.println("锁异常");
                }
            } else {
                if (!aBoolean.compareAndSet(true, false)) {
                    System.out.println("锁异常111");
                }
            }
        } else {
            System.out.println("错误");
        }
    }


    private boolean getPassport(LockObject level) {
        if (flag) {
            if (aBoolean.compareAndSet(false, true)) {
                preTime = System.currentTimeMillis();
                return true;
            }
        }
        addSort(level);
        return getPassport2(level);

    }

    private boolean getPassport2(LockObject level) {
        try {

            boolean await = level.getCountDownLatch().await(3, TimeUnit.SECONDS);
            if (await && aBoolean.compareAndSet(false, true)) {
                preTime = System.currentTimeMillis();
                flag = true;
                return true;
            } else {
                remove(level);
                System.out.println("还是没有获取到锁");
            }
        } catch (InterruptedException e) {
            //超时，或线程中断
            System.out.println("超时，或线程中断");
        }

        return false;

    }

    /**
     * 超时等情况进行移除list中的锁对象
     *
     * @param level
     */
    private synchronized void remove(LockObject level) {
        temp.remove(level);
        long now = System.currentTimeMillis();
        if (now - preTime > 10000L) { //大于十秒，还没有释放锁，有可能锁没有被释放
            returnPassport();
        }

    }

    private synchronized void addSort(LockObject level) {
        temp.add(level);
        Collections.sort(temp);

    }
}
