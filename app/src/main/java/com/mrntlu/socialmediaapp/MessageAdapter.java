package com.mrntlu.socialmediaapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private Activity activity;
    private DatabaseReference databaseReference;
    private DatabaseReference profilePicDatabase;
    private String displayName;
    private ArrayList<DataSnapshot> dataSnapshots;
    PublicMessage message;

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
    //

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(activity).inflate(R.layout.cardview_images,parent,false);
        ViewHolder viewHolder=new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        DataSnapshot snapshot=dataSnapshots.get(position);
        message=snapshot.getValue(PublicMessage.class);

        String author=message.getAuthor();
        holder.authorName.setText(author);

//        String msg=message.getMessage();
//        holder.messageText.setText(msg);

//        String date=message.getDate().toString();
//        holder.dateText.setText(date);

        RequestOptions requestOptions=new RequestOptions();
        //requestOptions.placeholder(R.drawable.loading_process);
        requestOptions.error(R.drawable.ic_sync_problem_black_24dp);

        Uri uri=Uri.parse(message.getImageUrl());
        Glide.with(activity).setDefaultRequestOptions(requestOptions).load(uri).into(holder.uploadedImage);
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

        holder.cardviewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog=new Dialog(view.getContext(),android.R.style.Theme_Black_NoTitleBar);
                showPopup(view,dataSnapshots.get(position).getValue(PublicMessage.class).getImageUrl(),holder.uploadedImage.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSnapshots.size();
    }

    //

    @Override
    public long getItemId(int i) {
        return 0;
    }

    void saveImage(Drawable drawable,String imageName){
        Toasty.info(activity, "Started to Save", Toast.LENGTH_SHORT).show();

        Log.d("test", "saveImage: "+drawable+" "+imageName);
//        Bitmap image= BitmapFactory.decodeResource(activity.getResources(),drawable);

        BitmapDrawable drawable1=(BitmapDrawable)drawable;
        Bitmap image=((BitmapDrawable) drawable).getBitmap();

        File path= Environment.getExternalStorageDirectory();

        Log.d("test", "saveImage: "+path);

        File dir=new File(path+"/Download/");
        dir.mkdir();

        File file=new File(dir,imageName);
        OutputStream out=null;

        try {
            out=new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG,100,out);
            out.flush();
            out.close();
            Toasty.success(activity, "Saved", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            e.printStackTrace();
            Toasty.error(activity, "Error! " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    void showPopup(View v, String imageURL, final int drawable){
        ImageButton backButton,downloadButton,shareButton;
        ImageView uploadedImage;
        customDialog.setContentView(R.layout.image_dialog);
        backButton=(ImageButton)customDialog.findViewById(R.id.backButton);
        uploadedImage=(ImageView)customDialog.findViewById(R.id.uploadedImage);
        downloadButton=(ImageButton)customDialog.findViewById(R.id.downloadButton);
        shareButton=(ImageButton)customDialog.findViewById(R.id.shareButton);
        final ImageView finalUploaded=uploadedImage;
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImage(finalUploaded.getDrawable(),"Wallpaper.png");
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toasty.info(activity, "Share Button Clicked", Toast.LENGTH_SHORT).show();
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

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView authorName;
//        TextView messageText;
//        TextView dateText;
        ConstraintLayout constraintLayout;
        ImageView uploadedImage;
        ImageView profileLogo;
        CardView cardviewLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            authorName=(TextView)itemView.findViewById(R.id.authorText);
//            messageText=(TextView)itemView.findViewById(R.id.messageText);
//            dateText=(TextView)itemView.findViewById(R.id.dateText);
            constraintLayout=(ConstraintLayout)itemView.findViewById(R.id.customMessage_layout);
            uploadedImage=(ImageView)itemView.findViewById(R.id.uploadedImage);
            profileLogo=(ImageView)itemView.findViewById(R.id.profileLogo);
            cardviewLayout=(CardView)itemView.findViewById(R.id.cardview_layout);
        }
    }
}
