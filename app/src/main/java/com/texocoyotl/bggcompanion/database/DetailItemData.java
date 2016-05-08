package com.texocoyotl.bggcompanion.database;

import android.database.Cursor;


public class DetailItemData {
    private final int bggId;
    private final String name;
    private final String yearPublished;
    private final int rank;
    private final String image;
    private final String description;
    private final int minPlayers;
    private final int maxPlayers;
    private final int minPlayTime;
    private final int maxPlayTime;
    private final int minAge;
    private final String categories;
    private final String mechanics;
    private final String families;
    private final String designer;
    private final String publishers;

    public DetailItemData(int bggId, String name, String yearPublished, int rank, String image, String description, int minPlayers, int maxPlayers, int minPlayTime, int maxPlayTime, int minAge, String categories, String mechanics, String families, String designer, String publishers) {
        this.bggId = bggId;
        this.name = name;
        this.yearPublished = yearPublished;
        this.rank = rank;
        this.image = image;
        this.description = description;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.minPlayTime = minPlayTime;
        this.maxPlayTime = maxPlayTime;
        this.minAge = minAge;
        this.categories = categories;
        this.mechanics = mechanics;
        this.families = families;
        this.designer = designer;
        this.publishers = publishers;
    }

    public int getBggId() {
        return bggId;
    }

    public String getName() {
        return name;
    }

    public String getYearPublished() {
        return yearPublished;
    }

    public int getRank() {
        return rank;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getMinPlayTime() {
        return minPlayTime;
    }

    public int getMaxPlayTime() {
        return maxPlayTime;
    }

    public int getMinAge() {
        return minAge;
    }

    public String getCategories() {
        return categories;
    }

    public String getMechanics() {
        return mechanics;
    }

    public String getFamilies() {
        return families;
    }

    public String getDesigner() {
        return designer;
    }

    public String getPublishers() {
        return publishers;
    }

    public static DetailItemData fromCursor(Cursor cursor){

        if (cursor != null && cursor.moveToFirst()) {
            return new DetailItemData(
                    cursor.getInt(Contract.DetailQuery.COLNUM_BGG_ID),
                    cursor.getString(Contract.DetailQuery.COLNUM_NAME),
                    cursor.getString(Contract.DetailQuery.COLNUM_YEAR_PUBLISHED),
                    cursor.getInt(Contract.DetailQuery.COLNUM_RANK),
                    cursor.getString(Contract.DetailQuery.COLNUM_IMAGE),
                    cursor.getString(Contract.DetailQuery.COLNUM_DESCRIPTION),
                    cursor.getInt(Contract.DetailQuery.COLNUM_MIN_PLAYERS),
                    cursor.getInt(Contract.DetailQuery.COLNUM_MAX_PLAYERS),
                    cursor.getInt(Contract.DetailQuery.COLNUM_MIN_PLAY_TIME),
                    cursor.getInt(Contract.DetailQuery.COLNUM_MAX_PLAY_TIME),
                    cursor.getInt(Contract.DetailQuery.COLNUM_MIN_AGE),
                    cursor.getString(Contract.DetailQuery.COLNUM_CATEGORIES),
                    cursor.getString(Contract.DetailQuery.COLNUM_MECHANICS),
                    cursor.getString(Contract.DetailQuery.COLNUM_FAMILIES),
                    cursor.getString(Contract.DetailQuery.COLNUM_DESIGNER),
                    cursor.getString(Contract.DetailQuery.COLNUM_PUBLISHERS)
            );
        }
        return null;
    }
}