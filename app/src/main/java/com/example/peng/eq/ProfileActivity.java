package com.example.peng.eq;

import android.app.Activity;
import android.app.Application;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ProfileActivity extends AppCompatActivity{

    private String hostId;
    private ListView upcommingEventListView;
    private ListView eventHistoryView;
    private ListView myEventsView;
    private String thisUser;
    static final int CAM_REQUEST = 1;
    private ImageView imageView;
    private Button confirmChangeIamge;
    private File imageFile;
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;
    FrameLayout progressBarHolder;
    LinearLayout entirePage;

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
        imageView = (ImageView) findViewById(R.id.profile_image);
        confirmChangeIamge = (Button) findViewById(R.id.confirm_change_image);
        confirmChangeIamge.setVisibility(View.GONE);
        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);
        entirePage = (LinearLayout) findViewById(R.id.entire_page);

//        showProgressBar(true);
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
                            } else {
                                //for upcoming events
                                eventIdUpcoming.add(eventId);
                                titleListUpcoming.add(eventName);
                                holderListUpcoming.add(username);
                                dateListUpcoming.add(strDate);
                            }
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
//                    showProgressBar(false);
                } else {
//                    showProgressBar(false);
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
//                                    ImageView profileImage = (ImageView) findViewById(R.id.profile_image);
                                    imageView.setImageBitmap(bmp);
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

    public void changeImage(View view) {
        Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = getFile();
        imageFile = file;
        camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(camera_intent, CAM_REQUEST);
//
//        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                switch (which){
//                    case DialogInterface.BUTTON_POSITIVE:
//                        //Yes button clicked
//                        break;
//
//                    case DialogInterface.BUTTON_NEGATIVE:
//                        //No button clicked
//                        break;
//                }
//            }
//        };
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
//                .setNegativeButton("No", dialogClickListener).show();
    }

    private File getFile() {
        File folder = new File("sdcard/camera_app");
        if(!folder.exists()) {
            folder.mkdir();
        }

        File image_file = new File(folder, "cam_image.jpg");
        return image_file;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String path = "sdcard/camera_app/cam_image.jpg";
        if(requestCode == CAM_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                imageView.setImageDrawable(Drawable.createFromPath(path));
                confirmChangeIamge.setVisibility(View.VISIBLE);
//                Matrix matrix = new Matrix();
//                matrix.postRotate((float) angle, pivotX, pivotY);
//                matrix.postRotate(90f, imageView.getDrawable().getBounds().width()/2, imageView.getDrawable().getBounds().height()/2);
//                imageView.setScaleType(ImageView.ScaleType.MATRIX);
//                imageView.setImageDrawable(Drawable.createFromPath(path));
//                imageView.setImageMatrix(matrix);

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void confirmChangeImage(View view) {
        //upload the image to database
        final ParseUser currentUser = ParseUser.getCurrentUser();
        imageView.buildDrawingCache();
        Bitmap bitmap = imageView.getDrawingCache();

        // Convert it to byte
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Compress image to lower quality scale 1 - 100
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] image = stream.toByteArray();

        // Create the ParseFile
        final ParseFile file = new ParseFile("cam_image.jpg", image);
        file.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    currentUser.put("profileImage", file);
                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException ex) {
                            if(ex == null) {
                                confirmChangeIamge.setVisibility(View.GONE);
                                Toast.makeText(ProfileActivity.this, "Your image has been saved.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ProfileActivity.this, "Oops, there's an error when saving your information.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(ProfileActivity.this, "Oops, there's an error when saving your information.", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    private void showProgressBar(boolean show){
        if(show) {
            inAnimation = new AlphaAnimation(0f, 1f);
            inAnimation.setDuration(500);
            progressBarHolder.setAnimation(inAnimation);
            progressBarHolder.setVisibility(View.VISIBLE);
            entirePage.setVisibility(View.GONE);
        } else {
            outAnimation = new AlphaAnimation(1f, 0f);
            outAnimation.setDuration(200);
            progressBarHolder.setAnimation(outAnimation);
            progressBarHolder.setVisibility(View.GONE);
            entirePage.setVisibility(View.VISIBLE);
        }
    }
}
