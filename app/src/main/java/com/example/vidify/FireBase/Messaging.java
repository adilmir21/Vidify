package com.example.vidify.FireBase;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.vidify.Activities.IncomingInvitation;
import com.example.vidify.Utilities.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class Messaging extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        String type = message.getData().get(Constants.REMOTE_MSG_TYPE);
        if(type!= null)
        {
            if(type.equals(Constants.REMOTE_MSG_INVITATION))
            {
                Intent intent = new Intent(getApplicationContext(), IncomingInvitation.class);
                intent.putExtra(
                        Constants.REMOTE_MSG_MEETING_TYPE,message.getData().get(Constants.REMOTE_MSG_MEETING_TYPE)
                );
                intent.putExtra(
                        Constants.KEY_NAME,message.getData().get(Constants.KEY_NAME)
                );
                intent.putExtra(
                        Constants.KEY_EMAIL,message.getData().get(Constants.KEY_EMAIL)
                );
                intent.putExtra(
                        Constants.REMOTE_MSG_INVITER_TOKEN, message.getData().get(Constants.REMOTE_MSG_INVITER_TOKEN)
                );
                intent.putExtra(
                        Constants.REMOTE_MSG_MEETING_ROOM,
                        message.getData().get(Constants.REMOTE_MSG_MEETING_ROOM)
                );
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            else if(type.equals(Constants.REMOTE_MSG_INVITATION_RESPONSE))
            {
                Intent intent = new Intent(Constants.REMOTE_MSG_INVITATION_RESPONSE);
                intent.putExtra(
                        Constants.REMOTE_MSG_INVITATION_RESPONSE,
                        message.getData().get(Constants.REMOTE_MSG_INVITATION_RESPONSE)
                );
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();

    }
}
