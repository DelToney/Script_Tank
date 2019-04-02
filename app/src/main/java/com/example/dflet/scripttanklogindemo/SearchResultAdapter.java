package com.example.dflet.scripttanklogindemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.View;

import java.io.Writer;
import java.util.ArrayList;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultAdapterViewHolder> {

    public Context c;
    public ArrayList<WriterSearchResult> arrayList;

    public SearchResultAdapter(Context c, ArrayList<WriterSearchResult> arrayList){
        this.c = c;
        this.arrayList = arrayList;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public SearchResultAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_writer_search_result, viewGroup, false);

        return new SearchResultAdapterViewHolder(v);
    }

    @Override
    public void onBindViewHolder( SearchResultAdapterViewHolder holder, int position) {
        WriterSearchResult writerSearchResult = arrayList.get(position);
        holder.title.setText(writerSearchResult.getTitle());
        holder.writer.setText(writerSearchResult.getWriter());
    }

    public class SearchResultAdapterViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView writer;

        public SearchResultAdapterViewHolder(View itemView){
            super(itemView);
            title = itemView.findViewById(R.id.bookTitle);
            writer = itemView.findViewById(R.id.authorName);
        }
    }

}
