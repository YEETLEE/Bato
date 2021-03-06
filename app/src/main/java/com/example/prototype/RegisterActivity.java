package com.example.prototype;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    MaterialEditText firstName, lastName, email, password;
    Button btn_register;

    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_register = findViewById(R.id.btn_register);

        auth = FirebaseAuth.getInstance();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_firstName = firstName.getText().toString();
                String txt_lastName = lastName.getText().toString();
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();

                if (TextUtils.isEmpty(txt_firstName) || TextUtils.isEmpty(txt_lastName) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)) {
                    Toast.makeText(RegisterActivity.this, "Morate pupuniti sva polja", Toast.LENGTH_SHORT).show();
                } else if (txt_password.length() < 8) {
                    Toast.makeText(RegisterActivity.this, "Lozinka mora imati bar 8 karaktera", Toast.LENGTH_SHORT).show();
                } else
                    register(txt_firstName, txt_lastName, txt_email, txt_password);
            }
        });

    }

    private void register(final String firstName, final String lastName, final String email, final String password) {

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    FirebaseUser firebaseUser = auth.getCurrentUser();
                                    assert firebaseUser != null;
                                    String userId = firebaseUser.getUid();
                                    reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("firstName", firstName);
                                    hashMap.put("id", userId);
                                    hashMap.put("imageURL", "default");
                                    hashMap.put("lastName", lastName);
                                    hashMap.put("email", email);
                                    hashMap.put("password", password);

                                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseAuth.getInstance().signOut();
                                                Intent intent = new Intent(RegisterActivity.this, StartActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registracija neuspjesna. Probajte drugu email adresu", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}