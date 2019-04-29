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

public class ChatMessagesAdapter extends RecyclerView.Adapter {

    private Activity activity;
    private String user_id;
    private final ArrayList<String> messages, ids;
    private static ChatClickListener mListener;
    private LinearLayout layout;


    public static class MyRecvViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case

        View view;
        TextView messageContent;

        public MyRecvViewHolder(View v) {
            super(v);
            //v.setOnClickListener(this);
           messageContent = v.findViewById(R.id.messageContent);
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClicked(getAdapterPosition());
        }
    }

    public static class MySentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case

        View view;
        TextView messageContent;

        public MySentViewHolder(View v) {
            super(v);
            //v.setOnClickListener(this);
            messageContent = v.findViewById(R.id.messageContent);
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClicked(getAdapterPosition());
        }
    }

    @Override
    public int getItemViewType(int position) {


        if (ids.get(position).compareTo(this.user_id) == 0) {
            // If the current user is the sender of the message
            return 1;
        } else {
            // If some other user sent the message
            return 0;
        }
    }

    public void setOnItemClickListener(ChatClickListener listener) {
        mListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
       LayoutInflater inflater = activity.getLayoutInflater();

       if (viewType == 0) {


           View rowView = inflater.inflate(R.layout.chat_item, null, true);


           MyRecvViewHolder vh = new MyRecvViewHolder(rowView);
           return vh;
       } else if (viewType == 1) {
           View rowView = inflater.inflate(R.layout.chat_item_sent, null, true);


           MySentViewHolder vh = new MySentViewHolder(rowView);
           return vh;
       }

       return null;
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (holder.getItemViewType() == 0) {
                MyRecvViewHolder h = ((MyRecvViewHolder)holder);
                h.messageContent.setText(messages.get(position));


            } else {
                MySentViewHolder h = ((MySentViewHolder) holder);
                h.messageContent.setText(messages.get(position));
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