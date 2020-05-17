package com.example.chatappwithfirebase.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chatappwithfirebase.UI.Fragments.ChatsFragment;
import com.example.chatappwithfirebase.UI.Fragments.ProfileFragment;
import com.example.chatappwithfirebase.UI.Fragments.UsersFragment;
import com.example.chatappwithfirebase.Model.User;
import com.example.chatappwithfirebase.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView txt_username;


    FirebaseUser firebaseUser;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar tool_bar=findViewById(R.id.toolbar);
        setSupportActionBar(tool_bar);
        getSupportActionBar().setTitle("");

        TabLayout tabLayout=findViewById(R.id.tab_layout);
        ViewPager viewPager=findViewById(R.id.view_pager);

        ViewpagerAdapter viewpagerAdapter=new ViewpagerAdapter(getSupportFragmentManager());
        viewpagerAdapter.addfragment(new ChatsFragment(),"Chats");
        viewpagerAdapter.addfragment(new UsersFragment(),"Users");
        viewpagerAdapter.addfragment(new ProfileFragment(),"Profile");


        viewPager.setAdapter(viewpagerAdapter);
        tabLayout.setupWithViewPager(viewPager);


        profile_image=findViewById(R.id.profile_image_user);
        txt_username=findViewById(R.id.txt_username);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                txt_username.setText(user.getUsername());
                if(user.getImageUrl().equals("default")) {
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                }
                else{
                    Glide.with(getApplicationContext()).load(user.getImageUrl()).into(profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this,StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            return true;
        }
        return false;
    }


    class ViewpagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment>fagments;
        private ArrayList<String>titles;

        public ViewpagerAdapter( FragmentManager fm) {
            super(fm);
            this.fagments=new ArrayList<>();
            this.titles=new ArrayList<>();

        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fagments.get(position);
        }

        @Override
        public int getCount() {
            return fagments.size();
        }
      public   void addfragment(Fragment fragment,String title){
            fagments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    public void status(String status){
        reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("status",status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}
