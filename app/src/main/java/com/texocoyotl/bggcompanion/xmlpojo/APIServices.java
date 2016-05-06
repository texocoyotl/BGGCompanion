package com.texocoyotl.bggcompanion.xmlpojo;

import com.texocoyotl.bggcompanion.xmlpojo.hotlist.HotListResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIServices {
    @GET("hot")
    Call<HotListResult> getHotList(@Query("type") String type);
}
