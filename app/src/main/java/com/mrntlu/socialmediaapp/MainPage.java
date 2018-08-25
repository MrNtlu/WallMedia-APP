package com.mrntlu.socialmediaapp;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainPage extends AppCompatActivity{

    //TODO Profile Logo slow
    //TODO Upload Button select or set link(Check link)

    Toolbar toolbar;
    NavigationView navigationView;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        toolbar=(Toolbar) findViewById(R.id.toolbar);
        navigationView=(NavigationView)findViewById(R.id.nav_menu);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment=null;
                Class fragmentClass;
                switch (item.getItemId()){
                    case R.id.nav1:
                        Toast.makeText(MainPage.this,"Nav 1",Toast.LENGTH_SHORT).show();
                        fragmentClass=Categories.class;
                        break;
                    case R.id.nav2:
                        Toast.makeText(MainPage.this,"Nav 2",Toast.LENGTH_SHORT).show();
                        fragmentClass=Categories.class;
                        break;
                    case R.id.nav3:
                        Toast.makeText(MainPage.this,"Nav 3",Toast.LENGTH_SHORT).show();
                        fragmentClass=Categories.class;
                        break;
                    case R.id.nav4:
                        Toast.makeText(MainPage.this,"Nav 4",Toast.LENGTH_SHORT).show();
                        fragmentClass=Categories.class;
                        break;
                }
                try{
                    fragment=new Categories(MainPage.this,"Abstract");
                }catch (Exception e){
                    e.printStackTrace();
                }
                FragmentManager fragmentManager=getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_layout,fragment).commit();
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(MainPage.this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }
}
