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

        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_BGG_ID + " INTEGER UNIQUE NOT NULL," +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_THUMBNAIL + " TEXT NOT NULL, " +
                COLUMN_YEAR_PUBLISHED + " TEXT NOT NULL, " +
                COLUMN_RANK + " INTEGER NOT NULL " +
                " );";

        public static Uri buildBoardGameUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static int getIDFromUri(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(1));
        }
    }
}
