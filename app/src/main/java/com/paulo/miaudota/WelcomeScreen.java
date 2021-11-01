package com.paulo.miaudota;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class WelcomeScreen extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseUser fUser;
    private DatabaseReference rootRef;
    private DatabaseReference uidRef;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("175731511537-u2dovn18s8aqk9km02jvse09b2kvd6gd.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient= GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();

        Button register = findViewById(R.id.btnRegisterWelcome);
        register.setOnClickListener(this);

        Button login = findViewById(R.id.btnLoginWelcome);
        login.setOnClickListener(this);

        FloatingActionButton google = findViewById(R.id.fab_google);
        google.setOnClickListener(this);

        progressBar = findViewById(R.id.progressBarWelcome);

    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnRegisterWelcome:
                startActivity(new Intent(WelcomeScreen.this, RegisterUser.class));
                break;
            case R.id.btnLoginWelcome:
                startActivity(new Intent(WelcomeScreen.this, Login.class));
                break;
            case R.id.fab_google:
                signIn();
                break;
        }
    }

    private void signIn() {
        progressBar.setVisibility(View.VISIBLE);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        launchSomeActivity.launch(signInIntent);
    }

    // Create lanucher variable inside onAttach or onCreate or global
    ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();

                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try {
                            // Google Sign In was successful, authenticate with Firebase
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            Log.d("firebaseOk", "firebaseAuthWithGoogle:" + account.getId());
                            firebaseAuthWithGoogle(account.getIdToken());
                        } catch (ApiException e) {
                            // Google Sign In failed, update UI appropriately
                            Log.w("firebaseError", "Google sign in failed", e);
                        }
                    }
                }
            });

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            fUser = mAuth.getCurrentUser();
                            validateUserByUID(fUser);
                            Log.d("googleSignInSuccess", "signInWithCredential:success");
                            progressBar.setVisibility(View.GONE);
                            redirectUser(fUser);
                        } else {
                            // If sign in fails, display a message to the user.
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(WelcomeScreen.this, "Falha ao tentar logar!", Toast.LENGTH_LONG).show();
                            Log.w("googleSignInFail", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    private void validateUserByUID(FirebaseUser fUser) {
        //procura o usuario no realtime database, se não encontrar cria ele com as infos básicas

        String uid = fUser.getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        uidRef = rootRef.child("Users").child(uid);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    createUserInRealtimeDB(fUser, uid);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("TAG",error.getMessage());
            }
        };
        uidRef.addListenerForSingleValueEvent(eventListener);

    }

    private void createUserInRealtimeDB(FirebaseUser firebaseUser, String uid) {
        if(firebaseUser != null) {

            String email = "";
            String nomeCompleto = "";
            String providerId = "";

            for (UserInfo profile : firebaseUser.getProviderData()) {
                providerId = profile.getProviderId();
                nomeCompleto = profile.getDisplayName();
                email = profile.getEmail();
            }

            User user = new User(nomeCompleto, email);

            String finalProviderId = providerId;
            FirebaseDatabase.getInstance().getReference("Users")
                    .child(uid)
                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.e("Status_Activity", "Usuário do " + finalProviderId + " criado com sucesso !");
                    } else {
                        Log.e("Warning_Activity", "Erro ao tentar cadastrar usuário do " + finalProviderId);
                    }
                }
            });
        }
    }

    private void redirectUser(FirebaseUser user) {
        if(user != null){
            startActivity(new Intent(WelcomeScreen.this, Home.class));
        }
    }

    @Override
    public void onBackPressed() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}