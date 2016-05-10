package com.texocoyotl.bggcompanion.about;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.texocoyotl.bggcompanion.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.about_version)
    TextView mVersionView;

    @BindView(R.id.fab)
    FloatingActionButton mFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            mVersionView.setText(getString(R.string.version_label, version));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (mFAB != null) {
            mFAB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new LibsBuilder().withActivityTitle(getString(R.string.about_libraries_title))
                            //provide a style (optional) (LIGHT, DARK, LIGHT_DARK_TOOLBAR)
                            .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                            //start the activity
                            .start(AboutActivity.this);
                }
            });
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
