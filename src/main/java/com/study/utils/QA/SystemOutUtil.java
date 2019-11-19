package com.study.utils.QA;

import java.io.*;
import java.util.List;

public class SystemOutUtil {
    public static void main(String[] args) {
        List<File> files = Util.allFile("D:\\development\\eap-maven\\src\\main", ".java");

        for (File file : files) {
            checkFile(file);
        }

    }



    //检查文件
    public static void checkFile(File file) {
        BufferedReader br = null;
        BufferedWriter wr = null;
        InputStreamReader isr = null;
        OutputStreamWriter isw = null;
        boolean needNew = false; //是否需要生成新文件




    }
}
