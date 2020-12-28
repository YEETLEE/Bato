package com.example.prototype;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
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

    TextView patch_title,
            patch_description,
            patch_city,
            patch_sender,
            patch_date;
    ImageView patch_image;
    Button patch_delete_btn;

    DatabaseReference reference;
    DatabaseReference reference2;
    FirebaseUser firebaseUser;

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
        patch_delete_btn = findViewById(R.id.patch_delete_btn);

        intent = getIntent();
        final String patchId = intent.getStringExtra("patchId");
        assert patchId != null;

        if (!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
            Intent intent = new Intent(ViewPatchActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        reference = FirebaseDatabase.getInstance().getReference("Patches").child(patchId);
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) return;

                Patch patch = snapshot.getValue(Patch.class);
                if (patch == null) return;

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
                        User sender_user = snapshot.getValue(User.class);
                        if (sender_user == null)
                            return;

                        patch_sender.setText(sender_user.getFirstName() + " " + sender_user.getLastName());

                        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (!sender_user.getId().equals(firebaseUser.getUid()))
                            patch_delete_btn.setVisibility(View.GONE);
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

        patch_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        patch_delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewPatchActivity.this);
                builder.setTitle("Da li ste sigurni da zelite da obrisete krpljenje?");
                builder.setMessage("Ova radnja nije povratna")
                        .setPositiveButton(Html.fromHtml("<font color='#000'>DA</font>"), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                reference = FirebaseDatabase.getInstance().getReference("Patches").child(patchId);
                                reference.addValueEventListener(new ValueEventListener() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (!snapshot.exists()) return;

                                        Patch patch = snapshot.getValue(Patch.class);
                                        if (patch == null) return;

                                        //delete patch from patches
                                        snapshot.getRef().removeValue();
                                        finish();

                                        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                        reference2 = FirebaseDatabase.getInstance().getReference("Users/" + firebaseUser.getUid() + "/myPatches").child(patchId);
                                        reference2.addValueEventListener(new ValueEventListener() {
                                            @SuppressLint("SetTextI18n")
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                //delete patch from user's myparches
                                                snapshot.getRef().removeValue();

                                                Intent i = new Intent(ViewPatchActivity.this, MainActivity.class);
                                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(i);
                                                finish();
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
                        })
                        .setNegativeButton(Html.fromHtml("<font color='#000'>NE</font>"), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                builder.create().show();
            }
        });


    }

}