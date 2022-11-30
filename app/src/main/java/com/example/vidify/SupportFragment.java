package com.example.vidify;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.vidify.Activities.OtherUsers;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SupportFragment extends Fragment {
FloatingActionButton button;
Context context;
    public SupportFragment() {
        // Required empty public constructor
    }
    public SupportFragment(Context context) {
        this.context = context;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_support, container, false);
        button = view.findViewById(R.id.call);
        button.setOnClickListener(v->
        {
            Uri u = Uri.parse("tel:" + "03204883958");
            Intent i = new Intent(Intent.ACTION_DIAL, u);

            try
            {
                startActivity(i);
            }
            catch (SecurityException s)
            {

                Toast.makeText(context, "An error occurred", Toast.LENGTH_LONG).show();
            }

        });
        return view;
    }
}