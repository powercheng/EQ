package com.example.peng.eq;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import org.w3c.dom.Text;

import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class DetailInfoActivity extends AppCompatActivity {
    private View mProgressView;
    private String objectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_info);

        mProgressView = findViewById(R.id.progress_bar);
//
//        //Cizhen 161008
        Intent intent = getIntent();
        objectId = intent.getStringExtra(MainActivity.OBJECT_ID);
//
        showProgress(true);

        //check if current user has attended the event, show cancel button
        //if not, show go button
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // do stuff with the user
            ParseQuery<ParseUser> queryUser = ParseUser.getQuery();
            queryUser.findInBackground(new FindCallback<ParseUser>() {
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e == null) {

                    } else {
                        // Something went wrong.
                    }
                }
            });
        } else {
            // show the signup or login screen
        }

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        //include the field if the field is a pointer
        query.include("hostId");
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    //retreiving objects
                    String title = object.getString("title");
                    TextView t = (TextView) findViewById(R.id.event_name);
                    t.setText(title);

                    String updatedAt = DateFormat.getDateTimeInstance().format(object.getUpdatedAt());
                    t = (TextView) findViewById(R.id.event_datetime);
                    t.setText(updatedAt);

                    int numberAttendee = object.getInt("attendNum");
                    t = (TextView) findViewById(R.id.event_attendees);
                    t.setText(Integer.toString(numberAttendee));

                    //to access pointer object.
                    ParseUser holder = object.getParseUser("hostId");
                    String hh = holder.getString("username");
                    t = (TextView) findViewById(R.id.event_holder);
                    t.setText(hh);

                    //file object, still figuring out this part..
                    ParseFile image = (ParseFile) object.get("image");
                    showProgress(false);
                } else {
                    // something went wrong
                    Dialog.showDialog(e.toString(), "", DetailInfoActivity.this);
                }
            }
        });
        //Cizhen 161008
//

    }

    //Cizhen 161008

    public void cancel(final View view) {
        showProgress(true);
        //increment attendee by one to the event
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query.getInBackground(objectId, new GetCallback<ParseObject>(){
            public void done(ParseObject object, ParseException e) {
                //save objects
                object.put("attendNum", object.getInt("attendNum") - 1);
                object.saveInBackground();
                Dialog.showDialog("Thank you!", "You've successfully attended.", DetailInfoActivity.this);

                //show and hide buttons
                Button btnCancel = (Button) findViewById(R.id.cancel_event);
                btnCancel.setVisibility(View.VISIBLE);
                Button btnGO = (Button) findViewById(R.id.cancel_event);
                btnGO.setVisibility(View.GONE);

                //refresh, how to????
//                TextView t = (TextView) findViewById(R.id.event_attendees);
                view.invalidate();

                showProgress(false);
            }
        });
    }

    public void attend(final View view){
        showProgress(true);
        //increment attendee by one to the event
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query.getInBackground(objectId, new GetCallback<ParseObject>(){
            public void done(ParseObject object, ParseException e) {
                //update and save object
                object.put("attendNum", object.getInt("attendNum") + 1);
                object.saveInBackground();
                Dialog.showDialog("Thank you!", "You've successfully attended.", DetailInfoActivity.this);

                //change button view
                Button btnGO = (Button) findViewById(R.id.cancel_event);
                btnGO.setVisibility(View.GONE);
                Button btnCancel = (Button) findViewById(R.id.cancel_event);
                btnCancel.setVisibility(View.VISIBLE);

                //refresh the view
//                TextView t = (TextView) findViewById(R.id.event_attendees);
                view.invalidate();

                showProgress(false);
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


    //Cizhen 161008


}

//
//public class DownloadImageTask extends AsyncTask {
//    @Override
//    protected Bitmap doInBackground(String... urls) {
//        return loadImageFromNetwork(urls[0]);
//    }
//
//    protected void onPostExecute(Bitmap result) {
//        mImageView.setImageBitmap(result);
//    }
//}
