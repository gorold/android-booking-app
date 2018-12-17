package com.gerald.java1dapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ViewBookingsActivity extends AppCompatActivity {

    private String TAG = "Wubadub";
    private SharedPreferences mPreferences;
    private static final String MY_ID = "myId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bookings);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ViewPager viewPager = findViewById(R.id.view_bookings_view_pager);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));

        TabLayout tabLayout = findViewById(R.id.view_bookings_tab_layout);
        tabLayout.setupWithViewPager(viewPager);

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

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        private Fragment[] childFragments;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            childFragments = new Fragment[] {
                    new CurrentBookings(),
                    new PendingBookings(),
                    new RejectedBookings()
            };
        }

        @Override
        public Fragment getItem(int i) {
            return childFragments[i];
        }

        @Override
        public int getCount() {
            return childFragments.length;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            String title = getItem(position).getClass().getName();
            return title.subSequence(title.lastIndexOf(".") + 1, title.lastIndexOf("B"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Toast.makeText(ViewBookingsActivity.this, "Goodbye!", Toast.LENGTH_LONG).show();
            mPreferences = getSharedPreferences("sharedPref", MODE_PRIVATE);
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putString(MY_ID, "");
            editor.apply();
            Intent intent = new Intent(ViewBookingsActivity.this, LoginActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
