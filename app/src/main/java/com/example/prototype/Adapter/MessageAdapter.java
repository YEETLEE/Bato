package com.example.prototype.Adapter;

import android.content.Context;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prototype.Model.Chat;
import com.example.prototype.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private final Context mContext;
    private final List<Chat> mChat;
    private final String imageUrl;

    FirebaseUser firebaseUser;

    public MessageAdapter(android.content.Context mContext, List<Chat> mChat, String imageUrl){
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        android.view.View view;
        if (viewType == MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
        }else {
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        Chat chat = mChat.get(position);
        String msg = chat.getMessage();
        assert msg != null;
        holder.msg_content.setText(msg);

        if(imageUrl.equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(mContext).load(imageUrl).into(holder.profile_image);
        }

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView msg_content;
        public ImageView profile_image;

        public ViewHolder(android.view.View itemView){
            super(itemView);

            msg_content = itemView.findViewById(R.id.msg_content);
            profile_image = itemView.findViewById(R.id.profile_image);
        }
    }

    @Override
    public int getItemViewType(int position){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(mChat.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }

    }

}
