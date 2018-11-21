package com.gerald.java1dapp;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class viewBooking extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("View Bookings");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_booking);

    }
}
