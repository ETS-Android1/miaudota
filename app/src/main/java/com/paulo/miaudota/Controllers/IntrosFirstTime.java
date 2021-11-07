package com.paulo.miaudota.Controllers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.paulo.miaudota.R;
import com.paulo.miaudota.SliderAdapter;

public class IntrosFirstTime extends AppCompatActivity implements View.OnClickListener {

        private ViewPager mSlideViewPager;
        private LinearLayout mDotLayout;
        private TextView[] mDots;
        private SliderAdapter adapter;
        private Button btnIntro;

        private SharedPreferences prefs;

        private FirebaseAuth mAuth;

        private int mCurrentPage;

        @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_intros_first_time);

            mSlideViewPager = findViewById(R.id.viewP2);
            mDotLayout = findViewById(R.id.dots_container);

            adapter = new SliderAdapter(this);
            mSlideViewPager.setAdapter(adapter);

            addDotsIndicator(0);

            mSlideViewPager.addOnPageChangeListener(viewListener);

            btnIntro = findViewById(R.id.btnIntro);
            btnIntro.setOnClickListener(this);

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

        public void addDotsIndicator(int position){
            mDots = new TextView[3];
            mDotLayout.removeAllViews();

            for(int i = 0;i<mDots.length;i++){
                mDots[i] = new TextView(this);
                mDots[i].setText(Html.fromHtml("&#9679;"));
                mDots[i].setTextSize(18);
                mDots[i].setTextColor(getResources().getColor(R.color.bluecachorro));

                mDotLayout.addView(mDots[i]);
            }

            if(mDots.length > 0){
                mDots[position].setTextColor(getResources().getColor(R.color.rosinha));

                if(mDots[position] == mDots[1]){
                    btnIntro.setText("Próximo");
                }
                else if(mDots[position] == mDots[2]){
                    btnIntro.setText("Continuar para o app");
                }

            }

        }

        ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                addDotsIndicator(position);
                mCurrentPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };

        public void onClick(View v) {
            if(v.getId() == R.id.btnIntro){
                if(mCurrentPage != 2){
                    mSlideViewPager.setCurrentItem(mCurrentPage+1);
                }
                else{
                    startActivity(new Intent(IntrosFirstTime.this, WelcomeScreen.class));
                }
            }
        }

        private void updateUI(FirebaseUser user) {
            if(user != null){
                startActivity(new Intent(IntrosFirstTime.this, HomeFragment.class));
            }
        }


}
