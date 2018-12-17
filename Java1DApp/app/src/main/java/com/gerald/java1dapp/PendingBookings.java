package com.gerald.java1dapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class PendingBookings extends Fragment {

    private SharedPreferences mPreferences;
    private static final String MY_ID = "myId";

    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter<BookingsModel, PendingBookingsHolder> adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_pending_bookings, container, false);
        mPreferences = this.getActivity().getSharedPreferences("sharedPref", MODE_PRIVATE);
        final String myId = mPreferences.getString(MY_ID, "");

        db = FirebaseFirestore.getInstance();
        Query pendingBookings = db.collection("Bookings").whereEqualTo("booker", myId).whereEqualTo("approved", false).whereEqualTo("rejected",false);
        FirestoreRecyclerOptions<BookingsModel> options = new FirestoreRecyclerOptions.Builder<BookingsModel>()
                .setQuery(pendingBookings, BookingsModel.class)
                .build();

        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        adapter = new FirestoreRecyclerAdapter<BookingsModel, PendingBookingsHolder>(options) {
            Context context;
            @Override
            protected void onBindViewHolder(@NonNull PendingBookingsHolder holder, int position, @NonNull BookingsModel model) {
                holder.setRoomNameId(model.getRoomName(), model.getRoomId());
                holder.setDate(model.getStartTime());
                holder.setTime(model.getStartTime(), model.getEndTime());
                holder.setPending(model.getBookees());
            }

            @NonNull
            @Override
            public PendingBookingsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                context = viewGroup.getContext();
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pending_bookings_card_layout, viewGroup, false);
                return new PendingBookingsHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        return rootView;
    }

    public class PendingBookingsHolder extends RecyclerView.ViewHolder {
        private View view;
        private Button buttonOptions;

        PendingBookingsHolder(View itemView) {
            super(itemView);
            view = itemView;
            buttonOptions = itemView.findViewById(R.id.buttonOptions);
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

        void setPending(Map<String, Boolean> pendingBookees) {
            TextView textView = view.findViewById(R.id.pending);
            String pendingList = "";
            for (Map.Entry<String, Boolean> entry: pendingBookees.entrySet()) {
                if (entry.getValue().equals(false)) {
                    if (pendingList.length() != 0)
                        pendingList = pendingList + ", " + entry.getKey();
                    else
                        pendingList = entry.getKey();
                }
            }

            textView.setText(String.format(getResources().getString(R.string.pending_bookees), pendingList));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter!=null) {
            adapter.stopListening();
        }
    }
}
