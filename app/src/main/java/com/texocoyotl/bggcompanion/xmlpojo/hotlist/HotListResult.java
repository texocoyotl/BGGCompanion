package com.texocoyotl.bggcompanion.xmlpojo.hotlist;


//<items termsofuse="http://boardgamegeek.com/xmlapi/termsofuse">
//    <item id="197376" rank="1">
//    </item>
//</items>

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "items", strict = false)
public class HotListResult {

    @ElementList(name = "item", inline = true)
    private List<Item> items;

    public List<Item> getItems ()
    {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "HotListResult{" +
                "items=" + items +
                '}';
    }
}
