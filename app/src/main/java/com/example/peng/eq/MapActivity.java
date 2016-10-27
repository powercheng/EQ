package com.example.peng.eq;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    public static final String TAG = MapActivity.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private Map<String,List<Marker>> mapType;
    private Map<String,List<Marker>> mapDayType;
    private List<Marker> list1,list2,list3,list4,list5,list6,list7;
    private Map<Marker,String> mapEvent = new HashMap<>();
    private Menu menu;
    public final static String EVENT_ID = "";
    private Button button1,button2,button3,button4,button5,button6,button7;
    boolean bool1,bool2,bool3,bool4,bool5,bool6,bool7;
    private LinearLayout buttons;
    private Set<String> set = new HashSet<>();
    private String choseType;
    private List<MarkerObj> markers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);



        buttons = (LinearLayout) findViewById(R.id.buttons);
        bool1 = false;
        bool2 = false;
        bool3 = false;
        bool4 = false;
        bool5 = false;
        bool6 = false;
        bool7 = false;
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button5 = (Button) findViewById(R.id.button5);
        button6 = (Button) findViewById(R.id.button6);
        button7 = (Button) findViewById(R.id.button7);

        set.add("Mon");
        set.add("Tue");
        set.add("Wed");
        set.add("Thu");
        set.add("Fri");
        set.add("Sat");
        set.add("Sun");

        choseType = "all";




        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.connect48);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MapActivity.this, ProfileActivity.class);
                startActivity(i);

            }
        });

        final Drawable upArrow = getResources().getDrawable(R.drawable.arrow24);
        toolbar.setOverflowIcon(upArrow);

        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(AppIndex.API).build();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main,menu);



/*        ParseQuery<ParseObject> query = ParseQuery.getQuery("EventType");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                for (int i = 0; i < objects.size(); i++) {
                    String str = objects.get(i).getString("typeName");
                    menu.add(Menu.NONE,i+10,Menu.NONE, str);
                }
                //progressDialog.dismiss();

            }
        });*/
        for(int i = 0; i < user.type.length;i++){
            menu.add(Menu.NONE,i+10,Menu.NONE, user.type[i]);
        }
        this.menu = menu;
        return true;
    }

  //  public void button1
