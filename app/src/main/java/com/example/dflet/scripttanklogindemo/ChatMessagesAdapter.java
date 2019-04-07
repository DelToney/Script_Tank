package com.example.dflet.scripttanklogindemo;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatMessagesAdapter extends RecyclerView.Adapter<ChatMessagesAdapter.MyViewHolder> {

    private Activity activity;
    private String user_id;
    private final ArrayList<String> messages, ids;
    private static ChatClickListener mListener;
    private LinearLayout layout;


    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case

        View view;
        TextView messageContent;

        public MyViewHolder(View v) {
            super(v);
            //v.setOnClickListener(this);
           messageContent = v.findViewById(R.id.messageContent);
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClicked(getAdapterPosition());
        }
    }

    public void setOnItemClickListener(ChatClickListener listener) {
        mListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
       LayoutInflater inflater = activity.getLayoutInflater();




        View rowView = inflater.inflate(R.layout.chat_item, null, true);




        MyViewHolder vh = new MyViewHolder(rowView);
        return vh;
    }


    public ChatMessagesAdapter(Activity context, ArrayList<String> messageData, ArrayList<String> ids,
                               String user_id) {

        super();
        this.activity = context;

        this.messages = messageData;
        this.ids = ids;

        this.user_id = user_id;

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.messageContent.setText(messages.get(position));
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.messageContent.getLayoutParams();
        if (ids.get(position).compareTo(this.user_id) != 0) {

            params.addRule(RelativeLayout.ALIGN_PARENT_START);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
           //holder.messageContent.setBackgroundColor(Color.MAGENTA);
            holder.messageContent.setLayoutParams(params);
        } else {
          //  holder.messageContent.setBackgroundColor(Color.GREEN);
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.messageContent.setLayoutParams(params);
        }



        return;

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return messages.size();
    }

    public interface ChatClickListener {
        void onItemClicked(int pos);
    }
}