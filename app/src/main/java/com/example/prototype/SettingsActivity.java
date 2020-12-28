package com.example.prototype;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.prototype.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    ImageButton btn_back, btn_saveData;

    TextView change_photo_text, firstName, lastName, email;

    CircleImageView change_photo_image;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    Uri imageUri;
    private static final int RESULT_LOAD_IMAGE = 1;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    private DatabaseReference databaseReference;
    private String myUri = "";
    private StorageTask uploadTask;
    private FirebaseAuth auth;

//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_settings);
//
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle(""); // Title = Settings
////        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // set up BACK arrow
//
//        auth = FirebaseAuth.getInstance();
//        databaseReference = FirebaseDatabase.getInstance().getReference().child("Image");
//        storageReference = FirebaseStorage.getInstance().getReference().child("Profile Picture");
//
//
//        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
//
//        change_photo_image = findViewById(R.id.change_photo_image);
//        firstName = findViewById(R.id.firstName);
//        lastName = findViewById(R.id.lastName);
//        email = findViewById(R.id.email);
//
//
//        reference.addValueEventListener(new ValueEventListener() { // postavi ime i prezime i sliku u header
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                User user = snapshot.getValue(User.class);
//
//                firstName.setText(user.getFirstName());
//                lastName.setText(user.getLastName());
//                email.setText(user.getEmail());
//
//                if (user.getImageURL().equals("default")) {
//                    change_photo_image.setImageResource(R.mipmap.ic_launcher);
//                } else {
//                    Glide.with(SettingsActivity.this).load(user.getImageURL()).into(change_photo_image);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });
//        btn_back = findViewById(R.id.btn_back);
////        btn_saveData = findViewById(R.id.btn_saveData);
//
//        btn_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
//                finish();
//            }
//        });
//
//    }
//
//        private void getUserInfo () {
//            databaseReference.child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
//                        if (snapshot.hasChild("image")) {
//                            String image = snapshot.child("image").getValue().toString();
//                            Glide.with(SettingsActivity.this).load(image).into(change_photo_image);
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//        }
//
//        public void openFolder () {
//            Intent intent = new Intent();
//            intent.setType("image/*");
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(Intent.createChooser(intent, "Complete action using"), RESULT_LOAD_IMAGE);
//        }
//
//        @Override
//        protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
//            super.onActivityResult(requestCode, resultCode, data);
//            if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
//                imageUri = data.getData();
//                change_photo_image.setImageURI(imageUri);
//
//            }
//        }
//
//        private void uploadFile () {
//            final ProgressDialog pd = new ProgressDialog(this);
//            pd.setTitle("Uploading Image....");
//            pd.show();
//
//            if (imageUri != null) {
//                final StorageReference fileRef = storageReference.child(auth.getCurrentUser().getUid() + ".jpg");
//                uploadTask = fileRef.putFile(imageUri);
//
//                uploadTask.continueWithTask(new Continuation() {
//                    @Override
//                    public Object then(@NonNull Task task) throws Exception {
//                        if (!task.isSuccessful()) {
//                            throw task.getException();
//                        }
//
//                        return fileRef.getDownloadUrl();
//                    }
//                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Uri> task) {
//                        if (task.isSuccessful()) {
//                            Uri downloadUri = task.getResult();
//                            myUri = downloadUri.toString();
//
//                            HashMap<String, Object> userMap = new HashMap<>();
//                            userMap.put("image", myUri);
//
//                            databaseReference.child(auth.getCurrentUser().getUid()).updateChildren(userMap);
//
//                            pd.dismiss();
//                        }
//                    }
//                });
//            } else {
//                pd.dismiss();
//                Toast.makeText(this, "Image not selected", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(""); // Title = Settings
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // set up BACK arrow

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Image");
        storageReference = FirebaseStorage.getInstance().getReference().child("Profile Picture");

        change_photo_image = findViewById(R.id.change_photo_image);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        btn_back = findViewById(R.id.btn_back);
        btn_saveData = findViewById(R.id.btn_saveData);


        // postavi ime i prezime i sliku u header
        reference.addValueEventListener(new ValueEventListener() { // postavi ime i prezime i sliku u header
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                firstName.setText(user.getFirstName());
                lastName.setText(user.getLastName());
                email.setText(user.getEmail());

                if (user.getImageURL().equals("default")) {
                    change_photo_image.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(SettingsActivity.this).load(user.getImageURL()).into(change_photo_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // vrati se na main activity
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                finish();
            }
        });

        // za mijenjanje slike
        change_photo_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFolder();
            }
        });

        // postavi izabranu sliku u bazu
        btn_saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });

    }

    // otvori galeriju i izaberi sliku
    public void openFolder() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // pokreni activity onactionresult
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), RESULT_LOAD_IMAGE);
    }

    // provjeri da li je slika izabrana i da li je sve ok, onda stavi src slike za sliku iz galerije
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            change_photo_image.setImageURI(imageUri);
        }
    }

    // ova funkcija se poziva klikom na tick-arrow
    private void uploadFile() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Slika se postavlja");
        pd.show();

        if (imageUri != null) {
            reference = FirebaseDatabase.getInstance().getReference("Users").child("imageURL");
            final StorageReference fileRef = storageReference.child(auth.getCurrentUser().getUid() + ".jpg");
            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        myUri = downloadUri.toString();

                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("imageURL", myUri);

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        reference.updateChildren(userMap);
                        pd.dismiss();
                    }
                }
            });
        } else {
            pd.dismiss();
            Toast.makeText(this, "Morate izabrati sliku", Toast.LENGTH_SHORT).show();
        }
    }
}