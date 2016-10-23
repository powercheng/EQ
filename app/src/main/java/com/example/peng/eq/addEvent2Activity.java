package com.example.peng.eq;



import android.app.*;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class addEvent2Activity extends AppCompatActivity {
    int year_x, month_x, day_x;
    int hour_x, minute_x, second_x;
    static final int DATE_ID = 0;
    static final int TIME_ID = 1;
    private TextView eventDate;
    private TextView eventTime;
    private EditText eventState;
    private EditText street,city,state,zip;
    private String hostId;
    private String time;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event2);

        Intent intent = getIntent();
        hostId = ParseUser.getCurrentUser().getObjectId();

        street = (EditText) findViewById(R.id.event_address);
        city = (EditText) findViewById(R.id.event_city);
        state = (EditText) findViewById(R.id.event_state);
        zip = (EditText) findViewById(R.id.event_zip);

        final Calendar cal = Calendar.getInstance();
        year_x = cal.get(Calendar.YEAR);
        month_x = cal.get(Calendar.MONTH);
        day_x = cal.get(Calendar.DAY_OF_MONTH);

        time = "";

        //initialize textview
        eventDate = (TextView) findViewById(R.id.event_date);
        eventTime = (TextView) findViewById(R.id.event_time);
        eventState = (EditText) findViewById(R.id.event_state);
        InputFilter[] filterArray = new InputFilter[2];
        filterArray[0] = new InputFilter.LengthFilter(2);
        filterArray[1] = new InputFilter.AllCaps();
        eventState.setFilters(filterArray);
        eventTime.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                //TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    eventTime.setError("", null);
                } else {
                    eventTime.setError("Please set the event time!");
                }
            }
        });

        eventDate.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    eventDate.setError("", null);
                } else {
                    eventDate.setError("Please set the event date!");
                }
            }
        });

    }


    private List<Double> searchFromLocationName(String name) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Double> res = new ArrayList<Double>();
        try {
            List<Address> result
                    = geocoder.getFromLocationName(name, 5);

            if ((result == null) || (result.isEmpty())) {
                Toast.makeText(addEvent2Activity.this,
                        "No matches were found or there is no backend service!",
                        Toast.LENGTH_LONG).show();
            } else {
                Address location = result.get(0);
                double lati = location.getLatitude();
                double longi = location.getLongitude();
                res.add(lati);
                res.add(longi);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return res;
    }

    public void addEventButtonOnclick(View view) {
        //validate all the input fields before submitting

        boolean hasError = false;

        EditText eventName = (EditText) findViewById(R.id.event_name);
        String strEventName = eventName.getText().toString();
        if(TextUtils.isEmpty(strEventName)) {
            eventName.setError("Please enter a title for the event!");
            hasError = true;
//            return;
        }

        EditText eventAddress = (EditText) findViewById(R.id.event_address);
        String strEventAddress = eventAddress.getText().toString();
        if(TextUtils.isEmpty(strEventAddress)) {
            eventAddress.setError("Please enter an address for the event!");
            hasError = true;
//            return;
        }

        EditText eventCity = (EditText) findViewById(R.id.event_city);
        String strEventCity = eventCity.getText().toString();
        if(TextUtils.isEmpty(strEventCity)) {
            eventCity.setError("Please enter the city!");
            hasError = true;
//            return;
        }

        EditText eventZip = (EditText) findViewById(R.id.event_zip);
        String strEventZip = eventZip.getText().toString();
        if(TextUtils.isEmpty(strEventZip)) {
            eventZip.setError("Please enter the zip!");
            hasError = true;
//            return;
        } else if(strEventZip.length() < 5) {
            eventZip.setError("Please enter a 5 digit zip!");
            hasError = true;
        }

        String strEventDate = eventDate.getText().toString();
        if(TextUtils.isEmpty(strEventDate)) {
            eventDate.setError("Please set the event date!");
            hasError = true;
//            return;
        }

        String strEventTime = eventTime.getText().toString();
        if(TextUtils.isEmpty(strEventTime)) {
            eventTime.setError("Please set the event time!");
            hasError = true;
//            return;
        }

        EditText eventMaxAttendee = (EditText) findViewById(R.id.max_attendee);
        String streEventMaxAttendee = eventMaxAttendee.getText().toString();
        if(TextUtils.isEmpty(streEventMaxAttendee)) {
            eventMaxAttendee.setError("Please enter a max number of attendees!");
            hasError = true;
//            return;
        }

        EditText eventDescription = (EditText) findViewById(R.id.event_description);
        String strEventDescription = eventDescription.getText().toString();
        if(TextUtils.isEmpty(strEventDescription)) {
            eventDescription.setError("Please enter a description for the event!");
            hasError = true;
//            return;
        }

        EditText eventState = (EditText) findViewById(R.id.event_state);
        String strEventState = eventState.getText().toString();
        if(TextUtils.isEmpty(strEventState) || strEventState.length() != 2) {
            eventState.setError("Please set the event date");
            hasError = true;
//            return;
        }

        //parse eventdatetime as Date format
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String strEventDateAll = strEventDate + " " + time;
        Date startDate = null;
        try {
            startDate = df.parse(strEventDateAll);

        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        //for comparison, initialize today's date
        Calendar today = Calendar.getInstance();
        today.clear(Calendar.HOUR);
        today.clear(Calendar.MINUTE);
        today.clear(Calendar.SECOND);
        Date todayDate = today.getTime();
        if(!startDate.after(todayDate)){
            hasError = true;
            eventDate.setError("Please enter a future date/time!");
        }

        if(!hasError) {
            String newAddress = street.getText().toString() + ", " + city.getText().toString() + ", " + state.getText().toString() + ", " + zip.getText().toString();
            List<Double> latlong = searchFromLocationName(newAddress);

            if(latlong == null || latlong.size() == 0) {
                street.setError("Please enter a valid address!");
            } else {
                //store info
                double latitude, longitude;
                latitude = latlong.get(0);
                longitude = latlong.get(1);

                ParseObject saveEventObj = new ParseObject("Event");
                saveEventObj.put("title", strEventName);
                saveEventObj.put("eventDateTime",  startDate);
                saveEventObj.put("address", strEventAddress);
                saveEventObj.put("street", strEventAddress);
                saveEventObj.put("zip", strEventZip);
                saveEventObj.put("state", strEventState);
                saveEventObj.put("city", strEventCity);
                saveEventObj.put("description", strEventDescription);
                saveEventObj.put("maxAttendNum", Integer.parseInt(streEventMaxAttendee));
                ParseGeoPoint eventLocation = new ParseGeoPoint(latitude, longitude);
                saveEventObj.put("eventLocation", eventLocation);
                ParseObject eventHost = ParseObject.createWithoutData("_User", hostId);
                saveEventObj.put("hostId", eventHost);
                saveEventObj.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null) {
                            Toast.makeText(addEvent2Activity.this, "The event is successfully saved!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(addEvent2Activity.this, MapActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(addEvent2Activity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        }
    }


    @Override
    protected android.app.Dialog onCreateDialog(int id){
        if(id == DATE_ID){
            return new DatePickerDialog(this, dpickerListener, year_x,month_x,day_x);
        }

        else if(id == TIME_ID) {
            return new TimePickerDialog(this, tpickerListener, hour_x, minute_x, false);
        }
        return null;
    }


    private TimePickerDialog.OnTimeSetListener tpickerListener
            = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            time = "";
            hour_x = hourOfDay;
            String ampm = "";
//            String time = "";
            if(hour_x < 12) {
                time += "0" + hour_x;
                ampm = "AM";
            }
            else {
                time += hour_x;
                hour_x -= 12;
                ampm = "PM";
            }
            time += ":";
            minute_x = minute;
            if(minute_x == 0) {
                eventTime.setText(hour_x + " : " + "00" + " " + ampm);
                time += "00";
            }
            else if(minute_x < 10){

                time += "0" + minute_x;
            } else {
                time += minute_x;
                eventTime.setText(hour_x + " : " + minute_x + " " + ampm);
            }

            //Toast.makeText(AddEventActivity.this, hour_x + " : " + minute_x, Toast.LENGTH_SHORT).show();
        }
    };

    private DatePickerDialog.OnDateSetListener dpickerListener
            = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String date = "";
            year_x = year;
            month_x = monthOfYear + 1;
            day_x = dayOfMonth;
            date += year_x + "/";
            if(month_x < 10) date += "0" + month_x + "/";
            else date += month_x + "/";
            if(day_x < 10) date += "0" + day_x;
            else date += day_x;
            eventDate.setText(date);
            //Toast.makeText(AddEventActivity.this, year_x + " / " + month_x + " / " + day_x, Toast.LENGTH_LONG).show();
        }
    };

    public void eventTimeTextViewOnclick(View view){
        showDialog(TIME_ID);
    }

    public void eventDateTextViewOnclick(View view){
//        eventDate.setOnClickListener(new View.OnClickListener(){
        showDialog(DATE_ID);
    }

/*
    public GeoPoint getLocationFromAddress(String strAddress){
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        GeoPoint p1 = null;
        try {
            address = coder.getFromLocationName(strAddress,5);
            if (address==null) {
                return null;
            }
            Address location=address.get(0);
            location.getLatitude();
            location.getLongitude();
            p1 = new GeoPoint((double) (location.getLatitude() * 1E6),
                    (double) (location.getLongitude() * 1E6));
            return p1;
        }
    }*/
}