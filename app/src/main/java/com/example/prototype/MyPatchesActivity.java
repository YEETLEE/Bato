package com.example.prototype;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.prototype.Adapter.MyPatchesAdapter;
import com.example.prototype.Model.UserPatch;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyPatchesActivity extends AppCompatActivity {

    private RecyclerView recycleView;

    private MyPatchesAdapter myPatchesAdapter;
    private List<UserPatch> mUserPatches;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_patches);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        final String userId = firebaseUser.getUid();

        recycleView = findViewById(R.id.recycler_view);
        recycleView.setHasFixedSize(true);
        recycleView.setLayoutManager(new LinearLayoutManager(MyPatchesActivity.this));

        mUserPatches = new ArrayList<UserPatch>();
        readPatches(userId);
    }
    private void readPatches(final String userId) {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users/"+userId+"/myPatches/");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUserPatches.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot == null) {
                        return;
                    }

                    String idVal = snapshot.getValue().toString();

                    UserPatch up = new UserPatch();
                    up.setId(idVal);
                    up.setValue(idVal);
                    mUserPatches.add(up);

                }

                myPatchesAdapter = new MyPatchesAdapter(MyPatchesActivity.this, mUserPatches);
                recycleView.setAdapter(myPatchesAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}