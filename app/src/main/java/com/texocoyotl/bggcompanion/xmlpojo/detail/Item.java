package com.texocoyotl.bggcompanion.xmlpojo.detail;

import com.texocoyotl.bggcompanion.xmlpojo.hotlist.Name;
import com.texocoyotl.bggcompanion.xmlpojo.hotlist.YearPublished;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(strict = false)
@ElementList(name = "item", inline = true)
public class Item {

    @Attribute
    private int id;

    @Element
    private String image;

    @ElementList(name = "name", inline = true)
    private List<Name> names;

    @Element
    private String description;

    @Element
    private YearPublished yearpublished;

    @Element
    private MinPlayers minplayers;

    @Element
    private MaxPlayers maxplayers;

    @Element
    private MinPlayTime minplaytime;

    @Element
    private MaxPlayTime maxplaytime;

    @Element
    private MinAge minage;

    @ElementList(name = "link", inline = true)
    private List<Link> links;

    public int getId() {
        return id;
    }

    public List<Name> getNames() {
        return names;
    }

    public YearPublished getYearpublished() {
        return yearpublished;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public MinPlayers getMinplayers() {
        return minplayers;
    }

    public MaxPlayers getMaxplayers() {
        return maxplayers;
    }

    public MinPlayTime getMinplaytime() {
        return minplaytime;
    }

    public MaxPlayTime getMaxplaytime() {
        return maxplaytime;
    }

    public MinAge getMinage() {
        return minage;
    }

    public List<Link> getLinks() {
        return links;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", names=" + names +
                ", yearpublished=" + yearpublished +
                ", image='" + image + '\'' +
                ", description='" + description + '\'' +
                ", minplayers=" + minplayers +
                ", maxplayers=" + maxplayers +
                ", minplaytime=" + minplaytime +
                ", maxplaytime=" + maxplaytime +
                ", minage=" + minage +
                ", links=" + links +
                '}';
    }
}
