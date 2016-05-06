package com.texocoyotl.bggcompanion.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class BGGCContentProvider extends ContentProvider {
    public BGGCContentProvider() {
    }

    private DatabaseHelper mDatabaseHelper;

    static final int MATCH_BOARDGAMES = 100;
    static final int MATCH_BOARDGAMES_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = Contract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, Contract.PATH_GAMES, MATCH_BOARDGAMES);
        matcher.addURI(authority, Contract.PATH_GAMES + "/#", MATCH_BOARDGAMES_WITH_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case MATCH_BOARDGAMES_WITH_ID:
                return Contract.BoardgameEntry.CONTENT_ITEM_TYPE;
            case MATCH_BOARDGAMES:
                return Contract.BoardgameEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        switch (sUriMatcher.match(uri)) {
            // "boardgames/*#"
            case MATCH_BOARDGAMES_WITH_ID: {
                int bggID = Contract.BoardgameEntry.getIDFromUri(uri);

                selection = Contract.BoardgameEntry.TABLE_NAME +
                        "." + Contract.BoardgameEntry.COLUMN_BGG_ID + " = ? ";
                selectionArgs = new String[]{String.valueOf(bggID)};

                return mDatabaseHelper.getReadableDatabase().query(
                        Contract.BoardgameEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
            }

            // "boardgames/"
            case MATCH_BOARDGAMES: {

                //if (null != selection && !selection.equals("")) selection += " AND ";
                selection = Contract.BoardgameEntry.COLUMN_RANK + " > 0";

                sortOrder = Contract.BoardgameEntry.COLUMN_RANK;

                return mDatabaseHelper.getReadableDatabase().query(
                        Contract.BoardgameEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase sqLiteDatabase = mDatabaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int count = 0;
        Uri returnUri;
        if ( null == selection ) selection = "1";
        switch (match) {
            case MATCH_BOARDGAMES:
                count = sqLiteDatabase.delete(Contract.BoardgameEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (count > 0) getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase sqLiteDatabase = mDatabaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        long resultId;
        Uri returnUri;

        switch (match) {
            case MATCH_BOARDGAMES:
                resultId = sqLiteDatabase.insert(Contract.BoardgameEntry.TABLE_NAME, null, values);
                if (resultId > 0) {
                    returnUri = ContentUris.withAppendedId(Contract.BoardgameEntry.CONTENT_URI, resultId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MATCH_BOARDGAMES:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(Contract.BoardgameEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
