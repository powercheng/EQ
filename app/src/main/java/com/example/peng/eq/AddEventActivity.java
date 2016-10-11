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
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.DatePickerDialog;
import android.widget.DatePicker;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Fragment;
import android.app.FragmentManager;


//public class AddEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
public class AddEventActivity extends AppCompatActivity {
    private View mProgressView;
    private String hostId;
    Button btn;
    int year_x, month_x, day_x;
    static final int DIALOG_ID = 0;


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

//        showProgress(true);

        //get current user or just search by id?
        //ParseQuery<ParseUser> queryUser = ParseUser.getQuery();
//        ParseUser currentUser = ParseUser.getCurrentUser();
//        if(currentUser != null) {
//
//        }else{
//            ParseQuery<ParseUser> queryUser = ParseUser.getQuery();
//            queryUser.getInBackground(hostId, new GetCallback<ParseUser>() {
//                @Override
//                public void done(ParseUser object, ParseException e) {
//                    if(e == null) {
//
//                    }else {
//                        Dialog.showDialog("No user found", "Please log in", AddEventActivity.this);
//                    }
//                }
//            })
//        }

    }

    public void showDialogOnButtonClick() {
        btn = (Button) findViewById(R.id.pick_date);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID);
            }

        });
    }


    @Override
    protected Dialog onCreateDialog(int id){
        if(id == DIALOG_ID)
            return new DatePickerDialog(this,dpickerListener, year_x,month_x,day_x);
        return null;
    }

    private DatePickerDialog.OnDateSetListener dpickerListener
            = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year_x = year;
            month_x = monthOfYear + 1;
            day_x = dayOfMonth;
            Toast.makeText(AddEventActivity.this, year_x + " / " + month_x + " / " + day_x, Toast.LENGTH_LONG).show();
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
