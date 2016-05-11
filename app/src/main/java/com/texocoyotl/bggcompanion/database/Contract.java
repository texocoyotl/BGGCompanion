package com.texocoyotl.bggcompanion.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class Contract {

    public static final String CONTENT_AUTHORITY = "com.texocoyotl.bggcompanion";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_GAMES = "boardgames";

    public static final class BoardgameEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_GAMES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GAMES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GAMES;

        public static final String TABLE_NAME = "boardgames";

        public static final String COLUMN_BGG_ID = "bgg_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_THUMBNAIL = "thumbnail";
        public static final String COLUMN_YEAR_PUBLISHED = "year_published";
        public static final String COLUMN_RANK = "hot_rank";

        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_MIN_PLAYERS = "min_players";
        public static final String COLUMN_MAX_PLAYERS = "max_players";
        public static final String COLUMN_MIN_PLAY_TIME = "min_play_time";
        public static final String COLUMN_MAX_PLAY_TIME = "max_play_time";
        public static final String COLUMN_MIN_AGE = "min_age";

        public static final String COLUMN_CATEGORIES = "categories";
        public static final String COLUMN_MECHANICS = "mechanics";
        public static final String COLUMN_FAMILIES = "families";
        public static final String COLUMN_DESIGNERS = "designers";
        public static final String COLUMN_PUBLISHERS = "publishers";

        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_BGG_ID + " INTEGER UNIQUE NOT NULL," +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_THUMBNAIL + " TEXT, " +
                COLUMN_YEAR_PUBLISHED + " TEXT NOT NULL, " +
                COLUMN_RANK + " INTEGER DEFAULT 0, " +
                COLUMN_IMAGE + " TEXT," +
                COLUMN_DESCRIPTION + " TEXT," +
                COLUMN_MIN_PLAYERS + " INTEGER ," +
                COLUMN_MAX_PLAYERS + " INTEGER ," +
                COLUMN_MIN_PLAY_TIME + " INTEGER ," +
                COLUMN_MAX_PLAY_TIME + " INTEGER ," +
                COLUMN_MIN_AGE + " INTEGER ," +
                COLUMN_CATEGORIES + " TEXT ," +
                COLUMN_MECHANICS + " TEXT ," +
                COLUMN_FAMILIES + " TEXT ," +
                COLUMN_DESIGNERS + " TEXT ," +
                COLUMN_PUBLISHERS + " TEXT " +
                " );";

        public static Uri buildBoardGameUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getIDFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class HotListQuery {
        public static final String[] COLUMNS = {
                BoardgameEntry._ID,
                BoardgameEntry.COLUMN_BGG_ID,
                BoardgameEntry.COLUMN_NAME,
                BoardgameEntry.COLUMN_THUMBNAIL,
                BoardgameEntry.COLUMN_YEAR_PUBLISHED,
                BoardgameEntry.COLUMN_RANK
        };

        public static final int COLNUM_ID = 0;
        public static final int COLNUM_BGG_ID = 1;
        public static final int COLNUM_NAME = 2;
        public static final int COLNUM_THUMBNAIL = 3;
        public static final int COLNUM_YEAR_PUBLISHED = 4;
        public static final int COLNUM_RANK = 5;
    }
    
    public static final class DetailQuery {
        public static final String[] COLUMNS = {
                BoardgameEntry._ID,
                BoardgameEntry.COLUMN_BGG_ID,
                BoardgameEntry.COLUMN_NAME,
                BoardgameEntry.COLUMN_YEAR_PUBLISHED,
                BoardgameEntry.COLUMN_RANK,
                BoardgameEntry.COLUMN_IMAGE,
                BoardgameEntry.COLUMN_DESCRIPTION,
                BoardgameEntry.COLUMN_MIN_PLAYERS,
                BoardgameEntry.COLUMN_MAX_PLAYERS,
                BoardgameEntry.COLUMN_MIN_PLAY_TIME,
                BoardgameEntry.COLUMN_MAX_PLAY_TIME,
                BoardgameEntry.COLUMN_MIN_AGE,
                BoardgameEntry.COLUMN_CATEGORIES,
                BoardgameEntry.COLUMN_MECHANICS,
                BoardgameEntry.COLUMN_FAMILIES,
                BoardgameEntry.COLUMN_DESIGNERS,
                BoardgameEntry.COLUMN_PUBLISHERS,
                BoardgameEntry.COLUMN_THUMBNAIL

        };

        public static final int COLNUM_ID = 0;
        public static final int COLNUM_BGG_ID = 1;
        public static final int COLNUM_NAME = 2;
        public static final int COLNUM_YEAR_PUBLISHED = 3;
        public static final int COLNUM_RANK = 4;
        public static final int COLNUM_IMAGE = 5;
        public static final int COLNUM_DESCRIPTION = 6;
        public static final int COLNUM_MIN_PLAYERS = 7;
        public static final int COLNUM_MAX_PLAYERS = 8;
        public static final int COLNUM_MIN_PLAY_TIME = 9;
        public static final int COLNUM_MAX_PLAY_TIME = 10;
        public static final int COLNUM_MIN_AGE = 11;
        public static final int COLNUM_CATEGORIES = 12;
        public static final int COLNUM_MECHANICS = 13;
        public static final int COLNUM_FAMILIES = 14;
        public static final int COLNUM_DESIGNERS = 15;
        public static final int COLNUM_PUBLISHERS = 16;
        public static final int COLNUM_THUMBNAIL = 17;

    }

    public static final class UpdateDetailQuery{

        public static final String SELECTION = BoardgameEntry.COLUMN_BGG_ID + " = ? ";

    }


}
