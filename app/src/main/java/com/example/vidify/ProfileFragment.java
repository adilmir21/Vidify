package com.example.vidify;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dropbox.core.v2.teamlog.SfTeamInviteDetails;
import com.example.vidify.Utilities.Constants;
import com.example.vidify.Utilities.PreferenceManager;

public class ProfileFragment extends Fragment {

    Context context;
    TextView name, available, id;
    public ProfileFragment() {
    }
    public ProfileFragment(Context context)
    {
        this.context = context;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        // Inflate the layout for this fragment

//        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
//        getIntent.setType("image/*");
//
//        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        pickIntent.setType("image/*");
//
//        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
//        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
//        startActivityForResult(pickIntent, PICK_IMAGE);
        PreferenceManager preferenceManager = new PreferenceManager(context);

        name = view.findViewById(R.id.UserName);
        available = view.findViewById(R.id.UserTag);
        id = view.findViewById(R.id.ID);

        name.setText("UserName : " + preferenceManager.getString(Constants.KEY_NAME));
        available.setText("Email : " + preferenceManager.getString(Constants.KEY_EMAIL));
        id.setText("User ID : " + preferenceManager.getString(Constants.KEY_USER_ID));
        return view;
    }
}