package com.study.utils.message;

import com.study.utils.excel.ExcelWriter;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

public class ReLinkParse {

    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static DateTimeFormatter out = DateTimeFormatter.ofPattern("HH:mm:ss");


    public static void main(String[] args) throws Exception {


        String path = "E:\\application\\WeChat\\WeChat Files\\WeChat Files\\yangpan0919\\FileStorage\\File\\2020-04\\";
        String name = "D1600-0083host.log.2";
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

        String s2 = "receive select.rsp ,equip id is 65535 , status (0). transaction id : 1";//连接机台的标志
        String startOn = "[VerifyLicense] verify success";//启动eap标志位
        List<Integer> startOnList = new ArrayList<>();
        List<LocalDateTime> startOnTimeList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {

            String s = list.get(i);
            if (s.endsWith(startOn)) {
                startOnList.add(i + 1);
                String substring = s.substring(s.indexOf("[") + 1, s.indexOf("]"));
                LocalDateTime time = LocalDateTime.parse(substring, dateTimeFormatter);
                startOnTimeList.add(time);
                continue;

            }
            if (s.equals(s2)) {
                // 连接/重连之后的关键字
                String s1 = list.get(i - 1);
                String substring = s1.substring(s1.indexOf("[") + 1, s1.indexOf("]"));
                LocalDateTime time = LocalDateTime.parse(substring, dateTimeFormatter);
                boolean relink = isRelink(startOnTimeList, time);
                if (relink) {
                    System.out.println("重连时间为:" + substring);
                } else {
                    System.out.println("eap启动连接上的时间为:" + substring);
                }

            }

        }


    }

    private static boolean isRelink(List<LocalDateTime> startOnTimeList, LocalDateTime time) {
        if (startOnTimeList.size() == 0) {
            return true;
        }
        LocalDateTime localDateTime = startOnTimeList.get(startOnTimeList.size() - 1);

        //时间相差20秒以上则表示是重连，不是一开始就连上去的
        LocalDateTime startTime = localDateTime.plusSeconds(20);
        if (startTime.isAfter(time)) {
            return false;
        }
        return true;
    }


}
