package com.coder24.cornershop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.coder24.cornershop.model.Users;
import com.coder24.cornershop.prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button joinNowButton, loginButton;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadingBar = new ProgressDialog(this);

        //To initialize paper plugin to remember me functionality
        Paper.init(this);

        joinNowButton = (Button) findViewById(R.id.main_join_now_btn);
        loginButton = (Button) findViewById(R.id.main_login_btn);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        joinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        //read saved data from Paper plugin
        String phoneNumberKey = Paper.book().read(Prevalent.userPhoneKey);
        String passwordKey = Paper.book().read(Prevalent.userPasswordKey);
        if(phoneNumberKey != "" && passwordKey != ""){
            if(!TextUtils.isEmpty(phoneNumberKey) && !TextUtils.isEmpty(passwordKey))
            {
                allowAccess(phoneNumberKey,passwordKey);
                loadingBar.setTitle("Already Logged in..");
                loadingBar.setMessage("Please wait...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }
        }
    }

    private void allowAccess(final String phoneNumber, final String password) {
        final DatabaseReference rootReference;
        rootReference = FirebaseDatabase.getInstance().getReference();

        rootReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.child("Users").child(phoneNumber).exists())){
                    Users users = dataSnapshot.child("Users").child(phoneNumber).getValue(Users.class);
                    if(users.getPhoneNumber().equals(phoneNumber)){
                        if(users.getPassword().equals(password)){
                            Toast.makeText(MainActivity.this, "Please wait, you are already logged in!....", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }
                        else{
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this, "Password is incorrect..", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "Account with this " + phoneNumber + " number do not exists.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}