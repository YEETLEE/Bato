package com.example.prototype.Adapter;

import android.annotation.SuppressLint;
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
import com.example.prototype.Model.User;
import com.example.prototype.ViewPatchActivity;
import com.example.prototype.Model.Patch;
import com.example.prototype.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PatchesAdapter extends RecyclerView.Adapter<PatchesAdapter.ViewHolder> {

    private final Context mContext;
    private final List<Patch> mPatches;
    DatabaseReference reference;

    public PatchesAdapter(android.content.Context mContext, List<Patch> mPatches) {
        this.mContext = mContext;
        this.mPatches = mPatches;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        android.view.View view = LayoutInflater.from(mContext).inflate(R.layout.patch_item_miso, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final Patch patch = mPatches.get(position);
        assert patch != null;
        holder.patch_title.setText(patch.getTitle());
        holder.patch_description.setText(patch.getDescription());

        if (patch.getImageURL().equals("default")) {
            holder.patch_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(patch.getImageURL()).into(holder.patch_image);
        }
        reference = FirebaseDatabase.getInstance().getReference("Patches");

        reference = FirebaseDatabase.getInstance().getReference("Users").child(patch.getSender());
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User sender_user = snapshot.getValue(User.class);
                holder.patch_sender.setText(sender_user.getFirstName()+" "+sender_user.getLastName());
                Glide.with(mContext).load(sender_user.getImageURL()).into(holder.sender_image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


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

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView patch_image;
        public ImageView sender_image;
        public TextView patch_title;
        public TextView patch_description;
        public TextView patch_sender;

        public ViewHolder(android.view.View itemView) {
            super(itemView);

            patch_image = itemView.findViewById(R.id.patch_image);
            sender_image = itemView.findViewById(R.id.sender_image);
            patch_title = itemView.findViewById(R.id.patch_title);
            patch_description = itemView.findViewById(R.id.patch_description);
            patch_sender = itemView.findViewById(R.id.patch_sender);
        }

    }

}
