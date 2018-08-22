package com.mrntlu.socialmediaapp;

import android.content.Intent;
import android.support.annotation.NonNull;
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

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText emailText,passwordText;
    private ProgressBar loginProgress;

    /*
    TODO https://www.udemy.com/android-app-development-with-java/learn/v4/t/lecture/7102972?start=0
    TODO https://firebase.google.com/docs/auth/android/password-auth
    TODO https://firebase.google.com/docs/database/android/start/
    TODO https://console.firebase.google.com/u/0/project/socialmedia-app-eb125/authentication/users
    TODO https://stackoverflow.com/questions/49589791/get-user-email-from-uid-without-login-to-app-firebase
    TODO https://stackoverflow.com/questions/38114358/firebase-setdisplayname-of-user-while-creating-user-android
    TODO https://firebase.google.com/docs/database/android/read-and-write#read_and_write_data
    TODO https://stackoverflow.com/questions/39536517/write-new-data-in-android-firebase-database
    TODO https://stackoverflow.com/questions/45162528/how-to-set-value-to-all-childs-data-in-firebase-database
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button=(Button)findViewById(R.id.button);
        Button registerButton=(Button)findViewById(R.id.registerButton);
        loginProgress=(ProgressBar)findViewById(R.id.loginProgress);

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
            Toast.makeText(this, "Email ve Password Boş Bırakılamaz.", Toast.LENGTH_SHORT).show();
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
