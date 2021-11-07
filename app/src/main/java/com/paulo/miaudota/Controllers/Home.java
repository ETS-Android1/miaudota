package com.paulo.miaudota.Controllers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.paulo.miaudota.R;

public class Home extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private GoogleSignInClient mGoogleSignInClient;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("175731511537-u2dovn18s8aqk9km02jvse09b2kvd6gd.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mGoogleSignInClient = GoogleSignIn.getClient(Home.this, gso);

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
                    Fragment currentFragment = Home.this.getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    int contador=  0;

                    switch (item.getItemId()){
                        case R.id.nav_home:
                            if (currentFragment instanceof HomeFragment) {
                                contador = 1;
                                Log.v("tag", "Corno clicando várias vezes na Home");
                                break;
                            }
                            selectedFrament = new HomeFragment();
                            break;
                        case R.id.nav_addPet:
                            if (currentFragment instanceof AddPetFragment) {
                                contador = 1;
                                Log.v("tag", "Corno clicando várias vezes na Message");
                                break;
                            }
                            selectedFrament = new AddPetFragment();
                            break;
                        case R.id.nav_profile:
                            if (currentFragment instanceof ProfileFragment) {
                                contador = 1;
                                Log.v("tag", "Corno clicando várias vezes no Perfil");
                                break;
                            }
                            selectedFrament = new ProfileFragment();
                            break;
                        case R.id.nav_meusPets:
                            if (currentFragment instanceof MyPetsFragment) {
                                contador = 1;
                                Log.v("tag", "Corno clicando várias vezes no Perfil");
                                break;
                            }
                            selectedFrament = new MyPetsFragment();
                            break;
                    }
                    if(contador == 0){
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFrament).commit();
                    }

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

//    @Override
//    public void onBackPressed(){
//        Log.e("Warning_Activity","onBackPressed Home");
//        counter++;
//
//        if(counter == 2){
//            deslogar();
//            finish();
//            System.exit(1);
//        }
//        else {
//            Toast.makeText(getBaseContext(), "Pressione novamente para sair.", Toast.LENGTH_LONG).show();
//        }
//    }

}