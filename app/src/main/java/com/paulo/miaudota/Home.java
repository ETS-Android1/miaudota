package com.paulo.miaudota;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Home extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private GoogleSignInClient mGoogleSignInClient;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user == null) {
            Log.e("Warning_Activity","onCreate EditProfile userNull-> ");
            startActivity(new Intent(Home.this, WelcomeScreen.class));
        }

        Log.e("Warning_Activity","onCreate Home");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener(){
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFrament = null;

                    switch (item.getItemId()){
                        case R.id.nav_home:
                            selectedFrament = new HomeFragment();
                            break;
                        case R.id.nav_message:
                            selectedFrament = new MessageFragment();
                            break;
                        case R.id.nav_profile:
                            selectedFrament = new ProfileFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFrament).commit();

                    return true;

                }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLogOut:
                deslogar();
                System.exit(1);
                break;
        }
    }

    private void deslogar(){
        mAuth.signOut();
        mGoogleSignInClient.signOut();
    }

    @Override
    public void onBackPressed(){
        Log.e("Warning_Activity","onBackPressed Home");
        counter++;

        if(counter == 2){
            deslogar();
            finish();
            System.exit(1);
            return;
        }
        else {
            Toast.makeText(getBaseContext(), "Pressione novamente para sair.", Toast.LENGTH_LONG).show();
        }
    }

}