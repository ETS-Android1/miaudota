package com.paulo.miaudota;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;


public class MainActivity extends AppCompatActivity {

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("com.opet.miaudota", MODE_PRIVATE);
        if (prefs.getBoolean("firstrun", true)) {
            prefs.edit().putBoolean("firstrun", false).apply();
            startActivity(new Intent(MainActivity.this, IntrosFirstTime.class));
        }
        else{
            startActivity(new Intent(MainActivity.this, WelcomeScreen.class));
        }
        Log.i("main", "onCreate fired");

    }

    @Override
    protected void onResume(){
        super.onResume();
        super.onBackPressed();
    }

}