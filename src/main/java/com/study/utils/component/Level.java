package com.study.utils.component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Level implements Comparable<Level> {

    private int level;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public Level(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }


    @Override
    public int compareTo(Level temp) {
        return temp.getLevel() - this.getLevel();
    }

    @Override
    public String toString() {
        return "LockObject{" +
                "level=" + level +
                '}';
    }

    public static void main(String[] args) throws InterruptedException {
        Level level = new Level(1);
        boolean await = level.getCountDownLatch().await(3, TimeUnit.SECONDS);
        System.out.println(await);

    }
}
