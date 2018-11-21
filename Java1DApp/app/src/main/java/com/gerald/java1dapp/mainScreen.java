package com.gerald.java1dapp;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;

public class mainScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Content Screen");

    }

    public void toLogInScreen(View view){
        startActivity(new Intent(this,MainActivity.class));
    }

    public void toBookInScreen(View view){
        startActivity(new Intent(this,bookingScreen.class));
    }

    public void toViewBookingScreen(View view){
        startActivity(new Intent(this, viewBooking.class));
    }
    public void toViewBookingCard(View view){
        startActivity(new Intent(this, bookingCard.class));
    }

}
