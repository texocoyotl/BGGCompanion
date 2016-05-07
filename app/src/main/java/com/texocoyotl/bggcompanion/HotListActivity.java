package com.texocoyotl.bggcompanion;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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

import com.crashlytics.android.Crashlytics;
import com.texocoyotl.bggcompanion.database.Contract;
import com.texocoyotl.bggcompanion.database.HotListItemData;

import io.fabric.sdk.android.Fabric;

public class HotListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor>,
        HotListAdapter.OnListFragmentInteractionListener {

    private static final String TAG = HotListActivity.class.getSimpleName() + "TAG_";
    private static final int HOT_LIST_LOADER = 0;
    private static final int mColumnCount = 2;
    private RecyclerView mRecyclerView;
    private HotListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_home);
        initViews();

        getSupportLoaderManager().initLoader(HOT_LIST_LOADER, null, this);

        //insertTestRow();


        //TODO: EVALUATE IF FETCHING DETAIL DATA IN TWO STEPS OR ONE BIG STEP

        //TODO: ADD A RECYCLERVIEW WITH A CURSOR ADAPTER THAT GETS SWAPED WITH THE RESULT OF THE LOADER
        //TODO: ADD A DETAIL ACTIVITY THAT FOLLOWS THE SAME LOGIC OF LOCAL DATA / DOWNLOAD.
        //TODO: USE FIELD LAST_UPDATED AS FILTER WHEN SYNC-ING

    }

    private void insertTestRow() {
        Uri bgUri = Contract.BoardgameEntry.CONTENT_URI;

        ContentValues contentValues = new ContentValues();

        contentValues.put(Contract.BoardgameEntry.COLUMN_BGG_ID, 2);
        contentValues.put(Contract.BoardgameEntry.COLUMN_NAME, "Test");
        contentValues.put(Contract.BoardgameEntry.COLUMN_THUMBNAIL, "http://none");
        contentValues.put(Contract.BoardgameEntry.COLUMN_YEAR_PUBLISHED, "2010");
        contentValues.put(Contract.BoardgameEntry.COLUMN_RANK, 1);

        Uri resultUri = getContentResolver().insert(bgUri, contentValues);

        Log.d(TAG, "insertRows: " + resultUri);
    }

    private void initViews() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Uri bgUri = Contract.BoardgameEntry.CONTENT_URI;
                getContentResolver().delete(bgUri, null, null);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.list);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, mColumnCount));

        mAdapter = new HotListAdapter(this, null);
        mRecyclerView.setAdapter(mAdapter);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
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
            do {
                Log.d(TAG, "logRows: " + data.getString(Contract.HotListQuery.COLNUM_NAME));
            } while (data.moveToNext());
        }
        else{
            Intent downloader = new Intent(this, HotListDownloader.class);
            startService(downloader);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onListFragmentInteraction(HotListItemData item) {
        Log.d(TAG, "onListFragmentInteraction: " + item.getName());
    }
}
