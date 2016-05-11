package com.texocoyotl.bggcompanion.search;

import android.content.Intent;
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
import rx.functions.Action1;
import rx.functions.Func1;
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
        if (v != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }

        String searchText = mSearchEdit.getText().toString();
        if (searchText.length() <= 0){
            mSearchInputLayout.setError("Cannot be empty");
        } else {
            mSearchInputLayout.setErrorEnabled(false);

            downloadListResults(searchText);
        }
    }

    private void downloadListResults(final String searchText) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_API_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        APIServices apiService = retrofit.create(APIServices.class);

        Observable<SearchResult> mSearchAPICall = apiService.getSearch("boardgame", searchText);

        mSearchSubscription = mSearchAPICall
                .subscribeOn(Schedulers.newThread())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Snackbar.make(mSearchEdit, "You need Internet connection to search", Snackbar.LENGTH_INDEFINITE)
                                .setAction("Retry", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        downloadListResults(searchText);
                                    }
                                })
                                .show();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SearchResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onNext(SearchResult result) {

                        List<Item> items = result.getItems();

                        if (BuildConfig.DEBUG) Log.d(TAG, "onNext: " + items);

                        mAdapter = new SearchAdapter(SearchActivity.this, items);
                        mSearchResultsList.setAdapter(mAdapter);
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
