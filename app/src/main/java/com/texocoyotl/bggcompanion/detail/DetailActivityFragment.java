package com.texocoyotl.bggcompanion.detail;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import rx.Observable;
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

    @BindView(R.id.detail_image)
    ImageView mImageView;


    @BindView(R.id.detail_expand_text_view_description)
    ExpandableTextView mDescriptionView;

    public DetailActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDetailUri = getActivity().getIntent().getParcelableExtra(HotListActivity.BUNDLE_KEY_DETAIL_URI);
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

            mDescriptionView.setText(item.getDescription());
            Glide.with(this).load("http:" + item.getThumbnail()).into(mImageView);

        }
        else{
            downloadDetailData();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void downloadDetailData() {

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

                        return count;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer result) {
                        if (result > 0)
                            getActivity().getSupportLoaderManager().restartLoader(DetailActivityFragment.DETAIL_LOADER, null, DetailActivityFragment.this);
                    }
                });
    }
}
