package com.example.prototype.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prototype.ViewPatchActivity;
import com.example.prototype.Model.Patch;
import com.example.prototype.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private final Context mContext;
    private final List<Patch> mPatches;

    public UserAdapter(android.content.Context mContext, List<Patch> mPatches){
        this.mContext = mContext;
        this.mPatches = mPatches;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        android.view.View view = LayoutInflater.from(mContext).inflate(R.layout.patch_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Patch patch = mPatches.get(position);
        assert patch != null;
        holder.patch_title.setText(patch.getTitle());

        if (patch.getImageURL().equals("default")){
            holder.patch_image.setImageResource(R.mipmap.ic_launcher);
        }
        else{
            Glide.with(mContext).load(patch.getImageURL()).into(holder.patch_image);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ViewPatchActivity.class);
                intent.putExtra("patchId", patch.getId());
                System.out.println(patch.getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPatches.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView patch_title;
        public ImageView patch_image;

        public ViewHolder(android.view.View itemView){
            super(itemView);

            patch_title = itemView.findViewById(R.id.patch_title);
            patch_image = itemView.findViewById(R.id.patch_image);
        }

    }

}
