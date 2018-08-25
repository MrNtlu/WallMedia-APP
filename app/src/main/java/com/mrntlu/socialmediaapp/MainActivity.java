package com.mrntlu.socialmediaapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText emailText,passwordText;
    private ProgressBar loginProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button=(Button)findViewById(R.id.button);
        Button registerButton=(Button)findViewById(R.id.registerButton);
        loginProgress=(ProgressBar)findViewById(R.id.loginProgress);

        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

        firebaseAuth=FirebaseAuth.getInstance();

        emailText=(EditText)findViewById(R.id.mailText);
        passwordText=(EditText)findViewById(R.id.passwordText);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attempLogin();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,RegisterPage.class));
            }
        });
    }

    private void attempLogin(){
        String email=emailText.getText().toString().replaceAll("\\s+","");
        String password=passwordText.getText().toString();
        if (email.equals("") || password.equals("")){
            Toasty.error(this, "Email ve Password Boş Bırakılamaz.", Toast.LENGTH_SHORT).show();
        }
        else{
            loginProgress.setVisibility(View.VISIBLE);
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        loginProgress.setVisibility(View.GONE);
                        startActivity(new Intent(MainActivity.this,MainPage.class));
                    }
                    else{
                        loginProgress.setVisibility(View.GONE);
                        task.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                            }
                        });
                        showErrorDialog("Lütfen Giriş Bilgilerinizi Kontrol Edin.");
                    }
                }
            });
        }
    }

    private void showErrorDialog(String message){
        new AlertDialog.Builder(this)
                .setTitle("Oops :(")
                .setMessage(message)
                .setPositiveButton("OK",null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
