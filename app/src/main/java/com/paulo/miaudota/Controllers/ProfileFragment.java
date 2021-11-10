package com.paulo.miaudota.Controllers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paulo.miaudota.R;
import com.paulo.miaudota.Models.User;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment implements View.OnClickListener{

    private TextView TextFullName,TextCPF,TextEmail,TextLocalizacao, TextDataNascimento, TextCelular;
    private CircleImageView profilePicture;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference reference;
    private GoogleSignInClient mGoogleSignInClient;
    private String userID;
    private String profilePicUrl;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("Warning_Activity","onCreateView -> ProfileFragment");

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Button deslogar = view.findViewById(R.id.btnLogOut);
        Button editarPerfil = view.findViewById(R.id.btnEditProfile);
        deslogar.setOnClickListener(this);
        editarPerfil.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("175731511537-u2dovn18s8aqk9km02jvse09b2kvd6gd.apps.googleusercontent.com")
                .requestEmail()
                .build();

        //inicilizando valores
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();
        profilePicture =  view.findViewById(R.id.profilePicture);
        TextFullName = view.findViewById(R.id.nomeProfile);
        TextCPF = view.findViewById(R.id.cpfProfile);
        TextEmail = view.findViewById(R.id.emailProfile);
        TextLocalizacao = view.findViewById(R.id.localizacaoProfile);
        TextDataNascimento = view.findViewById(R.id.dataNascimentoProfile);
        TextCelular = view.findViewById(R.id.celularProfile);
        progressBar = view.findViewById(R.id.progressBarProfile);

        if (user == null) {
            Log.e("Warning_Activity","onCreate EditProfile userNull-> ");
            startActivity(new Intent(getActivity(), WelcomeScreen.class));
        }

        //validar se o usuário já está completamente cadastrado, se não estiver redirecionar pra completar o perfil
        loadUser();
        return view;
    }

    @Override
    public void onStart() {
        progressBar.setVisibility(View.VISIBLE);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        Log.e("Warning_Activity","onStart -> ProfileFragment");
        super.onStart();
        if (user == null) {
            startActivity(new Intent(getActivity(), Home.class));
        }
    }

    @Override
    public void onResume() {
        Log.e("Warning_Activity","onResume -> ProfileFragment");
        super.onResume();
        loadUser();
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLogOut:
                deslogar();
                startActivity(new Intent(getActivity(), WelcomeScreen.class));
                System.exit(0);
                break;
            case R.id.btnEditProfile:
                redireUserToEditProfile();
                break;
        }
    }

    private void loadUser() {
        //Carregar informações do usuário completo
        progressBar.setVisibility(View.VISIBLE);
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                try {
                    profilePicUrl = user.getPhotoUrl().toString();
                }
                catch (Exception ex){
                    Log.e("Warning_Activity","profilePicUrl -> Erro ao buscar foto: " + ex);
                }

                if(userProfile.Cpf != null){

                    String nomeCompleto = userProfile.NomeCompleto;
                    String email = userProfile.Email;
                    String cpf = userProfile.Cpf;
                    String localizacao = userProfile.Cidade + "/" + userProfile.UF;
                    String dataNascimento = userProfile.DataNascimento;
                    String numCelular = userProfile.Ddd + "/" + userProfile.NumCelular;

                    TextFullName.setText(nomeCompleto);
                    TextEmail.setText(email);
                    TextCPF.setText(cpf);
                    TextLocalizacao.setText(localizacao);
                    TextDataNascimento.setText(dataNascimento);
                    TextCelular.setText(numCelular);

                    Glide.with(getActivity())
                            .load(profilePicUrl) // image url
                            .placeholder(R.drawable.profile_placeholder) // any placeholder to load at start
                            .error(R.drawable.profile_placeholder)  // any image in case of error
                            .override(200, 200) // resizing
                            .centerCrop()
                            .into(profilePicture);  // imageview object

                    //progressBar.setVisibility(View.INVISIBLE);
                }
                else{
                    Toast.makeText(getActivity(),"Você precisa finalizar seu cadastro !",Toast.LENGTH_LONG).show();
                    Log.e("Warning_Activity","");
                    redireUserToEditProfile();
                    //progressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Warning_Activity","loadUser -> erro onCancelled.");
                Toast.makeText(getActivity(),"Algo deu errado !",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void redireUserToEditProfile() {
        startActivity(new Intent(getActivity(), EditProfile.class));
    }

    private void deslogar(){
        mAuth.signOut();
        mGoogleSignInClient.signOut();
    }

}
