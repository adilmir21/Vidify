package com.example.vidify;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

public class InfoFragment extends Fragment {

    public InfoFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        VideoView videoview = view.findViewById(R.id.TeamVideo);
        Uri uri = Uri.parse("android.resource://com.example.vidify/"+R.raw.video);
        videoview.setVideoURI(uri);
        videoview.start();
        return view;
    }
}