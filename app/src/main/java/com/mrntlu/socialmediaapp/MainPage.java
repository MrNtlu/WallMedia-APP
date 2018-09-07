package com.mrntlu.socialmediaapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import es.dmoral.toasty.Toasty;

public class MainPage extends AppCompatActivity{

    //TODO Profile Logo slow
    //TODO Upload Button select or set link(Check link)

    Toolbar toolbar;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;

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
        View headerView=navigationView.getHeaderView(0);
        TextView navUsername=(TextView)headerView.findViewById(R.id.username_Text);
        navUsername.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FragmentManager fragmentManager=getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_layout,new Categories(MainPage.this,"Abstract")).commit();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment=null;
                try {
                    switch (item.getItemId()){
                        case R.id.socialmedia_list:
                            fragment=new Categories(MainPage.this,"Abstract");
                            break;
                        case R.id.abstract_list:
                            fragment=new ApiCategories(MainPage.this,1);
                            break;
                        case R.id.anime:
                            fragment=new ApiCategories(MainPage.this,3);
                            break;
                        case R.id.comics_list:
                            fragment=new ApiCategories(MainPage.this,8);
                            break;
                        case R.id.earth_list:
                            fragment=new ApiCategories(MainPage.this,10);
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
