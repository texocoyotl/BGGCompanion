package com.texocoyotl.bggcompanion.search;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.texocoyotl.bggcompanion.BuildConfig;
import com.texocoyotl.bggcompanion.R;
import com.texocoyotl.bggcompanion.database.Contract;
import com.texocoyotl.bggcompanion.detail.DetailActivity;
import com.texocoyotl.bggcompanion.xmlpojo.APIServices;
import com.texocoyotl.bggcompanion.xmlpojo.search.Item;
import com.texocoyotl.bggcompanion.xmlpojo.search.SearchResult;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SearchActivity extends AppCompatActivity implements SearchAdapter.OnListInteractionListener{

    private static final String TAG = "SearchTAG_";
    private Subscription mSearchSubscription;
    private SearchAdapter mAdapter;

    @BindView(R.id.search_edit)
    EditText mSearchEdit;

    @BindView(R.id.search_input_layout)
    TextInputLayout mSearchInputLayout;

    @BindView(R.id.search_list)
    RecyclerView mSearchResultsList;

    @BindView(R.id.loadingPanel)
    RelativeLayout mLoadingPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onDestroy() {
        if (mSearchSubscription != null) mSearchSubscription.unsubscribe();
        super.onDestroy();
    }

    @OnClick(R.id.a_search_button)
    void doSearch(View v){


        String searchText = mSearchEdit.getText().toString();
        if (searchText.length() <= 3){
            mSearchInputLayout.setError(getString(R.string.search_edit_size_error));
        } else {
            mSearchInputLayout.setError(null);
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            mLoadingPanel.setVisibility(View.VISIBLE);
            mSearchResultsList.setAdapter(null);
            downloadSearchResults(searchText);
        }
    }

    private void downloadSearchResults(final String searchText) {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork == null) {
            Snackbar.make(mSearchEdit, getString(R.string.snackbar_no_internet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.snackbar_action_retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            downloadSearchResults(searchText);
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

        Observable<SearchResult> mSearchAPICall = apiService.getSearch(searchText);

        mSearchSubscription = mSearchAPICall
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SearchResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mLoadingPanel.setVisibility(View.GONE);
                        Log.d(TAG, "onError: " + e.getMessage());
                        Snackbar.make(mSearchEdit, getString(R.string.search_snackbar_no_results), Snackbar.LENGTH_SHORT)
                                .show();
                    }

                    @Override
                    public void onNext(SearchResult result) {

                        List<Item> items = result.getItems();

                        if (BuildConfig.DEBUG) Log.d(TAG, "onNext: " + items);

                        mLoadingPanel.setVisibility(View.GONE);

                        if (items.size() > 0) {
                            mAdapter = new SearchAdapter(SearchActivity.this, items);
                            mSearchResultsList.setAdapter(mAdapter);
                        }
                    }
                });


    }

    @Override
    public void onListInteraction(Item item) {
        Intent i = new Intent(this, DetailActivity.class);
        i.putExtra(DetailActivity.BUNDLE_KEY_DETAIL_URI, Contract.BoardgameEntry.buildBoardGameUri(item.getId()));
        startActivity(i);
    }
}
