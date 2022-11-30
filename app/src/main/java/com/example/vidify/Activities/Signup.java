package com.example.vidify.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vidify.R;
import com.example.vidify.Utilities.Constants;
import com.example.vidify.Utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class Signup extends AppCompatActivity {

    FirebaseFirestore database;
    EditText name, email, pass;
    Button signup, login;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        database = FirebaseFirestore.getInstance();

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.password);

        signup = findViewById(R.id.l1);
        login = findViewById(R.id.s1);

        signup.setOnClickListener(v->
                {

                    String eMail, userName, userPass;
                    eMail = email.getText().toString();
                    userName = name.getText().toString();
                    userPass = pass.getText().toString();

                    FirebaseFirestore database = FirebaseFirestore.getInstance();
                    HashMap<String, Object> users = new HashMap<>();
                    users.put(Constants.KEY_NAME, userName);
                    users.put(Constants.KEY_PASSWORD, userPass);
                    users.put(Constants.KEY_EMAIL, eMail);

                    database.collection(Constants.KEY_COLLECTION_USERS).add(users).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                            preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                            preferenceManager.putString(Constants.KEY_NAME, userName);
                            preferenceManager.putString(Constants.KEY_EMAIL, eMail);
                            Intent intent = new Intent(getApplicationContext(), OtherUsers.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Signup.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                });
    }
}