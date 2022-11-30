package com.example.vidify.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.app.ActionBar;
import android.os.Bundle;
import android.widget.TableLayout;

import com.example.vidify.InfoFragment;
import com.example.vidify.ProfileFragment;
import com.example.vidify.R;
import com.example.vidify.SettingsFragment;
import com.example.vidify.SupportFragment;
import com.google.android.material.tabs.TabLayout;

public class MenuItemLoaded extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_item_loaded);
//
//        tabLayout = findViewById(R.id.tabLayout);
//        viewPager = findViewById(R.id.viewPager);
//
//        tabLayout.setupWithViewPager(viewPager);
//        tabLayout.selectTab();
//        VPAdapter vpAdapter = new VPAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
//        vpAdapter.addFragment(new ProfileFragment(getApplicationContext()),"Profile");
//        vpAdapter.addFragment(new SettingsFragment(),"Settings");
//        vpAdapter.addFragment(new InfoFragment(),"About Us");
//        vpAdapter.addFragment(new SupportFragment(),"Customer Support");
//        viewPager.setAdapter(vpAdapter);

//        ActionBar actionBar = getActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
        String name = getIntent().getStringExtra("name");
        switch (name) {
            case "Profile":
                loadFragment(new ProfileFragment(getApplicationContext()));
                break;
            case "Settings":
                loadFragment(new SettingsFragment());
                break;
            case "About Us":
                loadFragment(new InfoFragment());
                break;
            case "Customer Support":
                loadFragment(new SupportFragment());
                break;
        }
    }
    public void loadFragment(Fragment fragment)
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frameLayout,fragment);
        ft.commit();
    }
}