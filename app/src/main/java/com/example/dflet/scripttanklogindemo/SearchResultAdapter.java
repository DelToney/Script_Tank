package com.example.dflet.scripttanklogindemo;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewAdapterViewHolder> {
    private ArrayList<WriterSearchResult> mItemList;
    public onItemClickListener mListener;

    public interface onItemClickListener {
        void OnItemClick(int position);

    }

    public void setOnItemClickListener(onItemClickListener listener) {

    }






    //view holder: gets values and assigns them for us
    public class ViewAdapterViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;

        public ViewAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.profilePicture);
            mTextView1 = itemView.findViewById(R.id.ideaTitle);
            mTextView2 = itemView.findViewById(R.id.authorName);
        }
    }

    public SearchResultAdapter(ArrayList<WriterSearchResult> itemList) {
        mItemList = itemList;

    }

    @NonNull

    @Override
    public ViewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_writer_search_result, parent, false);
        ViewAdapterViewHolder vavh = new ViewAdapterViewHolder(v);
        return vavh;
    }
    //goes through each element and makes a card for each
    @Override
    public void onBindViewHolder(@NonNull ViewAdapterViewHolder holder, int position) {
        WriterSearchResult currentItem = mItemList.get(position);

        holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.mTextView1.setText(currentItem.getText1());
        holder.mTextView2.setText(currentItem.getText2());



    }

    @Override
    public int getItemCount() {
        return  mItemList == null ? 0 : mItemList.size();
    }
}
