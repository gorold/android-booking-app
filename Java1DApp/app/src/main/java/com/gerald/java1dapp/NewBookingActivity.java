package com.gerald.java1dapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class NewBookingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_booking);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_view_bookings:
                        Intent intent = new Intent(NewBookingActivity.this, ViewBookingsActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_new_booking:

                        break;
                    case R.id.navigation_view_tasks:
                        Intent intent1 = new Intent(NewBookingActivity.this, ViewTasksActivity.class);
                        startActivity(intent1);
                        break;
                }
                return false;
            }
        });
    }
}
