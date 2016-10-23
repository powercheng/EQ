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
        hostId = intent.getStringExtra(MainActivity.HOST_ID);

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
                    String[] eventIdUpcoming = new String[objects.size()];
                    String[] titleListUpcoming = new String[objects.size()];
                    String[] holderListUpcoming  = new String[objects.size()];
                    String[] dateListUpcoming  = new String[objects.size()];

                    String[] eventIdPast = new String[objects.size()];
                    String[] titleListPast = new String[objects.size()];
                    String[] holderListPast  = new String[objects.size()];
                    String[] dateListPast  = new String[objects.size()];

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
                                eventIdPast[countPast] = eventId;
                                titleListPast[countPast] = eventName;
                                holderListPast[countPast] = username;
                                dateListPast[countPast] = strDate;
                                countPast++;

//                            Toast.makeText(ProfileActivity.this, "past", Toast.LENGTH_SHORT).show();
                            } else {
                                //for upcoming events
                                eventIdUpcoming[countUpcoming] = eventId;
                                titleListUpcoming[countUpcoming] = eventName;
                                holderListUpcoming[countUpcoming] = username;
                                dateListUpcoming[countUpcoming] = strDate;
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
                            ProfileActivity.this, eventIdUpcoming, titleListUpcoming, holderListUpcoming, dateListUpcoming));
                    eventHistoryView.setAdapter(new CustomAdapter(ProfileActivity.this,
                            eventIdPast, titleListPast, holderListPast, dateListPast));

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
                    fileObj.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                            if(e == null) {
                                Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                                ImageView profileImage = (ImageView) findViewById(R.id.profile_image);
                                profileImage.setImageBitmap(bmp);
                            } else {
                                Toast.makeText(ProfileActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
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
                    String[] titleListAll = new String[objects.size()];
                    String[] holderListAll  = new String[objects.size()];
                    String[] dateListAll  = new String[objects.size()];
                    String[] eventIdListAll = new String[objects.size()];

                    int count = 0;
                    for(ParseObject obj : objects) {
//                        ParseObject eventObj = obj.getParseObject("eventId");
                        String eventId = obj.getObjectId();

                        Date eventDate = obj.getDate("eventDateTime");
                        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                        String strDate = df.format(eventDate);

                        String eventName = obj.getString("title");

                        eventIdListAll[count] = eventId;
                        titleListAll[count] = eventName;
                        holderListAll[count] = thisUser;
                        dateListAll[count] = strDate;

                        count++;
                    }


                    myEventsView.setAdapter(new CustomAdapter(
                            ProfileActivity.this, eventIdListAll, titleListAll, holderListAll, dateListAll));

                }
            }
        });
    }
}
