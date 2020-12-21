package com.example.prototype.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.prototype.MainActivity;
import com.example.prototype.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class ChatsFragment extends Fragment{

    EditText patch_title;
    EditText patch_description;
    String patch_city;
    Button patch_send;
    Button image_select;

    ImageView patch_image;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    public static final int RESULT_LOAD_IMAGE = 101;
    Uri uri = null;


//    String[] cities = {"Niksic", "Klicevo", "Danilovgrad", "Podgorica", "Zabljak", "Kotor", "Budva", "Gusinje", "Rozaje", "Andrijevia", "Berane", "Bijelo Polje","Bar", "Mojkovac", "Plav", "Cetinje", "Petnjica", "Pluzine", "Pljevlja", "Tivat", "Herceg Novi", "Ulcinj"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        android.view.View view = inflater.inflate(R.layout.fragment_chats,container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        patch_title = view.findViewById(R.id.patch_title);
        patch_description = view.findViewById(R.id.patch_description);
        patch_send = view.findViewById(R.id.patch_send);
        image_select = view.findViewById(R.id.image_select);
        patch_image = view.findViewById(R.id.patch_image);

        patch_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = patch_title.getText().toString();
                String description = patch_description.getText().toString();
                if(title.isEmpty() || description.isEmpty()){
                    Toast.makeText(getContext(), "All fields are required.", Toast.LENGTH_SHORT).show();
                }else writePatch(title, description, patch_city);
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

        image_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFolder();
            }
        });

        return view;
    }

     public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null){
            uri = data.getData();
            patch_image.setImageURI(uri);
        }
    }
    public void openFolder() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra("return-data", true);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), RESULT_LOAD_IMAGE);
    }

    private void writePatch(String title, String description, String city) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        String userId = firebaseUser.getUid();

        final int rndId = (int)(Math.random() * ((1000000000 - 99999999) + 1)) + 99999999;
        final String strRndId = String.valueOf(rndId);

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("id", strRndId);
        hashMap.put("title", title);
        hashMap.put("description", description);
        hashMap.put("imageURL", "default");
        hashMap.put("city", city);
        hashMap.put("sender", userId);

        reference = FirebaseDatabase.getInstance().getReference("Patches").child(String.valueOf(rndId));
        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), rndId+" "+strRndId+" SUCCESSFULLY ADDED TO FIREBASE ", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }

}