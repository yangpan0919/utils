package com.study.utils.timemap;

import java.util.HashMap;

public class TimeMap<K, V> extends HashMap<K, V> {

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, the old
     * value is replaced.
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or
     * <tt>null</tt> if there was no mapping for <tt>key</tt>.
     * (A <tt>null</tt> return can also indicate that the map
     * previously associated <tt>null</tt> with <tt>key</tt>.)
     */
    public V put(K key, V value) {

        return super.put(key, value);

    }

    public V put(K key, V value, int time) {

        //将改key值加入管理器中，进行管理，根据存活时间定时消除


        return super.put(key, value);

    }


    public V get(Object key) {

        return super.get(key);
    }


}
