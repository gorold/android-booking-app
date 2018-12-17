package com.gerald.java1dapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AvailableRoomsActivity extends AppCompatActivity {

    private final String TAG = "Wubadub";
    private SharedPreferences mPreferences;
    private static final String MY_ID = "myId";
    private FirebaseFirestore db;

    private FirestoreRecyclerAdapter<RoomsModel, AvailableRoomsModelHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_rooms);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mPreferences = getSharedPreferences("sharedPref", MODE_PRIVATE);
        final String myId = mPreferences.getString(MY_ID, "");

        final long startTime = getIntent().getLongExtra("START_TIME", -1);
        final long endTime = getIntent().getLongExtra("END_TIME", -1);


        RecyclerView recyclerView = findViewById(R.id.available_rooms_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        db = FirebaseFirestore.getInstance();
        Query query = db.collection("Rooms").whereEqualTo("available_" + myId, true);
        FirestoreRecyclerOptions<RoomsModel> options = new FirestoreRecyclerOptions.Builder<RoomsModel>()
                .setQuery(query, RoomsModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<RoomsModel, AvailableRoomsModelHolder>(options) {

            Context context;
            @Override
            protected void onBindViewHolder(@NonNull AvailableRoomsModelHolder holder, int position, @NonNull RoomsModel model) {
                holder.setRoomNameId(model.getName(), model.getRoomId());
                holder.setRoomCapacity(model.getCapacity());
                holder.setRoomType(model.getType());

                final Button chooseButton = holder.buttonChoose;
                final String roomId = model.getRoomId();
                final String roomName = model.getName();

                chooseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AvailableRoomsActivity.this, NewBookingActivity.class);
                        intent.putExtra("ROOM_ID",roomId);
                        intent.putExtra("START_TIME", startTime);
                        intent.putExtra("END_TIME", endTime);
                        intent.putExtra("ROOM_NAME", roomName);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public AvailableRoomsModelHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                context = viewGroup.getContext();
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.available_rooms_card_layout, viewGroup, false);
                return new AvailableRoomsModelHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.navigation_view_bookings:
                        Intent intent1 = new Intent(AvailableRoomsActivity.this, ViewBookingsActivity.class);
                        startActivity(intent1);
                        break;

                    case R.id.navigation_new_booking:
                        Intent intent2 = new Intent(AvailableRoomsActivity.this, NewBookingActivity.class);
                        startActivity(intent2);
                        break;

                    case R.id.navigation_view_tasks:
                        Intent intent3 = new Intent(AvailableRoomsActivity.this, ViewTasksActivity.class);
                        startActivity(intent3);
                        break;
                }
                return false;
            }
        });

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
            Toast.makeText(AvailableRoomsActivity.this, "Goodbye!", Toast.LENGTH_LONG).show();
            mPreferences = getSharedPreferences("sharedPref", MODE_PRIVATE);
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putString(MY_ID, "");
            editor.apply();
            Intent intent = new Intent(AvailableRoomsActivity.this, LoginActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class AvailableRoomsModelHolder extends RecyclerView.ViewHolder {
        private View view;
        private Button buttonChoose;

        AvailableRoomsModelHolder(View itemView) {
            super(itemView);
            view = itemView;
            buttonChoose = itemView.findViewById(R.id.button_choose_this_room);
        }

        void setRoomNameId(String roomName, String roomId) {
            int roomImage;
            ImageView imageView = view.findViewById(R.id.room_pic);
            String tempRoomId = roomId.replace(".", "");
            roomImage = getResources().getIdentifier("room_" + tempRoomId, "drawable", "com.gerald.java1dapp");
            if (roomImage == 0) {
                roomImage = getResources().getIdentifier("room_default", "drawable", "com.gerald.java1dapp");
            }

            imageView.setImageResource(roomImage);

            TextView textView = view.findViewById(R.id.room_name);
            textView.setText(String.format(getResources().getString(R.string.room_name_id), roomName, roomId));
        }

        void setRoomCapacity(String capacity) {
            TextView textView = view.findViewById(R.id.capacity);
            textView.setText(String.format(getResources().getString(R.string.room_capacity), capacity));
        }

        void setRoomType(String type) {
            TextView textView = view.findViewById(R.id.type);
            textView.setText(String.format(getResources().getString(R.string.room_type), type));
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
