package com.example.vidify.Listeners;

import com.example.vidify.Models.User;

public interface UsersListener {
    void initiateVideoMeeting(User user);
    void initiateAudioMeeting(User user);
    void onMultipleUsersAction(Boolean isMultipleUsersSelected);
}
