package com.study.utils.message;

import com.study.utils.excel.ExcelWriter;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class S9F9Parse3 {

    public static void main(String[] args) throws Exception {

        String path = "E:\\application\\WeChat\\WeChat Files\\WeChat Files\\yangpan0919\\FileStorage\\File\\2020-04\\";
        String name = "D3500-6019host.log";
        File file = new File(path + name);


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
        TreeMap<Integer, String> inMap = new TreeMap<>();
        TreeMap<Integer, LocalDateTime> inTimeMap = new TreeMap<>();
        TreeMap<Integer, Integer> inLineMap = new TreeMap<>();

        TreeMap<Integer, String> outMap = new TreeMap<>();
        TreeMap<Integer, LocalDateTime> outTimeMap = new TreeMap<>();
        TreeMap<Integer, Integer> outLineMap = new TreeMap<>();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter out = DateTimeFormatter.ofPattern("HH:mm:ss");


        for (int i = 0; i < list.size(); i++) {
            String s1 = list.get(i);
            if (s1.endsWith(" W input")) {
                s1 = s1.substring(0, s1.indexOf(" "));
                String s = list.get(i - 1);
                int index = s.indexOf("transactionId = ");
                String substring1 = s.substring(index + 16);
                index = Integer.parseInt(substring1.substring(0, substring1.indexOf(" ")));
                inMap.put(index, s1);
                inLineMap.put(index, i + 1);
                String substring = s.substring(s.indexOf("[") + 1, s.indexOf("]"));
                LocalDateTime inTime = LocalDateTime.parse(substring, dateTimeFormatter);
                inTimeMap.put(index, inTime);


            } else if ("S9F9 input".equals(s1)) {

                String s = list.get(i - 1);
                int index = s.indexOf("transactionId = ");
                String substring1 = s.substring(index + 16);
                index = Integer.parseInt(substring1.substring(0, substring1.indexOf(" ")));

                inMap.put(index, "S9F9");
                outMap.put(index, "S9F9");
                inLineMap.put(index, i + 1);
                outLineMap.put(index, i + 1);

                String substring = s.substring(s.indexOf("[") + 1, s.indexOf("]"));
                LocalDateTime inTime = LocalDateTime.parse(substring, dateTimeFormatter);
                inTimeMap.put(index, inTime);
                outTimeMap.put(index, inTime);

            } else if (s1.startsWith("receive linktest.req")) {

                int index = s1.indexOf("transaction id : ");
                index = Integer.parseInt(s1.substring(index + 17));
                inMap.put(index, "linktest");
                inLineMap.put(index, i + 1);
                String s = list.get(i - 1);

                String substring = s.substring(s.indexOf("[") + 1, s.indexOf("]"));
                LocalDateTime inTime = LocalDateTime.parse(substring, dateTimeFormatter);
                inTimeMap.put(index, inTime);

            } else if (s1.contains("sent message :receive linktest.rsp")) {
                int index = s1.indexOf("transaction id : ");
                index = Integer.parseInt(s1.substring(index + 17));
                outMap.put(index, "linktest");
                outLineMap.put(index, i + 1);

                String s = s1;

                String substring = s.substring(s.indexOf("[") + 1, s.indexOf("]"));
                LocalDateTime inTime = LocalDateTime.parse(substring, dateTimeFormatter);
                outTimeMap.put(index, inTime);
            } else if (s1.contains("[EquipmentEventDealer] response msg has received to")) {
                String s = s1;

                int index = s1.indexOf("transaction id : ");
                s1 = s1.substring(index + 17);
                index = Integer.parseInt(s1.substring(0, s1.indexOf(" ")));
                s1 = s1.substring(s1.lastIndexOf(":") + 1, s1.length() - 3);
                outMap.put(index, s1);
                outLineMap.put(index, i + 1);


                String substring = s.substring(s.indexOf("[") + 1, s.indexOf("]"));
                LocalDateTime inTime = LocalDateTime.parse(substring, dateTimeFormatter);
                outTimeMap.put(index, inTime);
            }


        }

        List<List<String>> result = new ArrayList<>();
        List<String> head = new ArrayList<>();
        head.add("transaction id");
        head.add("消息");
        head.add("收到消息时间");
        head.add("回复消息时间");
        head.add("时间差");
        result.add(head);

        List<List<String>> errorResult = new ArrayList<>();
        List<String> errorHead = new ArrayList<>();
        errorHead.add("transaction id");
        errorHead.add("消息");
        errorHead.add("收到消息时间");
        errorHead.add("回复消息时间");
        errorHead.add("时间差");
        errorResult.add(errorHead);


        inMap.forEach((x, y) -> {
            System.out.println(x);
            List<String> temp = new ArrayList<>();
            temp.add(x + "");//事务ID
            temp.add(y);//消息
            temp.add(inTimeMap.get(x).format(out));//收到消息时间
            if (outTimeMap.get(x) == null) {
                temp.add("");
                temp.add("没有回复。。。");

                errorResult.add(temp);
            } else {
                temp.add(outTimeMap.get(x).format(out));
                Duration between = Duration.between(inTimeMap.get(x), outTimeMap.get(x));
                long seconds = between.getSeconds();

                temp.add(seconds + "");
                if (seconds > 0 || "S9F9".equals(y)) {
                    errorResult.add(temp);
                }
            }
            result.add(temp);

        });

        Workbook sheets = ExcelWriter.exportDataForStr(result);
        sheets.write(FileUtils.openOutputStream(new File("C:\\Users\\Administrator\\Desktop\\消息分析.xlsx")));
        sheets = ExcelWriter.exportDataForStr(errorResult);
        sheets.write(FileUtils.openOutputStream(new File("C:\\Users\\Administrator\\Desktop\\消息分析ERROR.xlsx")));

        System.out.println("");

    }

}
