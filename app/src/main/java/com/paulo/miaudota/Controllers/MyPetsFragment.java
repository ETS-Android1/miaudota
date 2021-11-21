package com.paulo.miaudota.Controllers;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.paulo.miaudota.Models.Pet;
import com.paulo.miaudota.MyPetsRVAdapter;
import com.paulo.miaudota.PetRVAdapter;
import com.paulo.miaudota.R;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class MyPetsFragment extends Fragment implements MyPetsRVAdapter.PetClickInterface, MyPetsRVAdapter.PetClickDeleteInterface, MyPetsRVAdapter.PetClickAdoptInterface, MyPetsRVAdapter.PetClickEditInterface {

    private RecyclerView petRV;
    private ProgressBar progressBar;
    private ArrayList<Pet> petArrayList;
    private MyPetsRVAdapter myPetsRVAdapter;
    private String petId;
    SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private Dialog deleteDialog, adoptDialog;
    private TextView mensagemSemPet;
    private ImageView imagemSemPet;
    public int counter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("Warning_Activity","onCreateView -> MyPetsFragment");

        View view = inflater.inflate(R.layout.fragment_my_pets, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user == null) {
            Log.e("Warning_Activity","onCreateView MyPetsFragment userNull-> ");
            startActivity(new Intent(getActivity(), WelcomeScreen.class));
        }

        db = FirebaseFirestore.getInstance();
        petRV = view.findViewById(R.id.idRvMyPets);
        petArrayList = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Pets");
        myPetsRVAdapter = new MyPetsRVAdapter(petArrayList,getContext(),this,this,this,this);
        petRV.setLayoutManager(new LinearLayoutManager(getContext()));
        petRV.setAdapter(myPetsRVAdapter);
        progressBar = view.findViewById(R.id.progressBarMyPets);
        progressBar.setVisibility(View.VISIBLE);
        deleteDialog = new Dialog(getContext());
        adoptDialog = new Dialog(getContext());
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshMyPets);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                myPetsRVAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        mensagemSemPet = view.findViewById(R.id.mensagemSemMyPet);
        imagemSemPet = view.findViewById(R.id.imagemSemMyPet);
        counter = 0;

        getAllPets();

        return view;
    }

    @Override
    public void onPetClick(int position) {
        Intent intent = new Intent(getActivity(),Candidaturas.class);
        Pet petModel = petArrayList.get(position);
        petId = petModel.getPetId();
        intent.putExtra("petId", petId);
        startActivity(intent);
    }

    @Override
    public void onPetClickDelete(int position) {
        Pet petModel = petArrayList.get(position);
        petId = petModel.getPetId();
        Log.e("Warning_Activity","Deletando conta");
        openConfirmDeleteDialog(petId);
    }

    @Override
    public void onPetClickEdit(int position) {
        Fragment fragmentEditPet =  new EditPetFragment();
        Bundle bundle = new Bundle();
        Pet petModel = petArrayList.get(position);
        petId = petModel.getPetId();
        bundle.putString("PetId", petId);
        fragmentEditPet.setArguments(bundle);
        switchFragment(fragmentEditPet);
    }

    @Override
    public void onPetClickAdopt(int position) {
        Pet petModel = petArrayList.get(position);
        petId = petModel.getPetId();
        openConfirmAdoptDialog(petId);
    }

    private void openConfirmDeleteDialog(String petId) {
        deleteDialog.setContentView(R.layout.delete_confirmation_dialog);
        deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button btnDeletar, btnCancelar;
        btnDeletar = deleteDialog.findViewById(R.id.btnConfirmarDelete);
        btnCancelar = deleteDialog.findViewById(R.id.btnCancelarDelete);

        btnDeletar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletarRealOficial(petId);
                deleteDialog.dismiss();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog.dismiss();
            }
        });

        deleteDialog.show();
    }


    private void openConfirmAdoptDialog(String petId) {
        adoptDialog.setContentView(R.layout.adopt_confirmation_dialog);
        adoptDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button btnAdotarOk, btnAdotarCancelar;
        btnAdotarOk = adoptDialog.findViewById(R.id.btnConfirmarAdopt);
        btnAdotarCancelar = adoptDialog.findViewById(R.id.btnCancelarAdopt);

        btnAdotarOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adotarRealOficial(petId);
                adoptDialog.dismiss();
            }
        });

        btnAdotarCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adoptDialog.dismiss();
            }
        });

        adoptDialog.show();
    }

    private void deletarRealOficial(String petId) {
        databaseReference.child(petId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getContext(), "Pet apagado com sucesso", Toast.LENGTH_SHORT).show();
                Fragment fragmentMyPets =  new MyPetsFragment();
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.fragment_container, fragmentMyPets)
                        .addToBackStack(fragmentMyPets.getClass().getSimpleName())
                        .commit();
            }

        });
    }

    private void adotarRealOficial(String petId) {

        databaseReference.child(petId).child("isAdotado").setValue("true").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "Ficamos feliz que seu pet foi adotado !", Toast.LENGTH_SHORT).show();
                Fragment fragmentMyPets =  new MyPetsFragment();
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.fragment_container, fragmentMyPets)
                        .addToBackStack(fragmentMyPets.getClass().getSimpleName())
                        .commit();
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    public void switchFragment(Fragment fragment){
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }

    private void getAllPets() {
        petArrayList.clear();

        Query query = databaseReference.orderByChild("userId").equalTo(user.getUid());
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //artificio documentado para evitar que mensagem de sem pets aparece rapidamente ao dar load na pagina
                counter = 1;
                if(snapshot.getValue(Pet.class).getIsAdotado().equals("false")){
                    Log.e("PET", "pet nao adotado-> ");
                    petArrayList.add(snapshot.getValue(Pet.class));
                    myPetsRVAdapter.notifyDataSetChanged();
                }

                if(petArrayList.size() == 0){
                    imagemSemPet.setVisibility(View.VISIBLE);
                    mensagemSemPet.setVisibility(View.VISIBLE);

                }
                else{
                    imagemSemPet.setVisibility(View.INVISIBLE);
                    mensagemSemPet.setVisibility(View.INVISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                counter = 1;
                myPetsRVAdapter.notifyDataSetChanged();

                if(petArrayList.size() == 0){
                    imagemSemPet.setVisibility(View.VISIBLE);
                    mensagemSemPet.setVisibility(View.VISIBLE);

                }
                else{
                    imagemSemPet.setVisibility(View.INVISIBLE);
                    mensagemSemPet.setVisibility(View.INVISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                counter = 1;
                myPetsRVAdapter.notifyDataSetChanged();

                if(petArrayList.size() == 0){
                    imagemSemPet.setVisibility(View.VISIBLE);
                    mensagemSemPet.setVisibility(View.VISIBLE);

                }
                else{
                    imagemSemPet.setVisibility(View.INVISIBLE);
                    mensagemSemPet.setVisibility(View.INVISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                counter = 1;
                myPetsRVAdapter.notifyDataSetChanged();

                if(petArrayList.size() == 0){
                    imagemSemPet.setVisibility(View.VISIBLE);
                    mensagemSemPet.setVisibility(View.VISIBLE);

                }
                else{
                    imagemSemPet.setVisibility(View.INVISIBLE);
                    mensagemSemPet.setVisibility(View.INVISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                counter = 1;
                myPetsRVAdapter.notifyDataSetChanged();

                if(petArrayList.size() == 0){
                    imagemSemPet.setVisibility(View.VISIBLE);
                    mensagemSemPet.setVisibility(View.VISIBLE);

                }
                else{
                    imagemSemPet.setVisibility(View.INVISIBLE);
                    mensagemSemPet.setVisibility(View.INVISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if(petArrayList.size() == 0){
                    imagemSemPet.setVisibility(View.VISIBLE);
                    mensagemSemPet.setVisibility(View.VISIBLE);

                }
                else{
                    imagemSemPet.setVisibility(View.INVISIBLE);
                    mensagemSemPet.setVisibility(View.INVISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }
        }, 1000);


    }

}