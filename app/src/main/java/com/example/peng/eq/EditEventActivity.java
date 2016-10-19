package com.example.peng.eq;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class EditEventActivity extends AppCompatActivity {
    private String eventId;
    private View mProgressView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        Intent intent = getIntent();
        eventId = intent.getStringExtra(MainActivity.EVENT_ID);

        mProgressView = findViewById(R.id.progress_bar);

        showProgress(true);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query.include("hostId");
        query.getInBackground(eventId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null) {
                    String evetName = object.getString("title");
                    EditText et = (EditText) findViewById(R.id.event_name);
                    et.setText(evetName);

                    String eventDate = object.getString("eventDate");
                    String eventTime = object.getString("eventTime");
                    et = (EditText) findViewById(R.id.event_time);
                    et.setText(eventDate + "         " + eventTime);

                    String address = object.getString("address");
                    et = (EditText) findViewById(R.id.event_address);
                    et.setText(address);

                    ParseUser holder = object.getParseUser("hostId");
                    String hh = holder.getString("username");
                    et = (EditText) findViewById(R.id.event_holder);
                    et.setText(hh);

                }else {
                    Dialog.showDialog("Oops..", "There happens an error when searching", EditEventActivity.this);
                }
            }
        });

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
