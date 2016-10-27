package com.example.peng.eq;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DetailInfoActivity extends AppCompatActivity implements OnMapReadyCallback {
    private String eventId;
    private GoogleMap mMap;
    double tempLat, tempLong;
    public static String EVENT_ID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_info);

        //show map

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Cizhen 161008
        Intent intent = getIntent();
        String previous = intent.getStringExtra("from");
        if(previous.equals("MainActivity")) {
            eventId = intent.getStringExtra(MainActivity.EVENT_ID);
        } else if(previous.equals("CustomAdapter")) {
            eventId = intent.getStringExtra(CustomAdapter.EVENT_ID);
        } else if (previous.equals("MapActivity")) {
            eventId = intent.getStringExtra(MapActivity.EVENT_ID);
        }



        //check if current user has attended the event, show cancel button
        //if not, show go button
        final ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // do stuff with the user
            ParseUser user = ParseUser.getCurrentUser();
            final ParseQuery<ParseObject> queryConfirm = ParseQuery.getQuery("EventConfirm");

            //to query pointer
            ParseObject obj = ParseObject.createWithoutData("_User", user.getObjectId());
            queryConfirm.whereEqualTo("attendId", obj);
            obj = ParseObject.createWithoutData("Event", eventId);
            queryConfirm.whereEqualTo("eventId", obj);
            queryConfirm.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(e == null) {
                        final Button btnCancel = (Button) findViewById(R.id.cancel_event);
                        final Button btnGO = (Button) findViewById(R.id.attend_event);
                        final Button btnEdit = (Button) findViewById(R.id.edit_event);

                        if(objects.size() != 0) {
                            //there exists an attending state of the current user
                            //display cancel buttun
//                            Button btnGO = (Button) findViewById(R.id.attend_event);
                            btnGO.setVisibility(View.GONE);
//                            Button btnCancel = (Button) findViewById(R.id.cancel_event);
                            btnCancel.setVisibility(View.VISIBLE);
                        }else {
                            //search event table
                            ParseQuery<ParseObject> queryEvent = ParseQuery.getQuery("Event");
                            queryEvent.getInBackground(eventId, new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject object, ParseException e) {
                                    ParseUser host = object.getParseUser("hostId");
                                    try {
                                        String hostEmail = host.fetchIfNeeded().getEmail();
                                        String currentUserEmail = currentUser.fetchIfNeeded().getEmail();
                                        if(!hostEmail.equals(currentUserEmail)) {
                                            btnCancel.setVisibility(View.GONE);
                                            btnGO.setVisibility(View.VISIBLE);
                                            btnEdit.setVisibility(View.GONE);
                                        } else {
                                            //the host of the event can not attend his event
                                            btnEdit.setVisibility(View.VISIBLE);
                                            btnCancel.setVisibility(View.GONE);
                                            btnGO.setVisibility(View.GONE);
                                        }
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            });
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

                    String address = object.getString("street") + ", " + object.getString("city") + ", " + object.getString("state") + ", " + object.getString("zip");
                    t = (TextView) findViewById(R.id.event_address);
                    t.setText(address);

                    Date eventDateTime = object.getDate("eventDateTime");
                    t = (TextView) findViewById(R.id.event_time);
                    DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                    String strDateTime = df.format(eventDateTime);
                    String[] ss = strDateTime.split(" ");
                    t.setText(ss[0] + "    " + ss[1]);

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
                    if(fileObj != null) {
                        fileObj.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (e == null) {
                                    // Decode the Byte[] into Bitmap
                                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);

                                    // initialize
                                    ImageView image = (ImageView) findViewById(R.id.event_image);

                                    // Set the Bitmap into the ImageView
                                    image.setImageBitmap(bmp);
                                } else {
                                    Log.d("test", "Problem load image the data.");
                                }
//                                showProgress(false);
                            }
                        });
                    }
//                    showProgress(false);
                } else {
                    // something went wrong
                    Dialog.showDialog(e.toString(), "", DetailInfoActivity.this);
                }
            }
        });
        //Cizhen 161008
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    public void editEvent(View view) {
        Intent intent = new Intent(this, EditEventActivity.class);
        intent.putExtra("from", "DetailInfoActivity");
        intent.putExtra(EVENT_ID, eventId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    public void cancel(final View view) {
//        showProgress(true);
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
                ParseObject obj = ParseObject.createWithoutData("_User", ParseUser.getCurrentUser().getObjectId());
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
//                                showProgress(false);
                            }
                        }
                    }
                });
            }
        });
    }

    public void attend(final View view){
//        showProgress(true);
        //increment attendee by one to the event
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query.getInBackground(eventId, new GetCallback<ParseObject>(){
            public void done(ParseObject object, ParseException e) {
                //update and save object
                object.put("attendNum", object.getInt("attendNum") + 1);
                object.saveInBackground();

                //add record to EventConfirm table
                ParseObject eventConfirmObj = new ParseObject("EventConfirm");
                ParseObject obj = ParseObject.createWithoutData("_User", ParseUser.getCurrentUser().getObjectId());
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

            }
        });
    }

    //Cizhen 161008
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        this.finish();
    }
}
