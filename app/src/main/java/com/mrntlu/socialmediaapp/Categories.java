package com.mrntlu.socialmediaapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.Date;

import es.dmoral.toasty.Toasty;

import static android.app.Activity.RESULT_OK;

public class Categories extends Fragment {
    View v;
    private static final int PICK_IMAGE_REQUEST = 1;

    private DatabaseReference databaseReference;
    private MessageAdapter messageAdapter;
    private Uri imageUri;
    private StorageReference storageReference;

    Activity activity;
    String category;

    String miUrlOk;
    StorageTask uploadTask;

    RecyclerView listView;
    EditText editText;
    Button sendButton,progressButton;
    ImageButton backButton, uploadImage;
    ProgressBar progressBar,listviewLoadProgress;
    String userid;

    public Categories() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public Categories(Activity activity, String category) {
        this.activity = activity;
        this.category = category;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment_categories,container,false);
        listView = (RecyclerView) v.findViewById(R.id.listView);
        editText = (EditText) v.findViewById(R.id.editText);
        sendButton = (Button) v.findViewById(R.id.sendButton);
        backButton = (ImageButton) v.findViewById(R.id.backButton);
        uploadImage = (ImageButton)v.findViewById(R.id.uploadImage);
        progressBar=(ProgressBar)v.findViewById(R.id.progressBar);
        progressButton=(Button)v.findViewById(R.id.progressButton);

        listviewLoadProgress=(ProgressBar)v.findViewById(R.id.listviewLoadProgress);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        userid = user.getDisplayName();

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String message = editText.getText().toString();

                if (imageUri != null && !message.equals("")) {
                    editText.setText("");
                    progressBar.setVisibility(View.VISIBLE);
                    progressButton.setVisibility(View.VISIBLE);

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
                                if (userid == null) userid = "Anonymous";

                                Date currentTime = Calendar.getInstance().getTime();
                                Uri downloadUri = task.getResult();
                                miUrlOk = downloadUri.toString();

                                PublicMessage publicMessage = new PublicMessage(message, userid, currentTime, miUrlOk);
                                databaseReference.child("messages").push().setValue(publicMessage);

                                scrollMyListViewToBottom();
                                imageUri = Uri.parse("");
                                uploadImage.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_upload));
                                progressBar.setVisibility(View.GONE);
                                progressButton.setVisibility(View.GONE);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toasty.error(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            imageUri = Uri.parse("");
                            uploadImage.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_upload));
                            progressBar.setVisibility(View.GONE);
                            progressButton.setVisibility(View.GONE);
                        }
                    });
                } else {
                    Toasty.error(v.getContext(), "No file selected", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return v;
    }


    private String getFileExtension(Uri uri) {
        ContentResolver cR = activity.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void scrollMyListViewToBottom() {
        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.getLayoutManager().scrollToPosition(messageAdapter.getItemCount() - 1);
            }
        });
    }



    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(uploadImage);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        listviewLoadProgress.setVisibility(View.VISIBLE);

        messageAdapter = new MessageAdapter(activity, databaseReference, userid,listviewLoadProgress);

        final GridLayoutManager gridLayoutManager=new GridLayoutManager(activity,2);
        listView.setLayoutManager(gridLayoutManager);
        listView.setAdapter(messageAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        messageAdapter.cleanup();
    }
}
