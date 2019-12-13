package com.study.utils.test;

import sun.misc.VM;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) throws InterruptedException {

        System.out.println("maxDirectMemorym默认大小" + VM.maxDirectMemory() / 1024 / 1024 + "MB");

        //分配最大本地内存是1000000MB
//        int i = 1000000 * 1024 * 1024;
//        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(i);
//        System.out.println(byteBuffer.toString());
        List list = new ArrayList();

        try {
            System.out.println("开始");
            for (int i = 0; i < 100; i++) {
                test("qwe", i,list);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
//        test("qwe", 0);
//        test("qwe", 0);
//        test("qwe", 0);
        Thread.sleep(100000000L);


    }

    public static String test(String s, int count,List list) throws InterruptedException {
        System.out.println(Integer.MAX_VALUE);
//        int i = 1000000 * 1024 * 1024;
//        System.out.println(i);
        System.out.println("int最大内存" + Integer.MAX_VALUE/ 1024 / 1024 + "MB");
//        Thread.sleep(10000L);
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(Integer.MAX_VALUE/5);

//        int _1MB= 1024 * 1024;
//        byte[] a1, a2, a3, a4;
//        a1 = new byte[2 * _1MB];
//        a2 = new byte[2 * _1MB];
//        a3 = new byte[2 * _1MB];
//        a4 = new byte[2 * _1MB];
//        list.add( new byte[100 * _1MB]);
        int i1 = ++count;
        System.out.println(i1);
        return test(s, i1,list);
//        return "s";
    }


}
