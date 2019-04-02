package com.example.dflet.scripttanklogindemo;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class SearchResultContainer extends RecyclerView.ViewHolder {
    public TextView title;
    public TextView writer;

    public SearchResultContainer(@NonNull View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.bookTitle);
        writer = itemView.findViewById(R.id.authorName);
    }
}
