package com.example.gps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class JourneyDetailsActivity extends AppCompatActivity {
    TextView email,distanceD,timet,dated,alti,sped;
    DatabaseReference dbRef;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    ArrayList<GpsDetails> gpsDetail=new ArrayList<GpsDetails>();
    DecimalFormat decimalFormat=new DecimalFormat("0.00");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey_details);
        email=findViewById(R.id.journeyEmail);
        distanceD=findViewById(R.id.journeyDistance);
        timet=findViewById(R.id.journeyTime);
        dated=findViewById(R.id.journeyDate);
        alti=findViewById(R.id.journeyAltitude);
        sped=findViewById(R.id.journeySpeed);
        mAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        dbRef=database.getReference("Gps Details").child(mAuth.getCurrentUser().getUid());
        email.setText(mAuth.getCurrentUser().getEmail());
        getData();
    }
    void getData(){
        getUserDetails(new MyCallbackUser() {
            @Override
            public void onCallback(ArrayList<GpsDetails> gpsDetails) {
                gpsDetail.clear();
                for(int i=0;i<gpsDetails.size();i++){
                    gpsDetail.add(gpsDetails.get(i));
                }
                if(gpsDetail.size()>0){
                    email.setText(mAuth.getCurrentUser().getEmail());
                    calculateDistance();
                    calculateAltitude();
                    calculateSpeed();
                    dated.setText(gpsDetail.get(gpsDetail.size()-1).getDate());
                    timet.setText(gpsDetail.get(gpsDetail.size()-1).getTime());
                }
            }

        });
    }
    void calculateSpeed(){
        double cA=0;
        for(int i=0;i<gpsDetail.size();i++){
            if(cA<gpsDetail.get(i).getSpeed()) {
                cA = gpsDetail.get(i).getSpeed();
            }
        }
        sped.setText(String.valueOf(decimalFormat.format(cA*3.6))+ " km/hr");
    }
    void calculateAltitude(){
        double cA=0;
        for(int i=0;i<gpsDetail.size();i++){
            if(cA<gpsDetail.get(i).getAltitude()) {
                cA = gpsDetail.get(i).getAltitude();
            }
        }
        alti.setText(String.valueOf(decimalFormat.format(cA))+ " m");
    }
    void calculateDistance(){
        double dis=0;
        if(gpsDetail.size()>1){
            for(int i=0;i<gpsDetail.size()-1;i++){
                dis+=distance(gpsDetail.get(i).getLatitude(),gpsDetail.get(i).getLongitude(),gpsDetail.get(i+1).getLatitude(),gpsDetail.get(i+1).getLongitude());
            }

            distanceD.setText(String.valueOf(decimalFormat.format(dis))+" km");
        }
    }
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
    public void getUserDetails(final MyCallbackUser myCallbackUser){
        final ArrayList<GpsDetails> gpsDetails=new ArrayList<GpsDetails>();

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    gpsDetails.add(userSnapshot.getValue(GpsDetails.class));

                }
                myCallbackUser.onCallback(gpsDetails);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    public interface MyCallbackUser{
        void onCallback(ArrayList<GpsDetails> gpsDetails);
    }
}
