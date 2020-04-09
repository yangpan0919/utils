package com.study.utils.message;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class S9F9Parse2 {

    public static void main(String[] args) throws Exception {

        File file = new File("E:\\application\\WeChat\\WeChat Files\\WeChat Files\\yangpan0919\\FileStorage\\File\\2020-04\\D1500-0033host.txt");

        FileInputStream fileInputStream = FileUtils.openInputStream(file);
        BufferedReader br = null;
        BufferedWriter wr = null;
        InputStreamReader isr = null;
        OutputStreamWriter isw = null;

        List<String> list = new ArrayList<>();


        isr = new InputStreamReader(fileInputStream, "UTF-8");
        br = new BufferedReader(isr);
        String tmpString = "";
        while ((tmpString = br.readLine()) != null) {
            list.add(tmpString);
        }
        TreeMap<Integer, String> map = new TreeMap<>();
        TreeMap<Integer, String> outMap = new TreeMap<>();
        TreeMap<Integer, Integer> lineMap = new TreeMap<>();

        List<Integer> s9f9IndexList = new ArrayList<>();
        List<LocalDateTime> s9f9TimeList = new ArrayList<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (int i = 0; i < list.size(); i++) {
            if ("S9F9 input".equals(list.get(i))) {
                String s = list.get(i - 1);
                String substring = s.substring(s.indexOf("[") + 1, s.indexOf("]"));
                //60秒前
                LocalDateTime parse = LocalDateTime.parse(substring, dateTimeFormatter).minusSeconds(60);
                s9f9TimeList.add(parse);
                s9f9IndexList.add(i - 1);
            }
        }
        if (s9f9TimeList.size() == 0) {
            return;
        }
        LocalDateTime localDateTime = s9f9TimeList.get(0);
        boolean flag = false;
        List<String> strList = new ArrayList<>();
        for (int j = 0; j < s9f9IndexList.get(0); j++) {
            String s = list.get(j);
            if (!flag) {
                if (s.indexOf("[") == 0) {
                    String substring = s.substring(1, s.indexOf("]"));
                    //60秒前
                    LocalDateTime parse = LocalDateTime.parse(substring, dateTimeFormatter);
                    if (parse.isAfter(localDateTime)) {
                        flag = true;
                    }
                }
            }
            if (flag) {
                strList.add(s);
            }
        }
        s9f9ListParse(strList, s9f9IndexList.get(0));
        for (int i = 1; i < s9f9IndexList.size(); i++) {
            localDateTime = s9f9TimeList.get(i);
            flag = false;
            strList = new ArrayList<>();
            for (int j = s9f9IndexList.get(i - 1); j < s9f9IndexList.get(i); j++) {
                String s = list.get(j);
                if (!flag) {
                    if (s.indexOf("[") == 0) {
                        String substring = s.substring(1, s.indexOf("]"));
                        //60秒前
                        LocalDateTime parse = LocalDateTime.parse(substring, dateTimeFormatter);
                        if (parse.isAfter(localDateTime)) {
                            flag = true;
                        }
                    }
                }
                if (flag) {
                    strList.add(s);
                }
            }
            s9f9ListParse(strList, s9f9IndexList.get(i));

        }


    }

    /**
     * @param list s9f9in 前面一段时间的内容
     */
    private static void s9f9ListParse(List<String> list, int line) {
        Map<String, String> inMap = new HashMap<>();
        Map<String, String> outMap = new HashMap<>();
        for (int i = 1; i < list.size(); i++) {
            String s = list.get(i);
            if (s.endsWith(" W input")) {
                String s1 = list.get(i - 1);
                if (s1.contains("transactionId = ")) {
                    String s2 = s1.split("transactionId = ")[1];
                    String substring = s2.substring(0, s2.indexOf(" "));
                    inMap.put(substring, s1);
                } else {
                    System.out.println("错误");
                }
            } else if (s.contains(" output") && (!s.contains(" W output"))) {
                String s1 = list.get(i - 1);
                if (s1.contains("transactionId = ")) {
                    String s2 = s1.split("transactionId = ")[1];
                    String substring = s2.substring(0, s2.indexOf(" "));
                    outMap.put(substring, s1);
                } else {
                    System.out.println("错误");
                }
            }
        }
        AtomicBoolean temp = new AtomicBoolean(true);
        inMap.forEach((x, y) -> {
            if (outMap.get(x) == null) {
                System.out.println(y);
                temp.set(false);
            }
        });
        if (temp.get()) {
            System.out.println("无头s9f9in...坐标：" + line + 2);
        }

    }


}
