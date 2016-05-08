package com.texocoyotl.bggcompanion.detail;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.texocoyotl.bggcompanion.R;
import com.texocoyotl.bggcompanion.database.Contract;
import com.texocoyotl.bggcompanion.database.DetailItemData;
import com.texocoyotl.bggcompanion.hotlist.HotListActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = "DetailTAG_";
    private static final int DETAIL_LOADER = 1;
    private Uri detailUri;

    @BindView(R.id.detail_description)
    TextView mDetailView;

    @BindView(R.id.detail_image)
    ImageView mImageView;

    public DetailActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailUri = getActivity().getIntent().getParcelableExtra(HotListActivity.BUNDLE_KEY_DETAIL_URI);
        Log.d(TAG, "onCreate: " + detailUri.toString());


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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                detailUri,
                Contract.DetailQuery.COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        DetailItemData item = DetailItemData.fromCursor(data);
        if (item != null && !item.getImage().equals("")) {
            mDetailView.setText(item.getDescription());
            Glide.with(this).load("http:" + item.getImage()).into(mImageView);
        }
        else{

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
