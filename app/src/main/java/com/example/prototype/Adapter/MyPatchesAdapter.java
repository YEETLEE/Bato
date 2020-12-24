package com.example.prototype.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prototype.Model.Patch;
import com.example.prototype.Model.UserPatch;
import com.example.prototype.MyPatchesActivity;
import com.example.prototype.R;
import com.example.prototype.ViewPatchActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyPatchesAdapter extends RecyclerView.Adapter<MyPatchesAdapter.ViewHolder> {

    private final Context mContext;
    private final List<UserPatch> mUserPatches;
    DatabaseReference reference;

    public MyPatchesAdapter(Context mContext, List<UserPatch> mUserPatches) {
        this.mContext = mContext;
        this.mUserPatches = mUserPatches;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        android.view.View view = LayoutInflater.from(mContext).inflate(R.layout.patch_item_miso, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final UserPatch up = mUserPatches.get(position);
        assert up != null;

        reference = FirebaseDatabase.getInstance().getReference("Patches");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Patch patch = snapshot.getValue(Patch.class);
                    assert patch != null;

                    if (up.getId().equals(patch.getId())) {
                        holder.patch_title.setText(patch.getTitle());
                        Glide.with(mContext).load(patch.getImageURL()).into(holder.patch_image);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ViewPatchActivity.class);
                intent.putExtra("patchId", up.getId());
                System.out.println(up.getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUserPatches.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView patch_title;
        public ImageView patch_image;

        public ViewHolder(android.view.View itemView) {
            super(itemView);

            patch_title = itemView.findViewById(R.id.patch_title);
            patch_image = itemView.findViewById(R.id.patch_image);
        }

    }

}
