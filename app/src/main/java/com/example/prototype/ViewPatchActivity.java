package com.example.prototype;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.prototype.Model.Patch;
import com.example.prototype.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ViewPatchActivity extends AppCompatActivity {

    TextView patch_title;
    TextView patch_description;
    TextView patch_city;
    TextView patch_sender;
    ImageView patch_image;
    TextView patch_date;

    FirebaseUser firebaseUser;
    DatabaseReference reference;
    DatabaseReference reference2;

    public User sender_user;
    public Patch patch;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_patch);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Zakrpi Me");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        patch_title = findViewById(R.id.patch_title);
        patch_description = findViewById(R.id.patch_description);
        patch_city = findViewById(R.id.patch_city);
        patch_sender = findViewById(R.id.patch_sender);
        patch_image = findViewById(R.id.patch_image);
        patch_date = findViewById(R.id.patch_date);

        intent = getIntent();
        final String patchId = intent.getStringExtra("patchId");
        assert patchId != null;

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Patches").child(patchId);

        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                patch = snapshot.getValue(Patch.class);
                assert patch != null;
                patch_title.setText(patch.getTitle());
                patch_description.setText(patch.getDescription());
                patch_city.setText(patch.getCity());
                patch_date.setText(patch.getDate());
                Picasso.with(ViewPatchActivity.this).load(patch.getImageURL()).into(patch_image);

                reference2 = FirebaseDatabase.getInstance().getReference("Users").child(patch.getSender());
                reference2.addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        sender_user = snapshot.getValue(User.class);
                        patch_sender.setText(sender_user.getFirstName()+" "+sender_user.getLastName());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }

}