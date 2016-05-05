package com.texocoyotl.bggcompanion.xmlpojo.hotlist;

import org.simpleframework.xml.Attribute;


public class Thumbnail {

    @Attribute
    private String value;

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Thumbnail{" +
                "value='" + value + '\'' +
                '}';
    }
}
