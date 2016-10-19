package com.example.peng.eq;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;


public class MainActivity extends AppCompatActivity {
    private static boolean isParseInitialized = false;
    public final static String EVENT_ID = "";
    public final static String HOST_ID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {



//        ParseObject testObject = new ParseObject("Event");
//        testObject.put("title", "bar");
//        testObject.saveInBackground();
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    public void login(View view) {

        Intent intent = new Intent(this, LoginActivity.class);
        // Use TaskStackBuilder to build the back stack and get the PendingIntent

        startActivity(intent);
    }

    public void signup(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    //Cizhen 161008
    public void detail(View view) {
        //pass the objectid to the detamilInfoView
        Intent intent = new Intent(this, DetailInfoActivity.class);

        String objectid = "vG6L293QyB";
        intent.putExtra(EVENT_ID, objectid);
        startActivity(intent);
    }

    public void addEvent(View view) {
        //send the hostid
        Intent intent = new Intent(this, AddEventActivity.class);
        String hostId = "Xoxb8Adg7W";
        intent.putExtra(HOST_ID, hostId);
        startActivity(intent);
    }

    public void addEvent2(View view) {
        //send the hostid
        Intent intent = new Intent(this, addEvent2Activity.class);
        String hostId = "Xoxb8Adg7W";
        intent.putExtra(HOST_ID, hostId);
        startActivity(intent);
    }
    //Cizhen 161008
}
