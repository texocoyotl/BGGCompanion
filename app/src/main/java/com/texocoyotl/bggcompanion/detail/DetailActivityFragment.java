package com.texocoyotl.bggcompanion.detail;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.texocoyotl.bggcompanion.BuildConfig;
import com.texocoyotl.bggcompanion.R;
import com.texocoyotl.bggcompanion.database.Contract;
import com.texocoyotl.bggcompanion.database.DetailItemData;
import com.texocoyotl.bggcompanion.hotlist.HotListActivity;
import com.texocoyotl.bggcompanion.xmlpojo.APIServices;
import com.texocoyotl.bggcompanion.xmlpojo.detail.DetailResult;
import com.texocoyotl.bggcompanion.xmlpojo.detail.Item;

import org.simpleframework.xml.core.ValueRequiredException;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = "DetailTAG_";
    private static final int DETAIL_LOADER = 1;
    private Uri mDetailUri;
    private Subscription mDetailSubscription;
    private Context mContext;

    @BindView(R.id.detail_expand_text_view_description) ExpandableTextView mDescriptionView;
    @BindView(R.id.detail_players) TextView mPlayersView;
    @BindView(R.id.detail_play_time) TextView mPlayTimeView;
    @BindView(R.id.detail_min_age) TextView mMinAgeView;
    @BindView(R.id.detail_categories) TextView mCategoriesView;
    @BindView(R.id.detail_mechanics) TextView mMechanicsView;
    @BindView(R.id.detail_families) TextView mFamiliesView;
    @BindView(R.id.detail_designers) TextView mDesignersView;
    @BindView(R.id.detail_publishers) TextView mPublishersView;

    public DetailActivityFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDetailUri = getActivity().getIntent().getParcelableExtra(DetailActivity.BUNDLE_KEY_DETAIL_URI);
        Log.d(TAG, "onCreate: " + mDetailUri.toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getSupportLoaderManager().initLoader(DETAIL_LOADER, null, this);
    }

    @Override
    public void onDestroy() {
        if (mDetailSubscription != null)
            mDetailSubscription.unsubscribe();
        super.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                mDetailUri,
                Contract.DetailQuery.COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        DetailItemData item = DetailItemData.fromCursor(data);
        if (item != null && item.getImage() != null && !item.getImage().equals("")) {

            ((OnFragmentCallback) mContext).loadHeader(
                    "http:" + item.getImage(),
                    item.getName() + " (" + item.getYearPublished() + ")"
            );
            mDescriptionView.setText(Html.fromHtml(item.getDescription()
                    .replace("&#10;", "<br/>")
            ).toString());

            mPlayersView.setText(getString(R.string.detail_players_content, item.getMinPlayers(), item.getMaxPlayers()));
            mPlayTimeView.setText(getString(R.string.detail_play_time_content, item.getMinPlayTime(), item.getMaxPlayTime()));
            mMinAgeView.setText(getString(R.string.detail_min_age_content, item.getMinAge()));

            mCategoriesView.setText(item.getCategories());
            mMechanicsView.setText(item.getMechanics());
            mFamiliesView.setText(item.getFamilies());
            mDesignersView.setText(item.getDesigner());
            mPublishersView.setText(item.getPublishers());

        }
        else{
            downloadDetailData();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void downloadDetailData() {

        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork == null) {
            Snackbar.make(mDescriptionView, getString(R.string.snackbar_no_internet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.snackbar_action_retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            downloadDetailData();
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

        final String bggId = Contract.BoardgameEntry.getIDFromUri(mDetailUri);

        Observable<DetailResult> mDetailAPIcall = apiService.getDetail(bggId);

        mDetailSubscription = mDetailAPIcall
                .subscribeOn(Schedulers.newThread())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.d(TAG, "onError: " + throwable.getMessage());
                    }
                })
                .map(new Func1<DetailResult, Integer>() {
                    @Override
                    public Integer call(DetailResult result) {

                        List<Item> items = result.getItems();

                        //We only expect 1 row of items
                        Item item = items.get(0);

                        if (BuildConfig.DEBUG) Log.d(TAG, "call: " + item);

                        int count = getActivity().getContentResolver().update(
                                Contract.BoardgameEntry.CONTENT_URI,
                                DetailItemData.getContentValue(item),
                                Contract.UpdateDetailQuery.SELECTION,
                                new String[]{bggId}
                        );

                        if (BuildConfig.DEBUG) Log.d(TAG, "Rows updated " + count + " " + Thread.currentThread());

                        if (count == 0){
                            //This means that the detail page was launched from a search, and as most of those games are not on the
                            //hotlist, then we need to insert the new data

                            Uri insertedGame = getActivity().getContentResolver().insert(
                                    Contract.BoardgameEntry.CONTENT_URI,
                                    DetailItemData.getContentValue(item)
                            );

                            if (BuildConfig.DEBUG) Log.d(TAG, "Row inserted " + insertedGame.toString() + " " + Thread.currentThread());
                            if (insertedGame != null) count = 1;
                        }

                        return count;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof ValueRequiredException) Log.d(TAG, "onError: Parsing");
                        Log.d(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onNext(Integer result) {
                        if (result > 0)
                            getActivity().getSupportLoaderManager().restartLoader(DetailActivityFragment.DETAIL_LOADER, null, DetailActivityFragment.this);
                    }
                });
    }

    public interface OnFragmentCallback{
        public void loadHeader(String url, String title);
    }
}
