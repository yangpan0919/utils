package com.study.utils.message;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * 提取包涵某些字符的行内容
 */
public class TextParse {

    private static List<String> needStr = new ArrayList<>();
    private static List<String> targetList = new ArrayList<>();

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
        List<String> list = new ArrayList<>();
        FileInputStream fileInputStream = FileUtils.openInputStream(file);
        BufferedReader br = null;
        InputStreamReader isr = null;

        isr = new InputStreamReader(fileInputStream, "UTF-8");
        br = new BufferedReader(isr);
        String tmpString = "";
        while ((tmpString = br.readLine()) != null) {
            list.add(tmpString);
        }
        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            for (String s1 : needStr) {
                if (s.contains(s1)) {
                    targetList.add(s + "\n");
                }

            }
            if (s.contains("[HTDB800Host] Receive S6F11IN , CEID is 115")) {
                targetList.add("\n" + s + "\n");
            }
        }
        br.close();
        isr.close();
    }

    private static void parseFile(File file) throws Exception {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File file1 : files) {
                parseFile(file1);
            }
        } else {
            parse(file);
        }
    }

    public static void main(String[] args) throws Exception {
        File file = new File("C:\\Users\\Administrator\\Desktop\\temp");
        parseFile(file);

        output(targetList, "C:\\Users\\Administrator\\Desktop\\temp.txt");


    }


}
