package com.mrntlu.socialmediaapp;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;


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

        FragmentManager fragmentManager=getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_layout,new Categories(MainPage.this,"Abstract")).commit();


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment=null;
                try {
                    switch (item.getItemId()){
                        case R.id.nav1:
                            Toast.makeText(MainPage.this, "Nav 1", Toast.LENGTH_SHORT).show();
                            fragment=new ApiCategories(MainPage.this,1);
                            break;
                        case R.id.nav2:
                            Toast.makeText(MainPage.this, "Nav 2", Toast.LENGTH_SHORT).show();
                            fragment=new Categories(MainPage.this,"Anime");
                            break;
                        case R.id.nav3:
                            Toast.makeText(MainPage.this, "Nav 3", Toast.LENGTH_SHORT).show();
                            fragment=new Categories(MainPage.this,"Sport");
                            break;
                        case R.id.nav4:
                            Toast.makeText(MainPage.this, "Nav 4", Toast.LENGTH_SHORT).show();
                            fragment=new Categories(MainPage.this,"Technology");
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

        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(MainPage.this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }


}
