package com.study.utils.message;

import org.apache.commons.io.FileUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.*;

public class S9F9Parse {

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

        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            if (s.contains(" W input")) {
                String s1 = list.get(i - 1);
                if (s1.contains("transactionId = ")) {
                    String s2 = s1.split("transactionId = ")[1];
                    String substring = s2.substring(0, s2.indexOf(" "));
                    map.put(Integer.parseInt(substring), s.substring(0, s.indexOf(" ")));
                    lineMap.put(Integer.parseInt(substring), i);
                } else {
                    System.out.println("错误");
                }
//                System.out.println(s);

            } else if (s.contains(" output") && (!s.contains(" W output"))) {
                String s1 = list.get(i - 1);
                if (s1.contains("transactionId = ")) {
                    String s2 = s1.split("transactionId = ")[1];
                    String substring = s2.substring(0, s2.indexOf(" "));
                    outMap.put(Integer.parseInt(substring), s.substring(0, s.indexOf(" ")));
                } else {
                    System.out.println("错误");
                }

//                System.out.println(s);
            } else if (s.startsWith("S9F9 input")) {

                map.entrySet().forEach(
                        (x) -> {
                            Integer key = x.getKey();
                            String s1 = outMap.get(key);
                            if (s1 == null) {
                                System.out.println(x.getValue() + ":f" + key + ":" + outMap.get(key) + ":" + lineMap.get(key));
                            }

                        }
                );
                System.out.println(s + ":" + i);
                break;
            }
        }


    }


}
