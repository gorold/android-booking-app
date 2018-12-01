package com.gerald.java1dapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;

public class ViewBookingsActivity extends AppCompatActivity {

    private String TAG = "Wubadub";
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter<ViewBookingsModel, ViewBookingsModelHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bookings);

        RecyclerView recyclerView = findViewById(R.id.view_bookings_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        db = FirebaseFirestore.getInstance();
        Query query = db.collection("Bookings").whereEqualTo("booker", "1002029");
        FirestoreRecyclerOptions<ViewBookingsModel> options = new FirestoreRecyclerOptions.Builder<ViewBookingsModel>()
                .setQuery(query, ViewBookingsModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<ViewBookingsModel, ViewBookingsModelHolder>(options) {

            Context context;

            @Override
            protected void onBindViewHolder(@NonNull ViewBookingsModelHolder holder, int position, @NonNull ViewBookingsModel model) {
                holder.setBooker(model.getBooker());
                holder.setRoomId(model.getRoomId());
                holder.setStartTime(model.getStartTime());
                holder.setEndTime(model.getEndTime());
            }

            @NonNull
            @Override
            public ViewBookingsModelHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                context = viewGroup.getContext();
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_tasks_card_layout, viewGroup, false);
                return new ViewBookingsModelHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);

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

    public class ViewBookingsModelHolder extends RecyclerView.ViewHolder {
        private View view;

        ViewBookingsModelHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        void setBooker(String booker) {
            TextView textView = view.findViewById(R.id.booker);
            textView.setText(booker);
        }

        void setRoomId(String roomId) {
            TextView textView = view.findViewById(R.id.room_id);
            textView.setText(roomId);
        }

        void setStartTime(Date startTime) {
            TextView textView = view.findViewById(R.id.start_time);
            textView.setText(startTime.toString());
        }

        void setEndTime(Date endTime) {
            TextView textView = view.findViewById(R.id.end_time);
            textView.setText(endTime.toString());
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter!=null) {
            adapter.stopListening();
        }
    }
}
