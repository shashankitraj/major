package com.example.gps;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/*
    * Main Activity for the app
    * Contains all the information and sends information of the GPS to firebase.
    * Contains logout button for logging out user.
    * Structure for storing GPS Details:
     -root
      --<User1_uid>
        --Date and time(date and time of saving data)
          --altitude
          --speed
          --latitude
          --longitude
          --date
          --time
          --email
        --Date and time(date and time of saving data)
          --altitude
          --speed
          --latitude
          --longitude
          --date
          --time
          --email
*/
public class MainActivity extends AppCompatActivity {
    private static final double THRESHOLD_SPEED = 80 ;//Max Limit of Speed
    LocationManager locationManager;//Location Manager for GPS of the mobile
    double longitudeGPS, latitudeGPS;//Stores the latitude and longitude
    double speed,altitude;// Speed and altitude
    String request=""; // The request from Thingspeak is served at this vatiable . It is a JSON object.
    //Variables for UI Elements and Firebase Auth instance.
    TextView longitudeValueGPS, latitudeValueGPS;
    TextView speedValueGPS,altitudeValueGPS;
    TextView cardStatus;
    Button locationControllerGps,btnGetStatus;
    ProgressBar progressBar,progressBar1;
    String cardDetailsText="RFID Card not scanned.\nPress button to refresh status.";
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference dbRef,userRef,uref;//Database reference for diffrent opereations
    ArrayList<UserDetails> userDetail=new ArrayList<UserDetails>();//Array list to store user Details fetch from firebase.
    ArrayList<RFIDDetails> rfidDetail=new ArrayList<RFIDDetails>();//Array list to store rfid details from firebase
    boolean flag=false;//Used to check if the users card is scanned or not.
    boolean isFlag=false; // used to check if there are any entry in thinkspeak of the current users UID number . IF not done the app is stuck in checking mode finding the number.
    //Once the card is scanned then flag is set true.
    boolean speedUpdate=false;//used to send update to the user if the vechicle is overspeeding.
    //sends the message once in the session.
    private DatabaseReference dbRefUser;//used to fetch user number
    private String userNum="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getting the Location Manager instance for GPS.
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationControllerGps=findViewById(R.id.locationControllerGPS);
        longitudeValueGPS = (TextView) findViewById(R.id.longitudeValueGPS);
        latitudeValueGPS = (TextView) findViewById(R.id.latitudeValueGPS);
        speedValueGPS=findViewById(R.id.speedValueGPS);
        altitudeValueGPS=findViewById(R.id.altitudeValueGPS);
        progressBar=findViewById(R.id.progressBarMain);
        progressBar1=findViewById(R.id.progressBarMain1);
        locationControllerGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                longitudeValueGPS.setText("Fetching Value ...");
                latitudeValueGPS.setText("Fetching Value ...");
                speedValueGPS.setText("Fetching Value ...");
                altitudeValueGPS.setText("Fetching Value ...");
                //progressBar.setVisibility(View.VISIBLE);
                toggleGPSUpdates();
            }
        });

        cardStatus=findViewById(R.id.CardStatus);
        cardStatus.setText(cardDetailsText);
        btnGetStatus=findViewById(R.id.buttonRefreshStatus);
        mAuth=FirebaseAuth.getInstance();
        //button click listener for logging out the user.

        //Button click listener to check if the RFID card is scanned by the user or not.
        btnGetStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar1.setVisibility(View.VISIBLE);
                cardStatus.setText("Card details fetching.");
                getData();
            }
        });
        //Different database reference.
        database=FirebaseDatabase.getInstance();
        dbRef=database.getReference("Gps Details");
        userRef=database.getReference("User Details");
        uref=database.getReference("Uid");
        dbRefUser=database.getReference("User Details").child(mAuth.getUid());
        dbRefUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserDetails u=new UserDetails();
                u=dataSnapshot.getValue(UserDetails.class);
                userNum=u.getPhone();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //creating menu for topbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //creating action for button of meun
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case R.id.profile:
            startActivity(new Intent(getApplicationContext(),UserProfile.class));
            return(true);
        case R.id.exit:
            startActivity(new Intent(getApplicationContext(),MapsActivity.class));
            //this.finish();
            //moveTaskToBack(true);
            return(true);

    }
        return(super.onOptionsItemSelected(item));
    }


    //Getting the data from the Thingspeak server and parsing the data.
    //Thingspeak return data as a JSON object.
    //Using Volley to implement HTTP Get requests from the server.
    //Data from the RFID Hardware goes into the THINGSPEAK server in a channel created .
    //The Channel contains : RFID card number , Date and time of data upload(generated by the Thingspeak server)
    public void getData(){
        //Url to run the HTTP Get Request.
        final String url = "https://api.thingspeak.com/channels/1009719/fields/1.json?api_key=JHU9FNCAY9BPA04B&timezone=Asia/Kolkata";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        try {
            JSONObject object = new JSONObject();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    request=response.toString();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("Uid","Error");
                }
            });
            requestQueue.add(jsonObjectRequest);//Executing the url.
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("Uid",request);
        //Parsing the JSON object and storing them into Firebase
        // Database structure:
        // --root
        //   --Uid
        //     --(Integer count for all feeds)
        //       --date
        //       --time
        //       --uid
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
            checkStatus();//calling the check function so that it checks for RFID card scanned or not.
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //function so that it checks for RFID card scanned or not.
    //Checks if the user has scanned the card within the past 10 min or not.
    // If so then card status is changed to RFID card scanned.
    void checkStatus(){
        //getting user details at userDetails array list
        //implement for async behaviour.
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
        //getting rfid details at rfidDetails array list
        //implement for async behaviour.
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
        //checking for the index of the current user in the array list.
        int ind=0;
        for(int i=0;i<userDetail.size();i++){
            if(mAuth.getCurrentUser().getEmail().equals(userDetail.get(i).getEmail())){
                ind=i;
            }
        }
        //getting the latest date and time for the user.
        String d="",t="";
        for(int i=0;i<rfidDetail.size();i++){
            if(userDetail.get(ind).getRfid().equals(rfidDetail.get(i).getUid())){
                d=rfidDetail.get(i).getDate();
                t=rfidDetail.get(i).getTime();
                isFlag=true;
            }
        }

        String date=getDate();//current date
        String time=getTime();//current time
        //comparing to check if user scanned card in last 10 min or not.
        int h=0,h1=0;
        int m=0,m1=0;
        h=Integer.parseInt(time.substring(0,2));
        m=Integer.parseInt(time.substring(3,5));
        if(t.length()==0){
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
                cardDetailsText="Card Scanned.";
                cardStatus.setText(cardDetailsText);
                flag=true;
                return;
            }
        }

        else{
            cardDetailsText="Scan the card again and try again.";
            cardStatus.setText(cardDetailsText);
        }

        progressBar1.setVisibility(View.GONE);

    }
    //Get Details of the User from the Firebase Database
    //Stored in array list of userDetails.
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
    //Get Details of the User from the Firebase Database
    //Stored in array list of rfidDetails.
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
    //Interface is implemented in order to counter the Async behaviour of firebase .
    //Firebase uses Async behaviour.
    //Async: Doing the task on a secondary thread. While other tasks are being performed.
    //Main thread can perform other task as the secondary thread is working to fetch database.
    public interface MyCallbackUser{
        void onCallback(ArrayList<UserDetails> userDetails);
    }
    public interface MyCallbackRfid{
        void onCallback(ArrayList<RFIDDetails> rfidDetails);
    }
    //Check location function will check if the location setting for the device is on or not.
    private boolean checkLocation() {
        if(!isLocationEnabled())
            //calling show alert function for location setting off.
            showAlert();
        return isLocationEnabled();
    }
    //Function for checking if location setting is switched on or not.
    //Creating the alert dialog to show location permission.
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
    //Check location is enabled and Network setting is enabled.
    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    //function called when the user press the button for getting location.
    //location manager checks the location permission.
    //updates location if location is updated by 10 meters or in (2*60*1000) seconds.
    // locationListenerGPS is called through location manager for accessing data from gps as a location object.
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

    //Function for getting the date in yyyy-MM-dd format.
    public String getDate(){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        Date date=new Date();
        return simpleDateFormat.format(date);
    }
    //Function for getting the time in HH-mm-ss format.
    public String getTime(){
        SimpleDateFormat simpleDateFormat1=new SimpleDateFormat("HH:mm:ss");
        Date date1=new Date();
        return simpleDateFormat1.format(date1);
    }
    //Location manager function that returns the location of the device using inbuild GPS.
    //Gives the location when location is updated.
    private final LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitudeGPS = location.getLongitude();
            latitudeGPS = location.getLatitude();
            speed=location.getSpeed();
            altitude=location.getAltitude();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.INVISIBLE);
                    String ns="";
                    String ew="";
                    if(latitudeGPS >= 0){
                        ns="N";
                    }
                    else{
                        ns="S";
                    }
                    if(longitudeGPS > 0){
                        ew="E";
                    }
                    else {
                        ew="W";
                    }
                    DecimalFormat numberFormat = new DecimalFormat("0.00");
                    longitudeValueGPS.setText(numberFormat.format(longitudeGPS) + " \u00B0 " +ns);
                    latitudeValueGPS.setText(numberFormat.format(latitudeGPS) + " \u00B0 " +ew);
                    speedValueGPS.setText(numberFormat.format(speed)+" m/s");
                    altitudeValueGPS.setText(altitude+" m");
                    Toast.makeText(MainActivity.this, "GPS Provider update", Toast.LENGTH_SHORT).show();
                    if((speed*3.6)>THRESHOLD_SPEED && speedUpdate==false){

                        try{
                            PendingIntent sentIntent = null, deliveryIntent = null;
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage
                                    (userNum, null, "Your Vehicle was speeding at speeds greater than 80 on"+getDate()+" "+getTime(),
                                            sentIntent, deliveryIntent);
                        }
                        catch(Exception e){
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            String d=getDate();//get current date
            String t=getTime();//get current time
            //get current users email
            String email=mAuth.getCurrentUser().getEmail();
            //storing the values in firebase.
            GpsDetails gpsDetails=new GpsDetails(latitudeGPS,longitudeGPS,speed,altitude,email,d,t);
            String ref=d+" "+t;//combining the data and time for getting key for storing in database
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

    //action to perform on back button performed.
    @Override
    public void onBackPressed() {
        if(mAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }
        super.onBackPressed();
    }
}