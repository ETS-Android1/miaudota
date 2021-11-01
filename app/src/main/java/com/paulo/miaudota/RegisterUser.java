package com.paulo.miaudota;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextFullName,editTextCPF,editTextEmail,editTextCidade,editTextUf,editTextPassword,editTextDataNascimento;
    private FirebaseAuth mAuth;
    private FirebaseUser fUser;
    private ProgressBar progressBar;
    final Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date;
    private String dataN;
    private String dataAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        //metodo pra verificar se o usuário estiver logado, se estiver não deixar acessar a tela de cadastro

        mAuth = FirebaseAuth.getInstance();

        ImageView logo =  findViewById(R.id.logoRegister);
        logo.setOnClickListener(this);

        Button register = findViewById(R.id.btnRegister);
        register.setOnClickListener(this);

        editTextFullName = findViewById(R.id.nomeCadastro);
        editTextCPF = findViewById(R.id.cpfCadastro);
        editTextEmail = findViewById(R.id.emailCadastro);
        editTextDataNascimento = findViewById(R.id.btnDataNascimento);
        editTextDataNascimento.setOnClickListener(this);
        editTextCidade = findViewById(R.id.btnCidade);
        editTextUf = findViewById(R.id.btnUf);
        editTextPassword = findViewById(R.id.senhaCadastro);
        progressBar = findViewById(R.id.progressBarRegister);

        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };
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
            case R.id.btnDataNascimento:
                new DatePickerDialog(RegisterUser.this, R.style.my_dialog_theme ,date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
        }
    }

    private void registerUser() {
        updateLabel();
        String email = editTextEmail.getText().toString().trim();
        String nomeCompleto = editTextFullName.getText().toString().trim();
        String cpf = editTextCPF.getText().toString().trim();
        String senha = editTextPassword.getText().toString().trim();
        String cidade = editTextCidade.getText().toString().trim();
        String uf = editTextUf.getText().toString().trim();
        String dataNascimento = dataN.toString().trim();

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
        
        validateCPF();

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

        if(uf.length() > 2){
            editTextUf.setError("A unidade federativa não pode possuir mais que 2 caracteres");
            editTextUf.requestFocus();
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

        if(dataNascimento.equals(dataAtual)){
            editTextDataNascimento.setError("Campo obrigatório !!");
            editTextDataNascimento.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            User user = new User(nomeCompleto, cpf , email, cidade, uf, dataNascimento);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterUser.this, "Usuário cadastrado com sucesso !", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        startActivity(new Intent(RegisterUser.this, Login.class));
                                        RegisterUser.this.finish();
                                    }else{
                                        if(task.getException().toString().equals("ERROR_EMAIL_ALREADY_IN_USE")){
                                            Log.e("Warning_Activity","Else task.excpetiuon -> " + task.getException().toString());
                                            Toast.makeText(RegisterUser.this, "Este email já está sendo utilizado !", Toast.LENGTH_LONG).show();
                                        }
                                        Toast.makeText(RegisterUser.this, "Falha ao cadastrar usuário ! Tente novamente !", Toast.LENGTH_LONG).show();
                                        Log.e("Warning_Activity","Else task.isSucesseful -> Erro ao Registrar");
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                        else {
                            if(task.getException().toString().equals("ERROR_EMAIL_ALREADY_IN_USE")){
                                Log.e("Warning_Activity","Else task.excpetiuon -> " + task.getException());
                                Toast.makeText(RegisterUser.this, "Este email já está sendo utilizado !", Toast.LENGTH_LONG).show();
                            }
                            Toast.makeText(RegisterUser.this, "Falha ao cadastrar usuário ! Tente novamente !", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

    }

    private void validateCPF() {
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dataN = sdf.format(myCalendar.getTime());
        dataAtual = sdf.format(Calendar.getInstance().getTime());
        if(!dataN.toString().equals(dataAtual)){
            editTextDataNascimento.setText(dataN);
        }

    }
}