package com.study.utils.excel;

/**
 * Author: yangpan
 * Date: 2019-03-01
 * Time: 11:33
 * Description: 读取Excel时，封装读取的每一行的数据
 */
public class ExcelDataVO {

    /**
     * 姓名
     */
    private String name;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 居住地
     */
    private String location;

    /**
     * 职业
     */
    private String job;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }
}
