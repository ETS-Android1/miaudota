package com.paulo.miaudota;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button register = findViewById(R.id.btnRegisterIntro);
        register.setOnClickListener(this);

        Button login = findViewById(R.id.btnLoginIntro);
        login.setOnClickListener(this);
    }


    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnRegisterIntro:
                startActivity(new Intent(MainActivity.this, RegisterUser.class));
                break;
            case R.id.btnLoginIntro:
                startActivity(new Intent(MainActivity.this, WelcomeScreen.class));
                break;
        }
    }

}