package com.study.utils.pattern;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 提取包涵某些字符的行内容
 */
public class TextParse2 {

    private static String deviceCode = "DEXP010M0002";

    private static List<String> needStr = new ArrayList<>();
    private static List<String> targetList = new ArrayList<>();

    static String mdy = ".*LARM [0-9]{1,2}.*at (((0[1-9]|1[0-2])-(0[1-9]|1[0-9]|2[0-8])|(0[13-9]|1[0-2])-(29|30)|(0[13578]|1[02])-31)-[0-9]{4}|02-29-([0-9]{2}(0[48]|[2468][048]|[13579][26])|(0[48]|[2468][048]|[13579][26])00)) (([01][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9]))";
    static String mdy1 = ".*ALARM [0-9]{1,2}.*at (((0[1-9]|1[0-2])-(0[1-9]|1[0-9]|2[0-8])|(0[13-9]|1[0-2])-(29|30)|(0[13578]|1[02])-31)-[0-9]{4}|02-29-([0-9]{2}(0[48]|[2468][048]|[13579][26])|(0[48]|[2468][048]|[13579][26])00)) (([01][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9]))";
    private static String oldPlasmaProdutionPattern = ".* ([0-9]{4}-((0[1-9]|1[0-2])-(0[1-9]|1[0-9]|2[0-8])|(0[13-9]|1[0-2])-(29|30)|(0[13578]|1[02])-31)|([0-9]{2}(0[48]|[2468][048]|[13579][26])|(0[48]|[2468][048]|[13579][26])00)-02-29) .*";


    static {

        needStr.add("[SecsEquipModel] ----Received from Equip Strip Map Upload event - S6F11");
        needStr.add("[ClientImpl] Invoke, operation info:");
        needStr.add("binSet->getResult");
        needStr.add("receive select.rsp ,equip id is 65535 , status (0). transaction id : 1");
    }

    public static void output(List<String> list, String path) throws Exception {

        File file = new File(path);
        FileOutputStream fileOutputStream = FileUtils.openOutputStream(file);
        OutputStreamWriter isw = null;


        isw = new OutputStreamWriter(fileOutputStream, "UTF-8");

        for (String s : list) {
            isw.write(s);
        }
        isw.flush();
    }

    public static void parse(File file) throws Exception {

    }

    public static void parse(File file, List<String> list) throws Exception {
        FileInputStream fileInputStream = FileUtils.openInputStream(file);
        InputStreamReader isr = null;

        isr = new InputStreamReader(fileInputStream, "GBK");
        BufferedReader br = new BufferedReader(isr);
        String tmpString = "";
        while ((tmpString = br.readLine()) != null) {
//            System.out.println(tmpString);
            if (tmpString.contains("稼动率信息拋轉到數據庫:" + deviceCode)) {
                list.add(tmpString);
            }
        }

        br.close();
        isr.close();
    }

    private static void parseFile(File file, List<String> list) throws Exception {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File file1 : files) {
                parseFile(file1, list);
            }
        } else {
            parse(file, list);
        }
    }

    private static void handler(String name, List<Long> list) {
        Long start = list.get(0);

        for (int i = 1; i < list.size(); i++) {
            Long next = list.get(i);
            if (next - start > 1550) {
                System.out.println(name + ":" + start + ":" + next);
            }
            start = next;
        }

    }

    public static void main(String[] args) throws Exception {
//        File file = new File("E:\\application\\WeChat\\WeChat Files\\WeChat Files\\wxid_npbvodhremne22\\FileStorage\\File\\2020-11\\线路log");
        File file = new File("E:\\application\\WeChat\\WeChat Files\\WeChat Files\\wxid_npbvodhremne22\\FileStorage\\File\\2020-11\\FHlog");
        List<String> list = new ArrayList<>();
        parseFile(file, list);
        parseList(list);


//        output(targetList, "C:\\Users\\Administrator\\Desktop\\temp.txt");


    }

    private static void parseList(List<String> list) {
        Collections.sort(list);
//        for (String s : list) {
//            System.out.println(s);
//        }

        try {
            FileOutputStream fileOutputStream = FileUtils.openOutputStream(new File("C:\\Users\\Administrator\\Desktop\\" + deviceCode + ".txt"));
            OutputStreamWriter isw = null;


            isw = new OutputStreamWriter(fileOutputStream, "UTF-8");

            for (String s : list) {
                isw.write(s + "\n");
            }
            isw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
