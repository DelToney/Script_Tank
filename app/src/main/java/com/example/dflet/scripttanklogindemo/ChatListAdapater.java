package com.example.dflet.scripttanklogindemo;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatListAdapater  extends RecyclerView.Adapter<ChatListAdapater.MyViewHolder>  {

        private Activity activity;
        private final ArrayList<String> names;
        private static ChatListClickListener mListener;




        public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            // each data item is just a string in this case

            View view;
            TextView userView;
            ImageView profileView;
            public MyViewHolder(View v) {
                super(v);
                v.setOnClickListener(this);
                userView = v.findViewById(R.id.chatNames);
                profileView = v.findViewById(R.id.profilePicChatImageView);
            }

            @Override
            public void onClick(View v) {
                mListener.onItemClicked(getAdapterPosition());
            }
        }

        public void setOnItemClickListener(ChatListClickListener listener) {
            ChatListAdapater.mListener = listener;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ChatListAdapater.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
            LayoutInflater inflater=activity.getLayoutInflater();
            View rowView=inflater.inflate(R.layout.chat_list_item, null,true);


            MyViewHolder vh = new MyViewHolder(rowView);
            return vh;
        }


        public ChatListAdapater(Activity context, ArrayList<String> names) {

            super();
            this.activity=context;

            this.names = names;



        }

        @Override
        public void onBindViewHolder(ChatListAdapater.MyViewHolder holder, int position) {
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

        public interface ChatListClickListener {
            void onItemClicked(int pos);
        }

}
