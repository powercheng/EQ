package com.example.peng.eq;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;


public class MainActivity extends AppCompatActivity {
    //1223345678
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("Your Back4app application ID")
                .clientKey("You can found it on b4a core setting")
                .server("https://parseapi.back4app.com/").build()
        );
        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();
        setContentView(R.layout.activity_main);
    }

    //testing 161006....
}
