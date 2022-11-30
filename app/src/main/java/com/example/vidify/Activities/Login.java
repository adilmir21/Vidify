package com.example.vidify.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.vidify.R;
import com.example.vidify.Utilities.Constants;
import com.example.vidify.Utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Login extends AppCompatActivity {

    EditText email,pass;
    Button login,signup;
    FirebaseAuth auth;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferenceManager = new PreferenceManager(getApplicationContext());
        if(preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN))
        {
            Intent intent = new Intent(getApplicationContext(),OtherUsers.class);
            startActivity(intent);
            finish();
        }
        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email);
        pass = findViewById(R.id.password);
        login = findViewById(R.id.l1);
        signup = findViewById(R.id.s1);

        ImageView img = null;

        login.setOnClickListener(v ->
        {
            String eMail, userName, userPass;
            eMail = email.getText().toString();
            userPass = pass.getText().toString();
            if (eMail.trim().isEmpty()) {
                Toast.makeText(Login.this, "Enter Email", Toast.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(eMail).matches()) {
                Toast.makeText(Login.this, "Enter Valid Email", Toast.LENGTH_SHORT).show();
            } else if (userPass.trim().isEmpty()) {
                Toast.makeText(Login.this, "Enter Password", Toast.LENGTH_SHORT).show();
            }
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection(Constants.KEY_COLLECTION_USERS)
                    .whereEqualTo(Constants.KEY_EMAIL, eMail)
                    .whereEqualTo(Constants.KEY_PASSWORD, userPass)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                            preferenceManager.putString(Constants.KEY_USER_ID,documentSnapshot.getId());
                            preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                            preferenceManager.putString(Constants.KEY_EMAIL, documentSnapshot.getString(Constants.KEY_EMAIL));
                            Intent intent = new Intent(getApplicationContext(), OtherUsers.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {
                            Toast.makeText(Login.this, "Unable to Login", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        signup.setOnClickListener(view ->
        {
            startActivity(new Intent(Login.this, Signup.class));
        });
    }
}