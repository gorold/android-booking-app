package com.gerald.java1dapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NewBookingActivity extends AppCompatActivity {

    private final String TAG = "Wubadub";
    private SharedPreferences mPreferences;
    private static final String MY_ID = "myId";
    private static final String MY_NAME = "myName";

    private Button pickDate;
    private Button startTimeButton;
    private Button endTimeButton;
    private Button findRoom;
    private Button bookButton;
    private EditText userOne;
    private EditText userTwo;
    private static String timeId;
    private static Date startTime;
    private static Date endTime;
    private FirebaseFirestore db;
    static String selectedRoom;
    static String roomName;

    private static List<String> unavailableRooms = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_booking);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mPreferences = getSharedPreferences("sharedPref", MODE_PRIVATE);
        final String myId = mPreferences.getString(MY_ID, "");
        final String myName = mPreferences.getString(MY_NAME, "");

        pickDate = findViewById(R.id.pick_date);
        startTimeButton = findViewById(R.id.start_time_button);
        endTimeButton = findViewById(R.id.end_time_button);
        findRoom = findViewById(R.id.find_room);
        bookButton = findViewById(R.id.book_button);
        userOne = findViewById(R.id.user_one);
        userTwo = findViewById(R.id.user_two);

        Intent i = getIntent();
        if(i.getExtras()!=null) {
            if (i.getExtras().containsKey("ROOM_ID")) {
                selectedRoom = i.getStringExtra("ROOM_ID");
                long startTimeNumber = i.getLongExtra("START_TIME", -1);
                startTime = new Date();
                startTime.setTime(startTimeNumber);
                long endTimeNumber = i.getLongExtra("END_TIME", -1);
                endTime = new Date();
                endTime.setTime(endTimeNumber);
                roomName = i.getStringExtra("ROOM_NAME");

                DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
                String formattedDate = df.format(startTime);
                pickDate.setText(formattedDate);

                SimpleDateFormat format = new SimpleDateFormat("h:mm a");
                String startTimeOut = format.format(startTime);
                String endTimeOut = format.format(endTime);
                startTimeButton.setText(startTimeOut);
                endTimeButton.setText(endTimeOut);

                findRoom.setText(String.format(getResources().getString(R.string.room_name_id), roomName, selectedRoom));
            }
        }
        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize a new date picker dialog fragment
                DialogFragment dFragment = new DatePickerFragment();

                // Show the date picker dialog fragment
                dFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });


        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pickDate.getText().toString().equals("Pick a date")) {
                    Toast.makeText(NewBookingActivity.this, "Pick a date first!", Toast.LENGTH_LONG).show();
                } else {
                    timeId = "start";
                    DialogFragment newFragment = new TimePickerFragment();
                    newFragment.show(getSupportFragmentManager(), "timePicker");
                }
            }
        });


        endTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pickDate.getText().toString().equals("Pick a date")) {
                    Toast.makeText(NewBookingActivity.this, "Pick a date first!", Toast.LENGTH_LONG).show();
                } else {
                    timeId = "end";
                    DialogFragment newFragment = new TimePickerFragment();
                    newFragment.show(getSupportFragmentManager(), "timePicker");
                }
            }
        });

        findRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = FirebaseFirestore.getInstance();
                Query query = db.collection("Bookings").whereEqualTo("rejected", false);

                if (startTime.after(endTime)) {
                    Toast.makeText(NewBookingActivity.this, "You cannot start your booking after it ends!", Toast.LENGTH_LONG).show();
                }
                else {
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot snapshots = task.getResult();
                                for (DocumentSnapshot doc: snapshots.getDocuments()) {
                                    BookingsModel booking = doc.toObject(BookingsModel.class);
                                    if (!booking.isAvailable(startTime, endTime)) {
                                        unavailableRooms.add(booking.getRoomId());
                                    }
                                }
                                db.collection("Rooms").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                                            for (DocumentSnapshot doc: documentSnapshots) {
                                                if (!unavailableRooms.contains(doc.getId())) {
                                                    Map<String, Boolean> data = new HashMap<>();
                                                    data.put("available_" + myId, true);
                                                    db.collection("Rooms").document(doc.getId()).set(data, SetOptions.merge());
                                                }
                                                else {
                                                    Map<String, Boolean> data = new HashMap<>();
                                                    data.put("available_" + myId, false);
                                                    db.collection("Rooms").document(doc.getId()).set(data, SetOptions.merge());
                                                }
                                            }

                                            Intent intent = new Intent(NewBookingActivity.this, AvailableRoomsActivity.class);
                                            intent.putExtra("START_TIME", startTime.getTime());
                                            intent.putExtra("END_TIME", endTime.getTime());
                                            startActivity(intent);
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((findRoom.getText().equals("Find a room!"))
                        || (pickDate.getText().equals("Pick a date"))
                        || (startTimeButton.getText().equals("Pick a time"))
                        || (endTimeButton.getText().equals("Pick a time"))
                        || (userOne.getText().toString().equals(""))
                        || (userTwo.getText().toString().equals(""))) {
                    Toast.makeText(NewBookingActivity.this, "Please fill in the required fields", Toast.LENGTH_LONG).show();
                }
                else {
                    db = FirebaseFirestore.getInstance();
                    String userOneString = userOne.getText().toString();
                    String userTwoString = userTwo.getText().toString();
                    BookingsModel booking = new BookingsModel(myId, userOneString, userTwoString,
                            myName, startTime, endTime, selectedRoom, roomName);
                    db.collection("Bookings").add(booking);
                    Toast.makeText(NewBookingActivity.this, "Congratulations! Your booking is now pending!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(NewBookingActivity.this, ViewBookingsActivity.class);
                    startActivity(intent);
                }
            }
        });

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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            Toast.makeText(NewBookingActivity.this, "Goodbye!", Toast.LENGTH_LONG).show();
            mPreferences = getSharedPreferences("sharedPref", MODE_PRIVATE);
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putString(MY_ID, "");
            editor.apply();
            Intent intent = new Intent(NewBookingActivity.this, LoginActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dpd = new DatePickerDialog(getActivity(), this, year, month, day );
            return dpd;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            // Do something with the time chosen by the user
            Button button = getActivity().findViewById(R.id.pick_date);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(0);
            cal.set(year, month, dayOfMonth, 0, 0, 0);
            Date chosenDate = cal.getTime();

            // Format the date using style and locale
            DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
            String formattedDate = df.format(chosenDate);

            Calendar startTimeCal = Calendar.getInstance();
            startTimeCal.set(year, month, dayOfMonth);
            startTime = startTimeCal.getTime();

            Calendar endTimeCal = Calendar.getInstance();
            endTimeCal.set(year, month, dayOfMonth);
            endTime = endTimeCal.getTime();

            // Display the chosen date to app interface
            button.setText(formattedDate);
        }
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(),this, hour, minute, false);
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Button button;
            if (timeId.equals("start")) {
                button = getActivity().findViewById(R.id.start_time_button);
                Calendar startTimeCal = Calendar.getInstance();
                startTimeCal.setTime(startTime);
                startTimeCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                startTimeCal.set(Calendar.MINUTE, minute);
                startTime = startTimeCal.getTime();
            }
            else {
                button = getActivity().findViewById(R.id.end_time_button);
                Calendar endTimeCal = Calendar.getInstance();
                endTimeCal.setTime(endTime);
                endTimeCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                endTimeCal.set(Calendar.MINUTE, minute);
                endTime = endTimeCal.getTime();
            }
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
            cal.set(Calendar.MINUTE, minute);
            Date time = cal.getTime();
            SimpleDateFormat format = new SimpleDateFormat("h:mm a");
            String timeOut = format.format(time);
            button.setText(timeOut);
        }
    }
}
