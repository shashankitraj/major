package com.example.gps;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    double longitudeGPS, latitudeGPS;
    double speed,altitude;
    String request="";
    TextView longitudeValueGPS, latitudeValueGPS;
    TextView speedValueGPS,altitudeValueGPS;
    TextView cardStatus;
    Button locationControllerGps,btnLogout,btnGetStatus;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference dbRef,userRef,uref;
    ArrayList<UserDetails> userDetail=new ArrayList<UserDetails>();
    ArrayList<RFIDDetails> rfidDetail=new ArrayList<RFIDDetails>();
    boolean flag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationControllerGps=findViewById(R.id.locationControllerGPS);
        locationControllerGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleGPSUpdates();
            }
        });
        longitudeValueGPS = (TextView) findViewById(R.id.longitudeValueGPS);
        latitudeValueGPS = (TextView) findViewById(R.id.latitudeValueGPS);
        speedValueGPS=findViewById(R.id.speedValueGPS);
        altitudeValueGPS=findViewById(R.id.altitudeValueGPS);
        cardStatus=findViewById(R.id.CardStatus);
        btnLogout=findViewById(R.id.buttonLogout);
        btnGetStatus=findViewById(R.id.buttonRefreshStatus);
        mAuth=FirebaseAuth.getInstance();
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser()!=null){
                    mAuth.signOut();
                    startActivity(new Intent(getApplicationContext(),Login.class));
                }
            }
        });
        btnGetStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
        database=FirebaseDatabase.getInstance();
        dbRef=database.getReference("Gps Details");
        userRef=database.getReference("User Details");
        uref=database.getReference("Uid");


    }

    public void getData(){
        final String url = "https://api.thingspeak.com/channels/1009719/fields/1.json?api_key=JHU9FNCAY9BPA04B&timezone=Asia/Kolkata";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        try {
            JSONObject object = new JSONObject();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    request=response.toString();
                    Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("Uid","Error");
                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("Uid",request);
        JSONObject rootObject = null;
        RFIDDetails rfidDetails;
        try {
            rootObject = new JSONObject(request);
            JSONArray feeds=rootObject.optJSONArray("feeds");
            for(int i=0;i<feeds.length();i++){
                JSONObject temp=feeds.getJSONObject(i);
                rfidDetails=new RFIDDetails(temp.optString("created_at"),temp.optString("field1"));
                uref.child(Integer.toString(i)).setValue(rfidDetails);
            }
            checkStatus();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    void checkStatus(){
        getUserDetails(new MyCallbackUser() {
            @Override
            public void onCallback(ArrayList<UserDetails> userDetails) {
                userDetail.clear();
                for(int i=0;i<userDetails.size();i++){
                    userDetail.add(userDetails.get(i));
                }
            }
        });
        //cardStatus.setText(Integer.toString(userDetail.size()));

        getRFIDDetails(new MyCallbackRfid() {
            @Override
            public void onCallback(ArrayList<RFIDDetails> rfidDetails) {
                rfidDetail.clear();
                for(int i=0;i<rfidDetails.size();i++){
                    rfidDetail.add(rfidDetails.get(i));
                }
            }
        });
        //cardStatus.setText(Integer.toString(rfidDetail.size()));
        int ind=0;
        for(int i=0;i<userDetail.size();i++){
            if(mAuth.getCurrentUser().getEmail().equals(userDetail.get(i).getEmail())){
                ind=i;
            }
        }
        String d="",t="";
        for(int i=0;i<rfidDetail.size();i++){
            if(userDetail.get(ind).getRfid().equals(rfidDetail.get(i).getUid())){
                d=rfidDetail.get(i).getDate();
                t=rfidDetail.get(i).getTime();
            }
        }
        String date=getDate();
        String time=getTime();
        int h=0,h1=0;
        int m=0,m1=0;
        h=Integer.parseInt(time.substring(0,2));
        m=Integer.parseInt(time.substring(3,5));
        if(t.length()==0){
            cardStatus.setText("Card details fetching.");
            return;
        }
        h1=Integer.parseInt(t.substring(0,2));
        m1=Integer.parseInt(t.substring(3,5));
        if(date.equals(d)){
            m1+=10;
            if(m1>=60){
                m1%=60;
                h1+=1;
            }
            if(m<=m1 && h1==h || flag==true){
                cardStatus.setText("Card Scanned.");
                flag=true;
            }
            else
                cardStatus.setText("Scan the card again and try again.");
        }
    }
    public void getUserDetails(final MyCallbackUser myCallbackUser){
        final ArrayList<UserDetails> userDetails=new ArrayList<UserDetails>();
        userRef=database.getReference("User Details");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    userDetails.add(userSnapshot.getValue(UserDetails.class));
                 }
                myCallbackUser.onCallback(userDetails);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    public void getRFIDDetails(final MyCallbackRfid myCallbackRfid){
        final ArrayList<RFIDDetails> rfidDetails=new ArrayList<RFIDDetails>();
        uref=database.getReference("Uid");

        uref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    rfidDetails.add(userSnapshot.getValue(RFIDDetails.class));
                }
                myCallbackRfid.onCallback(rfidDetails);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    public interface MyCallbackUser{
        void onCallback(ArrayList<UserDetails> userDetails);
    }
    public interface MyCallbackRfid{
        void onCallback(ArrayList<RFIDDetails> rfidDetails);
    }
    private boolean checkLocation() {
        if(!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }
    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }
    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    @SuppressLint("MissingPermission")
    public void toggleGPSUpdates() {
        if(!checkLocation())
            return;
        if(locationControllerGps.getText().equals(getResources().getString(R.string.pause))) {
            locationManager.removeUpdates(locationListenerGPS);
            locationControllerGps.setText(R.string.resume);
        }
        else {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 2 * 60 * 1000, 10, locationListenerGPS);
            locationControllerGps.setText(R.string.pause);
        }
    }
    public String getDate(){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        Date date=new Date();
        return simpleDateFormat.format(date);
    }
    public String getTime(){
        SimpleDateFormat simpleDateFormat1=new SimpleDateFormat("HH:mm:ss");
        Date date1=new Date();
        return simpleDateFormat1.format(date1);
    }
    private final LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitudeGPS = location.getLongitude();
            latitudeGPS = location.getLatitude();
            speed=location.getSpeed();
            altitude=location.getAltitude();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    longitudeValueGPS.setText(longitudeGPS + "");
                    latitudeValueGPS.setText(latitudeGPS + "");
                    speedValueGPS.setText(speed+"");
                    altitudeValueGPS.setText(altitude+"");
                    Toast.makeText(MainActivity.this, "GPS Provider update", Toast.LENGTH_SHORT).show();
                }
            });
            String d=getDate();
            String t=getTime();
            String email=mAuth.getCurrentUser().getEmail();
            GpsDetails gpsDetails=new GpsDetails(latitudeGPS,longitudeGPS,speed,altitude,email,d,t);
            String ref=d+" "+t;
            dbRef.child(mAuth.getUid()).child(ref).setValue(gpsDetails);

            /*
            Thingspeak data upload module.
            RequestQueue queue= Volley.newRequestQueue(getApplicationContext());
            String url = "https://api.thingspeak.com/update?api_key=7ZNHS0VMTPU5DV1T&field1="+latitudeGPS+"&field2="+longitudeGPS+"&field3="+speed+"&field4="+altitude;
            StringRequest ExampleStringRequest;
            ExampleStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(MainActivity.this, "Data updated in Online DB", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this, "Cannot write to Online DB Storing in local DB", Toast.LENGTH_SHORT).show();
                }
            });

            queue.add(ExampleStringRequest);
            */

        }
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }
        @Override
        public void onProviderEnabled(String s) {
        }
        @Override
        public void onProviderDisabled(String s) {
        }
    };

    @Override
    public void onBackPressed() {
        if(mAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }
        super.onBackPressed();
    }
}