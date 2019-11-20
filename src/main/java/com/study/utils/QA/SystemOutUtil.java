package com.study.utils.QA;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 无用的System.out.在代码中的清除
 * 实际代码中的System.out需要手动筛选清除
 */
public class SystemOutUtil {
    public static void main(String[] args) {
        List<File> files = Util.allFile("D:\\development\\eap-maven\\src\\main", ".java");

        for (File file : files) {
            checkFile(file);
        }

    }


    //找到main方法注释掉
    public static void checkFile(File file) {


        BufferedReader br = null;
        BufferedWriter wr = null;
        InputStreamReader isr = null;
        OutputStreamWriter isw = null;
        boolean needNew = false; //是否需要生成新文件
        String str = "public static void main(";

        try {
            isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
            br = new BufferedReader(isr);
            String tmpString = "";
            List<String> list = new ArrayList<>();
            boolean flag = false;
            while ((tmpString = br.readLine()) != null) {
                list.add(tmpString);
            }

            int spaceNum = -1;

            for (int i = 0; i < list.size(); i++) {
                String s = list.get(i);

                if (s.contains(str) && !s.trim().startsWith("//")) {
                    if (file.getName().equals("EapClient.java")) {
                        continue;
                    }
                    flag = true;
                    spaceNum = Util.spaceCount(s);
                    list.set(i, "//" + s);
                    continue;
                }

                if (flag) {
//                    if (s.trim().startsWith("//")) {
//                        continue;
//                    }

                    needNew = true;
                    list.set(i, "//" + s.replace("System.out.println", "logger.info"));
                    if (spaceNum == Util.spaceCount(s)) {
                        spaceNum = -1;
                        flag = false;
                    }
                    continue;

                }
                if (s.trim().startsWith("//") && s.contains("System.out.println")) {
                    needNew = true;
                    list.set(i, "");
                } else if (s.trim().startsWith("//") && s.contains("System.out.print")) {
                    needNew = true;
                    list.set(i, "");
                }


            }

            if (!needNew) {
                return;
            }
            FileOutputStream fileOutputStream = FileUtils.openOutputStream(file);
            isw = new OutputStreamWriter(fileOutputStream, "utf-8");
            wr = new BufferedWriter(isw);
            for (String s : list) {
                wr.write(s + "\n");
            }
            wr.flush();


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
