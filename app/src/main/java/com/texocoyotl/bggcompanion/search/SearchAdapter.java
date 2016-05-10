package com.texocoyotl.bggcompanion.search;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.texocoyotl.bggcompanion.R;
import com.texocoyotl.bggcompanion.database.HotListItemData;
import com.texocoyotl.bggcompanion.xmlpojo.search.Item;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private static final String TAG = "SearchAdapterTAG_";
    private final Context mContext;
    private List<Item> items;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mRow;
        private final TextView mYearPublishedView;
        private final TextView mNameView;

        public ViewHolder(View view) {
            super(view);
            mRow = view;

            mNameView = (TextView) view.findViewById(R.id.search_row_name);
            mYearPublishedView = (TextView) view.findViewById(R.id.search_row_year_published);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }

    public SearchAdapter(Context context, List<Item> items) {
        mContext = context;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.search_row,
                        parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Item item = items.get(position);

        holder.mYearPublishedView.setText(String.valueOf(item.getYearpublished().getValue()));
        holder.mNameView.setText(item.getName().getValue());

        holder.mRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mContext) ((OnListInteractionListener) mContext).onListInteraction(item);
            }
        });
    }

    public interface OnListInteractionListener {
        void onListInteraction(Item item);
    }
}
