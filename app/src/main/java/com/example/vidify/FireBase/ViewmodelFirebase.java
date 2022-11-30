package com.example.vidify.FireBase;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.vidify.Activities.Login;
import com.example.vidify.Activities.OtherUsers;
import com.example.vidify.Models.User;
import com.example.vidify.Utilities.Constants;
import com.example.vidify.Utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class ViewmodelFirebase extends ViewModel {
//    Context context;
//    public ViewmodelFirebase() {
//    }
//
//    PreferenceManager preferenceManager = new PreferenceManager();
//    public void sendFCMToken(String token)
//    {
//        FirebaseFirestore database = FirebaseFirestore.getInstance();
//        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID));
//        documentReference.update(Constants.KEY_FCM_TOKEN,token).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(this,"Token Not Updated" + e.getMessage(),Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//    public void signOut()
//    {
//        Toast.makeText(getApplicationContext(),"Signing out.. ",Toast.LENGTH_SHORT).show();
//        FirebaseFirestore database = FirebaseFirestore.getInstance();
//        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID));
//        HashMap<String, Object> updates  = new HashMap<>();
//        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
//        documentReference.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        preferenceManager.clearPreference();
//                        startActivity(new Intent(getApplicationContext(), Login.class));
//                        finish();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(getApplicationContext(),"Unable To SignOut!",Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//    public void addFriend(String userID) {
//        FirebaseFirestore database = FirebaseFirestore.getInstance();
//        database.collection(Constants.KEY_COLLECTION_USERS)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        String myUserId = preferenceManager.getString(Constants.KEY_USER_ID);
//
//                        if (task.isSuccessful() && task.getResult() != null) {
//
//
//                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
//                                if (myUserId.equals(documentSnapshot.getId())) {
//
//                                    continue;
//                                }
//
//                                if (!userID.equals(myUserId) && userID.contains(documentSnapshot.getId())) {
//
//                                    User user = new User();
//                                    user.name = documentSnapshot.getString(Constants.KEY_NAME);
//                                    user.email = "Available";
//                                    user.token = documentSnapshot.getString(Constants.KEY_FCM_TOKEN);
//                                    if (user.token == null) {
//                                        user.email = "Not Available";
//                                    } else {
//                                        user.email = "Available";
//                                    }
//                                    if(!user.name.isEmpty()) {
//                                        DBInterface dbInterface = new DBClass(getApplicationContext());
//
//                                        dbInterface.addFriend(user.name, user.token, user.email);
//                                        // enteredID.setVisibility(View.GONE);
//
//                                        getFriends();
//                                        break;
//                                    }
//                                }
//                                //Log.d("moja", "Yeah buddy (3)");
//                            }
//
//                        }
//                        else
//                        {
//                            textErrorMessage.setText("Error Occured");
//                            textErrorMessage.setVisibility(View.VISIBLE);
//                        }
//                    }
//                });
//    }

}
