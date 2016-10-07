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

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("5mUU6pV0Ljoc3Er2ZrJoUUBC93kBeEfuvu2vkIUw")
                .clientKey("0vj8zObedfUjbtTc92cX8TIR2ccEEhlIdgfvf5BG")
                .server("https://parseapi.back4app.com/").build()
        );
//        ParseObject testObject = new ParseObject("Event");
//        testObject.put("title", "bar");
//        testObject.saveInBackground();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //testing 161006....
}
