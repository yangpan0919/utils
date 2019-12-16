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


    public static void main(String[] args) throws InterruptedException {
        Model model = new Model();
//        model.test();
//        System.out.println(model.getaBoolean());
//        System.out.println(model.getTemp());
//        System.out.println(model.isFlag());
        CountDownLatch countDownLatch = new CountDownLatch(2);
        new Thread(() -> {
            countDownLatch.countDown();
            model.test1();
        }).start();
        new Thread(() -> {
            countDownLatch.countDown();
            model.test2();
        }).start();
        Thread.sleep(5000L);

        System.out.println(model.getaBoolean());
//        System.out.println(model.get());
        System.out.println(model.isFlag());
        model.test();
    }

    private void test2() {
        int finalI = 2;
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
    }

    private void test1() {
        int finalI = 1;
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
    }

    List<LockObject> list = new ArrayList<>();

    AtomicBoolean aBoolean = new AtomicBoolean(false);

    long preTime;

    volatile boolean flag = true;


    public AtomicBoolean getaBoolean() {
        return aBoolean;
    }

    public boolean isFlag() {
        return flag;
    }

//    public void test1(int i) {
//        if (getPassport(new LockObject(i))) {
//            System.out.println(System.currentTimeMillis() + "执行的ID为:" + i);
//            try {
//                Thread.sleep(2L);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            returnPassport(i);
//        } else {
//            System.out.println("获取失败的ID为:" + i);
//        }
//    }

    public void test() {

        int count = 100;
        CountDownLatch countDownLatch = new CountDownLatch(count);

        for (int i = 3; i < count; i++) {

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
//
//    private synchronized void returnPassport(int i) {
//        if (aBoolean.get()) {
//            System.out.println("111" + i);
//            if (temp.size() > 0) {
//                System.out.println("1");
//                flag = false;
//                System.out.println(temp);
//                //从集合中获取，下一个对象
//                if (aBoolean.compareAndSet(true, false)) {
//                    System.out.println("3");
//                    temp.get(0).getCountDownLatch().countDown();
//                    temp.remove(0);
//                    if (temp.size() == 0) {
//                        System.out.println("4");
//                        flag = true;
//                    }
//                } else {
//                    System.out.println("锁异常");
//                }
//            } else {//集合里面没有元素
//                System.out.println("2");
//                if (!aBoolean.compareAndSet(true, false)) {
//                    System.out.println("锁异常111");
//                }
//            }
//        } else {
//            System.out.println("错误111");
//        }
//    }

    /**
     * 返回资源后，先处理list中优先级大的对象
     */
    private void listHandler() {
        flag = false;
        //从集合中获取，下一个对象
        if (aBoolean.compareAndSet(true, false)) {
            list.get(0).getCountDownLatch().countDown();
            list.remove(0);
            if (list.size() == 0) {
                flag = true;
            }
        } else {
            System.out.println("锁异常");
        }
    }

    private synchronized void returnPassport() {
        if (aBoolean.get()) {
            if (list.size() > 0) {
                listHandler();
            } else {//集合里面没有元素
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
            //处理并发，1执行了释放完时，2刚好入队了
            if (flag && aBoolean.compareAndSet(false, true)) {
                preTime = System.currentTimeMillis();
                return true;
            }
            boolean await = level.getCountDownLatch().await(3, TimeUnit.SECONDS);
            if (await && aBoolean.compareAndSet(false, true)) {
                preTime = System.currentTimeMillis();
                flag = true;
                return true;
            } else {
                return remove(level);
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
    private synchronized boolean remove(LockObject level) {
        long now = System.currentTimeMillis();
        if (now - preTime > 30000L) { //大于三十秒，还没有释放锁，有可能锁没有被释放,强制直接直接执行
            returnPassport();
            return true;
        }
        System.out.println("超时，还是没有获取到锁");
        list.remove(level);
        return false;
    }

    private synchronized void addSort(LockObject level) {
        list.add(level);
        Collections.sort(list);

    }
}
