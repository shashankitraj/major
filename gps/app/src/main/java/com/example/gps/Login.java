package com.example.gps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

/*
    * This is the landing page of the app.
    * Has function to Login the user.
    * Checks if the user email is verified of not and the allows the user to login.
    * Input: User email and password.
    * New user can go to register page through the text at the bottom.
*/
public class Login extends AppCompatActivity {
    //Variables for UI Elements and Firebase Auth instance.
    TextView textView;
    EditText etEmail,etPassword;
    Button btnLogin;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //getting instance of the UI elements and Firebase Auth
        textView=findViewById(R.id.textviewLogin);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignup();
            }
        });
        mAuth=FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null && mAuth.getCurrentUser().isEmailVerified()){
            //Checking if the app already has the user registered and the email is verified.
            //Starting the main activity for such users.
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }
        btnLogin=findViewById(R.id.buttonLogin);
        //Login button with click listener to login user.
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();

            }
        });
    }

    private void userLogin() {
        //Function to login the user.
        //Getting the instance of edit text UI element with name and password.
        etEmail=findViewById(R.id.editTextEmailLogin);
        etPassword=findViewById(R.id.editTextPasswordLogin);
        //getting string from edit text
        //Trim function removes leading and trailing spaces.
        String email=etEmail.getText().toString().trim();
        String password=etPassword.getText().toString().trim();
        if(email.length()==0)
            etEmail.setError("This field can not be blank");
        if(password.length()==0)
            etPassword.setError("This field can not be blank");
        //calling the sign in with email and password firebase function.
        else{
            if(!checkEmail(email)){
                etEmail.setError("Email not valid.");
            }
            else{
                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            if(mAuth.getCurrentUser().isEmailVerified()){
                                //checking if the email is verified or not.

                                Toast.makeText(getApplicationContext(),"Authentication Successful",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            }
                            else
                                //giving toast if email is not verified.
                                Toast.makeText(getApplicationContext(),"Email not verified",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //Toast for auth failed.
                            Toast.makeText(getApplicationContext(),"Authentication Failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        }

    }
    //Checks for valid email using inbuild pattern.
    boolean checkEmail(String email){
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
    void openSignup(){
        //starting signup activity for new user to signup.
        startActivity(new Intent(getApplicationContext(),Signup.class));
    }
}
