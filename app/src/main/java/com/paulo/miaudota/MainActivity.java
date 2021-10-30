package com.paulo.miaudota;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout dotsLayout;
    SliderAdapter adapter;
    ViewPager2 pager2;
    int list[];
    TextView[] dots;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dotsLayout = findViewById(R.id.dots_container);
        pager2 = findViewById(R.id.viewP2);

        list = new int[3];
        list[0] = getResources().getColor(R.color.mainBackground);
        list[1] = getResources().getColor(R.color.mainBackground);
        list[2] = getResources().getColor(R.color.mainBackground);

        adapter = new SliderAdapter(list);
        pager2.setAdapter(adapter);

        dots = new TextView[3];
        dotsIndicator();

        pager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                selectIndicator(position);
                super.onPageSelected(position);
            }
        });

        Button login = findViewById(R.id.btnLoginIntro);
        login.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            // Check if user is signed in (non-null) and update UI accordingly.
            FirebaseUser currentUser = mAuth.getCurrentUser();
            updateUI(currentUser);
        } catch (Exception e) {
            // Google Sign In failed, update UI appropriately
            Log.w("userErrorOnStart", "Check failed: ", e);
        }

    }

    private void selectIndicator(int position) {
        for(int i=0;i<dots.length;i++){
            if(i == position){
                dots[i].setTextColor(getResources().getColor(R.color.rosinha));
            }
            else{
                dots[i].setTextColor(getResources().getColor(R.color.bluecachorro));
            }
        }
    }

    private void dotsIndicator() {
        for(int i = 0; i<dots.length;i++){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#9679;"));
            dots[i].setTextSize(18);
            dotsLayout.addView(dots[i]);
        }
    }

    public void onClick(View v) {
        if(v.getId() == R.id.btnLoginIntro){
            startActivity(new Intent(MainActivity.this, WelcomeScreen.class));
        }
    }

    private void updateUI(FirebaseUser user) {
        if(user != null){
            startActivity(new Intent(MainActivity.this, Profile.class));
        }
    }

}