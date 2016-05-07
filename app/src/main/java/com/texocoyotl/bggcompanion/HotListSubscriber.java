package com.texocoyotl.bggcompanion;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.texocoyotl.bggcompanion.database.Contract;
import com.texocoyotl.bggcompanion.xmlpojo.hotlist.HotListResult;
import com.texocoyotl.bggcompanion.xmlpojo.hotlist.Item;

import java.util.ArrayList;
import java.util.List;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

public class HotListSubscriber extends Subscriber<HotListResult> {
    private static final String TAG = "TAG_";
    private static final int MAX_HOTLIST_RESULTS = 30;
    private final HotListActivity mContext;

    public HotListSubscriber(HotListActivity context) {
        super();
        mContext = context;
    }

    @Override
    public void onNext(HotListResult result) {
        List<ContentValues> values = new ArrayList<ContentValues>();

        List<Item> items = result.getItems();
        for(int i = 0; i < items.size() && i < MAX_HOTLIST_RESULTS; i++){
            Item item = items.get(i);
            ContentValues value = new ContentValues();
            value.put(Contract.BoardgameEntry.COLUMN_BGG_ID, item.getId());
            value.put(Contract.BoardgameEntry.COLUMN_NAME, item.getName().getValue());
            value.put(Contract.BoardgameEntry.COLUMN_THUMBNAIL, item.getThumbnail().getValue());
            value.put(Contract.BoardgameEntry.COLUMN_YEAR_PUBLISHED, item.getYearpublished().getValue());
            value.put(Contract.BoardgameEntry.COLUMN_RANK, item.getRank());

            values.add(value);
        }

        if ( values.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[values.size()];
            values.toArray(cvArray);
            mContext.getContentResolver().bulkInsert(Contract.BoardgameEntry.CONTENT_URI, cvArray);
        }

        Log.d(TAG, "Rows inserted " + values.size());

    }

    @Override
    public void onCompleted() {
        mContext.getSupportLoaderManager().restartLoader(HotListActivity.HOT_LIST_LOADER, null, mContext);
    }

    @Override
    public void onError(Throwable e) {
        // cast to retrofit.HttpException to get the response code
        if (e instanceof HttpException) {
            HttpException response = (HttpException)e;
            int code = response.code();
        }
    }
}
