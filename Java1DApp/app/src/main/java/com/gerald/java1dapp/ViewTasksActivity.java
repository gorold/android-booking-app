package com.gerald.java1dapp;

import com.gerald.java1dapp.R;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ViewTasksActivity extends AppCompatActivity {

    private final static String TAG = "Wubadub";
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter<ViewTasksModel, ViewTasksModelHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tasks);

        RecyclerView recyclerView = findViewById(R.id.view_tasks_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        db = FirebaseFirestore.getInstance();
        Query query = db.collection("Bookings").whereEqualTo("bookees.1002029", false).whereEqualTo("rejected", false);
        FirestoreRecyclerOptions<ViewTasksModel> options = new FirestoreRecyclerOptions.Builder<ViewTasksModel>()
                .setQuery(query, ViewTasksModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<ViewTasksModel, ViewTasksModelHolder>(options) {

            Context context;

            @Override
            protected void onBindViewHolder(@NonNull ViewTasksModelHolder holder, int position, @NonNull ViewTasksModel model) {
                holder.setBooker(model.getBooker());
                holder.setRoomId(model.getRoomId());
                holder.setStartTime(model.getStartTime());
                holder.setEndTime(model.getEndTime());

                final Button buttonOptions = holder.buttonOptions;
                final String documentId = getSnapshots().getSnapshot(position).getId();

                holder.buttonOptions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popup = new PopupMenu(context, buttonOptions);
                        popup.inflate(R.menu.view_tasks_popup_menu);

                        final DocumentReference booking = db.document("Bookings/"+documentId);

                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                switch (item.getItemId()) {

                                    case R.id.view_tasks_accept:
                                        booking.update("bookees.1002029", true);
                                        booking.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot doc = task.getResult();
                                                    if (doc.exists()) {
                                                        Map<String, Boolean> bookeeStatus = (Map) doc.get("bookees");
                                                        if (bookeeStatus.containsValue(false)) {
                                                            Log.d(TAG, "Contains false");
                                                        } else {
                                                            Log.d(TAG, "No false");
                                                            db.document("Bookings/" + documentId).update("approved", true);
                                                            // logic to change all approved to true if bookees are all true
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                        return true;

                                    case R.id.view_tasks_reject:
                                        booking.update("bookees.1002029", false);
                                        db.document("Bookings/" + documentId).update("rejected", true);
                                        return true;
                                }
                                return false;
                            }
                        });
                        popup.show();
                    }
                });
            }

            @NonNull
            @Override
            public ViewTasksModelHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                context = viewGroup.getContext();
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_tasks_card_layout, viewGroup, false);
                return new ViewTasksModelHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.navigation_view_bookings:
                        Intent intent1 = new Intent(ViewTasksActivity.this, ViewBookingsActivity.class);
                        startActivity(intent1);
                        break;

                    case R.id.navigation_new_booking:
                        Intent intent2 = new Intent(ViewTasksActivity.this, NewBookingActivity.class);
                        startActivity(intent2);
                        break;

                    case R.id.navigation_view_tasks:
                        break;
                }
                return false;
            }
        });
    }

    public class ViewTasksModelHolder extends RecyclerView.ViewHolder {
        private View view;
        private Button buttonOptions;

        ViewTasksModelHolder(View itemView) {
            super(itemView);
            view = itemView;
            buttonOptions = itemView.findViewById(R.id.buttonOptions);
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



