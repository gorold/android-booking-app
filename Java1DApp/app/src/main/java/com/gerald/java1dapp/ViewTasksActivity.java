package com.gerald.java1dapp;

import com.gerald.java1dapp.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

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

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ViewTasksActivity extends AppCompatActivity {

    private final static String TAG = "Wubadub";
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter<BookingsModel, ViewTasksModelHolder> adapter;
    private SharedPreferences mPreferences;
    private static final String MY_ID = "myId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tasks);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mPreferences = getSharedPreferences("sharedPref", MODE_PRIVATE);
        final String myId = mPreferences.getString(MY_ID, "");

        RecyclerView recyclerView = findViewById(R.id.view_tasks_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        db = FirebaseFirestore.getInstance();
        Query query = db.collection("Bookings").whereEqualTo("bookees."+myId, false).whereEqualTo("rejected", false);
        FirestoreRecyclerOptions<BookingsModel> options = new FirestoreRecyclerOptions.Builder<BookingsModel>()
                .setQuery(query, BookingsModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<BookingsModel, ViewTasksModelHolder>(options) {

            Context context;

            @Override
            protected void onBindViewHolder(@NonNull ViewTasksModelHolder holder, int position, @NonNull BookingsModel model) {
                holder.setBookerName(model.getBookerName());
                holder.setRoomNameId(model.getRoomName(), model.getRoomId());
                holder.setDate(model.getStartTime());
                holder.setTime(model.getStartTime(), model.getEndTime());

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
                                        booking.update("bookees." + myId, true);
                                        booking.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot doc = task.getResult();
                                                    if (doc.exists()) {
                                                        Map<String, Boolean> bookeeStatus = (Map) doc.get("bookees");
                                                        if (bookeeStatus.containsValue(false)) {
                                                        } else {
                                                            db.document("Bookings/" + documentId).update("approved", true);
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                        return true;

                                    case R.id.view_tasks_reject:
                                        booking.update("bookees." + myId, false);
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

        void setBookerName(String bookerName) {
            TextView textView = view.findViewById(R.id.booker_name);
            textView.setText(String.format(getResources().getString(R.string.from_booker), bookerName));
        }

        void setRoomNameId(String roomName, String roomId) {
            TextView textView = view.findViewById(R.id.room_name);
            textView.setText(String.format(getResources().getString(R.string.room_name_id_task), roomName, roomId));
        }

        void setDate(Date startTime) {
            TextView textView = view.findViewById(R.id.booking_date);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String date = dateFormat.format(startTime);
            SimpleDateFormat dayFormat = new SimpleDateFormat("EE");
            String day = dayFormat.format(startTime);
            textView.setText(String.format(getResources().getString(R.string.set_date_task), day, date));
        }

        void setTime(Date startTime, Date endTime) {
            TextView textView = view.findViewById(R.id.timing);
            SimpleDateFormat format = new SimpleDateFormat("h:mm");
            SimpleDateFormat format1 = new SimpleDateFormat("h:mm a");
            String startTime_out = format.format(startTime);
            String endTime_out = format1.format(endTime);
            textView.setText(String.format(getResources().getString(R.string.start_end_time_task), startTime_out, endTime_out));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            Toast.makeText(ViewTasksActivity.this, "Goodbye!", Toast.LENGTH_LONG).show();
            mPreferences = getSharedPreferences("sharedPref", MODE_PRIVATE);
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putString(MY_ID, "");
            editor.apply();
            Intent intent = new Intent(ViewTasksActivity.this, LoginActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}



