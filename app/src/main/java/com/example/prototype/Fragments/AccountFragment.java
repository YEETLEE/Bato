package com.example.prototype.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.prototype.Model.User;
import com.example.prototype.MyPatchesActivity;
import com.example.prototype.R;
import com.example.prototype.StartActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class AccountFragment extends Fragment {

    CircleImageView profile_image;
    TextView username;
    ImageView logout_image;
    Button my_patches_btn;

    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    FirebaseUser firebaseUser;
    StorageReference st_reference;
    DatabaseReference db_reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        android.view.View view = inflater.inflate(R.layout.fragment_account, container, false);

        profile_image = view.findViewById(R.id.profile_image);
        username = view.findViewById(R.id.username);
        logout_image = view.findViewById(R.id.logout_image);
        my_patches_btn = view.findViewById(R.id.my_patches_btn);

        st_reference = FirebaseStorage.getInstance().getReference("ProfileImage");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db_reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        db_reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user == null) return;

                try {
                    username.setText(user.getFirstName() + " " + user.getLastName());
                    if (user.getImageURL().equals("default"))
                        profile_image.setImageResource(R.mipmap.ic_launcher);
                    else
                        Glide.with(AccountFragment.this).load(user.getImageURL()).into(profile_image);
                } catch (Exception e) {
                    System.out.println(e.toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        logout_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(getActivity(), StartActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFolder();
            }
        });

        my_patches_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MyPatchesActivity.class));
            }
        });

        return view;
    }


    public void openFolder() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Slika se postavlja");
        pd.show();
        pd.setCancelable(false);

        if (imageUri != null) {
            final StorageReference fileReference = st_reference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();


                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("imageURL", mUri);
                        db_reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        db_reference.updateChildren(hashMap);

                        pd.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Greska", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(getContext(), "Izaberite sliku", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(getContext(), "Slika se postavlja", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }

}