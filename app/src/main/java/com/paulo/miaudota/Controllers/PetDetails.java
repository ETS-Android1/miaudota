package com.paulo.miaudota.Controllers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.paulo.miaudota.Models.Candidatura;
import com.paulo.miaudota.Models.Pet;
import com.paulo.miaudota.Models.User;
import com.paulo.miaudota.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PetDetails extends AppCompatActivity implements View.OnClickListener {

    private TextView nomePetTv, tipoPetTv, localizacaoPetTv, idadePetTv, userPetTv, dataCadastroPetTv, descricaoPetTv;
    private Button btnZap, btnAdotar;
    private ImageView imagemPetDetails, imagemGeneroDetails;
    private String petId, nomeUsuario, dataCadastroPetStr;
    private Date dataCadastroPet = null;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Pet petModel = null;
    private ProgressBar progressBar;
    private String userId, nomeUsuarioCandidatura, emailUsuario, localizacaoUsuario, profilePicUsuario, celularUsuario, dataCandidatura, candidaturaId;
    private Candidatura candidaturaModel = null;
    private Boolean jaCandidatou = false, usuarioIncompleto;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_details);

        progressBar = findViewById(R.id.progressBarDetails);
        progressBar.setVisibility(View.VISIBLE);
        nomePetTv = findViewById(R.id.nomePetDetails);
        tipoPetTv = findViewById(R.id.tipoPetDetails);
        localizacaoPetTv = findViewById(R.id.localizacaoPetDetails);
        idadePetTv = findViewById(R.id.idadePetDetails);
        userPetTv = findViewById(R.id.donoDoPetDetails);
        dataCadastroPetTv = findViewById(R.id.dataCadastroPetDetails);
        descricaoPetTv = findViewById(R.id.descricaoDetails);
        imagemGeneroDetails = findViewById(R.id.generoDetails);
        imagemPetDetails = findViewById(R.id.imagemPetDetails);
        btnZap = findViewById(R.id.btnZap);
        btnZap.setOnClickListener(this);
        btnAdotar = findViewById(R.id.btnAdotar);
        btnAdotar.setOnClickListener(this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userId = user.getUid();

        if (user == null) {
            Log.e("Warning_Activity","onCreate EditProfile userNull-> ");
            startActivity(new Intent(PetDetails.this, WelcomeScreen.class));
        }

        if (getIntent().hasExtra("petId")) {
            petId = getIntent().getStringExtra("petId");
        }

        checkUserCandidatou(petId, userId);
    }

    private void checkUserCandidatou(String petId, String userId) {
        DatabaseReference candidaturasRef = firebaseDatabase.getReference("Candidaturas").child(userId + petId);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    jaCandidatou = true;
                    loadPet(petId);
                }
                else{
                    jaCandidatou = false;
                    loadPet(petId);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("TAG",error.getMessage());
            }
        };
        candidaturasRef.addListenerForSingleValueEvent(eventListener);
    }

    private void loadPet(String petId) {

        Query query = databaseReference.child("Pets").orderByChild("petId").equalTo(petId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("PET", "achou o pet -> " + petId);

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    Log.e("PET", "Entrou  no data -> ");
                    petModel = childSnapshot.getValue(Pet.class);
                }
                if (petModel != null) {
                    String mesOuMeses, anoOuAnos;
                    nomePetTv.setText(petModel.getPetName());
                    tipoPetTv.setText(petModel.getTipoPet());
                    localizacaoPetTv.setText(petModel.getCidadePet() + "/" + petModel.getUfPet());
                    buscarNomeDoAnunciante(petModel.getUserId());
                    descricaoPetTv.setText(petModel.getDescricaoPet());
                    mesOuMeses = petModel.getIdadeMesesPet().equals("1") ? " mês" : " meses";
                    anoOuAnos = petModel.getIdadeAnosPet().equals("1") ? " ano" : " anos" ;
                    idadePetTv.setText(petModel.getIdadeAnosPet() + anoOuAnos + " e " + petModel.getIdadeMesesPet() + mesOuMeses);
                    dataCadastroPetTv.setText(petModel.getDataCadastro());

                    if(jaCandidatou == true){
                        btnAdotar.setText("Solicitado");
                        btnAdotar.setEnabled(false);
                    }

                    if(petModel.getGeneroPet().equals("Masculino")){
                        Glide.with(PetDetails.this)
                                .load(R.drawable.male) // image url
                                .placeholder(R.drawable.male) // any placeholder to load at start
                                .error(R.drawable.male)  // any image in case of error
                                .override(200, 200) // resizing
                                .centerCrop()
                                .into(imagemGeneroDetails);  // imageview object
                    }
                    else{
                        Glide.with(PetDetails.this)
                                .load(R.drawable.female) // image url
                                .placeholder(R.drawable.female) // any placeholder to load at start
                                .error(R.drawable.female)  // any image in case of error
                                .override(200, 200) // resizing
                                .centerCrop()
                                .into(imagemGeneroDetails);  // imageview object
                    }

                    Glide.with(PetDetails.this)
                            .load(petModel.getPetImg()) // image url
                            .placeholder(R.drawable.profile_placeholder) // any placeholder to load at start
                            .error(R.drawable.profile_placeholder)  // any image in case of error
                            .override(200, 200) // resizing
                            .centerCrop()
                            .into(imagemPetDetails);  // imageview

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }, 1000);

                } else {
                    Toast.makeText(PetDetails.this, "Ocorreu um erro ao carregar a página !", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PetDetails.this, "Ocorreu um erro ao carregar a página !!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void buscarNomeDoAnunciante(String userId) {
        DatabaseReference databaUsers = firebaseDatabase.getReference("Users");
        DatabaseReference username = databaUsers.child(userId).child("NomeCompleto");

        username.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nomeUsuario = dataSnapshot.getValue().toString();
                Log.e("fireballe", "n -> " + nomeUsuario);
                userPetTv.setText(nomeUsuario);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnZap:
                abrirZapDoAnunciante();
                break;
            case R.id.btnAdotar:
                pegarInfosCandidatura();
                break;
        }
    }

    private void candidatarUsuario() {
        DatabaseReference candidaturasRef = firebaseDatabase.getReference("Candidaturas");
        candidaturaId = userId + petId;

        candidaturasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(usuarioIncompleto == true){
                    startActivity(new Intent(PetDetails.this, EditProfile.class));
                    Toast.makeText(PetDetails.this, "Você deve completar seu perfil para se candidatar !", Toast.LENGTH_LONG).show();
                    return;
                }
                candidaturasRef.child(candidaturaId).setValue(candidaturaModel);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(PetDetails.this, "Candidatura feita com sucesso !", Toast.LENGTH_LONG).show();
                checkUserCandidatou(petId, userId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PetDetails.this, "Erro ao enviar dados do pet !", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void abrirZapDoAnunciante() {
        String texto = "Olá, vi que você postou animal pra adotar no Miaudota, ele ainda está disponível ?";
        String numCelular = "55" + petModel.getDdd().trim() + petModel.getNumCelular().trim();
        String url="https://api.whatsapp.com/send?phone="+ numCelular + "&text=" + texto;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        try {
            startActivity(i);
        }catch (Exception ex){
            Toast.makeText(PetDetails.this, "Ocorreu um erro ao tentar abrir o whastsapp !!", Toast.LENGTH_LONG).show();
        }
    }

    private void pegarInfosCandidatura() {
        //Carregar informações do usuário
        DatabaseReference usersRef = firebaseDatabase.getReference("Users");
        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                try {
                    profilePicUsuario = user.getPhotoUrl().toString();
                }
                catch (Exception ex){
                    Log.e("Warning_Activity","profilePicUrl -> Erro ao buscar foto: " + ex);
                }
                petId = petModel.getPetId();
                nomeUsuarioCandidatura = userProfile.NomeCompleto;
                if(userProfile.Cpf == null){
                    usuarioIncompleto = true;
                }
                else {
                    usuarioIncompleto = false;
                }
                emailUsuario = userProfile.Email;
                localizacaoUsuario = userProfile.Cidade + "/" + userProfile.UF;
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                dataCandidatura = sdf.format(Calendar.getInstance().getTime());
                celularUsuario = userProfile.Ddd + userProfile.NumCelular;
                candidaturaModel = new Candidatura(petId, userId, emailUsuario, nomeUsuarioCandidatura, celularUsuario, localizacaoUsuario, dataCandidatura, profilePicUsuario);
                candidatarUsuario();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Warning_Activity","loadUser -> erro onCancelled.");
            }
        });
    }
}