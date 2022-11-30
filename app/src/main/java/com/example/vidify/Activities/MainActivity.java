package com.example.vidify.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.vidify.R;

public class MainActivity extends AppCompatActivity {
    Animation rotate;
    ImageView logo;
    String[] permissions = new String[] {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
    private int requestCode = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logo = findViewById(R.id.imageView);
        rotate = AnimationUtils.loadAnimation(MainActivity.this,R.anim.rotate);
        logo.startAnimation(rotate);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this, Login.class));
            }
        },2000);
    }
    private boolean isGranted()
    {
        for(String permission: permissions)
        {
            if(ActivityCompat.checkSelfPermission(this,permission)!= PackageManager.PERMISSION_GRANTED)
            {
                return false;
            }
        }
        return true;
    }
    private void askPermissions()
    {
        ActivityCompat.requestPermissions(this,permissions,requestCode);
    }
}