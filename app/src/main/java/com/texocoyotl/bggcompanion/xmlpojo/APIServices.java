package com.texocoyotl.bggcompanion.xmlpojo;

import com.texocoyotl.bggcompanion.xmlpojo.detail.DetailResult;
import com.texocoyotl.bggcompanion.xmlpojo.hotlist.HotListResult;
import com.texocoyotl.bggcompanion.xmlpojo.search.SearchResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface APIServices {
    @GET("hot?type=boardgame")
    Observable<HotListResult> getHotList();

    @GET("thing")
    Observable<DetailResult> getDetail(@Query("id") String id);

    @GET("search?type=boardgame")
    Observable<SearchResult> getSearch(@Query("query") String query);

}
