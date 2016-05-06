package com.texocoyotl.bggcompanion;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.texocoyotl.bggcompanion.database.Contract;
import com.texocoyotl.bggcompanion.xmlpojo.APIServices;
import com.texocoyotl.bggcompanion.xmlpojo.hotlist.HotListResult;
import com.texocoyotl.bggcompanion.xmlpojo.hotlist.Item;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class HotListDownloader extends IntentService {
    private static final String TAG = HotListDownloader.class.getSimpleName() + "TAG_";

    public static final String BASE_URL = "http://www.boardgamegeek.com/xmlapi2/";

    public HotListDownloader() {
        super("HotListDownloader");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(SimpleXmlConverterFactory.create())
                    .build();

            APIServices apiService = retrofit.create(APIServices.class);

            String type = "boardgame";

            Call<HotListResult> call = apiService.getHotList(type);

            HotListResult result = null;
            List<ContentValues> values = new ArrayList<ContentValues>();

            try {
                result = call.execute().body();
                Log.d(TAG, "Data fetched: " + result);

                for(Item item: result.getItems()){
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
                    getContentResolver().bulkInsert(Contract.BoardgameEntry.CONTENT_URI, cvArray);
                }

                Log.d(TAG, "onHandleIntent: Rows inserted " + values.size());

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
