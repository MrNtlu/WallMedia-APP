package com.mrntlu.socialmediaapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.vansuita.materialabout.builder.AboutBuilder;
import com.vansuita.materialabout.views.AboutView;

public class AboutFragment extends Fragment {

    View v;

    public AboutFragment() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AboutView aboutView = AboutBuilder.with(view.getContext())
                .setPhoto(R.mipmap.profile_picture)
                .setCover(R.mipmap.profile_cover)
                .setName("MrNtlu")
                .setSubTitle("Mobile Developer")
                .addLinkedInLink("burak-fidan")
                .setAppIcon(R.mipmap.ic_launcher)
                .setAppName(R.string.app_name)
                .addGooglePlayStoreLink("")//TODO Change
                .addGitHubLink("MrNtlu")
                .addEmailLink("mrntlu@gmail.com")
                .addWebsiteLink("http://mrntlu.com/")
                .addFiveStarsAction()
                .setVersionNameAsAppSubTitle()
                .addFeedbackAction("mrntlu@gmail.com")
                .addMoreFromMeAction("MrNtlu")
                .addShareAction(R.string.app_name)
                .setWrapScrollView(false)
                .setLinksAnimated(true)
                .setShowAsCard(true)
                .build();

        FrameLayout frameLayout=(FrameLayout)view.findViewById(R.id.about_frameLayout);
        frameLayout.addView(aboutView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment_about, container, false);
        return v;
    }

}
