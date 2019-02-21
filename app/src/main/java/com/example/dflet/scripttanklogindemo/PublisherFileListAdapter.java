package com.example.dflet.scripttanklogindemo;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class PublisherFileListAdapter extends ArrayAdapter<String>
{
    private Activity activity;
    private final ArrayList<String> names, files;

    public PublisherFileListAdapter(Activity context, ArrayList<String> names,
                                    ArrayList<String> files) {

        super(context, R.layout.file_list_item, names);
        this.activity=context;
        this.files = files;
        this.names = names;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        LayoutInflater inflater=activity.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.file_list_item, null,true);

        TextView userView = rowView.findViewById(R.id.file_usernames);
        TextView fileView = rowView.findViewById(R.id.file_filenames);

        userView.setText(names.get(position));
        fileView.setText(files.get(position));




        return rowView;
    }
}
