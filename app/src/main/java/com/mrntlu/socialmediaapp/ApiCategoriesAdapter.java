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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jgabrielfreitas.core.BlurImageView;
import com.joaquimley.faboptions.FabOptions;
import com.like.LikeButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class ApiCategoriesAdapter extends RecyclerView.Adapter<ApiCategoriesAdapter.ViewHolder>{

    private Activity activity;
    Dialog customDialog;
    String category;
    ProgressBar progressBar;
    private DatabaseReference databaseReference;
    String displayName;

    ArrayList<Uri> thumbLinks=new ArrayList<Uri>();
    ArrayList<Uri> imageLinks=new ArrayList<Uri>();
    ArrayList<Integer> imageID=new ArrayList<Integer>();

    //TODO https://stackoverflow.com/questions/43959582/how-to-check-if-a-value-exists-in-firebase-database-android
    //android firebase check if data exists
    //https://github.com/JoaquimLey/faboptions

    public ApiCategoriesAdapter(Activity activity, ArrayList<Uri> thumbLinks, ArrayList<Uri> imageLinks, ProgressBar progressBar, ArrayList<Integer> imageID) {
        this.activity = activity;
        this.thumbLinks = thumbLinks;
        this.imageLinks = imageLinks;
        this.progressBar=progressBar;
        this.imageID=imageID;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(activity).inflate(R.layout.categories_custom,parent,false);
        ViewHolder viewHolder=new ViewHolder(v);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        displayName=FirebaseAuth.getInstance().getCurrentUser().getEmail().toString().replace("@","").replace(".","");
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        RequestOptions requestOptions=new RequestOptions();
        //requestOptions.placeholder(R.drawable.loading_process).centerInside();
        requestOptions.error(R.drawable.ic_sync_problem_black_24dp).centerInside();

        holder.uploadedImageProgressBar.setVisibility(View.VISIBLE);
        Glide.with(activity).setDefaultRequestOptions(requestOptions).load(thumbLinks.get(position)).listener(new RequestListener<Drawable>() {
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
        holder.uploadedImageProgressBar.setVisibility(View.GONE);

        holder.cardviewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog=new Dialog(view.getContext(),android.R.style.Theme_Black_NoTitleBar);
                showPopup(view,imageLinks.get(position).toString(),imageID.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return thumbLinks.size();
    }

    void saveImage(Drawable drawable, String imageName){
        try {
            Bitmap image=((BitmapDrawable) drawable).getBitmap();

            File path= Environment.getExternalStorageDirectory();

            File dir=new File(path+"/Download/");
            dir.mkdir();

            File file=new File(dir,imageName);

            OutputStream out=new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG,100,out);
            out.flush();
            out.close();
            Toasty.success(activity, activity.getString(R.string.imageSaved), Toast.LENGTH_SHORT).show();
        }
        catch (NullPointerException e){
            Toasty.error(activity,activity.getString(R.string.waittoload),Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            e.printStackTrace();
            Toasty.error(activity, activity.getString(R.string.error_occured)+" "+ e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    public void shareDrawable(Context context, String imageURL) {
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

    void showPopup(View v, final String imageURL, final int id){
        customDialog.setContentView(R.layout.image_dialog);
        final ImageView uploadedImage=(ImageView)customDialog.findViewById(R.id.uploadedImage);
        final ProgressBar imageLoadProgress=(ProgressBar)customDialog.findViewById(R.id.imageLoadProgress);
        final ImageView finalUploaded=uploadedImage;
        final ConstraintLayout constraintLayout=(ConstraintLayout)customDialog.findViewById(R.id.dialog_layout);
        final BlurImageView backgroundImage=(BlurImageView)customDialog.findViewById(R.id.backgroundImage);
        final LikeButton likeButton=(LikeButton)customDialog.findViewById(R.id.heart_Button);

        final FabOptions fabOptions=(FabOptions)customDialog.findViewById(R.id.fab_options);
        fabOptions.setButtonsMenu(R.menu.fab_menu);
        fabOptions.setBackgroundColor(activity, ContextCompat.getColor(activity,R.color.colorAccent));
        fabOptions.setFabColor(R.color.colorAccent);

        fabOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.faboptions_favorite:
                        databaseReference.child(displayName).orderByChild("imageURL").equalTo(imageURL).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    for (DataSnapshot child: dataSnapshot.getChildren()) {
                                        child.getRef().removeValue();
                                    }
                                    likeButton.setLiked(false);
                                    likeButton.setVisibility(View.GONE);
                                }else{
                                    FavoritesMessage favoritesMessage=new FavoritesMessage(imageURL);
                                    databaseReference.child(displayName).push().setValue(favoritesMessage);
                                    likeButton.setVisibility(View.VISIBLE);
                                    likeButton.onClick(likeButton);
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
                        saveImage(finalUploaded.getDrawable(),id+".jpg");
                        break;

                    case R.id.faboptions_share:
                        shareDrawable(activity,imageURL);
                        break;

                    default:
                        // no-op
                }
            }
        });

        databaseReference.child(displayName).orderByChild("imageURL").equalTo(imageURL).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (likeButton.isLiked()){
                        likeButton.setLiked(false);
                    }
                    likeButton.setVisibility(View.VISIBLE);
                    likeButton.onClick(likeButton);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        imageLoadProgress.setVisibility(View.VISIBLE);

        Glide.with(v.getContext()).load(imageURL).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                imageLoadProgress.setVisibility(View.GONE);
                Toasty.error(activity,activity.getString(R.string.failed_to_load),Toast.LENGTH_SHORT).show();
                return false;
            }
            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                imageLoadProgress.setVisibility(View.GONE);
                backgroundImage.setImageDrawable(resource);
                backgroundImage.setBlur(10);

                return false;
            }
        }).into(uploadedImage);

        customDialog.show();


    }

    void removeFromFirebase(String displayName,String URL){
        databaseReference.child(displayName).orderByChild("imageURL").equalTo(URL).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    child.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
}
