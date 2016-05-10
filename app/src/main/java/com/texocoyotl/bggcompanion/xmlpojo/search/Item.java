package com.texocoyotl.bggcompanion.xmlpojo.search;

import com.texocoyotl.bggcompanion.xmlpojo.hotlist.Name;
import com.texocoyotl.bggcompanion.xmlpojo.hotlist.YearPublished;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

@ElementList(name = "item", inline = true)
public class Item {

    @Attribute
    private String type;

    @Attribute
    private int id;

    @Element
    private Name name;

    @Element(required = false)
    private YearPublished yearpublished;

    public String getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public Name getName() {
        return name;
    }

    public YearPublished getYearpublished() {
        return yearpublished;
    }

    @Override
    public String toString() {
        return "Item{" +
                "type='" + type + '\'' +
                ", id=" + id +
                ", name=" + name +
                ", yearpublished=" + yearpublished +
                '}';
    }
}
