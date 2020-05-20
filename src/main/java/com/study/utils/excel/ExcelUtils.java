package com.study.utils.excel;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 列开头添加指定内容
 */
public class ExcelUtils {

    public static String str = "plc:";

    public static void main(String[] args) throws IOException {


        /************** 读取Excel流程 ******************/
        // 设定Excel文件所在路径
        String excelFileName = "C:\\Users\\Administrator\\Desktop\\temp.xls";
        // 读取Excel文件内容
        List<List<String>> readResultStr = ExcelReader.readExcelForStr(excelFileName, null);
        for (int i = 1; i < readResultStr.size(); i++) {
            List<String> list = readResultStr.get(i);
            list.set(0, str + list.get(0));
        }

        Workbook workbook = ExcelWriter.exportDataForStr(readResultStr);
        workbook.write(FileUtils.openOutputStream(new File("C:\\Users\\Administrator\\Desktop\\temp2.xls")));

    }
}
