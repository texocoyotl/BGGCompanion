package com.texocoyotl.bggcompanion.detail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.texocoyotl.bggcompanion.R;
import com.texocoyotl.bggcompanion.database.Contract;
import com.texocoyotl.bggcompanion.hotlist.HotListActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements DetailActivityFragment.OnFragmentCallback{

    @BindView(R.id.detail_image)
    ImageView mImageView;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolBar;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    private String mBGGID;
    private String mTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Uri mDetailUri = getIntent().getParcelableExtra(HotListActivity.BUNDLE_KEY_DETAIL_URI);
        mBGGID = Contract.BoardgameEntry.getIDFromUri(mDetailUri);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void loadHeader(String url, String title) {
        Glide.with(this).load(url).into(mImageView);
        mTitle = title;
        mCollapsingToolBar.setTitle(title);
        mCollapsingToolBar.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(createShareIntent());
            }
        });

    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");

        String text = getString(R.string.share_text, mTitle, mBGGID);

        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        return shareIntent;
    }
}
