package com.study.utils.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class LockObject implements Comparable<LockObject> {

    private int level;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public LockObject(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }


    @Override
    public int compareTo(LockObject temp) {
        return temp.getLevel() - this.getLevel();
    }

    @Override
    public String toString() {
        return "LockObject{" +
                "level=" + level +
                '}';
    }

    public static void main(String[] args) throws InterruptedException {
        LockObject lockObject = new LockObject(1);
        boolean await = lockObject.getCountDownLatch().await(3, TimeUnit.SECONDS);
        System.out.println(await);

    }
}