//qqqqq
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if(item.getTitle().equals("all")) {
            choseType = "all";
            setVisible();
            return true;
        } else if(item.getTitle().equals("Add Event")) {
            Intent i = new Intent(MapActivity.this, addEvent2Activity.class);
            startActivity(i);
            return true;
        } else if(item.getTitle().equals("calender")) {
            if(buttons.getVisibility() == View.VISIBLE)
                buttons.setVisibility(View.GONE);
            else
                buttons.setVisibility(View.VISIBLE);
            return true;
        } else {
            choseType = item.getTitle().toString();
            setVisible();
            return true;
        }

    }
    public void button1(View view){
        if(bool1) {
            button1.setBackground(getResources().getDrawable(R.drawable.red));
            set.add("Mon");
            bool1 = false;
        } else {
            button1.setBackgroundColor(getResources().getColor(R.color.red));
            set.remove("Mon");
            bool1 = true;
        }
        setVisible();
    }

    public void button2(View view){
        if(bool2) {
            button2.setBackground(getResources().getDrawable(R.drawable.orange));
            set.add("Tue");
            bool2 = false;
        } else {
            button2.setBackgroundColor(getResources().getColor(R.color.orange));
            set.remove("Tue");
            bool2 = true;
        }
        setVisible();
    }
    public void button3(View view){
        if(bool3) {
            button3.setBackground(getResources().getDrawable(R.drawable.yellow));
            set.add("Wed");
            bool3 = false;
        } else {
            button3.setBackgroundColor(getResources().getColor(R.color.yellow));
            set.remove("Wed");
            bool3 = true;
        }
        setVisible();
    }
    public void button4(View view){
        if(bool4) {
            button4.setBackground(getResources().getDrawable(R.drawable.green));
            set.add("Thu");
            bool4 = false;
        } else {
            button4.setBackgroundColor(getResources().getColor(R.color.green));
            set.remove("Thu");
            bool4 = true;
        }
        setVisible();
    }
    public void button5(View view){
        if(bool5) {
            button5.setBackground(getResources().getDrawable(R.drawable.cyan));
            set.add("Fri");
            bool5 = false;
        } else {
            button5.setBackgroundColor(getResources().getColor(R.color.cyan));
            set.remove("Fri");
            bool5 = true;
        }
        setVisible();
    }
    public void button6(View view){
        if(bool6) {
            button6.setBackground(getResources().getDrawable(R.drawable.blue));
            set.add("Sat");
            bool6 = false;
        } else {
            button6.setBackgroundColor(getResources().getColor(R.color.blue));
            set.remove("Sat");
            bool6 = true;
        }
        setVisible();
    }

    public void button7(View view){
        if(bool7) {
            button7.setBackground(getResources().getDrawable(R.drawable.violet));
            set.add("Sun");
            bool7 = false;
        } else {
            button7.setBackgroundColor(getResources().getColor(R.color.violet));
            set.remove("Sun");
            bool7 = true;
        }
        setVisible();
    }


    private void setVisible(){
        if(choseType.equals("all")){
            for(MarkerObj m : markers) {
                if(set.contains(m.getDay())) {
                    m.getMarker().setVisible(true);
                } else {
                    m.getMarker().setVisible(false);
                }
            }
        }
        else {
            for(MarkerObj m : markers) {
                if(m.getType().equals(choseType)) {
                    if(set.contains(m.getDay())) {
                        m.getMarker().setVisible(true);
                    } else {
                        m.getMarker().setVisible(false);
                    }
                } else {
                    m.getMarker().setVisible(false);
                }
            }
        }
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        // Add a marker in Sydney and move the camera
/*        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void handleNewLocation(Location location) {
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        mapType = new HashMap<>();
        mapDayType = new HashMap<>();

        list1 = new ArrayList<>();
        list2 = new ArrayList<>();
        list3 = new ArrayList<>();
        list4 = new ArrayList<>();
        list5 = new ArrayList<>();
        list6 = new ArrayList<>();
        list7 = new ArrayList<>();
        mapDayType.put("Mon",list1);
        mapDayType.put("Tue",list2);
        mapDayType.put("Wed",list3);
        mapDayType.put("Thu",list4);
        mapDayType.put("Fri",list5);
        mapDayType.put("Sat",list6);
        mapDayType.put("Sun",list7);

        ParseGeoPoint findLocation = new ParseGeoPoint(currentLatitude, currentLongitude);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query.whereNear("eventLocation", findLocation);
        query.setLimit(20);//Set the limit of query result
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                //fetched_data.clear();//List<double[]>
                for (int i = 0; i < objects.size(); i++) {
                    double tempLat = objects.get(i).getParseGeoPoint("eventLocation").getLatitude();
                    double tempLng = objects.get(i).getParseGeoPoint("eventLocation").getLongitude();
                    String eventId = objects.get(i).getObjectId();
                    String title = objects.get(i).getString("title");
                    ParseObject type = objects.get(i).getParseObject("type");
                    String typename = null;
                    try {
                        typename = type.fetchIfNeeded().getString("typeName");
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    LatLng temp = new LatLng(tempLat, tempLng);
                    Date date = objects.get(i).getDate("eventDateTime");
                    String str = new SimpleDateFormat("E", Locale.US).format(date);
                    Marker m;
                    if (str.equals("Mon")){
                        m = mMap.addMarker(new MarkerOptions().position(temp).title(title)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));


                    }
                    else if (str.equals("Tue")){
                        m = mMap.addMarker(new MarkerOptions().position(temp).title(title)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                    }
                    else if (str.equals("Wed")){
                        m = mMap.addMarker(new MarkerOptions().position(temp).title(title)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                    }
                    else if (str.equals("Thu")){
                        m = mMap.addMarker(new MarkerOptions().position(temp).title(title)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    }
                    else if (str.equals("Fri")){
                        m = mMap.addMarker(new MarkerOptions().position(temp).title(title)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                    }
                    else if (str.equals("Sat")){
                        m = mMap.addMarker(new MarkerOptions().position(temp).title(title)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    } else {
                        m = mMap.addMarker(new MarkerOptions().position(temp).title(title)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                    }

                    MarkerObj markerobj = new MarkerObj(m,str,typename);
                    markers.add(markerobj);
                    mapEvent.put(m,eventId);


                    /*mapDayType.get(str).add(m);

                    if(mapType.containsKey(typename)) {
                        mapType.get(typename).add(m);

                    } else {
                        List<Marker> list = new LinkedList<Marker>();
                        list.add(m);
                        mapType.put(typename,list);

                    }
                    mapEvent.put(m,eventId);*/

                }
                //progressDialog.dismiss();

            }
        });
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker arg0) {
                Intent intent = new Intent(getBaseContext(), DetailInfoActivity.class);
                String str = mapEvent.get(arg0);
                intent.putExtra(EVENT_ID,str);

                intent.putExtra("from","MapActivity");
                // Starting the  Activity
                startActivity(intent);
            }
        });

    }

    @Override
    public void onConnected(Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            // Blank for a moment...
        }
        else {
            handleNewLocation(location);
        };
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            moveTaskToBack(true);
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }



    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Map Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient.connect();
        AppIndex.AppIndexApi.start(mGoogleApiClient, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(mGoogleApiClient, getIndexApiAction());
        mGoogleApiClient.disconnect();
    }



/*    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }*/
}
