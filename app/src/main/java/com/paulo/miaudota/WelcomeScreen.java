package com.paulo.miaudota;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeScreen extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        Button register = findViewById(R.id.btnRegisterWelcome);
        register.setOnClickListener(this);

        Button login = findViewById(R.id.btnLoginWelcome);
        login.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnRegisterWelcome:
                startActivity(new Intent(WelcomeScreen.this, RegisterUser.class));
                break;
            case R.id.btnLoginWelcome:
                startActivity(new Intent(WelcomeScreen.this, Login.class));
                break;
        }
    }
}