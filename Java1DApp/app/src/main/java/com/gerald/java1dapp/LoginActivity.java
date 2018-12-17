package com.gerald.java1dapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    int RC_SIGN_IN = 1;
    private FirebaseFirestore db;
    private SharedPreferences mPreferences;
    private static final String MY_ID = "myId";
    private static final String MY_NAME = "myName";
    private static final String TAG = "Wubadub";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mPreferences = getSharedPreferences("sharedPref", MODE_PRIVATE);
        String myId = mPreferences.getString(MY_ID, "");


        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build());
        if (myId != "") {
            Intent intent = new Intent(LoginActivity.this, ViewBookingsActivity.class);
            startActivity(intent);
        } else {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(), RC_SIGN_IN
            );
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                final Query query = FirebaseFirestore.getInstance().collection("Users").whereEqualTo("email", user.getEmail());
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            String studentId = querySnapshot.getDocuments().get(0).getId();
                            String studentName = querySnapshot.getDocuments().get(0).get("name").toString();
                            mPreferences = getSharedPreferences("sharedPref", MODE_PRIVATE);
                            SharedPreferences.Editor editor = mPreferences.edit();
                            editor.putString(MY_ID, studentId);
                            editor.putString(MY_NAME, studentName);
                            Log.d(TAG, "User's Student Id: " + studentId);
                            Log.d(TAG, studentName);
                            editor.apply();
                        }
                    }
                });
                Intent intent = new Intent(LoginActivity.this, ViewBookingsActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
    }

}
