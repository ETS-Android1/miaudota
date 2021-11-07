package com.paulo.miaudota.Controllers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.paulo.miaudota.R;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextEmail,editTextPassword;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        Button logar = findViewById(R.id.btnLogin);
        logar.setOnClickListener(this);

        TextView criarConta = findViewById(R.id.createAccount);
        criarConta.setOnClickListener(this);

        editTextEmail = findViewById(R.id.emailLogin);
        editTextPassword = findViewById(R.id.senhaLogin);
        progressBar = findViewById(R.id.progressBarLoginEmail);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLogin:
                logarUsuario();
                break;
            case R.id.createAccount:
                startActivity(new Intent(Login.this, RegisterUser.class));
                break;
        }
    }

    private void logarUsuario() {
        String email = editTextEmail.getText().toString().trim();
        String senha = editTextPassword.getText().toString().trim();

        if(email.isEmpty()){
            editTextEmail.setError("Campo obrigatório !!");
            editTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Por favor digite um email válido!");
            editTextEmail.requestFocus();
            return;
        }

        if(senha.isEmpty()){
            editTextPassword.setError("Campo obrigatório !!");
            editTextPassword.requestFocus();
            return;
        }

        if(senha.length() < 6){
            editTextPassword.setError("Digite uma senha com no minimo 6 caracteres.");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //redirect do corno
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(Login.this, Home.class));
                    Login.this.finish();
                }
                else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Login.this, "Falha ao logar ! Verifique os dados inseridos.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}