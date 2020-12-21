package com.example.prototype.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prototype.Adapter.UserAdapter;
import com.example.prototype.Model.Patch;
import com.example.prototype.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {

    private RecyclerView recycleView;

    private UserAdapter userAdapter;
    private List<Patch> mPatches;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        recycleView = view.findViewById(R.id.recycler_view);
        recycleView.setHasFixedSize(true);
        recycleView.setLayoutManager(new LinearLayoutManager(getContext()));

        mPatches = new ArrayList<Patch>();

        readPatches();

        return view;
    }

    private void readPatches(){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Patches");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mPatches.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Patch patch = snapshot.getValue(Patch.class);

                    assert patch != null;
                    assert firebaseUser != null;
                    if (!patch.getId().equals(firebaseUser.getUid())){
                        mPatches.add(patch);
                    }
                }

                userAdapter = new UserAdapter(getContext(), mPatches);
                recycleView.setAdapter(userAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}