package com.texocoyotl.bggcompanion.xmlpojo.detail;

import org.simpleframework.xml.Attribute;

/**
 * Created by admin on 5/8/2016.
 */
public class MaxPlayTime {

    @Attribute
    private String value;

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "MaxPlayTime{" +
                "value='" + value + '\'' +
                '}';
    }
}
