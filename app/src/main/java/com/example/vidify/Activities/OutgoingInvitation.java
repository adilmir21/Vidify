package com.example.vidify.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vidify.Models.User;
import com.example.vidify.Network.APIClient;
import com.example.vidify.Network.APIService;
import com.example.vidify.R;
import com.example.vidify.Utilities.Constants;
import com.example.vidify.Utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.reflect.TypeToken;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutgoingInvitation extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    private String inviterToken = null;
    private String meetingRoom = null;
    private String meetingtype = null;
    private TextView textFirstChar;
    private TextView username;
    private int rejectionCount = 0;
    private int totalReceivers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing_invitation);
        preferenceManager = new PreferenceManager(getApplicationContext());

        //inviterToken = FirebaseMessaging.getInstance().getToken().toString();
        ImageView imageMeetingType = findViewById(R.id.imageMeetingType);
        meetingtype = getIntent().getStringExtra("type");
        if(meetingtype!=null)
        {
            if(meetingtype.equals("video"))
            {
                imageMeetingType.setImageResource(R.drawable.ic_video);

            }
            else
            {
                imageMeetingType.setImageResource(R.drawable.ic_call);
            }
        }
        textFirstChar = findViewById(R.id.textFirstChar);
        username = findViewById(R.id.textUsername);

        User user = (User)getIntent().getSerializableExtra("user");
        if(user!=null)
        {
            textFirstChar.setText(user.name.substring(0,1));
            username.setText(user.name);
        }
        ImageView imageStopInvitation = findViewById(R.id.imageStopInvitation);
        imageStopInvitation.setOnClickListener(v->
        {
            PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
            preferenceManager.putBoolean( Constants.KEY_CALL_ENDED,true);
            if(getIntent().getBooleanExtra("isMultiple",false))
            {
                Type type = new TypeToken<ArrayList<User>>() {
                }.getType();
                ArrayList<User> receivers = new Gson().fromJson(getIntent().getStringExtra("selectedUsers"), type);
                cancelInvitation(null, receivers);
            }
            else
            {
                if(user != null)
                {
                    cancelInvitation(user.token,null);
                }
            }

        });

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isSuccessful() && task.getResult() != null)
                {
                    inviterToken = task.getResult().toString();

                    if(meetingtype!= null)
                    {
                        if(getIntent().getBooleanExtra("isMultiple",false)) {
                            Type type = new TypeToken<ArrayList<User>>() {
                            }.getType();
                            ArrayList<User> receivers = new Gson().fromJson(getIntent().getStringExtra("selectedUsers"), type);
                            if(receivers!=null)
                            {
                                totalReceivers = receivers.size();
                            }
                            initiateMeeting(meetingtype, null, receivers);
                        }
                        else
                        {
                            if(user!=null)
                            {
                                totalReceivers = 1;
                                initiateMeeting(meetingtype,user.token,null);
                            }
                        }
                    }

                }
            }
        });
    }

    private void initiateMeeting(String meetingType, String recieverToken, ArrayList<User> receivers)
    {
         try {
             JSONArray tokens = new JSONArray();
             if(recieverToken!=null)
             {
                 tokens.put(recieverToken);
             }

             if(receivers!=null && receivers.size()>0)
             {
                 StringBuilder usernames = new StringBuilder();
                 for(int i=0;i<receivers.size();i++)
                 {
                     tokens.put(receivers.get(i).token);
                     usernames.append(receivers.get(i).name).append("\n");

                 }
                 textFirstChar.setVisibility(View.GONE);
                 username.setText(usernames.toString());
             }

             JSONObject body = new JSONObject();
             JSONObject data = new JSONObject();
             data.put(Constants.REMOTE_MSG_TYPE,Constants.REMOTE_MSG_INVITATION);
             data.put(Constants.REMOTE_MSG_MEETING_TYPE,meetingType);
             data.put(Constants.KEY_NAME,preferenceManager.getString(Constants.KEY_NAME));
             data.put(Constants.KEY_EMAIL,preferenceManager.getString(Constants.KEY_EMAIL));
             data.put(Constants.REMOTE_MSG_INVITER_TOKEN,inviterToken);

             meetingRoom = preferenceManager.getString(Constants.KEY_USER_ID) + "_" + UUID.randomUUID().toString().substring(0,5);
             data.put(Constants.REMOTE_MSG_MEETING_ROOM,meetingRoom);

             body.put(Constants.REMOTE_MSG_DATA,data);
             body.put("priority","high");
             body.put(Constants.REMOTE_MSG_REGISTRATION_IDS,tokens);


             sendRemoteMessage(body.toString(),Constants.REMOTE_MSG_INVITATION);
         }
         catch (Exception e)
         {
             Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
             finish();
         }
    }

    private void sendRemoteMessage(String remoteMessageBody, String type)
    {
        APIClient.getClient().create(APIService.class).sendRemoteMessage(
                Constants.getRemoteMessageHeaders(),remoteMessageBody).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if(response.isSuccessful())
                {


                    if(type.equals(Constants.REMOTE_MSG_INVITATION))
                    {
                        Toast.makeText(getApplicationContext(),"Invitation Successful",Toast.LENGTH_SHORT).show();
                    }
                    else if(type.equals(Constants.REMOTE_MSG_INVITATION_RESPONSE))
                    {
                        Toast.makeText(getApplicationContext(),"Invitation Cancelled",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),response.message(),Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
    private void cancelInvitation(String receiverToken,ArrayList<User> receivers)
    {
        try {
            JSONArray tokens = new JSONArray();
            if(receiverToken != null)
            {
                tokens.put(receiverToken);
            }
            if(receivers != null && receivers.size()>0)
            {
                for(User user : receivers)
                {
                    tokens.put(user.token);
                }
            }
            tokens.put(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE,Constants.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE,Constants.REMOTE_MSG_INVITATION_CANCELLED);
            body.put(Constants.REMOTE_MSG_DATA,data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS,tokens);

            sendRemoteMessage(body.toString(),Constants.REMOTE_MSG_INVITATION_RESPONSE);
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);
            if(type != null)
            {
                if(type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED))
                {
                    try {
                        URL serverURL = new URL("https://meet.jit.si");

                        JitsiMeetConferenceOptions.Builder builder = new JitsiMeetConferenceOptions.Builder();
                        builder.setServerURL(serverURL);
                        builder.setWelcomePageEnabled(false);
                        builder.setRoom(meetingRoom);
                        if(meetingtype.equals("audio"))
                        {
                            builder.setVideoMuted(true);

                        }
                        JitsiMeetActivity.launch(OutgoingInvitation.this,builder.build());
                        finish();
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(getApplicationContext()," "+e.getMessage(),Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                else if(type.equals(Constants.REMOTE_MSG_INVITATION_REJECTED))
                {
                    rejectionCount+=1;
                    if(rejectionCount == totalReceivers)
                    {
                        Toast.makeText(getApplicationContext(),"Invitation Rejected",Toast.LENGTH_SHORT).show();
                        finish();
                    }

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
}