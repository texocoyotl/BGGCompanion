package com.texocoyotl.bggcompanion.xmlpojo.hotlist;

//    <item id="197376" rank="1">
//        <thumbnail value="//cf.geekdo-images.com/images/pic2996490_t.jpg"/>
//        <name value="Charterstone"/>
//        <yearpublished value="2017"/>
//    </item>

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

@ElementList(name = "item", inline = true)
public class Item {

    @Attribute
    private int id;

    @Attribute
    private int rank;

    @Element
    private Thumbnail thumbnail;

    @Element
    private Name name;

    @Element
    private YearPublished yearpublished;

    public int getId() {
        return id;
    }

    public int getRank() {
        return rank;
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
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
                "id=" + id +
                ", rank=" + rank +
                ", thumbnail=" + thumbnail +
                ", name=" + name +
                ", yearpublished=" + yearpublished +
                '}';
    }
}
