package com.example.vidify.Utilities;

import java.util.HashMap;

public class Constants {
    public static final String KEY_COLLECTION_USERS = "Users";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_NAME = "name";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_FCM_TOKEN = "fcm_token"; // firebase cloud messaging (FCM)
    public static final String KEY_PREFERENCE_NAME = "videoMeetingPreference";
    public static final String KEY_IS_SIGNED_IN = "isSignedIn";
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    public static final String REMOTE_MSG_TYPE = "type";
    public static final String REMOTE_MSG_INVITATION = "invitation";
    public static final String REMOTE_MSG_MEETING_TYPE = "meetingType";
    public static final String REMOTE_MSG_INVITER_TOKEN = "inviterToken";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";
    public static final String REMOTE_MSG_INVITATION_RESPONSE = "invitationResponse";
    public static final String REMOTE_MSG_INVITATION_ACCEPTED = "accepted";
    public static final String REMOTE_MSG_INVITATION_REJECTED = "rejected";
    public static final String REMOTE_MSG_INVITATION_CANCELLED = "cancelled";
    public static final String REMOTE_MSG_MEETING_ROOM = "meetingRoom";
    public static  String KEY_CALL_ENDED= "";

    public static HashMap<String,String> getRemoteMessageHeaders() {
        HashMap<String,String> headers = new HashMap<>();
        headers.put(
                Constants.REMOTE_MSG_AUTHORIZATION,
                "key=AAAA68s4Bkw:APA91bEG4t7nICbZVORBGRsiGhuSF8jXCjVyvssc01y2UyDaJ3Li0LwUDOWCQzqBOaHtD7-AYxdmh1iAf3yIyl-sS3VFFC3qhcYvpSM0domuyVYSwzxvv4x7ywMfjxtNJj1vVarKCZmh"
        );
        headers.put(Constants.REMOTE_MSG_CONTENT_TYPE,"application/json");
        return headers;
    }
}
