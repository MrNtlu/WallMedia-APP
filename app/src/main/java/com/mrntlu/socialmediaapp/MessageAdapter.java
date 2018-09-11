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
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.joaquimley.faboptions.FabOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import es.dmoral.toasty.Toasty;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private Activity activity;
    private DatabaseReference databaseReference;
    private String displayName;
    private ArrayList<DataSnapshot> dataSnapshots;
    FavoritesMessage message;

    Dialog customDialog;
    ProgressBar progressBar;

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

    public MessageAdapter(Activity activity, DatabaseReference ref, String displayName, ProgressBar progressBar) {
        this.activity = activity;
        this.displayName = displayName;
        this.databaseReference = ref.child(displayName);
        this.progressBar=progressBar;
        dataSnapshots=new ArrayList<>();
        this.databaseReference.addChildEventListener(listener);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(activity).inflate(R.layout.categories_custom,parent,false);
        ViewHolder viewHolder=new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        DataSnapshot snapshot=dataSnapshots.get(position);
        message=snapshot.getValue(FavoritesMessage.class);

        RequestOptions requestOptions=new RequestOptions();
        requestOptions.error(R.drawable.ic_sync_problem_black_24dp).centerInside();

        Uri uri=Uri.parse(message.getImageURL());

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

        holder.cardviewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog=new Dialog(view.getContext(),android.R.style.Theme_Black_NoTitleBar);
                showPopup(view,dataSnapshots.get(position).getValue(FavoritesMessage.class).getImageURL(),position);
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

    void showPopup(View v, final String imageURL, final int position){
        ImageView uploadedImage;
        customDialog.setContentView(R.layout.image_dialog);
        uploadedImage=(ImageView)customDialog.findViewById(R.id.uploadedImage);
        final ProgressBar imageLoadProgress=(ProgressBar)customDialog.findViewById(R.id.imageLoadProgress);
        final ImageView finalUploaded=uploadedImage;

        final FabOptions fabOptions=(FabOptions)customDialog.findViewById(R.id.fab_options);
        fabOptions.setButtonsMenu(R.menu.fav_fab_menu);
        fabOptions.setBackgroundColor(activity, ContextCompat.getColor(activity,R.color.colorAccent));
        fabOptions.setFabColor(R.color.colorAccent);

        fabOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.faboptions_favorite:
                        databaseReference.orderByChild("imageURL").equalTo(imageURL).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    for (DataSnapshot child: dataSnapshot.getChildren()) {
                                        child.getRef().removeValue();
                                    }
                                    dataSnapshots.remove(position);
                                    notifyDataSetChanged();
                                    Toasty.success(activity,"Successfully removed from favs.",Toast.LENGTH_SHORT).show();
                                }else{
                                    Toasty.error(activity,"Its already removed from favs :(",Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        break;

                    case R.id.faboptions_back:
                        customDialog.dismiss();
                        break;
                    case R.id.faboptions_download:
                        String[] splittedURL;
                        String ID;
                        String imageSaveName;
                        try{
                            splittedURL=imageURL.toString().split("/");
                            ID=splittedURL[splittedURL.length-1];
                            imageSaveName=ID.substring(0,ID.length()-3)+"jpg";
                        }catch (Exception e){
                            Random rand = new Random();
                            int  n = rand.nextInt(100) + 1;
                            e.printStackTrace();
                            imageSaveName="MyWallpaper"+n+".jpg";
                        }
                        Log.d("test", "onClick: "+imageSaveName);
                        saveImage(finalUploaded.getDrawable(),imageSaveName);
                        break;

                    case R.id.faboptions_share:
                        shareDrawable(activity,imageURL);
                        break;

                    default:
                        // no-op
                }
            }
        });

        imageLoadProgress.setVisibility(View.VISIBLE);
        Glide.with(v.getContext()).load(imageURL).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                imageLoadProgress.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                imageLoadProgress.setVisibility(View.GONE);
                return false;
            }
        }).into(uploadedImage);
        customDialog.show();
    }

    public void cleanup(){
        databaseReference.removeEventListener(listener);
    }

    public void shareDrawable(Context context,String imageURL) {
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
        ImageView uploadedImage;
        CardView cardviewLayout;
        ProgressBar uploadedImageProgressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            uploadedImage=(ImageView)itemView.findViewById(R.id.uploadedImage);
            cardviewLayout=(CardView)itemView.findViewById(R.id.cardview_layout);
            uploadedImageProgressBar=(ProgressBar)itemView.findViewById(R.id.uploadedImageProgressBar);
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
