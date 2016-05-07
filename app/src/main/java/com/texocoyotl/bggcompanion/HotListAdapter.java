package com.texocoyotl.bggcompanion;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.texocoyotl.bggcompanion.database.HotListItemData;


public class HotListAdapter extends CursorRecyclerViewAdapter<HotListAdapter.ViewHolder> {

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;
    private static final String TAG = "LOGTAG_";
    private final Context mListener;
    public View mActivated;
    public int mActivatedPos;
    private boolean mUseTodayRow;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mRow;
        private final TextView mBGGIDView;
        private final TextView mNameView;

        public ViewHolder(View view) {
            super(view);
            mRow = view;

            mNameView = (TextView) view.findViewById(R.id.hotlist_row_name);
            mBGGIDView = (TextView) view.findViewById(R.id.hotlist_row_id);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }

    public HotListAdapter(Context context, Cursor cursor) {
        super(context,cursor);
        mListener = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.hot_list_row,
                        parent, false);
        return new ViewHolder(view);
    }

    @SuppressWarnings("ResourceType")
    @Override
    public void onBindViewHolder(final ViewHolder holder, Cursor cursor) {
        final HotListItemData item = HotListItemData.fromCursor(cursor);
        final int position = cursor.getPosition();

        holder.mBGGIDView.setText(String.valueOf(item.getBggId()));
        holder.mNameView.setText(item.getName());

        holder.mRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    ((OnListFragmentInteractionListener) mListener).onListFragmentInteraction(item);
                }
            }
        });
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(HotListItemData item);
    }
}
