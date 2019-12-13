package com.study.utils.component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class LockObject implements Comparable<LockObject> {

    private int level;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public LockObject(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean getPassport(Test test) {
        try {
            countDownLatch.await(20, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();

        }
        test.getPassport(this);


        return false;
    }

    @Override
    public int compareTo(LockObject o) {
        return this.level = o.getLevel();
    }

    public static void main(String[] args) {

    }
}
