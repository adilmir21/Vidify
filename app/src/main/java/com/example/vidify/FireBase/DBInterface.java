package com.example.vidify.FireBase;

import android.database.Cursor;

public interface DBInterface {
    public void addFriend(String Name, String Token, String Email);
    public Cursor getFriends();
    public void deleteOneRow(String column);
    public void update(String Name, String Token, String Email);
}