package com.example.peng.eq;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;


public class MainActivity extends AppCompatActivity {
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

    public void login(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void signup() {

    }
}
