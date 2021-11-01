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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity implements View.OnClickListener  {

    private EditText editTextFullName,editTextCPF,editTextEmail,editTextCidade,editTextUf,editTextPassword,editTextDataNascimento;
    private CircleImageView profilePicture;
    private ProgressBar progressBar;
    private String userID;
    private String profilePicUrl;
    private String dataN;
    final Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference reference;

    @Override
    protected void onStart() {
        Log.e("Warning_Activity","onStart EditProfile -> ");
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user == null) {
            Log.e("Warning_Activity","onStart EditProfile userNull-> ");
            startActivity(new Intent(EditProfile.this, Home.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("Warning_Activity","onCreate EditProfile -> ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //carregando informações do usuário
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();
        profilePicture =  findViewById(R.id.profilePictureEdit);
        editTextFullName = findViewById(R.id.nomeProfileEdit);
        editTextCPF = findViewById(R.id.cpfProfileEdit);
        editTextEmail = findViewById(R.id.emailProfileEdit);
        editTextCidade = findViewById(R.id.cidadeProfileEdit);
        editTextUf = findViewById(R.id.ufProfileEdit);
        //editTextPassword = findViewById(R.id.senhaProfileEdit);
        editTextDataNascimento = findViewById(R.id.btnDataNascimentoProfileEdit);
        editTextDataNascimento.setOnClickListener(this);
        progressBar = findViewById(R.id.progressBarProfileEdit);

        progressBar.setVisibility(View.VISIBLE);
        Button updateProfile = findViewById(R.id.btnUpdate);
        updateProfile.setOnClickListener(this);

        if (user == null) {
            Log.e("Warning_Activity","onCreate EditProfile userNull-> ");
            startActivity(new Intent(EditProfile.this, WelcomeScreen.class));
        }

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

        try {
            profilePicUrl = user.getPhotoUrl().toString();
            Log.e("Warning_Activity","providerId -> " + user.getProviderId());
        }
        catch (Exception ex){
            Log.e("Warning_Activity","profilePicUrl -> Erro ao buscar foto: " + ex);
        }

        //Carregar informações do usuário completo
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User userProfile = snapshot.getValue(User.class);
                String nomeCompleto = "";
                String email = "";

                if(userProfile.Cpf != null){
                    //se for completo carregada tudo
                    nomeCompleto = userProfile.NomeCompleto;
                    email = userProfile.Email;
                    String cpf = userProfile.Cpf;
                    String cidade = userProfile.Cidade;
                    String uf = userProfile.UF;
                    String dataNascimento = userProfile.DataNascimento;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date dataFormatada = null;
                    try {
                        dataFormatada = dateFormat.parse(dataNascimento);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    myCalendar.setTime(dataFormatada);

                    editTextFullName.setText(nomeCompleto);
                    editTextEmail.setText(email);
                    editTextCPF.setText(cpf);
                    editTextCidade.setText(cidade);
                    editTextUf.setText(uf);
                    editTextDataNascimento.setText(dataNascimento);

                    Glide.with(EditProfile.this)
                            .load(profilePicUrl) // image url
                            .placeholder(R.drawable.profile_placeholder) // any placeholder to load at start
                            .error(R.drawable.profile_placeholder)  // any image in case of error
                            .override(200, 200) // resizing
                            .centerCrop()
                            .into(profilePicture);  // imageview object

                    progressBar.setVisibility(View.INVISIBLE);
                }
                else{
                    //se for incompleto carrega email e nome e a foto
                    nomeCompleto = userProfile.NomeCompleto;
                    email = userProfile.Email;

                    editTextFullName.setText(nomeCompleto);
                    editTextEmail.setText(email);

                    Glide.with(EditProfile.this)
                            .load(profilePicUrl) // image url
                            .placeholder(R.drawable.profile_placeholder) // any placeholder to load at start
                            .error(R.drawable.profile_placeholder)  // any image in case of error
                            .override(200, 200) // resizing
                            .centerCrop()
                            .into(profilePicture);  // imageview object

                    progressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Warning_Activity","loadUser -> erro onCancelled.");
                Toast.makeText(EditProfile.this,"Algo deu errado !",Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnUpdate:
                updateProfile();
                break;
            case R.id.btnDataNascimentoProfileEdit:
                new DatePickerDialog(EditProfile.this, R.style.my_dialog_theme ,date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
        }
    }

    private void updateProfile() {
        updateLabel();

        String email = editTextEmail.getText().toString().trim();
        String nomeCompleto = editTextFullName.getText().toString().trim();
        String cpf = editTextCPF.getText().toString().trim();
        //String senha = editTextPassword.getText().toString().trim();
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

        User user = new User(nomeCompleto, cpf , email, cidade, uf, dataNascimento);
        reference.child(userID).setValue(user);
        //reference.child(userID).child("NomeCompleto").setValue(nomeCompleto);
        progressBar.setVisibility(View.INVISIBLE);
        //startActivity(new Intent(EditProfile.this, ProfileFragment.class));
        Intent intent = new Intent(EditProfile.this,ProfileFragment.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); finish();
    }

    private void validateCPF() {

    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dataN = sdf.format(myCalendar.getTime());
        editTextDataNascimento.setText(dataN);
    }

}