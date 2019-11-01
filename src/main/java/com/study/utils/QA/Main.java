package com.study.utils.QA;

import java.io.File;
import java.util.List;

/**
 * 在项目代码全部格式化的情况下使用
 */
public class Main {


    public static void main(String[] args) {

        List<File> files = Util.allFile("D:\\development\\eap-maven\\src\\main\\java\\cn\\tzauto\\octopus", ".java");
        for (File file : files) {
            Util.checkFile(file, "MybatisSqlSession.get", "sqlSession.close()");
        }
        for (String s : Util.result) {
            System.out.println(s);
        }

    }
}
