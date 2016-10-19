package com.example.peng.eq;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DetailInfoActivity extends AppCompatActivity implements OnMapReadyCallback {
    private View mProgressView;
    private String eventId;
    private GoogleMap mMap;
    double tempLat, tempLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_info);

        //show map

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mProgressView = findViewById(R.id.progress_bar);

        //Cizhen 161008
        Intent intent = getIntent();
        eventId = intent.getStringExtra(MainActivity.EVENT_ID);

        showProgress(true);

        //check if current user has attended the event, show cancel button
        //if not, show go button
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // do stuff with the user
            ParseUser user = ParseUser.getCurrentUser();
            ParseQuery<ParseObject> queryConfirm = ParseQuery.getQuery("EventConfirm");

            //to query pointer
            ParseObject obj = ParseObject.createWithoutData("_User", user.getObjectId());
            queryConfirm.whereEqualTo("attendId", obj);
            obj = ParseObject.createWithoutData("Event", eventId);
            queryConfirm.whereEqualTo("eventId", obj);
            queryConfirm.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(e == null) {
                        if(objects.size() != 0) {
                            //there exists an attending state of the current user
                            //display cancel buttun
                            Button btnGO = (Button) findViewById(R.id.attend_event);
                            btnGO.setVisibility(View.GONE);
                            Button btnCancel = (Button) findViewById(R.id.cancel_event);
                            btnCancel.setVisibility(View.VISIBLE);
                        }else {
                            Button btnCancel = (Button) findViewById(R.id.cancel_event);
                            btnCancel.setVisibility(View.GONE);
                            Button btnGO = (Button) findViewById(R.id.attend_event);
                            btnGO.setVisibility(View.VISIBLE);
//                            btnGO.setEnabled(false);
                        }
                    }else {

                    }
                }
            });

        } else {

        }

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        //include the field if the field is a pointer
        query.include("hostId");
        query.getInBackground(eventId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    //retreiving objects
                    String title = object.getString("title");
                    TextView t = (TextView) findViewById(R.id.event_name);
                    t.setText(title);

                    String address = object.getString("address");
                    t = (TextView) findViewById(R.id.event_address);
                    t.setText(address);

                    String eventDate = object.getString("eventDate");
                    String eventTime = object.getString("eventTime");
                    t = (TextView) findViewById(R.id.event_time);
                    t.setText(eventDate + "           " + eventTime);

                    int numberAttendee = object.getInt("attendNum");
                    t = (TextView) findViewById(R.id.event_attendees);
                    t.setText(Integer.toString(numberAttendee) + " people are going.");

                    String description = object.getString("description");
                    t = (TextView) findViewById(R.id.event_description);
                    t.setText(description);

                    //to access pointer object.
                    ParseUser holder = object.getParseUser("hostId");
                    String hh = holder.getString("username");
                    t = (TextView) findViewById(R.id.event_holder);
                    t.setText(hh);

                    //enable or disable button
                    int maxNumAttendee = object.getInt("maxAttendNum");
                    int attendNum = object.getInt("attendNum");
                    if(maxNumAttendee == attendNum){
                        t = (TextView) findViewById(R.id.event_attendees);
                        t.setText("maximum attendees");
                        Button btn = (Button) findViewById(R.id.attend_event);
                        btn.setEnabled(false);
                    }

                    //get location
                    tempLat = object.getParseGeoPoint("eventLocation").getLatitude();
                    tempLong = object.getParseGeoPoint("eventLocation").getLongitude();
                    LatLng temp = new LatLng(tempLat, tempLong);
                    mMap.addMarker(new MarkerOptions().position(temp).title("event")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(temp));

                    //file object, still figuring out this part..
                    ParseFile fileObj = (ParseFile) object.get("image");
                    fileObj.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {

                            if (e == null) {
                                // Decode the Byte[] into
                                // Bitmap
                                Bitmap bmp = BitmapFactory
                                        .decodeByteArray(
                                                data, 0,
                                                data.length);

                                // initialize
                                ImageView image = (ImageView) findViewById(R.id.event_image);

                                // Set the Bitmap into the
                                // ImageView
                                image.setImageBitmap(bmp);

                            } else {
                                Log.d("test", "Problem load image the data.");
                            }
                            showProgress(false);
                        }
                    });

                } else {
                    // something went wrong
                    Dialog.showDialog(e.toString(), "", DetailInfoActivity.this);
                }
            }
        });
        //Cizhen 161008
    }

    public void cancel(final View view) {
        showProgress(true);
        //increment attendee by one to the event
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query.getInBackground(eventId, new GetCallback<ParseObject>(){
            public void done(final ParseObject object, ParseException e) {
                //save objects
                object.put("attendNum", object.getInt("attendNum") - 1);
                object.saveInBackground();

                //remove the whole record in EventConfirm table
                ParseQuery<ParseObject> queryConfirm = ParseQuery.getQuery("EventConfirm");
                //should change to current user, if current user is null, should let them log in first
                ParseObject obj = ParseObject.createWithoutData("_User", "Xoxb8Adg7W");
                queryConfirm.whereEqualTo("attendId", obj);
                obj = ParseObject.createWithoutData("Event", eventId);
                queryConfirm.whereEqualTo("eventId", obj);
                queryConfirm.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if(e == null) {
                            if(objects.size() != 0) {
                                objects.get(0).deleteInBackground();

                                Dialog.showDialog("Thank you!", "You've successfully canceled.", DetailInfoActivity.this);

                                //show and hide buttons
                                Button btnCancel = (Button) findViewById(R.id.cancel_event);
                                btnCancel.setVisibility(View.GONE);
                                Button btnGo = (Button) findViewById(R.id.attend_event);
                                btnGo.setVisibility(View.VISIBLE);

                                TextView t = (TextView) findViewById(R.id.event_attendees);
//                                int a = object.getInt("attendNum");
                                t.setText(Integer.toString(object.getInt("attendNum")) + " people are going");
                                showProgress(false);
                            }
                        }
                    }
                });
            }
        });
    }

    public void attend(final View view){
        showProgress(true);
        //increment attendee by one to the event
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query.getInBackground(eventId, new GetCallback<ParseObject>(){
            public void done(ParseObject object, ParseException e) {
                //update and save object
                object.put("attendNum", object.getInt("attendNum") + 1);
                object.saveInBackground();

                //add record to EventConfirm table
                ParseObject eventConfirmObj = new ParseObject("EventConfirm");
                ParseObject obj = ParseObject.createWithoutData("_User", "Xoxb8Adg7W");
                //should change to current user
                eventConfirmObj.put("attendId", obj);
                obj = ParseObject.createWithoutData("Event", eventId);
                eventConfirmObj.put("eventId", obj);
                eventConfirmObj.saveInBackground();

                Dialog.showDialog("Thank you!", "You've successfully attended.", DetailInfoActivity.this);

                //change button view
                Button btnGO = (Button) findViewById(R.id.attend_event);
                btnGO.setVisibility(View.GONE);
                Button btnCancel = (Button) findViewById(R.id.cancel_event);
                btnCancel.setVisibility(View.VISIBLE);

                //refresh the view
                TextView t = (TextView) findViewById(R.id.event_attendees);
                t.setText(Integer.toString(object.getInt("attendNum"))+ " people are going");


//                t.setText(object.getInt("attendNum"));
                // view.invalidate();

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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        mMap.setMyLocationEnabled(true);
        // Add a marker in Sydney and move the camera
/*        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }



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
