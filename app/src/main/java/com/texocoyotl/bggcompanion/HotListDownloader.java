package com.texocoyotl.bggcompanion;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.texocoyotl.bggcompanion.xmlpojo.APIServices;
import com.texocoyotl.bggcompanion.xmlpojo.hotlist.HotListResult;

import java.io.IOException;

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
            try {
                result = call.execute().body();
                Log.d(TAG, "onResponse: " + result);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
