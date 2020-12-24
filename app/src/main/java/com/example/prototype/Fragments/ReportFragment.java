package com.example.prototype.Fragments;

//    String[] cities = {"Niksic", "Klicevo", "Danilovgrad", "Podgorica", "Tuzi", "Zabljak", "Kotor", "Savnik", "Budva", "Gusinje", "Rozaje", "Andrijevca", "Berane", "Bijelo Polje","Bar", "Mojkovac", "Plav", "Cetinje", "Petnjica", "Pluzine", "Pljevlja", "Tivat", "Herceg Novi", "Ulcinj"};

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.prototype.MainActivity;
import com.example.prototype.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class ReportFragment extends Fragment {

    EditText patch_title;
    EditText patch_description;
    String patch_city;
    Button select_image;
    Button patch_send;

    FirebaseUser firebaseUser;
    StorageReference st_reference;
    DatabaseReference db_reference;
    DatabaseReference user_reference;
    public static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;
    private static String patchId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        android.view.View view = inflater.inflate(R.layout.fragment_report, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        st_reference = FirebaseStorage.getInstance().getReference("PatchImages");

        patch_title = view.findViewById(R.id.patch_title);
        patch_description = view.findViewById(R.id.patch_description);
        patch_send = view.findViewById(R.id.patch_send);
        select_image = view.findViewById(R.id.select_image);

        patch_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = patch_title.getText().toString();
                String description = patch_description.getText().toString();
                if (title.isEmpty() || description.isEmpty() || imageUri == null || patch_city.equals("Izaberi")) {
                    Toast.makeText(getContext(), "Popunite sva polja.", Toast.LENGTH_SHORT).show();
                } else uploadImage(title, description, patch_city);
            }
        });
        select_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFolder();
            }
        });

        Spinner spin = view.findViewById(R.id.spinner1);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                patch_city = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
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

    private void uploadImage(final String title, final String description, final String city) {
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
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        assert firebaseUser != null;
                        final String userId = firebaseUser.getUid();

//                        final int rndId = (int) (Math.random() * ((1000000000 - 99999999) + 1)) + 99999999;
//                        patchId = String.valueOf(rndId);
                        String rndId = String.valueOf(System.currentTimeMillis());

                        final HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("id", rndId);
                        hashMap.put("title", title);
                        hashMap.put("description", description);
                        hashMap.put("imageURL", mUri);
                        hashMap.put("city", city);
                        hashMap.put("sender", userId);
                        SimpleDateFormat formatter = new SimpleDateFormat("d MMM yyyy HH:mm:ss");
                        String strDate = formatter.format(new Date());
                        hashMap.put("date", strDate);

                        db_reference = FirebaseDatabase.getInstance().getReference("Patches").child(rndId);
                        db_reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    HashMap<String, Object> userPatchMap = new HashMap<>();
                                    userPatchMap.put(patchId, patchId);
                                    user_reference = FirebaseDatabase.getInstance().getReference("Users/"+userId+"/myPatches/");
                                    user_reference.updateChildren(userPatchMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getContext(), "Hvala na prijavljivanju problema", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getContext(), MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            }
                                        }
                                    });

                                    Toast.makeText(getContext(), "Hvala na prijavljivanju problema", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getContext(), MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }
                        });

                       pd.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Radnja neuspjesna", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), "Morate izabrati sliku", Toast.LENGTH_SHORT).show();
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
                select_image.setClickable(false);
                select_image.setText("Slika postavljena ✔");
                select_image.setBackground(Drawable.createFromPath("#ffffff"));
                select_image.setTextColor(Color.parseColor("#0066cc"));
            }
        }
    }
}