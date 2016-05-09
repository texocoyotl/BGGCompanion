package com.texocoyotl.bggcompanion.xmlpojo.detail;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(strict = false)
@ElementList(name = "link", inline = true)
public class Link {

    public static final String CATEGORY = "boardgamecategory";
    public static final String MECHANIC = "boardgamemechanic";
    public static final String FAMILY = "boardgamefamily";
    public static final String DESIGNERS = "boardgamedesigner";
    public static final String PUBLISHER = "boardgamepublisher";

    @Attribute
    private String type;

    @Attribute
    private String value;

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Link{" +
                "type='" + type + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
