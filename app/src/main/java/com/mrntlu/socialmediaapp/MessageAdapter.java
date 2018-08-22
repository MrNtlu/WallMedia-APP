package com.mrntlu.socialmediaapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MessageAdapter extends BaseAdapter {

    private Activity activity;
    private DatabaseReference databaseReference;
    private DatabaseReference profilePicDatabase;
    private String displayName;
    private ArrayList<DataSnapshot> dataSnapshots;

    Dialog customDialog;
    ProgressBar progressBar;

    private ChildEventListener listener=new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            dataSnapshots.add(dataSnapshot);
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            dataSnapshots.remove(dataSnapshot);
            notifyDataSetChanged();
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public MessageAdapter(Activity activity, DatabaseReference ref, String displayName, ProgressBar progressBar) {
        this.activity = activity;
        this.displayName = displayName;
        this.databaseReference = ref.child("messages");
        this.profilePicDatabase=ref.child("profile");
        this.progressBar=progressBar;
        this.databaseReference.addChildEventListener(listener);

        dataSnapshots=new ArrayList<>();
    }

    static class ViewHolder{
        TextView authorName;
        TextView messageText;
        TextView dateText;
        ConstraintLayout constraintLayout;
        ImageView uploadedImage;
        ImageView profileLogo;
    }

    @Override
    public int getCount() {
        return dataSnapshots.size();
    }

    @Override
    public PublicMessage getItem(int i) {
        DataSnapshot snapshot=dataSnapshots.get(i);
        return snapshot.getValue(PublicMessage.class);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view==null){
            LayoutInflater inflater=(LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.custom_message,viewGroup,false);
            final ViewHolder holder=new ViewHolder();
            holder.authorName=(TextView)view.findViewById(R.id.authorText);
            holder.messageText=(TextView)view.findViewById(R.id.messageText);
            holder.dateText=(TextView)view.findViewById(R.id.dateText);
            holder.constraintLayout=(ConstraintLayout)view.findViewById(R.id.customMessage_layout);
            holder.uploadedImage=(ImageView)view.findViewById(R.id.uploadedImage);
            holder.profileLogo=(ImageView)view.findViewById(R.id.profileLogo);

            view.setTag(holder);
        }
        final PublicMessage message=getItem(i);
        final ViewHolder holder=(ViewHolder)view.getTag();

        String author=message.getAuthor();
        holder.authorName.setText(author);

        String msg=message.getMessage();
        holder.messageText.setText(msg);

        String date=message.getDate().toString();
        holder.dateText.setText(date);

        RequestOptions requestOptions=new RequestOptions();
        //requestOptions.placeholder(R.drawable.loading_process);
        requestOptions.error(R.drawable.ic_sync_problem_black_24dp);

        Uri uri=Uri.parse(message.getImageUrl());
        Glide.with(view.getContext()).setDefaultRequestOptions(requestOptions).load(uri).into(holder.uploadedImage);
        progressBar.setVisibility(View.GONE);

        final DatabaseReference authorDatabase=profilePicDatabase.child(author);
        authorDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Glide.with(activity).load(dataSnapshot.getValue(Upload.class).getImageUrl()).into(holder.profileLogo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.uploadedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog=new Dialog(view.getContext(),android.R.style.Theme_Black_NoTitleBar);
                showPopup(view,message.getImageUrl());
            }
        });

        return view;
    }

    void showPopup(View v,String imageURL){
        ImageButton backButton,downloadButton,shareButton;
        ImageView uploadedImage;
        customDialog.setContentView(R.layout.image_dialog);
        backButton=(ImageButton)customDialog.findViewById(R.id.backButton);
        uploadedImage=(ImageView)customDialog.findViewById(R.id.uploadedImage);
        downloadButton=(ImageButton)customDialog.findViewById(R.id.downloadButton);
        shareButton=(ImageButton)customDialog.findViewById(R.id.shareButton);

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity, "Download Button Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity, "Share Button Clicked", Toast.LENGTH_SHORT).show();
            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
            }
        });
        Glide.with(v.getContext()).load(imageURL).into(uploadedImage);
        customDialog.show();
    }



    public void cleanup(){
        databaseReference.removeEventListener(listener);
    }
}
