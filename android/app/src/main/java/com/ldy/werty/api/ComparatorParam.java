package com.ldy.werty.api;

import java.util.Comparator;

/**
 * Created by lidongyang on 2016/10/19.
 */
public class ComparatorParam implements Comparator {
    public int compare(Object arg0, Object arg1) {
        BasicNameValuePair param0=(BasicNameValuePair)arg0;
        BasicNameValuePair param1=(BasicNameValuePair)arg1;

        return param0.getName().compareTo(param1.getName());
    }

    public static class BasicNameValuePair {

        private String name;
        private String value;

        public BasicNameValuePair(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }
}
