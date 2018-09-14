package com.mrntlu.socialmediaapp;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;

public class MainPage extends AppCompatActivity{

    //TODO Profile Logo slow
    //TODO Upload Button select or set link(Check link)

    Toolbar toolbar;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    AdView adView;

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        toolbar=(Toolbar) findViewById(R.id.toolbar);
        navigationView=(NavigationView)findViewById(R.id.nav_menu);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        adView=(AdView)findViewById(R.id.adView);
        MobileAds.initialize(this,"ca-app-pub-7421130457283934~8128237156");
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        View headerView=navigationView.getHeaderView(0);
        TextView navUsername=(TextView)headerView.findViewById(R.id.username_Text);
        final ImageView userImage=(ImageView)headerView.findViewById(R.id.user_image);
            FirebaseDatabase.getInstance().getReference().child("profile").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString().replace("@","").replace(".","")).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        Glide.with(MainPage.this).load(dataSnapshot.getValue(Upload.class).getImageUrl()).into(userImage);
                    }catch (Exception e){
                        userImage.setImageDrawable(ContextCompat.getDrawable(MainPage.this,R.mipmap.ic_launcher));
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    userImage.setImageDrawable(ContextCompat.getDrawable(MainPage.this,R.mipmap.ic_launcher));
                }
            });
        try{
            navUsername.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        }catch (Exception e){
            navUsername.setText(getString(R.string.app_name));
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FragmentManager fragmentManager=getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_layout,new MainFragment(MainPage.this)).commit();

        ActivityCompat.requestPermissions(MainPage.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment=null;
                try {
                    switch (item.getItemId()){
                        case R.id.mainpage_list:
                            fragment=new MainFragment(MainPage.this);
                            break;
                        case R.id.socialmedia_list:
                            fragment=new Categories(MainPage.this);
                            break;
                        case R.id.abstract_list:
                            fragment=new ApiCategories(MainPage.this,1);
                            break;
                        case R.id.animal_list:
                            fragment=new ApiCategories(MainPage.this,2);
                            break;
                        case R.id.anime:
                            fragment=new ApiCategories(MainPage.this,3);
                            break;
                        case R.id.artistic:
                            fragment=new ApiCategories(MainPage.this,4);
                            break;
                        case R.id.comics_list:
                            fragment=new ApiCategories(MainPage.this,8);
                            break;
                        case R.id.dark_list:
                            fragment=new ApiCategories(MainPage.this,9);
                            break;
                        case R.id.earth_list:
                            fragment=new ApiCategories(MainPage.this,10);
                            break;
                        case R.id.fantasy_list:
                            fragment=new ApiCategories(MainPage.this,11);
                            break;
                        case R.id.game_list:
                            fragment=new ApiCategories(MainPage.this,14);
                            break;
                        case R.id.holiday_list:
                            fragment=new ApiCategories(MainPage.this,15);
                            break;
                        case R.id.Humor_list:
                            fragment=new ApiCategories(MainPage.this,13);
                            break;
                        case R.id.man_made_list:
                            fragment=new ApiCategories(MainPage.this,16);
                            break;
                        case R.id.Military_list:
                            fragment=new ApiCategories(MainPage.this,18);
                            break;
                        case R.id.Misc_list:
                            fragment=new ApiCategories(MainPage.this,19);
                            break;
                        case R.id.Movie_list:
                            fragment=new ApiCategories(MainPage.this,20);
                            break;
                        case R.id.Music_list:
                            fragment=new ApiCategories(MainPage.this,22);
                            break;
                        case R.id.Photography_list:
                            fragment=new ApiCategories(MainPage.this,24);
                            break;
                        case R.id.Sci_fi_list:
                            fragment=new ApiCategories(MainPage.this,27);
                            break;
                        case R.id.Sports_list:
                            fragment=new ApiCategories(MainPage.this,28);
                            break;
                        case R.id.Technology_list:
                            fragment=new ApiCategories(MainPage.this,30);
                            break;
                        case R.id.TV_Show_list:
                            fragment=new ApiCategories(MainPage.this,29);
                            break;
                        case R.id.Vehicles_list:
                            fragment=new ApiCategories(MainPage.this,31);
                            break;
                        case R.id.video_game_list:
                            fragment=new ApiCategories(MainPage.this,32);
                            break;
                        case R.id.Weapons_list:
                            fragment=new ApiCategories(MainPage.this,34);
                            break;
                        case R.id.about_fragment:
                            fragment=new AboutFragment();
                            break;
                        case R.id.sign_out:
                            Toasty.info(MainPage.this,"Logged OUT",Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().signOut();
                            break;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                if (fragment!=null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).commit();
                }else{
                    startActivity(new Intent(MainPage.this,MainActivity.class));
                }
                return true;
            }
        });

        toggle=new ActionBarDrawerToggle(MainPage.this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.search_menu,menu);
        MenuItem item=menu.findItem(R.id.searchToolbar);
        final SearchView searchView=(SearchView)item.getActionView();

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                }
                else if (!b){
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    searchView.setIconified(true);
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new SearchFragment(MainPage.this,s.replace(" ","+"))).commit();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}
