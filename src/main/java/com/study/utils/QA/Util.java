package com.study.utils.QA;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Util {


    /**
     * 获取文件夹,里面的的所有以shffix 结尾的文件,加入list中
     *
     * @param path
     * @param suffix
     * @return
     */
    public static List<File> allFile(String path, String suffix) {
        if (suffix == null) {
            suffix = "";
        }
        File file = new File(path);
        List<File> list = new ArrayList<>();
        if (!file.exists()) {
            System.out.println("文件不存在:" + path);
            return list;
        }

        if (file.isDirectory()) {
            dirHandler(file, suffix, list);

        } else {
            if (file.getName().endsWith(suffix)) {
                list.add(file);
            }
        }

        return list;

    }

    /**
     * 处理文件夹，将需要的文件加入list中
     *
     * @param file
     * @param suffix
     * @param list
     */
    public static void dirHandler(File file, String suffix, List<File> list) {

        File[] files = file.listFiles();
        for (File file1 : files) {
            if (file1.isFile() && file1.getName().endsWith(suffix)) {
                list.add(file1);
            } else if (file1.isDirectory()) {
                dirHandler(file1, suffix, list);
            }
        }

    }

    public static List<String> result = new ArrayList<>();

    /*
     *
     * @param file
     * @param str
     * @param targetStr
     */
    //检查文件
    public static void checkFile(File file, String str, String targetStr) {
        BufferedReader br = null;
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
            br = new BufferedReader(isr);
            String tmpString = "";
            List<String> list = new ArrayList<>();
            boolean temp = false;
            while ((tmpString = br.readLine()) != null) {

                list.add(tmpString);
            }

            int closeCount = 0; //非同级句法块，sqlSession.close()之后需要，return；

            boolean flag = false;
            boolean test = false;
            int startCount = 0;
            int endCount = 0;
            int count = 0;
            int tryCount = 0; //同一语句块中可能有过个try catch ,用以处理try
            boolean nullCheck = false;
            boolean isFinally = false;
            String name = null;
            for (int i = 0; i < list.size(); i++) {

                String s = list.get(i);

                if (s.trim().startsWith("//")) {
                    continue;
                } else if (s.trim().startsWith("/*")) {
                    temp = true;
                    if (s.trim().endsWith("*/")) {
                        temp = false;
                    }
                    continue;
                }
                if (temp) {
                    if (s.trim().endsWith("*/")) {
                        temp = false;
                    }
                    continue;
                }
                if (file.getName().equals("EquipModel.java") && i == 519) {
                    System.out.println("");
                }

                if (file.getName().equals("EquipModel.java") && i == 779) {
                    System.out.println("");
                }


                if (s.contains(str)) {
                    if (s.contains("try")) {
                        continue;
                    }
                    name = parseName(s, str);
                    targetStr = name + ".close";
                    flag = true;
                    count = i + 1;

                    int i1 = needPre(i, list, name);
                    if (i > i1) {
                        count = i1 + 1;
                        //确定{ 和  } 的个数
                        int[] arr = findCount(i, i1, list);
                        startCount = arr[0];
                        endCount = arr[1];

                        if (arr[2] == 1) {
                            test = true; //有try
                        }

                        continue;

                    }


                    if (!s.contains("SqlSession ")) {
//                        startCount++;

                    }


                }
                if (flag) {
                    if (s.indexOf("{") > 0) {
                        startCount++;
                        if (s.contains("try") && startCount == endCount + 1) {
                            tryCount = i; //好像同级try的数量没有影响
                            test = true;
                        }
                        if (closeCount == 1) {
                            //子集里面try  finally 中的sqlSession也会被提醒
                            addData(file.getName(), count);
                            closeCount = 0;
                            flag = false;
                            test = false;
                            startCount = 0;
                            endCount = 0;
                            tryCount = 0;
                            nullCheck = false;
                            isFinally = false;
                            continue;
                        }
                    } else {
                        if (closeCount == 1 && s.contains("return")) {
                            closeCount = 0;
                        }
                    }


                    if (s.indexOf("}") > 0) {
                        endCount++;
                    }


                    if (startCount == endCount) {
                        if (s.contains(targetStr)) {
                            flag = false;
                            test = false;
                            startCount = 0;
                            endCount = 0;
                            tryCount = 0;
                            nullCheck = false;
                            isFinally = false;
                            continue;
                        }
                    } else if (s.contains(targetStr)) {
                        //return 的判断  关闭之后必须接return ， 非正常关闭  标志位
                        closeCount = 1;
                    }

                    if (test && startCount == endCount + 1) {
                        if (s.contains(targetStr)) {
                            flag = false;
                            test = false;
                            startCount = 0;
                            endCount = 0;
                            tryCount = 0;
                            //清除return的判断 标志位
                            closeCount = 0;
                            nullCheck = false;
                            isFinally = false;
                            continue;
                        } else if (s.contains("finally")) {
                            isFinally = true;
                        }
                    } else if (isFinally && test && startCount == endCount + 2) {
                        if (s.contains("if (" + name + " != null) {")) {
                            nullCheck = true;
                        } else if (nullCheck) {
                            if (s.contains(targetStr)) {
                                flag = false;
                                test = false;
                                startCount = 0;
                                endCount = 0;
                                tryCount = 0;
                                //清除return的判断 标志位
                                closeCount = 0;
                                nullCheck = false;
                                isFinally = false;
                                continue;
                            }
                        }
                    }

                    if (endCount > startCount) {
                        addData(file.getName(), count);
                        closeCount = 0;
                        flag = false;
                        test = false;
                        startCount = 0;
                        endCount = 0;
                        tryCount = 0;
                        nullCheck = false;
                        isFinally = false;
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        } finally {
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    /**
     * 下标0 {个数 下标1 }个数
     * 下标2：0没有try  1有
     *
     * @return
     */
    private static int[] findCount(int end, int start, List<String> list) {
        int[] result = new int[3];
        int temp0 = 0;
        int temp1 = 0;
        int temp2 = 0;
        for (int i = start + 1; i <= end; i++) {
            String s = list.get(i);
            if (s.contains("{")) {
                temp0++;
            }
            if (s.contains("}")) {
                temp1++;
            }
            if (s.contains("try ")) {
                temp2 = 1;
            }
        }
        result[0] = temp0;
        result[1] = temp1;
        result[2] = temp2;
        return result;

    }

    /**
     * 检查是否需要前面的内容
     *
     * @return
     */
    private static int needPre(int index, List<String> list, String name) {
        if (list.get(index).contains("SqlSession ")) {
            return index;
        }

        for (int i = index - 1; i > 1; i--) {
            String s = list.get(i);
            if (s.contains("SqlSession ") && s.contains(name)) {
                return i;
            }
        }
        System.out.println("解析异常，没有找到SqlSession 的定义语句");
        System.exit(0);
        return 0;

    }

    //获取定义的变量名
    private static String parseName(String s, String str) {
        s = s.substring(0, s.indexOf(str)).trim();
        if (s.endsWith("=")) {
            s = s.substring(0, s.length() - 1).trim();
            String result = s.substring(s.lastIndexOf(" ") + 1);
            return result;
        } else {
            System.out.println("解析错误");
            System.exit(0);
        }
        return null;


    }

    /**
     * 添加数据
     *
     * @param name
     * @param count
     */
    private static void addData(String name, int count) {
        String s1 = "文件名为：" + name;
        int length = s1.length();
        if (length < 40) {
            for (int j = 0; j < 40 - length; j++) {
                s1 = s1 + " ";
            }
        }
        result.add(s1 + "所在行数为：" + count);
    }


    /**
     * 返回字符串的前置空格数
     *
     * @param str
     * @return
     */
    public static int spaceCount(String str) {

        return spaceCount(str, 0, str.length());
    }

    private static int spaceCount(String str, int i, int max) {
        if (i == max) {
            return i;
        }

        if (" ".equals(str.substring(i, i + 1))) {
            return spaceCount(str, i + 1, max);
        }

        return i;
    }

    /**
     * 在字符窗前面添加空格
     *
     * @param str
     * @param num
     * @return
     */
    public static String addPreSpace(String str, int num) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < num; i++) {
            sb.append(" ");
        }
        sb.append(str);
        return sb.toString();
    }

    public static void main(String[] args) {
        String name = "logger.error(\"获取设备信息失败,检查通讯状况\");";
        System.out.println(noConstantStr(name, "\""));
    }

    /**
     * 去除两个符号的中间部分
     *
     * @param str
     * @param temp
     * @return
     */
    public static String noConstantStr(String str, String temp) {
        String[] arr = str.split("");
        boolean flag = true;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            String s = arr[i];
            if (s.equals(temp)) {
                flag = !flag;
                continue;
            }
            if (flag) {
                sb.append(s);
            }
        }
        return sb.toString();
    }


}
