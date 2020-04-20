package com.example.gps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*
 * Signup page : Page used to Signup new users.
 * Inputs: Name , Email , Password , Phone_No and RFID Number.
 * Email is used to send verification email.
 * Password is of length greater than 6.
 TODO: Add string check for password. For better password conditions.
 * RFID is unique RFID number assigned to each user . This number is unique and is assigned to each user during signup.
 * Check box is just to add fancy looks. Could add conditions later.
 * Upon registration of the user the user is sent a verification email to verify the email.
 * Using Firebase Auth to register users.
 * Firebase refernece: Stores the Name,Email,Phone no,RFID in following structure:
   root
    --User Details
     - <User Uid 1>
       -- Name
       --Email
       --Phone
       --RFID
     - <User Uid 2>
       -- Name
       --Email
       --Phone
       --RFID
  *This can be used to get info of the user and can be used later.
 */
public class Signup extends AppCompatActivity {
    TextView textView;
    //EditText for Name, Email , Password , Phone and RFID
    EditText etName,etEmail,etPassword,etPhone,etRfid;
    //Checkbox for condition to agree.
    CheckBox checkBox;
    //Button to register.
    Button btnRegister;
    //Firebase Auth instance.
    private FirebaseAuth mAuth;
    //Firebase Database instance
    FirebaseDatabase database;
    //Database Reference
    DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //Getting the reference to the textview for going to login page and button to register.
        textView=findViewById(R.id.textviewRegister);
        btnRegister=findViewById(R.id.buttonSignup);
        //Firebase instances for auth and database.
        mAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        //Setting reference to database :
        // root -> User Details
        myRef=database.getReference("User Details");
        //Click listener on the Button for registration.
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Function to register the user.
                registerUser();
            }
        });
        // Click Reference to go back to login page.
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Function to open login page.
                openLogin();
            }
        });
    }

    private void registerUser() {
        //Getting the reference of edit text box.
        etName=findViewById(R.id.editTextName);
        etEmail=findViewById(R.id.editTextEmail);
        etPassword=findViewById(R.id.editTextPassword);
        etPhone=findViewById(R.id.editTextPhone);
        etRfid=findViewById(R.id.editTextRfid);
        checkBox=findViewById(R.id.checkBoxSignup);
        //getting the value of all the edit text box.
        //Trim removes the leading and trailing spaces.
        final String email=etEmail.getText().toString().trim();
        final String password=etPassword.getText().toString().trim();
        final String name=etName.getText().toString().trim();
        final String phone=etPhone.getText().toString().trim();
        final String rfid=etRfid.getText().toString().trim();
        //Checkig password length greater than 6
        //To add the password check for better password.
        if(password.length()<6){
            Toast.makeText(getApplicationContext(),"Password length must be greater than 6.",Toast.LENGTH_SHORT).show();
        }
        //Checking if checkbox is checked or not.
        if(!checkBox.isChecked()){
            //Toast if checkbox is not checked.
            Toast.makeText(getApplicationContext(),"Agree to terms and conditions to register.",Toast.LENGTH_SHORT).show();
        }
        //Password length is greater than 6 and checkbox is cheked then user is registered.
        else {

            //Create user with email and password.
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                //Sending the user the email to verify.
                                FirebaseUser Fuser = FirebaseAuth.getInstance().getCurrentUser();
                                Fuser.sendEmailVerification();
                                //Storing name , email , phone , rfid through the UserDetails.java POJO class.
                                UserDetails user=new UserDetails(name,email,phone,rfid);
                                myRef.child(mAuth.getUid()).setValue(user);
                                Toast.makeText(getApplicationContext(),"Verify Email and Login",Toast.LENGTH_SHORT).show();
                                //Going back to Login class.
                                openLogin();

                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(getApplicationContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    void openLogin(){
        //Login page opening function.
        startActivity(new Intent(getApplicationContext(),Login.class));
    }
}
