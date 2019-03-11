package com.example.dflet.scripttanklogindemo;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class WriterRequestListAdapter extends RecyclerView.Adapter<WriterRequestListAdapter.MyViewHolder>  {

    private Activity activity;
    private final ArrayList<String> names;
    private static WriterClickListener mListener;



    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case

        View view;
        TextView userView;
        ImageView profileView;
        public MyViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            userView = v.findViewById(R.id.writerNames);
            profileView = v.findViewById(R.id.profileImageView);
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClicked(getAdapterPosition());
        }
    }

    public void setOnItemClickListener(WriterClickListener listener) {
        WriterRequestListAdapter.mListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public WriterRequestListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        LayoutInflater inflater=activity.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.writer_list_item, null,true);


        MyViewHolder vh = new MyViewHolder(rowView);
        return vh;
    }


    public WriterRequestListAdapter(Activity context, ArrayList<String> names
                                    ) {

        super();
        this.activity=context;

        this.names = names;

       // this.keys = keys;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.userView.setText(names.get(position));
        holder.profileView.setImageResource(R.drawable.ic_person_black_24dp);


        return;

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return names.size();
    }

    public interface WriterClickListener {
        void onItemClicked(int pos);
    }



}
