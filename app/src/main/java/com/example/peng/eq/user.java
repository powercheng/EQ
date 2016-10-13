package com.example.peng.eq;

import com.parse.Parse;

/**
 * Created by peng on 10/10/2016.
 */

public class user extends android.app.Application {


    @Override
    public void onCreate() {
        super.onCreate();

        //This will only be called once in your app's entire lifecycle.
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("5mUU6pV0Ljoc3Er2ZrJoUUBC93kBeEfuvu2vkIUw")
                .clientKey("0vj8zObedfUjbtTc92cX8TIR2ccEEhlIdgfvf5BG")
                .server("https://parseapi.back4app.com/").build()
        );

    }

}
