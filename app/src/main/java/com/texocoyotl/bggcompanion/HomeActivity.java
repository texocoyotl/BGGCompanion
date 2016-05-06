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
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.texocoyotl.bggcompanion.database.Contract;
import com.texocoyotl.bggcompanion.xmlpojo.APIServices;
import com.texocoyotl.bggcompanion.xmlpojo.hotlist.HotListResult;

import io.fabric.sdk.android.Fabric;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = HomeActivity.class.getSimpleName() + "TAG_";
    private static final int HOT_LIST_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_home);
        initViews();

        getSupportLoaderManager().initLoader(HOT_LIST_LOADER, null, this);

        //insertTestRow();
//        Uri bgUri = Contract.BoardgameEntry.CONTENT_URI;
//        getContentResolver().delete(bgUri, null, null);

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
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data.moveToFirst()) {

            do {
                Log.d(TAG, "logRows: " + data.getString(data.getColumnIndex(Contract.BoardgameEntry.COLUMN_NAME)));
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
}
