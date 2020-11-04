package com.group8.odin.common.models;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.group8.odin.R;

/*  Created by: Matthew Tong
    Created on: 2020-11-04
    Description: Activity for Welcome Screen
*/

public class WelcomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        //mTextView = (TextView) findViewById(R.id.text);

        // Enables Always-on
        //setAmbientEnabled();
    }
    public void onBtnSigUpClick (View view){
        Button btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(WelcomeScreen.this, SignUp.class);
                startActivity(intent);
            }
        });
    }
}

/*TODO: WelcomeScreen
        create fragment for login
*       create fragment for forgot password
        add input checks on all input field*/