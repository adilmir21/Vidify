package com.example.vidify.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vidify.FireBase.DBClass;
import com.example.vidify.FireBase.DBInterface;
import com.example.vidify.Models.User;
import com.example.vidify.Network.APIClient;
import com.example.vidify.Network.APIService;
import com.example.vidify.R;
import com.example.vidify.Utilities.Constants;
import com.example.vidify.Utilities.PreferenceManager;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncomingInvitation extends AppCompatActivity {

    private String meetingType = null;
    Uri notification;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_invitation);
        ImageView imageMeetingType = findViewById(R.id.imageMeetingType);
        meetingType = getIntent().getStringExtra(Constants.REMOTE_MSG_MEETING_TYPE);
        checkIfFriend(getIntent().getStringExtra(Constants.KEY_NAME),getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN),"Available");
        Log.d("moja check", "checking ");

        if (meetingType != null) {
            if (meetingType.equals("video")) {
                imageMeetingType.setImageResource(R.drawable.ic_video);
            } else {
                imageMeetingType.setImageResource(R.drawable.ic_call);
            }
        }
        TextView textFirstChar = findViewById(R.id.textFirstChar);
        TextView textUsername = findViewById(R.id.textUsername);
        String name = getIntent().getStringExtra(Constants.KEY_NAME);
        if (!name.isEmpty()) {
            textFirstChar.setText(name.substring(0, 1));
        }
        textUsername.setText(name);

        ImageView imageAcceptInvitation = findViewById(R.id.imageAcceptinvitation);
        ImageView imageRejectInvitation = findViewById(R.id.imageRejectinvitation);

        notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        mp = MediaPlayer.create(getApplicationContext(), notification);
        mp.start();
        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());

        CountDownTimer timer = new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                mp.stop();
                sendInvitationResponse(Constants.REMOTE_MSG_INVITATION_REJECTED, getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN));
            }
        }.start();
        imageAcceptInvitation.setOnClickListener(v ->
        {
            mp.stop();
            timer.cancel();
            sendInvitationResponse(Constants.REMOTE_MSG_INVITATION_ACCEPTED, getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN));
        });

        imageRejectInvitation.setOnClickListener(v ->
        {
            mp.stop();
            timer.cancel();
            sendInvitationResponse(Constants.REMOTE_MSG_INVITATION_REJECTED, getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN));
        });
    }

    private void sendInvitationResponse(String type, String receiverToken) {
        try {
            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE, type);
            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(), type);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void sendRemoteMessage(String remoteMessageBody, String type) {
        APIClient.getClient().create(APIService.class).sendRemoteMessage(
                Constants.getRemoteMessageHeaders(), remoteMessageBody).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    if (type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)) {
                        try {
                            URL serverURL = new URL("https://meet.jit.si");
                            JitsiMeetConferenceOptions.Builder builder = new JitsiMeetConferenceOptions.Builder();
                            builder.setServerURL(serverURL);
                            builder.setWelcomePageEnabled(false);
                            builder.setRoom(getIntent().getStringExtra(Constants.REMOTE_MSG_MEETING_ROOM));
                            if (meetingType.equals("audio")) {
                                builder.setVideoMuted(true);

                            }
                            JitsiMeetActivity.launch(IncomingInvitation.this, builder.build());
                            finish();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), " " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Invitation Rejected", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), response.message(), Toast.LENGTH_SHORT).show();
                    finish();
                }

            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);
            if (type != null) {
                if (type.equals(Constants.REMOTE_MSG_INVITATION_CANCELLED)) {
                    mp.stop();
                    Toast.makeText(getApplicationContext(), "Invitation Cancelled", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                invitationResponseReceiver,
                new IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE)
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                invitationResponseReceiver
        );
    }

    public void checkIfFriend(String names,String tokens,String emails) {

        ArrayList<User> friendsArrayList = new ArrayList<>();
        DBInterface dbInterface = new DBClass(getApplicationContext());
        Cursor cursor = dbInterface.getFriends();
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(names + " is Not in Your Friends List. Do you want to Add "+ names+" As your Friend?");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Yes. I know "+names+" ",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dbInterface.addFriend(names,tokens,emails);
                        dialog.cancel();
                    }
                });
        builder1.setIcon(android.R.drawable.ic_dialog_alert);
        builder1.setNegativeButton(
                "No. I might know him But I don't want them as my Friend",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        if (cursor.getCount() == 0) {
            Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
        }
        else
        {
            while (cursor.moveToNext()) {

                String name = cursor.getString(1);
                String token = cursor.getString(2);
                String email = cursor.getString(3);
                User user = new User();
                user.name = name;
                user.email = email;
                user.token = token;

                friendsArrayList.add(user);
            }
        }
        if(!friendsArrayList.isEmpty())
        {
            boolean found = false;
            for(int i=0;i<friendsArrayList.size();i++)
            {
                if(friendsArrayList.get(i).token.contains(tokens))
                {
                    found = true;
                }
            }

            if(!found)
            {
                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        }
        else
        {
            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    }
}