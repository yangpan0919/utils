package com.study.utils.QA;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.*;

/**
 * error日志打印的是否合理
 */
public class ErrorUtil {

    public static int count = 0;
    public static int count1 = 0;
    public static List<String> otherList = new ArrayList<>();
    public static List<Integer> nullList = new ArrayList<>();
    public static List<String> nullNameList = new ArrayList<>();
    public static Map<String, List<Integer>> map = new HashMap<>();


    public static void main(String[] args) {
        List<File> files = Util.allFile("D:\\development\\eap-maven\\src\\main", ".java");

        for (File file : files) {
            checkFile(file);
        }

        for (int i = 0; i < nullList.size(); i++) {
            System.out.println(nullNameList.get(i) + "-------" + nullList.get(i));
        }
        System.out.println(count);
        System.out.println(count1);

        Set<Map.Entry<String, List<Integer>>> entries = map.entrySet();
        for (Map.Entry<String, List<Integer>> entry : entries) {
            System.out.println(entry.getKey() + "++++++" + entry.getValue());
        }

    }

    //检查文件
    public static void checkFile(File file) {
        BufferedReader br = null;
        BufferedWriter wr = null;
        InputStreamReader isr = null;
        OutputStreamWriter isw = null;
        boolean needNew = false; //是否需要生成新文件

        List<Integer> nullList = new ArrayList<>();
        List<String> nullNameList = new ArrayList<>();

        if (file.getName().equals("EsecDB2009Host.java")) {
            System.out.println("iii");
        }
        try {
            isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
            br = new BufferedReader(isr);
            String tmpString = "";
            List<String> list = new ArrayList<>();
            boolean temp = false;
            while ((tmpString = br.readLine()) != null) {
                if (tmpString.trim().startsWith("+")) {
                    list.set(list.size() - 1, list.get(list.size() - 1) + " " + tmpString.trim());
                    continue;
                }
                list.add(tmpString);
            }

            String methodName = "";
            boolean isCatch = false;
            String catchName = "";
            int spaceNum = -1;
            List<Integer> changeList = new ArrayList<>();
            List<Integer> addList = new ArrayList<>();
            List<String> addList1 = new ArrayList<>();
            List<String> changeList1 = new ArrayList<>();

            int needChange = 0;  //需要添加日志的行标
            String loggerName = null; //日志声明的对象名称

            int catchNum = 0;

            for (int i = 0; i < list.size(); i++) {

                if (i == 553) {
                    System.out.println(i);
                }
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
//                if (s.trim().length() < 4) {
//                    continue;
//                }
                if (s.contains("Logger.getLogger")) {
                    if (s.indexOf("=") < 0) {
                        otherList.add(s);
                        continue;
                    }
                    loggerName = s.substring(0, s.indexOf("=")).trim();

                    loggerName = loggerName.substring(loggerName.lastIndexOf(" ")).trim();
                } else if (s.contains("LoggerFactory.getLogger")) {
                    loggerName = s.substring(0, s.indexOf("=")).trim();

                    loggerName = loggerName.substring(loggerName.lastIndexOf(" ")).trim();
                }
//前面四四个空格表示需要的方法必须是成员方法; 内部类中的方法未涉及
                if (s.contains("{") && Util.spaceCount(s) == 4 && !s.contains("=") && s.indexOf("(") > 0 && (s.contains("public") || s.contains("private") || s.contains("void"))) {

                    methodName = s.substring(0, s.indexOf("(")).trim();

                    methodName = methodName.substring(methodName.lastIndexOf(" ") + 1);
                    isCatch = false;
                    spaceNum = -1;
                    needChange = 0;
                } else if (Util.spaceCount(s) == 4 && s.contains("   class ")) {  //内部类，类名
                    methodName = s.substring(s.indexOf("   class ") + 9).trim();
                    methodName = methodName.substring(0, methodName.indexOf(" "));
                }

                if (s.contains("} catch (")) {

                    if (isCatch) {
                        if (s.trim().startsWith("throw ")) {
                            isCatch = false;
                            spaceNum = -1;
                            needChange = 0;
                            catchName = null;
                        } else {
                            if (s.contains(".printStackTrace(")) {
                                needChange = i;

                            }
//                    System.out.println("----");
                            if (Util.spaceCount(s) == spaceNum) {

                                if (loggerName == null) {
                                    nullList.add(i);
                                    nullNameList.add(file.getName());
//                            continue;
                                }
                                needNew = true;
                                String value = (loggerName == null ? "logger" : loggerName) + ".error(\"方法：" + methodName + "\"," + catchName + ");";
                                value = Util.addPreSpace(value, spaceNum + 4); //空格数
                                if (needChange == 0) {
                                    addList.add(catchNum);
                                    addList1.add(value);
                                } else {
                                    changeList.add(needChange);
                                    changeList1.add(value);//logger 变量名位置
                                    needChange = 0;
                                }
                                spaceNum = -1;
                                isCatch = false;
                                catchName = null;
                            }

                        }

                    }

                    catchNum = i;
                    isCatch = true;
                    spaceNum = Util.spaceCount(s);
                    catchName = s.substring(s.lastIndexOf("Exception") + 9).trim();
                    catchName = catchName.substring(0, catchName.indexOf(")")).trim();
                    count++;

                    continue;

                }
//                if (s.contains("} catch (")) {
//                    count1++;
//                }


                if (isCatch) {
                    if (s.trim().startsWith("throw ")) {
                        isCatch = false;
                        spaceNum = -1;
                        needChange = 0;
                        catchName = null;
                        continue;
                    }
                    if (s.contains("printStackTrace()")) {
                        needChange = i;

                    }
//                    System.out.println("----");
                    if (Util.spaceCount(s) == spaceNum) {

                        if (loggerName == null) {
                            nullList.add(i);
                            nullNameList.add(file.getName());
//                            continue;
                        }
                        needNew = true;
                        String value = (loggerName == null ? "logger" : loggerName) + ".error(\"方法：" + methodName + "\"," + catchName + ");";
                        value = Util.addPreSpace(value, spaceNum + 4); //空格数
                        if (needChange == 0) {
                            addList.add(catchNum);
                            addList1.add(value);
                        } else {
                            changeList.add(needChange);
                            changeList1.add(value);//logger 变量名位置
                            needChange = 0;
                        }
                        spaceNum = -1;
                        isCatch = false;
                        catchName = null;
                    }


                }

                if (s.contains(".error(")) {
//                    ,e)
                    spaceNum = -1;
                    System.out.println(methodName + ":::::" + s);
                    String substring = s.substring(0, s.indexOf("error(") + 6);
                    String substring1 = s.substring(s.indexOf("error(") + 6);
                    String s1 = null;
                    needNew = true;
                    if (substring1.startsWith("\"")) {
                        s1 = substring + "\"方法：" + methodName + "->" + substring1.substring(1);
                    } else {
                        s1 = substring + "\"方法：" + methodName + "->\" +" + substring1;
                    }
                    if (isCatch) {


                        if (!s.contains(", " + catchName + ")")) {//, e)
                            String substring2 = s1.substring(0, s1.lastIndexOf(")"));
                            String substring3 = s1.substring(s1.lastIndexOf(")"));
                            String s2 = Util.noConstantStr(s, "\"");

                            if (s2.contains(",")) {
                                s1 = substring2.replace("Exception", "Exception2");
                                s1 = s1.substring(0, s1.lastIndexOf(",") + 1) + catchName + substring3;
                            } else {
                                s1 = substring2 + ", " + catchName + substring3;
                            }
                        }
                        isCatch = false;
                        catchName = null;
                        needChange = 0;
                    }
                    list.set(i, s1);
                }
            }

//            if (addList.size() > 0) {
//                System.out.println(file.getName());
//                System.out.println(addList);
//                System.out.println(addList1);
//            }
//            if (changeList.size() > 0) {
//                System.out.println(file.getName());
//                System.out.println(changeList);
//                System.out.println(changeList1);
//            }
//            System.out.println("+++++++++++++++++++++++");

            if (!needNew) {
                return;
            }
            FileOutputStream fileOutputStream = FileUtils.openOutputStream(file);
            isw = new OutputStreamWriter(fileOutputStream, "utf-8");
            wr = new BufferedWriter(isw);


            List<String> result = new ArrayList<>();
            for (int i = 0; i < changeList.size(); i++) {
                list.set(changeList.get(i), changeList1.get(i));
            }

            for (int i = 0; i < list.size(); i++) {
                result.add(list.get(i));
                for (int i1 = 0; i1 < addList.size(); i1++) {
                    if (i == addList.get(i1)) {
                        result.add(addList1.get(i1));
                        addList.remove(0);
                        addList1.remove(0);
                        break;
                    }
                }


                if (nullNameList.size() > 0) {
                    String s = nullNameList.get(0);
                    String className = s.substring(0, s.indexOf("java") - 1);
                    if (list.get(i).contains(" " + className + " ")) {
                        result.add("    private static final Logger logger = Logger.getLogger(" + className + ".class);");
                        nullNameList.clear();

                        List<String> tempList = new ArrayList<>();

                        for (int j = 0; j < result.size(); j++) {
                            tempList.add(result.get(j));
                            if (result.get(j).trim().startsWith("package ")) {
                                tempList.add("");
                                tempList.add("import org.apache.log4j.Logger;");
                            }
                        }
                        result = tempList;

                    }
                }

            }
            int perCount = 0;
            isCatch = false;
            for (int i = 0; i < result.size(); i++) {
                String s = result.get(i);
                if (s.contains("} catch (")) {
                    if (isCatch) {
                        if (s.contains(".printStackTrace(")) {
                            continue;
                        }
                    }
                    perCount = Util.spaceCount(s);
                    isCatch = true;
                    wr.write(s + "\n");
                    continue;
                }
                if (isCatch) {
                    if (s.contains(".printStackTrace(")) {
                        continue;
                    }
                    if (s.contains(".info(") || s.contains(".debug(")) {
                        List<Integer> lineList = map.get(file.getName());
                        if (lineList == null) {
                            lineList = new ArrayList<>();
                            map.put(file.getName(), lineList);
                        }
                        lineList.add(i + 1);

                    }
                    if (Util.spaceCount(s) == perCount) {//结束
                        perCount = 0;
                        isCatch = false;

                    }
                }

                wr.write(s + "\n");

            }


//            for (String s : result) {
////                if (s.contains(".printStackTrace(")) {
////                    continue;
////                }
//                wr.write(s + "\n");
//            }
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
