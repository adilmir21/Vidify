package com.example.vidify.Activities;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.vidify.Adapters.UsersAdapter;
import com.example.vidify.FireBase.DBClass;
import com.example.vidify.FireBase.DBInterface;
import com.example.vidify.Listeners.UsersListener;
import com.example.vidify.Models.User;
import com.example.vidify.R;
import com.example.vidify.Utilities.Constants;
import com.example.vidify.Utilities.PreferenceManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OtherUsers extends AppCompatActivity implements UsersListener {

    private AdView adView;
    private String appUnitId = "ca-app-pub-5615461584302678/6460763347";

    TextView userView,textErrorMessage;
    TextView signOut;
    RecyclerView usersRecyclerView;
    ImageButton friend;
    EditText enteredID;
    Button submit;
    private List<User> users;
    private UsersAdapter usersAdapter;
    private  PreferenceManager preferenceManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView imageConference;
    private int REQUEST_CODE_BATTERY_OPTIMIZATIONS = 1;
    public List<User> friendsArrayList;
    ImageView error;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent = new Intent(getApplicationContext(),OtherUsers.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.after_signup);
        View views = findViewById(R.id.mainview);
        if(views!=null) {
            String tag = (String) views.getTag();
            if (tag.equals("xlarge")) {
                Log.d("moja", "X Large Screen");
            }
        }

        adView = findViewById(R.id.adView);
        initializeBannerAd(appUnitId);

        loadBannerAd();

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        findViewById(R.id.imageMenu).setOnClickListener(v->
        {
            drawerLayout.openDrawer(GravityCompat.START);
        });

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Are you sure you want to Sign Out?");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();


                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder1.setIcon(android.R.drawable.ic_dialog_alert);

        friend = findViewById(R.id.addFriend);
        enteredID = findViewById(R.id.friendID);
        submit = findViewById(R.id.submit);

        friend.setOnClickListener(v->
        {
            enteredID.setVisibility(View.VISIBLE);
            submit.setVisibility(View.VISIBLE);
            submit.setOnClickListener(view->
            {
                String id = enteredID.getText().toString();
                if(id.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"ID can't be Empty",Toast.LENGTH_SHORT).show();
                }
                else {
                    addFriend(id);
                    enteredID.setText("");
                    enteredID.setVisibility(View.GONE);
                    submit.setVisibility(View.GONE);
                }
            });
        });

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getTitle().equals("Copy User ID"))
                {
                    ClipboardManager myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                    String myUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    ClipData myClip = ClipData.newPlainText("text", myUserId);
                    myClipboard.setPrimaryClip(myClip);
                    Toast.makeText(getApplicationContext(), "Text Copied",
                            Toast.LENGTH_SHORT).show();
                }
                else if(item.getTitle().equals("Sign Out"))
                {
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), MenuItemLoaded.class);
                    intent.putExtra("name", item.getTitle());
                    intent.putExtra("userName",preferenceManager.getString(Constants.KEY_NAME));
                    intent.putExtra("id",preferenceManager.getString(Constants.KEY_USER_ID));
                    intent.putExtra("email",preferenceManager.getString(Constants.KEY_EMAIL));
                    intent.putExtra("AppName",getPackageName());
                    drawerLayout.closeDrawer(GravityCompat.START);
                    startActivity(intent);
                }
                return true;
            }
        });

        imageConference = findViewById(R.id.imageConference);

        userView = findViewById(R.id.textTitle);
        signOut = findViewById(R.id.signout);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                boolean res = isConnected();
                if(res) {
                    getFriends();
                }
                else
                {
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.wifi,null);
                    ImageView imageView = layout.findViewById(R.id.notConnected);
                    imageView.setImageResource(R.drawable.internet);
                    TextView text = (TextView) layout.findViewById(R.id.textWifi);
                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.BOTTOM, 0, -5);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                    friendsArrayList.clear();
                    usersAdapter.notifyDataSetChanged();
                    error.setImageResource(R.drawable.wifi);
                    error.setVisibility(View.VISIBLE);

                }
            }
        });
        error = findViewById(R.id.internetError);


        preferenceManager = new PreferenceManager(getApplicationContext());
        userView.setText(preferenceManager.getString(Constants.KEY_NAME));

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isSuccessful() && task.getResult() != null)
                {
                    sendFCMToken(task.getResult().toString());
                }
            }
        });


        usersRecyclerView = findViewById(R.id.usersRecycler);
        textErrorMessage = findViewById(R.id.textErrorMessage);

        users = new ArrayList<>();
        friendsArrayList = new ArrayList<>();
        if(isConnected()) {
            getFriends();
        }
        else
        {
            friendsArrayList.clear();
            usersAdapter = new UsersAdapter(friendsArrayList,this);
            usersAdapter.setContext(getApplicationContext());
            usersRecyclerView.setAdapter(usersAdapter);
            usersAdapter.notifyDataSetChanged();
            error.setImageResource(R.drawable.wifi);
            error.setVisibility(View.VISIBLE);
        }
        checkForBatteryOptimizations();
    }

    private void getUsers()
    {
        swipeRefreshLayout.setRefreshing(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        swipeRefreshLayout.setRefreshing(false);
                        String myUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                        if(task.isSuccessful() && task.getResult() != null)
                        {
                            users.clear();
                            for(QueryDocumentSnapshot documentSnapshot:task.getResult())
                            {
                                if(myUserId.equals(documentSnapshot.getId()))
                                {
                                    continue;
                                }
                                User user = new User();
                                user.name = documentSnapshot.getString(Constants.KEY_NAME);
                                user.email = "Available";
                                user.token = documentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                                if(user.token == null)
                                {
                                    user.email = "Not Available";
                                }
                                else
                                {
                                    user.email = "Available";
                                }
                                users.add(user);
                            }
                            if(users.size()>0)
                            {
                                usersAdapter.notifyDataSetChanged();
                            }
                            else
                            {
                                textErrorMessage.setText("No Users Available");
                            }
                        }
                        else
                        {
                            textErrorMessage.setText("No Users Available");
                            textErrorMessage.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    public void sendFCMToken(String token)
    {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.update(Constants.KEY_FCM_TOKEN,token).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(OtherUsers.this,"Token Not Updated" + e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void signOut()
    {
        Toast.makeText(getApplicationContext(),"Signing out.. ",Toast.LENGTH_SHORT).show();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID));
        HashMap<String, Object> updates  = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        preferenceManager.clearPreference();
                        startActivity(new Intent(getApplicationContext(),Login.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Unable To SignOut!",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void initiateVideoMeeting(User user) {
        if(user.token==null || user.token.trim().isEmpty())
        {
            Toast.makeText(this,
                    user.name + " is not Available for now",
                    Toast.LENGTH_SHORT).show();
        }
        else
        {
            Intent intent = new Intent(getApplicationContext(),OutgoingInvitation.class);
            intent.putExtra("user",user);
            intent.putExtra("type","video");
            startActivity(intent);

        }
    }

    @Override
    public void initiateAudioMeeting(User user) {
        if(user.token==null || user.token.trim().isEmpty())
        {
            Toast.makeText(this,
                    user.name + " is not Available for now",
                    Toast.LENGTH_SHORT).show();
        }
        else
        {
           Intent intent = new Intent(getApplicationContext(),OutgoingInvitation.class);

           intent.putExtra("user",user);
           intent.putExtra("type","audio");
           startActivity(intent);
        }
    }

    @Override
    public void onMultipleUsersAction(Boolean isMultipleUsersSelected) {
        if(isMultipleUsersSelected)
        {
            imageConference.setVisibility(View.VISIBLE);
            imageConference.setOnClickListener(v->
            {
                Intent intent = new Intent(getApplicationContext(),OutgoingInvitation.class);
                intent.putExtra("selectedUsers",new Gson().toJson(usersAdapter.getSelectedUsers()));
                intent.putExtra("type","video");
                intent.putExtra("isMultiple",true);
                startActivity(intent);
            });
        }
        else
        {
            imageConference.setVisibility(View.GONE);
        }
    }

    private void checkForBatteryOptimizations()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            if(!powerManager.isIgnoringBatteryOptimizations(getPackageName()))
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(OtherUsers.this);
                builder.setTitle("Warning");
                builder.setMessage("Battery Optimization enabled! It can interrupt background services.");
                builder.setPositiveButton("Disable", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                        startActivityForResult(intent,REQUEST_CODE_BATTERY_OPTIMIZATIONS);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_BATTERY_OPTIMIZATIONS)
        {
            checkForBatteryOptimizations();
        }
    }
    public void getFriends()
    {
        swipeRefreshLayout.setRefreshing(true);
        DBInterface dbInterface = new DBClass(getApplicationContext());
        Cursor cursor = dbInterface.getFriends();
        if(cursor.getCount() == 0)
        {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getApplicationContext(),"No Data",Toast.LENGTH_SHORT).show();
        }
        else
        {

            friendsArrayList = new ArrayList<>();
            while (cursor.moveToNext())
            {
                String ids = cursor.getString(0);
                String name = cursor.getString(1);
                String token = cursor.getString(2);
                String email = cursor.getString(3);
                User user = new User();
                Log.d("moja list", "getFriends: "+ids);
                user.id = ids;
                user.name = name;
                user.email = email;
                user.token = token;

                friendsArrayList.add(user);
            }
            if(friendsArrayList.size()>0)
            {
                swipeRefreshLayout.setRefreshing(false);
                usersAdapter = new UsersAdapter(friendsArrayList,this);
                usersAdapter.setContext(getApplicationContext());
                usersRecyclerView.setAdapter(usersAdapter);
                usersAdapter.notifyDataSetChanged();
            }
            else
            {
                textErrorMessage.setText("No Users Available");
            }
        }
    }
    public void addFriend(String userID) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        String myUserId = preferenceManager.getString(Constants.KEY_USER_ID);

                        if (task.isSuccessful() && task.getResult() != null) {


                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                if (myUserId.equals(documentSnapshot.getId())) {

                                    continue;
                                }

                                if (!userID.equals(myUserId) && userID.contains(documentSnapshot.getId())) {

                                    User user = new User();
                                    user.id = documentSnapshot.getId();
                                    user.name = documentSnapshot.getString(Constants.KEY_NAME);
                                    user.email = "Available";
                                    user.token = documentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                                    Log.d("Moja", "onComplete: "+ user.id);
                                    if (user.token == null) {
                                        user.email = "Not Available";
                                    } else {
                                        user.email = "Available";
                                    }

                                    if(!user.name.isEmpty()) {
                                        boolean found = false;
                                        for(int i=0;i<friendsArrayList.size();i++)
                                        {
                                            if(friendsArrayList.get(i).name.contains(user.name))
                                            {
                                                found = true;
                                            }
                                        }
                                        if(!found) {
                                            DBInterface dbInterface = new DBClass(getApplicationContext());
                                            dbInterface.addFriend(user.id,user.name, user.token, user.email);
                                            getFriends();
                                            LayoutInflater inflater = getLayoutInflater();
                                            View layout = inflater.inflate(R.layout.toast,null);
                                            ImageView imageViewss = layout.findViewById(R.id.imageFriends);
                                            imageViewss.setImageResource(R.drawable.friends);
                                            TextView text = (TextView) layout.findViewById(R.id.textSuccess);
                                            text.setText(user.name + " is added your friend");
                                            Toast toast = new Toast(getApplicationContext());
                                            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                            toast.setDuration(Toast.LENGTH_LONG);
                                            toast.setView(layout);
                                            toast.show();
                                        }
                                        else
                                        {
                                            LayoutInflater inflater = getLayoutInflater();
                                            View layout = inflater.inflate(R.layout.toast,null);
                                            ImageView imageViewss = layout.findViewById(R.id.imageFriends);
                                            imageViewss.setImageResource(R.drawable.good);
                                            TextView text = (TextView) layout.findViewById(R.id.textSuccess);
                                            text.setText(user.name + " is already your friend");
                                            Toast toast = new Toast(getApplicationContext());
                                            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                            toast.setDuration(Toast.LENGTH_LONG);
                                            toast.setView(layout);
                                            toast.show();
                                            //Toast.makeText(getApplicationContext(),"Friend all ready Exists",Toast.LENGTH_SHORT).show();
                                        }
                                        break;
                                    }
                                }

                            }

                        }
                        else
                        {
                            textErrorMessage.setText("Error Occured");
                            textErrorMessage.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }
    public void removeFriend(String Name)
    {
        DBInterface dbInterface = new DBClass(getApplicationContext());
        dbInterface.deleteOneRow(Name);
    }
    public boolean isConnected() {
        swipeRefreshLayout.setRefreshing(false);
        if(error.getVisibility() == View.VISIBLE)
        {
            error.setVisibility(View.GONE);
        }
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        /* NetworkInfo is deprecated in API 29 so we have to check separately for higher API Levels */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Network network = cm.getActiveNetwork();
            if (network == null) {
                return false;
            }
            NetworkCapabilities networkCapabilities = cm.getNetworkCapabilities(network);
            if (networkCapabilities == null) {
                return false;
            }
            boolean isInternetSuspended = !networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_SUSPENDED);
            return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                    && !isInternetSuspended;
        } else {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
    }

    public void initializeBannerAd(String appUnitId)
    {
        MobileAds.initialize(this);
    }
    public void loadBannerAd()
    {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
    public void checkIfChanged()
    {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        String myUserId = preferenceManager.getString(Constants.KEY_USER_ID);

                        DBInterface dbInterface = new DBClass(getApplicationContext());

                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                for(int i=0;i<friendsArrayList.size();i++)
                                {
                                    Log.d("mojas", " "+ friendsArrayList.get(i).name);
                                    if(friendsArrayList.get(i).id.contains(documentSnapshot.getId()))
                                    {
                                        String id = documentSnapshot.getString(Constants.KEY_USER_ID);
                                        String name = documentSnapshot.getString(Constants.KEY_NAME);
                                        String email = friendsArrayList.get(i).email;
                                        String token = documentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                                        dbInterface.update(id, name,token,email);
                                    }
                                }
                            }
                        }
                    }
                });
    }
}
