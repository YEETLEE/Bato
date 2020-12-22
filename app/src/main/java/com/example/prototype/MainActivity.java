package com.example.prototype;

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

    CircleImageView profile_image;
    TextView displayName;

    DatabaseReference databaseReference;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        displayName = findViewById(R.id.displayName);
        profile_image = findViewById(R.id.profile_image);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");
        auth = FirebaseAuth.getInstance();

        firebaseUser = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() { // postavi ime i prezime i sliku u header
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                String firstName = user.getFirstName();
                String lastName = user.getLastName();
                displayName.setText(firstName +" "+ lastName);

                if(user.getImageURL().equals("default")){
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                }else{
//                    Glide.with(MainActivity.this).load(user.getImageURL()).into(profile_image);
                    Picasso.with(MainActivity.this).load(user.getImageURL()).into(profile_image);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        TabLayout tabLayout = findViewById(R.id.tab_layout); // element iz layout fajla u koji stavljamo fragmente
        ViewPager viewPager = findViewById(R.id.view_pager);

        ViewPageAdapter viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());

        viewPageAdapter.addFragment(new ReportFragment(), "Prijavi");
        viewPageAdapter.addFragment(new PatchesFragment(), "Problemi");
        viewPageAdapter.addFragment(new AccountFragment(), "Moj profil"); // "My profile"

        viewPager.setAdapter(viewPageAdapter);  // u dodaj 3 fragnemte u ovaj view
        tabLayout.setupWithViewPager(viewPager);


    }

    // menu_main.xml items
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater =  getMenuInflater();
//        inflater.inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.logout:
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(MainActivity.this, StartActivity.class));
//                finish();
//                return true;
//
//            case R.id.settings:
//                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
//                finish();
//                return true;
//
//            default:
//                return super.onContextItemSelected(item);
//        }
//    }

    class  ViewPageAdapter extends FragmentPagerAdapter{

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPageAdapter(FragmentManager fm){
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

        public void addFragment(Fragment fragment, String title){
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