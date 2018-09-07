package com.mrntlu.socialmediaapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;

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
    String category;

    /*TODO
    https://wall.alphacoders.com/api.php#collapse_collection
    https://wall.alphacoders.com/api2.0/get.php?auth=481cfa6f70112be63d18faaf10a597dd&method=search&term=boku+no+hero
    https://wall.alphacoders.com/api2.0/get.php?auth=481cfa6f70112be63d18faaf10a597dd&method=category_list
    https://wall.alphacoders.com/api2.0/get.php?auth=481cfa6f70112be63d18faaf10a597dd&method=category&id=3&page=1&sort=rating
    https://github.com/bluelinelabs/LoganSquare
    https://android-arsenal.com/details/1/1550
    https://github.com/google/gson
    https://www.youtube.com/watch?v=y2xtLqP8dSQ
    https://www.youtube.com/results?search_query=android+json+parser
     */

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

    public MessageAdapter(Activity activity,String category, DatabaseReference ref, String displayName, ProgressBar progressBar) {
        this.activity = activity;
        this.category=category;
        this.displayName = displayName;
        this.databaseReference = ref.child("messages").child(category);
        this.profilePicDatabase=ref.child("profile");
        this.progressBar=progressBar;
        dataSnapshots=new ArrayList<>();
        this.databaseReference.addChildEventListener(listener);
    }

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

        RequestOptions requestOptions=new RequestOptions();
        //requestOptions.override(1280,720);
        //requestOptions.placeholder(R.drawable.loading_process).centerInside();
        requestOptions.error(R.drawable.ic_sync_problem_black_24dp).centerInside();

        Uri uri=Uri.parse(message.getImageUrl());
        holder.uploadedImageProgressBar.setVisibility(View.VISIBLE);
        Glide.with(activity).setDefaultRequestOptions(requestOptions).load(uri).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                holder.uploadedImageProgressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.uploadedImageProgressBar.setVisibility(View.GONE);
                return false;
            }
        }).into(holder.uploadedImage);
        progressBar.setVisibility(View.GONE);

        final DatabaseReference authorDatabase=profilePicDatabase.child(author);
        authorDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.profileLogoProgress.setVisibility(View.VISIBLE);

                RequestOptions ro=new RequestOptions();
                ro.error(R.drawable.ic_sync_problem_black_24dp).centerInside();

                try {
                    Glide.with(activity).setDefaultRequestOptions(ro).load(dataSnapshot.getValue(Upload.class).getImageUrl()).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            holder.profileLogoProgress.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.profileLogoProgress.setVisibility(View.GONE);
                            return false;
                        }
                    }).into(holder.profileLogo);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.cardviewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog=new Dialog(view.getContext(),android.R.style.Theme_Black_NoTitleBar);
                showPopup(view,dataSnapshots.get(position).getValue(PublicMessage.class).getImageUrl());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSnapshots.size();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    void saveImage(Drawable drawable,String imageName){
        Toasty.info(activity, "Started to Save", Toast.LENGTH_SHORT).show();

        Bitmap image=((BitmapDrawable) drawable).getBitmap();

        File path= Environment.getExternalStorageDirectory();

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

    void showPopup(View v, final String imageURL){
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
                shareDrawable(activity,imageURL,"filename");
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

    public void shareDrawable(Context context,String imageURL,String fileName) {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
            i.putExtra(Intent.EXTRA_TEXT, imageURL);
            activity.startActivity(Intent.createChooser(i, "Share URL"));
        }
        catch (Exception e) {
            Toasty.error(context, "Error! "+e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView authorName;
        ImageView uploadedImage;
        ImageView profileLogo;
        CardView cardviewLayout;
        ProgressBar uploadedImageProgressBar,profileLogoProgress;

        public ViewHolder(View itemView) {
            super(itemView);
            authorName=(TextView)itemView.findViewById(R.id.authorText);
            uploadedImage=(ImageView)itemView.findViewById(R.id.uploadedImage);
            profileLogo=(ImageView)itemView.findViewById(R.id.profileLogo);
            cardviewLayout=(CardView)itemView.findViewById(R.id.cardview_layout);
            uploadedImageProgressBar=(ProgressBar)itemView.findViewById(R.id.uploadedImageProgressBar);
            profileLogoProgress=(ProgressBar)itemView.findViewById(R.id.profileLogoProgress);
        }
    }

    /*

    //TODO onBind
    //
    // jsonParser(1);
    private void jsonParser(int categories){
        String url="https://wall.alphacoders.com/api2.0/get.php?auth="+API_TOKEN+"&method=category&id="+categories+"&page=1&sort=rating";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                JSONArray jsonArray = response.getJSONArray("wallpapers");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    PublicMessage publicMessage = new PublicMessage("test",
                                            "MrNtlu",  Calendar.getInstance().getTime(), jsonArray.getJSONObject(i).getString("url_thumb") );

                                    notifyDataSetChanged();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);
    }

    */
}
