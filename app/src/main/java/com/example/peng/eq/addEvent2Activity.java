package com.example.peng.eq;

import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class addEvent2Activity extends AppCompatActivity {
    private EditText street,city,state,zip;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event2);
        street = (EditText) findViewById(R.id.street);
        city = (EditText) findViewById(R.id.city);
        state = (EditText) findViewById(R.id.state);
        zip = (EditText) findViewById(R.id.zip);

    }

    public void submit(View view) {


        String newAddress = street.getText().toString() + ", " + city.getText().toString() + ", " + state.getText().toString() + ", " + zip.getText().toString();
        searchFromLocationName(newAddress);

    }
    private void searchFromLocationName(String name){
        Geocoder geocoder = new Geocoder(this ,Locale.getDefault());
        try {
            List<Address> result
                    = geocoder.getFromLocationName(name, 5);

            if ((result == null)||(result.isEmpty())){
                Toast.makeText(addEvent2Activity.this,
                        "No matches were found or there is no backend service!",
                        Toast.LENGTH_LONG).show();
            }
            Address location = result.get(0);
            double lati = location.getLatitude();
            double longi = location.getLongitude();

        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


/*
    public GeoPoint getLocationFromAddress(String strAddress){

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        GeoPoint p1 = null;

        try {
            address = coder.getFromLocationName(strAddress,5);
            if (address==null) {
                return null;
            }
            Address location=address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new GeoPoint((double) (location.getLatitude() * 1E6),
                    (double) (location.getLongitude() * 1E6));

            return p1;
        }
    }*/
}
