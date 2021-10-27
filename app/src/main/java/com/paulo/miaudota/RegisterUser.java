package com.paulo.miaudota;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextFullName,editTextCPF,editTextEmail,editTextPassword;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        ImageView logo =  findViewById(R.id.logoRegister);
        logo.setOnClickListener(this);

        Button register = findViewById(R.id.btnRegister);
        register.setOnClickListener(this);

        editTextFullName = findViewById(R.id.nomeCadastro);
        editTextCPF = findViewById(R.id.cpfCadastro);
        editTextEmail = findViewById(R.id.emailCadastro);
        editTextPassword = findViewById(R.id.senhaCadastro);

        progressBar = findViewById(R.id.progressBarRegister);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.logoRegister:
                startActivity(new Intent(RegisterUser.this, MainActivity.class));
                break;
            case R.id.btnRegister:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String nomeCompleto = editTextFullName.getText().toString().trim();
        String cpf = editTextCPF.getText().toString().trim();
        String senha = editTextPassword.getText().toString().trim();

        if(nomeCompleto.isEmpty()){
            editTextFullName.setError("Campo obrigatório !!");
            editTextFullName.requestFocus();
            return;
        }

        if(cpf.isEmpty()){
            editTextCPF.setError("Campo obrigatório !!");
            editTextCPF.requestFocus();
            return;
        }

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
        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            User user = new User(nomeCompleto, cpf , email);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterUser.this, "Usuário cadastrado com sucesso !", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        startActivity(new Intent(RegisterUser.this, Login.class));
                                    }else{
                                        Toast.makeText(RegisterUser.this, "Falha ao cadastrar usuário ! Tente novamente !", Toast.LENGTH_LONG).show();
                                        Log.e("Warning_Activity","Else task.isSucesseful -> Erro ao Registrar");
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                        else {
                        Toast.makeText(RegisterUser.this, "Falha ao cadastrar usuário ! Tente novamente !", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });

    }
}