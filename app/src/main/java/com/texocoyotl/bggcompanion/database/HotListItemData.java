package com.texocoyotl.bggcompanion.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.texocoyotl.bggcompanion.xmlpojo.hotlist.Item;

/**
 * Created by admin on 5/7/2016.
 */

public class HotListItemData {
    private final int bggId;
    private final String name;
    private final String thumbnail;
    private final String yearPublished;
    private final int rank;

    public int getBggId() {
        return bggId;
    }

    public String getName() {
        return name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getYearPublished() {
        return yearPublished;
    }

    public int getRank() {
        return rank;
    }

    public HotListItemData(int bggId, String name, String thumbnail, String yearPublished, int rank) {
        this.bggId = bggId;
        this.name = name;
        this.thumbnail = thumbnail;
        this.yearPublished = yearPublished;
        this.rank = rank;
    }

    public static HotListItemData fromCursor(Cursor cursor){

        if (cursor != null) {
            return new HotListItemData(
                cursor.getInt(Contract.HotListQuery.COLNUM_BGG_ID),
                cursor.getString(Contract.HotListQuery.COLNUM_NAME),
                cursor.getString(Contract.HotListQuery.COLNUM_THUMBNAIL),
                cursor.getString(Contract.HotListQuery.COLNUM_YEAR_PUBLISHED),
                cursor.getInt(Contract.HotListQuery.COLNUM_RANK)
            );
        }
        return null;
    }

    public static ContentValues getContentValue(Item item) {
        ContentValues value = new ContentValues();

        value.put(Contract.BoardgameEntry.COLUMN_BGG_ID, item.getId());
        value.put(Contract.BoardgameEntry.COLUMN_NAME, item.getName().getValue());
        value.put(Contract.BoardgameEntry.COLUMN_THUMBNAIL, item.getThumbnail().getValue());
        value.put(Contract.BoardgameEntry.COLUMN_YEAR_PUBLISHED, item.getYearpublished().getValue());
        value.put(Contract.BoardgameEntry.COLUMN_RANK, item.getRank());

        return value;
    }
}
