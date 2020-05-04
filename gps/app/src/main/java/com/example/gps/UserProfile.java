package com.example.gps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

//This page fetches the user details and displays them in a text views.
//Values fetched : Name , Email, Phone no , RFID Number
// Also gives a feature of logout to the user
//Database reference to userDetails in Firebase fetches details of user from firebase based on uid.
public class UserProfile extends AppCompatActivity {
    //Profile Elements
    TextView profileName,profileEmail,profilePhone,profileRFID;
    Button btnLogout;
    FirebaseAuth mAuth;
    FirebaseDatabase dbref;
    DatabaseReference userRef;
    UserDetails userDetails=new UserDetails();
    UserDetails u=new UserDetails();
    ImageView editName,editPhone;
    TextView inputDialogName;
    EditText inputDialogText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        profileName=findViewById(R.id.userProfileName);
        profileEmail=findViewById(R.id.userProfileEmail);
        profilePhone=findViewById(R.id.userProfilePhone);
        profileRFID=findViewById(R.id.userProfileRFID);
        btnLogout=findViewById(R.id.buttonProfileLogout);
        editName=findViewById(R.id.editNameProfile);
        editPhone=findViewById(R.id.editPhoneProfile);
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
        editPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialogName();
            }
        });
        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showInputDialogPhone();
            }
        });
        getData();
    }
    void getData(){
        getDetails(new MyCallbackUser(){
            @Override
            public void onCallback(UserDetails userDetails) {
                u=userDetails;
               profileName.setText(u.getName());
               profileEmail.setText(u.getEmail());
               profilePhone.setText(u.getPhone());
               profileRFID.setText(u.getRfid());
            }
        });
    }
    public interface MyCallbackUser{
        void onCallback(UserDetails userDetails);
    }
    void getDetails(final MyCallbackUser myCallbackUser){
        dbref=FirebaseDatabase.getInstance();
        userRef=dbref.getReference("User Details").child(mAuth.getCurrentUser().getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userDetails=dataSnapshot.getValue(UserDetails.class);
                myCallbackUser.onCallback(userDetails);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }

    protected void showInputDialogName() {
        LayoutInflater layoutInflater = getLayoutInflater();
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
        alertDialogBuilder.setTitle("Name Edit");
        alertDialogBuilder.setView(promptView);
        inputDialogName=promptView.findViewById(R.id.textViewInputDialogName);
        inputDialogText=promptView.findViewById(R.id.edittextInputDialogName);
        //inputDialogText.setHint(hint);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        profileName.setText(inputDialogText.getText().toString());
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
}
