package com.mrntlu.socialmediaapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class RegisterPage extends AppCompatActivity {

    EditText emailText, passwordText, confirmPassText, nickNameText;
    ImageButton backButton;
    Button registerButton;
    ImageView uploadImage;
    private FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;

    private DatabaseReference databaseReference;
    private Uri imageUri;
    private StorageReference storageReference;
    String miUrlOk;
    StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);
        registerButton = (Button) findViewById(R.id.registerButton);
        backButton = (ImageButton) findViewById(R.id.backButton);
        emailText = (EditText) findViewById(R.id.emailText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        confirmPassText = (EditText) findViewById(R.id.confirmPassText);
        nickNameText = (EditText) findViewById(R.id.nickNameText);
        uploadImage = (ImageView) findViewById(R.id.uploadImage);

        storageReference = FirebaseStorage.getInstance().getReference("profile_pics");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(nickNameText.getText().toString()).build();
                    //TODO Display Name
                    user.updateProfile(profileUpdates);
                }
            }
        };

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterPage.this, MainActivity.class));
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emailCheck() && passwordConfirm()) {
                    createFirebaseUser();
                    final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                            + "." + getFileExtension(imageUri));

                    uploadTask = fileReference.putFile(imageUri);

                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return fileReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                String miUrlOk = downloadUri.toString();

                                Upload upload = new Upload(nickNameText.getText().toString(), miUrlOk);
                                databaseReference.child("profile").child(nickNameText.getText().toString()).setValue(upload);

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(RegisterPage.this, "Email geçersiz veya Şifre kısa.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(uploadImage);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private boolean emailCheck() {
        return emailText.getText().toString().contains("@");
    }

    private boolean passwordConfirm() {
        return passwordText.getText().length() > 5 && passwordText.getText().toString().equals(confirmPassText.getText().toString());
    }

    private void createFirebaseUser() {
        String email = emailText.getText().toString().replaceAll("\\s+", "");
        String password = passwordText.getText().toString();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    task.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterPage.this, "Failed! "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    });
                }
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterPage.this, "Success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterPage.this, MainActivity.class));
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }
}
