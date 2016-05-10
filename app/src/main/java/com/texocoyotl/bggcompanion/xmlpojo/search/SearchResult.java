package com.texocoyotl.bggcompanion.xmlpojo.search;


import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "items", strict = false)
public class SearchResult {

    @ElementList(name = "item", inline = true)
    private List<Item> items;

    public List<Item> getItems ()
    {
        return items;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "items=" + items +
                '}';
    }
}
