package com.example.peng.eq;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by peng on 10/10/2016.
 */

public class user extends android.app.Application {

    public static String[] type;
    public static Map<String,String> map = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();

        //This will only be called once in your app's entire lifecycle.
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("5mUU6pV0Ljoc3Er2ZrJoUUBC93kBeEfuvu2vkIUw")
                .clientKey("0vj8zObedfUjbtTc92cX8TIR2ccEEhlIdgfvf5BG")
                .server("https://parseapi.back4app.com/").build()
        );

        ParseQuery<ParseObject> query = ParseQuery.getQuery("EventType");
        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                type = new String[objects.size()];
                for(int i = 0; i< objects.size();i++) {
                    type[i] = objects.get(i).getString("typeName");
                    map.put(type[i],objects.get(i).getObjectId());
                }
                //progressDialog.dismiss();

            }
        });
    }

}
