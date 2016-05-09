package com.texocoyotl.bggcompanion.xmlpojo.detail;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(strict = false)
@ElementList(name = "link", inline = true)
public class Link {
    @Attribute
    private String type;

    @Attribute
    private String value;

}
