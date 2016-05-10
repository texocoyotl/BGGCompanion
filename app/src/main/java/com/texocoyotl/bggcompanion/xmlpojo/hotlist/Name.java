package com.texocoyotl.bggcompanion.xmlpojo.hotlist;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(strict = false)
public class Name {

    @Attribute
    private String value;

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Name{" +
                "value='" + value + '\'' +
                '}';
    }
}
