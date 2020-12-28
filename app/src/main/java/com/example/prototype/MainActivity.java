package com.example.prototype;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.prototype.Fragments.AccountFragment;
import com.example.prototype.Fragments.ReportFragment;
import com.example.prototype.Fragments.PatchesFragment;
import com.example.prototype.Model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    DatabaseReference reference;

//    CircleImageView profile_image;
//    TextView displayName;

    DatabaseReference databaseReference;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("");

//        displayName = findViewById(R.id.displayName);
//        profile_image = findViewById(R.id.profile_image);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");

        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        if (!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    auth.signOut();
                    Intent intent = new Intent(MainActivity.this, StartActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    return;
                }
                User user = snapshot.getValue(User.class);

                String firstName = user.getFirstName();
                String lastName = user.getLastName();
//                displayName.setText(firstName +" "+ lastName);

//                if(user.getImageURL().equals("default")){
//                    profile_image.setImageResource(R.mipmap.ic_launcher);
//                }else{
////                    Glide.with(MainActivity.this).load(user.getImageURL()).into(profile_image);
//                    Picasso.with(MainActivity.this).load(user.getImageURL()).into(profile_image);
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);

        ViewPageAdapter viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());

        viewPageAdapter.addFragment(new ReportFragment(), "Prijavi");
        viewPageAdapter.addFragment(new PatchesFragment(), "Problemi");
        viewPageAdapter.addFragment(new AccountFragment(), "Moj profil");

        viewPager.setAdapter(viewPageAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    static class ViewPageAdapter extends FragmentPagerAdapter {

        private final ArrayList<Fragment> fragments;
        private final ArrayList<String> titles;

        ViewPageAdapter(FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

}