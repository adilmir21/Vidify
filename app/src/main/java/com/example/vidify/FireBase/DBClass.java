package com.example.vidify.FireBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

public class DBClass implements DBInterface{
    Context context;
    DBHelper helper;

    public DBClass(Context context) {
        this.context = context;
        helper = new DBHelper(context);
    }

    @Override
    public void addFriend(String Name, String Token, String Email) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Name",Name);
        contentValues.put("Token",Token);
        contentValues.put("Email",Email);
        Log.d("moja", "In add Friend");
        long result = db.insert("Friends",null,contentValues);
        if(result == -1)
        {
            Toast.makeText(context,"Not Successful",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(context,"Friend added Successful",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Cursor getFriends() {
        String query = "SELECT * FROM Friends";
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        if(db != null)
        {
            cursor = db.rawQuery(query,null);
        }
        return cursor;
    }

    @Override
    public void deleteOneRow(String column) {
        SQLiteDatabase db = helper.getWritableDatabase();
        //db.execSQL("DELETE FROM " + table+ " WHERE Name ='"+column+"'");
        db.delete("Friends","Name=?",new String[]{column});
        Toast.makeText(context,"Removed Successfully",Toast.LENGTH_SHORT).show();
        db.close();
    }

    @Override
    public void update(String Name, String Token, String Email) {
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Name", Name);
        contentValues.put("Token", Token);
        contentValues.put("Email", Email);
        long result = database.update("Friends",contentValues,"Name=?",new String []{Name});
        if (result == -1) {
            Toast.makeText(context, "Not Updated Successful", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Updated Successful", Toast.LENGTH_SHORT).show();
        }
    }
}
