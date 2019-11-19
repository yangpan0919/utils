package com.study.utils.timemap;

import java.util.HashMap;
import java.util.Map;

public class Element {

    private Long time;


    private Map<String,String> map = new HashMap<>();


    public String put(String key, String value) {

        return map.put(key, value);

    }

    public String put(String key, String value, int time) {

        //将改key值加入管理器中，进行管理，根据存活时间定时消除


        return map.put(key, value);

    }


    public String get(Object key) {

        return map.get(key);
    }



}
