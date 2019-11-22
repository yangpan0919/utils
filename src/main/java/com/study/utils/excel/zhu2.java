package com.study.utils.excel;

import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class zhu2 {

    public static String point = "!@#$%^&*";
    public static Map<String, List<String>> map = new HashMap();


    public static List<String> headList = new ArrayList<>();

    static {
        headList.add("");//此处可以添加过滤用的表头
//        headList.add("Text");//
    }

    public static void main(String[] args) {


//        Map<String, List<List<String>>> map = new HashMap();
        String path = "C:\\Users\\Administrator\\Desktop\\zwh"; //目录文件夹
//        Set<String> dirFiles = new HashSet<>();

        File file = new File(path);
        File[] peopleFile = file.listFiles(); //获取所有的文件夹
        for (File countyDir : peopleFile) {
            String peopleName = countyDir.getName();
            if (countyDir.isFile()) {
                System.out.println(peopleName + ":不是文件夹");
                continue;
            }
            String countyDirPath = countyDir.getAbsolutePath(); //所有唯一标志符的key
            map.put(countyDirPath, new ArrayList<>());
            handleCountyDir(countyDir, countyDirPath);
        }


        String outPath = "C:\\Users\\Administrator\\Desktop\\YP2";  //输出的文件目录
        for (File countyDir : peopleFile) {
            String peopleName = countyDir.getName();
            if (countyDir.isFile()) {
                System.out.println(peopleName + ":不是文件夹");
                continue;
            }
            String countyDirPath = countyDir.getAbsolutePath();
            List<String> list = map.get(countyDirPath);
            String s = outPath + "\\" + countyDir.getName() + ".xlsx";

            export(list, s);

        }

    }

    private static void handleCountyDir(File file, String key) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File file1 : files) {
                handleCountyDir(file1, key);
            }
        } else if (file.isFile()) {
            if (file.getName().endsWith(".xls") || file.getName().endsWith(".xlsx")) {
                List<List<String>> readResultStr = ExcelReader.readExcelForStr(file.getAbsolutePath());

                map.get(key).addAll(handlerResultList(readResultStr, file.getAbsolutePath()));
            }
        }


    }

//    private static void handleCountyFile(File file,String key) {
//
//        if(file.isDirectory()){
//            File[] files = file.listFiles();
//            for (File file1 : files) {
//                handleCountyFile(file1,key);
//            }
//        }else if(file.isFile()){
//            List<List<String>> readResultStr = ExcelReader.readExcelForStr(file.getAbsolutePath(), null);
//            map.get(key).addAll(handlerResultList(readResultStr));
//
//        }
//
//    }

    private static List<String> handlerResultList(List<List<String>> readResultStr, String path) {
        List<String> result = new ArrayList<>();
        if (readResultStr.size() == 0) {
            return result;
        }
        boolean isFirst = true; //是否取第一列
        int count = -1;
        List<String> list1 = readResultStr.get(0);
        for (int i = 0; i < list1.size(); i++) {
            if (list1.get(i).trim().equalsIgnoreCase("text")) {
                count = i;
                break;
            }
        }
        if (count == -1) {
            System.out.println(path + "：处的文件中没有text 的表头");
            return result;
        }


//        if (readResultStr.size() > 10) {
//            for (int i = 4; i < 8; i++) {
//                List<String> list = readResultStr.get(i);
//                if (list.get(0).trim().equals("")) {
//                    count++;
//                }
//            }
//        } else if (readResultStr.size() > 4) {
//            for (int i = readResultStr.size() - 5; i < readResultStr.size() - 1; i++) {
//                List<String> list = readResultStr.get(i);
//                if (list.get(0).trim().equals("")) {
//                    count++;
//                }
//            }
//        } else if (readResultStr.size() > 2) {
//
//            List<String> list = readResultStr.get(1);
//            if (list.get(0).trim().equals("")) {
//                count = 2;
//            }
//            list = readResultStr.get(2);
//            if (list.get(0).trim().equals("")) {
//                count++;
//            }
//
//
//        } else {
//            System.out.println(path + "处的文件内容数据量太少了");
//            return result;
//        }
//        if (count >= 3) {
//            isFirst = false;
//        }

        for (int i = 0; i < readResultStr.size(); i++) {
            List<String> list = readResultStr.get(i);

            if (list == null || list.size() == 0) {
                continue;
            }
            if (count >= list.size()) {
                continue;
            }

            String s1 = list.get(count);
            s1 = s1 == null ? "" : s1.trim();
            if (headList.contains(s1)) {
                continue;
            }
            result.add(s1);


//            for (int i1 = 0; i1 < list.size(); i1++) {
//                String s = list.get(i1);
//                if (s == null) {
//                    list.set(i1, "");
//                }
//            }
//
//            if (isFirst) {
//                String s = list.get(0).trim();
//                if (headList.contains(s)) {
//                    continue;
//                }
//                result.add(s);
//            } else {
//                if (list.size() < 2) {
//                    System.out.println(path + "处的文件有问题，请看看内容仔细核对！！！");
//                }
//                String s = list.get(1).trim();
//                if (headList.contains(s)) {
//                    continue;
//                }
//                result.add(list.get(1));
//            }
        }
        return result;

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


    public static void export(List<String> lists, String exportFilePath) {
        List<List<String>> dataList = new ArrayList<>();
        for (String str : lists) {
            List<String> list = new ArrayList<>();
            list.add(str);
            dataList.add(list);
        }

        Workbook workbookStr = ExcelWriter.exportDataForStr(dataList);

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
