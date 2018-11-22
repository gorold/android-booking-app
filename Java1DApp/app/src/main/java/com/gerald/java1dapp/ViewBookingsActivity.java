package com.gerald.java1dapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class ViewBookingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bookings);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_view_bookings:
                        break;
                    case R.id.navigation_new_booking:
                        Intent intent1 = new Intent(ViewBookingsActivity.this, NewBookingActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.navigation_view_tasks:
                        Intent intent2 = new Intent(ViewBookingsActivity.this, ViewTasksActivity.class);
                        startActivity(intent2);
                        break;
                }
                return false;
            }
        });
    }
}
