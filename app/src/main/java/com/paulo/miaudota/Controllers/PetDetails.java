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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.paulo.miaudota.Models.Pet;
import com.paulo.miaudota.Models.User;
import com.paulo.miaudota.R;

import java.util.Calendar;

public class PetDetails extends AppCompatActivity implements View.OnClickListener {

    private TextView nomePetTv, tipoPetTv, localizacaoPetTv, idadePetTv, userPetTv, dataCadastroPetTv, descricaoPetTv;
    private Button btnZap, btnAdotar;
    private ImageView imagemPetDetails, imagemGeneroDetails;
    private String petId, nomeUsuario;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Pet petModel = null;
    private User userModel = null;
    private ProgressBar progressBar;

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

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        if (getIntent().hasExtra("petId")) {
            petId = getIntent().getStringExtra("petId");
        }

        loadPet(petId);

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

                    progressBar.setVisibility(View.INVISIBLE);


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
        }
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
}