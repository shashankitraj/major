package com.example.gps;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<GpsDetails> gpsDetail=new ArrayList<GpsDetails>();
    private FirebaseDatabase database;
    private DatabaseReference dbRef;
    private FirebaseAuth mAuth;
    Button btn;
    LatLng lastLatLong;
    FloatingActionButton fbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        database=FirebaseDatabase.getInstance();
        mAuth=FirebaseAuth.getInstance();
        dbRef=database.getReference("Gps Details").child(mAuth.getCurrentUser().getUid());
        fbtn=findViewById(R.id.floatingActionButtonMaps);
        fbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),JourneyDetailsActivity.class));
            }
        });
        btn=findViewById(R.id.buttonMapsActivity);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserDetails(new MyCallbackUser() {
                    @Override
                    public void onCallback(ArrayList<GpsDetails> gpsDetails) {
                        gpsDetail.clear();
                        for(int i=0;i<gpsDetails.size();i++){
                            gpsDetail.add(gpsDetails.get(i));
                        }

                    }
                });
                if(gpsDetail.size()>0){

                     lastLatLong=new LatLng(gpsDetail.get(gpsDetail.size()-1).getLatitude(), gpsDetail.get(gpsDetail.size()-1).getLongitude());
                    onMapReady(mMap);
                }

            }
        });

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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //LatLng sydney = new LatLng(-34, 151);
        //Toast.makeText(getApplicationContext(),String.valueOf(gpsDetail.size()),Toast.LENGTH_SHORT).show();
        if(gpsDetail.size()>0){
            mMap.addMarker(new MarkerOptions().position(lastLatLong).title("Last known location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLatLong,5));
        }
    }
}
