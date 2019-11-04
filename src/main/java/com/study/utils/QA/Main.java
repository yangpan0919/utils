package com.study.utils.QA;

import java.io.File;
import java.util.List;

/**
 * 在项目代码全部格式化的情况下使用
 */
public class Main {


    public static void main(String[] args) {

        List<File> files = Util.allFile("D:\\development\\code\\eap\\src\\main", ".java");
        for (File file : files) {
            Util.checkFile(file, "MybatisSqlSession.get", "sqlSession.close()");
        }
        System.out.println(Util.result.size());
        for (String s : Util.result) {
            System.out.println(s);
        }

    }
}
