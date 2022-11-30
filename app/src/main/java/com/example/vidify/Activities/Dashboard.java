package com.example.vidify.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vidify.R;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

public class Dashboard extends AppCompatActivity {

    EditText code;
    Button share, join;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        code = findViewById(R.id.code);
        share = findViewById(R.id.share);
        join = findViewById(R.id.join);

        URL url;

        try {
            url = new URL("https://meet.jit.si");
            JitsiMeetConferenceOptions defaultOptions = new JitsiMeetConferenceOptions.Builder().setServerURL(url).setWelcomePageEnabled(false).build();
            JitsiMeet.setDefaultConferenceOptions(defaultOptions);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        join.setOnClickListener(v->
        {
            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder().setRoom(code.getText().toString()).setWelcomePageEnabled(false).build();

            JitsiMeetActivity.launch(Dashboard.this,options);

        });

        share.setOnClickListener(v->
        {
            if(code.getText().toString().equals(""))
            {
                Toast.makeText(Dashboard.this,"Please Create a Code First!",Toast.LENGTH_SHORT).show();
            }
            else {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, code.getText().toString());
                shareIntent.setType("text/plain");
                shareIntent = Intent.createChooser(shareIntent, "Share Code Via: ");
                startActivity(shareIntent);
            }
        });
    }
}