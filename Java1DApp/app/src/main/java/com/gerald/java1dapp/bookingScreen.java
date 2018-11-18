package com.gerald.java1dapp;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class bookingScreen extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_screen);

        Button setLocation = (Button) findViewById(R.id.setLocationButton);    //Setting Location Button
        setLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(bookingScreen.this, checkList.class));
            }
        });

        Button bookButton = (Button) findViewById(R.id.bookButton);
        bookButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Toast booked = Toast.makeText(getApplicationContext(),"Room has been booked!",Toast.LENGTH_LONG);
                booked.show();
            }
        });

        Button button = (Button) findViewById(R.id.datePicker); //assign the button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
    }

    public void setLocation(View v) {
        Intent setlocation = new Intent(bookingScreen.this, checkList.class);
        startActivity(setlocation);
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentdate = DateFormat.getDateInstance().format(c.getTime()); // uses the calendar instance to generate the string and stores it in the phone using the Date format

        TextView textView = (TextView) findViewById(R.id.Date); //assign a id to the textview
        textView.setText(currentdate);

    }

    public void setPax(View v) {
        EditText edittext = findViewById(R.id.numPax); //Gets the text from the message input box
        String userMessage = edittext.getText().toString(); // converts the text to string.
        // change if you want to send information to another side
//        Intent intent = new Intent(this,bookingScreen.class);
//        intent.putExtra("EXTRA_MESSAGE",userMessage);

        //startActivity(intent);
    }

}


