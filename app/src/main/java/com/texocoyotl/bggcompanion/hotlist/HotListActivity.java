package com.texocoyotl.bggcompanion.hotlist;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.crashlytics.android.Crashlytics;
import com.texocoyotl.bggcompanion.BuildConfig;
import com.texocoyotl.bggcompanion.R;
import com.texocoyotl.bggcompanion.about.AboutActivity;
import com.texocoyotl.bggcompanion.database.Contract;
import com.texocoyotl.bggcompanion.database.HotListItemData;
import com.texocoyotl.bggcompanion.detail.DetailActivity;
import com.texocoyotl.bggcompanion.search.SearchActivity;
import com.texocoyotl.bggcompanion.xmlpojo.APIServices;
import com.texocoyotl.bggcompanion.xmlpojo.hotlist.HotListResult;
import com.texocoyotl.bggcompanion.xmlpojo.hotlist.Item;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class HotListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor>,
        HotListAdapter.OnListFragmentInteractionListener {

    public static final int HOT_LIST_LOADER = 0;
    private static final String TAG = HotListActivity.class.getSimpleName() + "TAG_";
    private static final String lastDownloadKey = "LAST_DOWNLOAD";
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;

    private HotListAdapter mAdapter;
    private Subscription mHotListSubscription;

    @BindView(R.id.hotlist)
    RecyclerView mRecyclerView;

    @BindView(R.id.loadingPanel)
    RelativeLayout mLoadingPanel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        setTitle(R.string.app_name_hotlist);
        initViews();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        long lastSync = prefs.getLong(lastDownloadKey, 0);

        if (System.currentTimeMillis() - lastSync >= DAY_IN_MILLIS) {
            Uri bgUri = Contract.BoardgameEntry.CONTENT_URI;
            getContentResolver().delete(bgUri, null, null);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(lastDownloadKey, System.currentTimeMillis());
            editor.apply();
        }

        getSupportLoaderManager().initLoader(HOT_LIST_LOADER, null, this);

        //TODO: EVALUATE IF FETCHING DETAIL DATA IN TWO STEPS OR ONE BIG STEP
        //TODO: USE FIELD LAST_UPDATED AS FILTER WHEN SYNC-ING

    }

    private void initViews() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        int columnCount = getResources().getInteger(R.integer.hotlist_column_count);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, columnCount));

        mAdapter = new HotListAdapter(this, null);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void onDestroy() {
        if (mHotListSubscription != null)
            mHotListSubscription.unsubscribe();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.home, menu);
//        return true;
//    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_about) {
            startActivity(new Intent(this, AboutActivity.class));
        } else if (id == R.id.nav_search){
            startActivity(new Intent(this, SearchActivity.class));
        }

//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri boardgamesURI = Contract.BoardgameEntry.CONTENT_URI;

        return new CursorLoader(
                this,
                boardgamesURI,
                Contract.HotListQuery.COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);

        if (data.moveToFirst()) {
            mLoadingPanel.setVisibility(View.GONE);
            Log.d(TAG, "logRows: " + data.getCount());
        } else {
            downloadHotListData();
        }
    }

    private void downloadHotListData() {
        mLoadingPanel.setVisibility(View.VISIBLE);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork == null) {
            mLoadingPanel.setVisibility(View.GONE);
            Snackbar.make(mRecyclerView, getString(R.string.snackbar_no_internet_initial), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.snackbar_action_retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            downloadHotListData();
                        }
                    })
                    .show();
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_API_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        APIServices apiService = retrofit.create(APIServices.class);

        Observable<HotListResult> mHotListAPIcall = apiService.getHotList();

        mHotListSubscription = mHotListAPIcall
                .subscribeOn(Schedulers.newThread())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.d(TAG, "onError: " + throwable.getMessage());
                    }
                })
                .map(new Func1<HotListResult, Integer>() {
                    @Override
                    public Integer call(HotListResult result) {
                        List<ContentValues> values = new ArrayList<ContentValues>();

                        List<Item> items = result.getItems();
                        for (int i = 0; i < items.size() && i < 30; i++) {
                            Item item = items.get(i);
                            values.add(HotListItemData.getContentValue(item));
                        }

                        if (values.size() > 0) {
                            ContentValues[] cvArray = new ContentValues[values.size()];
                            values.toArray(cvArray);
                            getContentResolver().bulkInsert(Contract.BoardgameEntry.CONTENT_URI, cvArray);
                        }

                        Log.d(TAG, "Rows inserted " + values.size() + " " + Thread.currentThread());

                        return values.size();
                    }

                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mLoadingPanel.setVisibility(View.GONE);
                        Log.d(TAG, "onError: " + e.getMessage());
                        Snackbar.make(mRecyclerView, getString(R.string.snackbar_parse_error), Snackbar.LENGTH_INDEFINITE)
                                .setAction(getString(R.string.snackbar_action_retry), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        downloadHotListData();
                                    }
                                })
                                .show();
                    }

                    @Override
                    public void onNext(Integer result) {
                        mLoadingPanel.setVisibility(View.GONE);
                        if (result > 0)
                            getSupportLoaderManager().restartLoader(HotListActivity.HOT_LIST_LOADER, null, HotListActivity.this);
                    }
                });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onListFragmentInteraction(View view, HotListItemData item) {
        Log.d(TAG, "onListFragmentInteraction: " + item.getName());
        Intent i = new Intent(this, DetailActivity.class);
        i.putExtra(DetailActivity.BUNDLE_KEY_DETAIL_URI, Contract.BoardgameEntry.buildBoardGameUri(item.getBggId()));

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                new Pair<View, String>(view, getString(R.string.transition_name_image))
        );
        ActivityCompat.startActivity(HotListActivity.this, i, options.toBundle());
    }
}
