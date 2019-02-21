package com.example.dflet.scripttanklogindemo;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class WriterRequestListAdapter extends ArrayAdapter<String> {

    private Activity activity;
    private final ArrayList<String> names;

    public WriterRequestListAdapter(Activity context, ArrayList<String> names
                                    ) {

        super(context, R.layout.writer_list_item, names);
        this.activity=context;

        this.names = names;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        LayoutInflater inflater=activity.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.writer_list_item, null,true);

        TextView userView = rowView.findViewById(R.id.writerNames);
        ImageView profileView = rowView.findViewById(R.id.profileImageView);

        userView.setText(names.get(position));
        profileView.setImageResource(R.drawable.ic_person_black_24dp);


        return rowView;
    }
}
