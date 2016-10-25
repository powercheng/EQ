package com.example.peng.eq;

import android.app.Activity;
import android.app.Application;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity{

    private String hostId;
    private ListView upcommingEventListView;
    private ListView eventHistoryView;
    private ListView myEventsView;
    private String thisUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //initialize variables
        Intent intent = getIntent();
        hostId = ParseUser.getCurrentUser().getObjectId();

        TabHost host = (TabHost)findViewById(R.id.tabHost);
        host.setup();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("UpcomingEvent");
        spec.setContent(R.id.upcoming_event);
//        spec.setContent(new Intent(this,UpcommingEvent.class));
        spec.setIndicator("Upcoming Events");
        host.addTab(spec);

        // Find the ListView resource.
        upcommingEventListView = (ListView) findViewById(R.id.upcoming_event);
        myEventsView = (ListView) findViewById(R.id.my_event);
        eventHistoryView = (ListView) findViewById(R.id.event_history);

        //perform query to search for event information
        ParseQuery<ParseObject> queryConfirm = ParseQuery.getQuery("EventConfirm");
        ParseObject attendeeObj = ParseObject.createWithoutData("_User", hostId);
        queryConfirm.whereEqualTo("attendId", attendeeObj);
        queryConfirm.include("eventId");
        queryConfirm.orderByDescending("eventDateTime");
        queryConfirm.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null) {
                    //now, we get all dataset for the current user
                    //we then need to group them into different groups by comparing date ONLY so far
                    List<String> eventIdUpcoming = new ArrayList<String>();
                    List<String> titleListUpcoming = new ArrayList<String>();
                    List<String> holderListUpcoming  = new ArrayList<String>();
                    List<String> dateListUpcoming  = new ArrayList<String>();

                    List<String> eventIdPast = new ArrayList<String>();
                    List<String> titleListPast = new ArrayList<String>();
                    List<String> holderListPast  = new ArrayList<String>();
                    List<String> dateListPast  = new ArrayList<String>();

                    int countPast = 0;
                    int countUpcoming = 0;

                    for(ParseObject obj : objects) {
                        ParseObject eventObj = obj.getParseObject("eventId");
                        String eventId = eventObj.getObjectId();

                        Date eventDate = eventObj.getDate("eventDateTime");
                        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                        String strDate = df.format(eventDate);
                        //for comparison, initialize today's date
                        Calendar today = Calendar.getInstance();
                        today.clear(Calendar.HOUR);
                        today.clear(Calendar.MINUTE);
                        today.clear(Calendar.SECOND);
                        Date todayDate = today.getTime();

                        String eventName = eventObj.getString("title");
                        ParseUser user = eventObj.getParseUser("hostId");
                        String username = "";
                        try{
                            username = user.fetchIfNeeded().getString("username");
                            if(todayDate.after(eventDate)) {
                                //for past events
                                eventIdPast.add(eventId);
                                titleListPast.add(eventName);
                                holderListPast.add(username);
                                dateListPast.add(strDate);
                                countPast++;

//                            Toast.makeText(ProfileActivity.this, "past", Toast.LENGTH_SHORT).show();
                            } else {
                                //for upcoming events
                                eventIdUpcoming.add(eventId);
                                titleListUpcoming.add(eventName);
                                holderListUpcoming.add(username);
                                dateListUpcoming.add(strDate);
                                countUpcoming++;
//                            Toast.makeText(ProfileActivity.this, "upcoming", Toast.LENGTH_SHORT).show();
                            }

//                            titleListAll[count] = eventName;
//                            holderListAll[count] = username;
//                            dateListAll[count] = strDate;
                        } catch (ParseException pe){

                        }
//                        count++;
                    }

                    upcommingEventListView.setAdapter(new CustomAdapter(
                            ProfileActivity.this,
                            eventIdUpcoming.toArray(new String[eventIdUpcoming.size()]),
                            titleListUpcoming.toArray(new String[titleListUpcoming.size()]),
                            holderListUpcoming.toArray(new String[holderListUpcoming.size()]),
                            dateListUpcoming.toArray(new String[dateListUpcoming.size()]))
                    );
                    eventHistoryView.setAdapter(new CustomAdapter(
                            ProfileActivity.this,
                            eventIdPast.toArray(new String[eventIdPast.size()]),
                            titleListPast.toArray(new String[titleListPast.size()]),
                            holderListPast.toArray(new String[holderListPast.size()]),
                            dateListPast.toArray(new String[dateListPast.size()]))
                    );

                } else {
                    Toast.makeText(ProfileActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        host.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if(tabId == "MyEvent"){
                    getMyEvents();
                }
//                Toast.makeText(getApplicationContext(), "Click on tab: "+ tabId, Toast.LENGTH_SHORT).show();
            }
        });


        //Tab 2
        spec = host.newTabSpec("EventHistory");
        spec.setContent(R.id.event_history);
        spec.setIndicator("Event History");
        host.addTab(spec);

        //load records for history


        //Tab 3
        spec = host.newTabSpec("MyEvent");
        spec.setContent(R.id.my_event);
        spec.setIndicator("My Events");
        host.addTab(spec);

        //load records for event held by current user


        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(hostId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if(e == null) {
                    //get username
                    String username = object.getString("username");
                    TextView t = (TextView) findViewById(R.id.username);
                    t.setText(username);
                    thisUser = username;

                    //get profile image
                    ParseFile fileObj = (ParseFile) object.get("profileImage");
                    if(fileObj != null) {
                        fileObj.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (e == null) {
                                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    ImageView profileImage = (ImageView) findViewById(R.id.profile_image);
                                    profileImage.setImageBitmap(bmp);
                                } else {
                                    Toast.makeText(ProfileActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getMyEvents(){
        ParseQuery<ParseObject> queryConfirm = ParseQuery.getQuery("Event");
        ParseObject hostObj = ParseObject.createWithoutData("_User", hostId);
        queryConfirm.whereEqualTo("hostId", hostObj);
        queryConfirm.orderByDescending("eventDateTime");
        queryConfirm.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null) {
                    List<String> titleListAll = new ArrayList<String>();
                    List<String> holderListAll  = new ArrayList<String>();
                    List<String> dateListAll  = new ArrayList<String>();
                    List<String> eventIdListAll = new ArrayList<String>();

                    int count = 0;
                    for(ParseObject obj : objects) {
//                        ParseObject eventObj = obj.getParseObject("eventId");
                        String eventId = obj.getObjectId();

                        Date eventDate = obj.getDate("eventDateTime");
                        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                        String strDate = df.format(eventDate);

                        String eventName = obj.getString("title");

                        eventIdListAll.add(eventId);
                        titleListAll.add(eventName);
                        holderListAll.add(thisUser);
                        dateListAll.add(strDate);

                        count++;
                    }

                    myEventsView.setAdapter(new CustomAdapter(
                            ProfileActivity.this,
                            eventIdListAll.toArray(new String[eventIdListAll.size()]),
                            titleListAll.toArray(new String[titleListAll.size()]),
                            holderListAll.toArray(new String[holderListAll.size()]),
                            dateListAll.toArray(new String[dateListAll.size()]))
                    );
                }
            }
        });
    }

    public void logout(View view) {
        ParseUser.logOut();
        ParseUser user = ParseUser.getCurrentUser();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
//        String aa = "";
    }
}
