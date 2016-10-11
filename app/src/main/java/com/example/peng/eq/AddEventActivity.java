package com.example.peng.eq;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.*;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.DatePickerDialog;
import android.widget.DatePicker;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Fragment;
import android.app.FragmentManager;

import org.w3c.dom.Text;

import static android.R.attr.format;


//public class AddEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
public class AddEventActivity extends AppCompatActivity {
    private View mProgressView;
    private String hostId;
    TextView date_btn;
    int year_x, month_x, day_x;
    static final int DATE_ID = 0;
    static final int TIME_ID = 1;
    TextView time_btn;
    int hour_x, minute_x, second_x;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        mProgressView = findViewById(R.id.progress_bar);
        Intent intent = getIntent();
        hostId = intent.getStringExtra(MainActivity.HOST_ID);

        final Calendar cal = Calendar.getInstance();
        year_x = cal.get(Calendar.YEAR);
        month_x = cal.get(Calendar.MONTH);
        day_x = cal.get(Calendar.DAY_OF_MONTH);

        showDialogOnButtonClick();
        showTimeDialogOnButtonClick();

//        showProgress(true);

        //get current user or just search by id?
        //ParseQuery<ParseUser> queryUser = ParseUser.getQuery();
        ParseUser currentUser = ParseUser.getCurrentUser();
        if(currentUser != null) {

        }else{
            ParseQuery<ParseUser> queryUser = ParseUser.getQuery();
            queryUser.getInBackground(hostId, new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser object, ParseException e) {
                    if(e == null) {
                        TextView t = (TextView) findViewById(R.id.event_holder);
                        t.setText(object.getString("username"));
                    }else {
                        //Dialog.showDialog("No user found", "Please log in", AddEventActivity.this);
                    }
                }
            });
        }

    }

    //show time dialog
    public void showTimeDialogOnButtonClick(){
        time_btn = (TextView) findViewById(R.id.pick_time);
        time_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showDialog(TIME_ID);
            }
        });
    }

    public void showDialogOnButtonClick() {
        date_btn = (TextView) findViewById(R.id.pick_date);
        date_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showDialog(DATE_ID);
            }

        });
    }


    @Override
    protected Dialog onCreateDialog(int id){
        if(id == DATE_ID){
            return new DatePickerDialog(this,dpickerListener, year_x,month_x,day_x);
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
            hour_x = hourOfDay;
            minute_x = minute;
            time_btn.setText(hour_x + " : " + minute_x);
            //Toast.makeText(AddEventActivity.this, hour_x + " : " + minute_x, Toast.LENGTH_SHORT).show();
        }
    };

    private DatePickerDialog.OnDateSetListener dpickerListener
            = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year_x = year;
            month_x = monthOfYear + 1;
            day_x = dayOfMonth;
            date_btn.setText(year_x + "/" + month_x + "/" + day_x);
            //Toast.makeText(AddEventActivity.this, year_x + " / " + month_x + " / " + day_x, Toast.LENGTH_LONG).show();
        }
    };

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

    public void confirm(View view){
         boolean error = false;
         String eventName = "";
        String eventDate = "";
        String eventTime = "";
        int attendNum = 0;

        //detect if all required fields are entered
        TextView tt = (TextView)findViewById(R.id.event_name);
        if(isEmptyOrNull(tt)) {
            com.example.peng.eq.Dialog.showDialog("","Please enter an event name.", AddEventActivity.this);
            error = true;
        }else{
            eventName = tt.getText().toString();
        }

        tt = (TextView) findViewById(R.id.pick_date);
        if(isEmptyOrNull(tt)){
            com.example.peng.eq.Dialog.showDialog("","Please enter an event date.", AddEventActivity.this);
            error = true;
        }else{
            eventDate = tt.getText().toString();
        }

        tt = (TextView)findViewById(R.id.pick_time);
        if(isEmptyOrNull(tt)){
            com.example.peng.eq.Dialog.showDialog("","Please enter an event time.", AddEventActivity.this);
            error = true;
        }else {
            eventTime = tt.getText().toString();
        }

        tt = (TextView)findViewById(R.id.max_attendee);
        if(isEmptyOrNull(tt)){
            com.example.peng.eq.Dialog.showDialog("","Please enter the maximum number of attendees.", AddEventActivity.this);
            error = true;
        }else {
            attendNum = Integer.parseInt(tt.getText().toString());
        }

        if(!error){
            ParseObject event = new ParseObject("Event");
            event.put("title", eventName);
            event.put("eventDate", eventDate);
            event.put("eventTime", eventTime);
            event.put("maxAttendNum", attendNum);
            event.saveInBackground(new SaveCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        //myObjectSavedSuccessfully();

                    } else {
                        //myObjectSaveDidNotSucceed();

                    }
                }
            });
        }




    }

    private boolean isEmptyOrNull(TextView tt) {
        boolean res = false;
        String strTxt = tt.getText().toString();
        if(strTxt == null || strTxt.isEmpty()) {
            res = true;
        }else {
            res = false;
        }
        return res;
    }

//    //another example of datepicker
//    public void data_picker(View view) {
//        DatePickerFragment fragment = new DatePickerFragment();
//        fragment.show(getFragmentManager(), "date");
//    }
//
//    private void setDate(final Calendar calendar){
//        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
//        ((TextView) findViewById(R.id.showDate)).setText(dateFormat.format(calendar.getTime()));
//    }
//
//    public void onDateSet(DatePicker view, int year, int month, int day) {
//        Calendar cal = new GregorianCalendar(year, month, day);
//        setDate(cal);
//    }
//
//    public static class DatePickerFragment extends DialogFragment{
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            final Calendar c = Calendar.getInstance();
//            int year = c.get(Calendar.YEAR);
//            int month = c.get(Calendar.MONTH);
//            int day = c.get(Calendar.DAY_OF_MONTH);
//
//            return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener)getActivity(), year, month, day);
//        }
//    }
}
