package com.study.utils.QA;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SystemOutUtil2 {

    public static int count = 0;

    public static void main(String[] args) {
        List<File> files = Util.allFile("D:\\development\\eap-maven\\src\\main", ".java");

        for (File file : files) {
            checkFile(file);
        }
        System.out.println(count);

    }


    //找到main方法注释掉
    public static void checkFile(File file) {


        BufferedReader br = null;
        BufferedWriter wr = null;
        InputStreamReader isr = null;
        OutputStreamWriter isw = null;
        boolean needNew = false; //是否需要生成新文件

        try {
            isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
            br = new BufferedReader(isr);
            String tmpString = "";
            List<String> list = new ArrayList<>();
            boolean flag = false;
            while ((tmpString = br.readLine()) != null) {
                if (tmpString.contains("System.out.print")) {
                    count++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                if (isw != null) {

                    isw.close();
                }
                if (wr != null) {

                    wr.close();
                }
                if (isr != null) {

                    isr.close();
                }
                if (br != null) {

                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
