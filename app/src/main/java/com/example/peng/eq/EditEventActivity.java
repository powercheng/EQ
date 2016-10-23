package com.example.peng.eq;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.vision.text.Text;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
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

public class EditEventActivity extends AppCompatActivity {
    private String eventId;
    private View mProgressView;
    private TextView eventTime, eventDate;
    private int year_x, month_x, day_x;
    private int hour_x, minute_x, second_x;
    static final int DATE_ID = 0;
    static final int TIME_ID = 1;
    private EditText street,city,state,zip;
    private String time;
    private Date startDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        Intent intent = getIntent();


        //initialize variables
        eventId = intent.getStringExtra(MainActivity.EVENT_ID);

        street = (EditText) findViewById(R.id.event_address);
        city = (EditText) findViewById(R.id.event_city);
        state = (EditText) findViewById(R.id.event_state);
        zip = (EditText) findViewById(R.id.event_zip);

        startDate = null;
        eventDate = (TextView) findViewById(R.id.event_date);
        eventTime = (TextView) findViewById(R.id.event_time);
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

//        mProgressView = findViewById(R.id.progress_bar);

//        showProgress(true);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query.include("hostId");
        query.getInBackground(eventId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null) {
                    String evetName = object.getString("title");
                    EditText et = (EditText) findViewById(R.id.event_name);
                    et.setText(evetName);

                    //set date and time
                    Date eventDate = object.getDate("eventDateTime");
                    DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                    String strEventDate = df.format(eventDate);
                    String[] ss = strEventDate.split(" ");
                    TextView t = (TextView) findViewById(R.id.event_date);
                    t.setText(ss[0]);
                    t = (TextView) findViewById(R.id.event_time);
                    t.setText(ss[1]);

                    String street = object.getString("street");
                    et = (EditText) findViewById(R.id.event_address);
                    et.setText(street);

                    String city = object.getString("city");
                    et = (EditText) findViewById(R.id.event_city);
                    et.setText(city);

                    String state = object.getString("state");
                    et = (EditText) findViewById(R.id.event_state);
                    et.setText(state);

                    String zip = object.getString("zip");
                    et = (EditText) findViewById(R.id.event_zip);
                    et.setText(zip);

                    int maxAttend = object.getInt("maxAttendNum");
                    et = (EditText) findViewById(R.id.max_attendee);
                    et.setText(Integer.toString(maxAttend));

                    String description = object.getString("description");
                    et = (EditText)findViewById(R.id.event_description);
                    et.setText(description);

                }else {
                    Dialog.showDialog("Oops..", "There happens an error when searching for the event", EditEventActivity.this);
                }
            }
        });

    }

    public void editEventButtonOnclick(View view) {
        //check the completeness of information
        boolean hasError = false;

        EditText eventName = (EditText) findViewById(R.id.event_name);
        final String strEventName = eventName.getText().toString();
        if(TextUtils.isEmpty(strEventName)) {
            eventName.setError("Please enter a title for the event!");
            hasError = true;
//            return;
        }

        EditText eventAddress = (EditText) findViewById(R.id.event_address);
        final String strEventAddress = eventAddress.getText().toString();
        if(TextUtils.isEmpty(strEventAddress)) {
            eventAddress.setError("Please enter an address for the event!");
            hasError = true;
//            return;
        }

        EditText eventCity = (EditText) findViewById(R.id.event_city);
        final String strEventCity = eventCity.getText().toString();
        if(TextUtils.isEmpty(strEventCity)) {
            eventCity.setError("Please enter the city!");
            hasError = true;

        }

        EditText eventZip = (EditText) findViewById(R.id.event_zip);
        final String strEventZip = eventZip.getText().toString();
        if(TextUtils.isEmpty(strEventZip)) {
            eventZip.setError("Please enter the zip!");
            hasError = true;

        } else if(strEventZip.length() < 5) {
            eventZip.setError("Please enter a 5 digit zip!");
            hasError = true;
        }

        final String strEventDate = eventDate.getText().toString();
        if(TextUtils.isEmpty(strEventDate)) {
            eventDate.setError("Please set the event date!");
            hasError = true;

        }

        final String strEventTime = eventTime.getText().toString();
        if(TextUtils.isEmpty(strEventTime)) {
            eventTime.setError("Please set the event time!");
            hasError = true;

        }

        EditText eventMaxAttendee = (EditText) findViewById(R.id.max_attendee);
        final String streEventMaxAttendee = eventMaxAttendee.getText().toString();
        if(TextUtils.isEmpty(streEventMaxAttendee)) {
            eventMaxAttendee.setError("Please enter a max number of attendees!");
            hasError = true;

        }

        EditText eventDescription = (EditText) findViewById(R.id.event_description);
        final String strEventDescription = eventDescription.getText().toString();
        if(TextUtils.isEmpty(strEventDescription)) {
            eventDescription.setError("Please enter a description for the event!");
            hasError = true;
        }

        EditText eventState = (EditText) findViewById(R.id.event_state);
        final String strEventState = eventState.getText().toString();
        if(TextUtils.isEmpty(strEventState)) {
            eventState.setError("Please enter the event date!");
            hasError = true;
        }

        //parse eventdatetime as Date format
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String strEventDateAll = strEventDate + " " + time;
//        Date startDate = null;
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
                final double latitude, longitude;
                latitude = latlong.get(0);
                longitude = latlong.get(1);

                //update current event
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                query.getInBackground(eventId, new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject saveEventObj, ParseException e) {
                        if(e == null) {
                            saveEventObj.put("eventDateTime",  startDate);
                            saveEventObj.put("title", strEventName);
                            saveEventObj.put("street", strEventAddress);
                            saveEventObj.put("city", strEventCity);
                            saveEventObj.put("zip", strEventZip);
                            saveEventObj.put("state", strEventState);
                            saveEventObj.put("description", strEventDescription);
                            saveEventObj.put("maxAttendNum", Integer.parseInt(streEventMaxAttendee));
                            ParseGeoPoint eventLocation = new ParseGeoPoint(latitude, longitude);
                            saveEventObj.put("eventLocation", eventLocation);
                            //no need update hostId
        //                ParseObject eventHost = ParseObject.createWithoutData("_User", hostId);
        //                saveEventObj.put("hostId", eventHost);
                            saveEventObj.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e == null) {
                                        Toast.makeText(EditEventActivity.this, "The event is successfully saved!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(EditEventActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {

                        }
                    }
                });
            }

        }
    }

    private List<Double> searchFromLocationName(String name) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Double> res = new ArrayList<Double>();
        try {
            List<Address> result
                    = geocoder.getFromLocationName(name, 5);

            if ((result == null) || (result.isEmpty())) {
                Toast.makeText(EditEventActivity.this,
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

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
}
