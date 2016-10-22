package com.example.peng.eq;

import android.app.Activity;
import android.app.Application;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfileActivity extends AppCompatActivity{

    private String hostId;
    private ListView upcommingEventListView;

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
        TabHost.TabSpec spec = host.newTabSpec("Tab One");
        spec.setContent(R.id.upcoming_event);
//        spec.setContent(new Intent(this,UpcommingEvent.class));
        spec.setIndicator("Upcoming Events");
        host.addTab(spec);

        // Find the ListView resource.
        upcommingEventListView = (ListView) findViewById(R.id.upcoming_event);
        // Create and populate a List of planet names.

        // Set the ArrayAdapter as the ListView's adapter.

        ParseQuery<ParseObject> queryUpcoming = ParseQuery.getQuery("EventConfirm");

        ParseObject attendeeObj = ParseObject.createWithoutData("_User", hostId);
        queryUpcoming.whereEqualTo("attendId", attendeeObj);
        queryUpcoming.include("eventId");
        queryUpcoming.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if(e == null) {
                    String[] titleList = new String[objects.size()];
                    String[] holderList = new String[objects.size()];
                    String[] dateList = new String[objects.size()];
                    int count = 0;
                    for(ParseObject obj : objects) {
                        ParseObject eventObj = obj.getParseObject("eventId");
                        titleList[count] = eventObj.getString("title");
                        ParseObject holderObj = eventObj.getParseObject("hostId");
                        holderList[count] = holderObj.getString("username");
                        dateList[count] = eventObj.getString("eventDate") + "   " + eventObj.getString("eventTime");
                        count++;
                    }

                    upcommingEventListView.setAdapter(new CustomAdapter(ProfileActivity.this, titleList, holderList, dateList));
                } else {
                    Toast.makeText(ProfileActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        //Tab 2
        spec = host.newTabSpec("Tab Two");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Event History");
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Tab Three");
        spec.setContent(R.id.tab3);
        spec.setIndicator("My Events");
        host.addTab(spec);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(hostId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if(e == null) {
                    //get username
                    String username = object.getString("username");
                    TextView t = (TextView) findViewById(R.id.username);
                    t.setText(username);

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
}
