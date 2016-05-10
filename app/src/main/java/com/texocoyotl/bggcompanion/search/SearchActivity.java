package com.texocoyotl.bggcompanion.search;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.texocoyotl.bggcompanion.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.search_edit)
    EditText mSearchEdit;

    @BindView(R.id.search_input_layout)
    TextInputLayout mSearchInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @OnClick(R.id.a_search_button)
    void doSearch(View v){
        if (v != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }

        String searchText = mSearchEdit.getText().toString();
        if (searchText.length() <= 0){
            mSearchInputLayout.setError("Cannot be empty");
        } else {
            mSearchInputLayout.setErrorEnabled(false);

            searchFor(searchText);
        }
    }

    private void searchFor(String searchText) {

    }

}
