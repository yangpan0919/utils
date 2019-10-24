package com.study.utils.excel;

import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class zhu {

    public static String point = "!@#$%^&*";

    public static void main(String[] args) {
        Map<String, List<List<String>>> map = new HashMap();
        String path = "C:\\Users\\Administrator\\Desktop\\zwh"; //目录文件夹
//        Set<String> dirFiles = new HashSet<>();

        File file = new File(path);
        File[] peopleFile = file.listFiles(); //每个人的文件夹
        for (File peopleDir : peopleFile) {
            String peopleName = peopleDir.getName();
            if (peopleDir.isFile()) {
                System.out.println(peopleName + ":不是文件夹");
            }
            File[] files = peopleDir.listFiles(); //每个人下面的文件夹
            for (File dirFile : files) {
                String dirFileName = dirFile.getName();  //每个具体的文件夹名
                if (dirFile.isFile()) {
                    System.out.println(dirFileName + ":不是文件夹");
                }
                File[] excelFiles = dirFile.listFiles();
                for (File targetFile : excelFiles) {
                    String targetFileName = targetFile.getName();
                    int i = targetFileName.lastIndexOf(".");
                    targetFileName = targetFileName.substring(0, i);
                    String key = dirFileName + point + targetFileName;
                    if (map.get(key) == null) {
                        map.put(key, new ArrayList<>());
                    }
                    String absolutePath = targetFile.getAbsolutePath();
                    List<List<String>> lists = ExcelReader.readExcelForStr(absolutePath,key);
                    map.get(key).addAll(lists);
                }
            }
        }
        Set<String> strings = map.keySet();

        String outPath = "C:\\Users\\Administrator\\Desktop\\YP";  //输出的文件目录

        for (String string : strings) {
            List<List<String>> lists = map.get(string);
            int i = string.indexOf(point);
            String s = outPath + "\\" + string.substring(0, i) + "\\";
            s = s + string.substring(i + point.length()) + ".xlsx";
            export(lists, s,string);
        }


    }

    public static void checkFile(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                String parent = file.getParent();
                File file1 = new File(parent);
                checkDir(file1);
                checkFile(file);
            }
        }
    }

    public static void checkDir(File file) {
        if (!file.exists()) {
            if (!file.mkdir()) {
                String parent = file.getParent();
                File file1 = new File(parent);
                checkDir(file1);
                checkDir(file);
            }
        } else {
            if (file.isFile()) {
                System.out.println("已有该文件，但不是文件夹:-->" + file.getName());
                System.exit(0);
            }
        }
    }


    public static void export(List<List<String>> lists, String exportFilePath,String key) {
        Workbook workbookStr = ExcelWriter.exportDataForStr(lists,key);

        // 以文件的形式输出工作簿对象
        FileOutputStream fileOut = null;
        try {
            File exportFile = new File(exportFilePath);
            checkFile(exportFile);

            fileOut = new FileOutputStream(exportFilePath);
            workbookStr.write(fileOut);
            fileOut.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fileOut) {
                    fileOut.close();
                }
                if (null != workbookStr) {
                    workbookStr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
