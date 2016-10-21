package com.example.peng.eq;

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
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ProfileActivity extends AppCompatActivity {

    private String hostId;
    TabHost tabHost;

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
        spec.setContent(R.id.tab1);
//        spec.setContent(new Intent(this,UpcommingEvent.class));
        spec.setIndicator("Upcoming Events");
        host.addTab(spec);

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        TextView t1 = (TextView)findViewById(R.id.text1);
        TextView t2 = (TextView)findViewById(R.id.text2);

        switch (id){
            case 1:
                t1.setVisibility(View.VISIBLE);
                t2.setVisibility(View.GONE);
                break;
            case 2:
                t2.setVisibility(View.VISIBLE);
                t1.setVisibility(View.GONE);
        }
        return super.onOptionsItemSelected(item);
    }
}
