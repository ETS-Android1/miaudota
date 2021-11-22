package com.paulo.miaudota.Controllers;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.StorageReference;
import com.paulo.miaudota.Models.Pet;
import com.paulo.miaudota.PetRVAdapter;
import com.paulo.miaudota.PetsFilterAdapter;
import com.paulo.miaudota.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class HomeFragment extends Fragment implements PetRVAdapter.PetClickInterface, PetsFilterAdapter.PetFilterClickInterface {

    private RecyclerView petRV, filterRv;
    private ProgressBar progressBar;
    private ArrayList<Pet> petArrayList;
    private PetRVAdapter petRVAdapter;
    private PetsFilterAdapter petsFilterAdapter;
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<Integer> mImageUrls = new ArrayList<>();
    private String petId;
    private TextView mensagemSemPet;
    private ImageView imagemSemPet;
    SwipeRefreshLayout swipeRefreshLayout;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("Warning_Activity","onCreateView -> HomeFragment");

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user == null) {
            Log.e("Warning_Activity","onCreateView HomeFragment userNull-> ");
            startActivity(new Intent(getActivity(), WelcomeScreen.class));
        }

        petRV = view.findViewById(R.id.idRvPets);
        petArrayList = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Pets");
        petRVAdapter = new PetRVAdapter(petArrayList,getContext(),this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        petRV.setLayoutManager(gridLayoutManager);
        petRV.setAdapter(petRVAdapter);
        progressBar = view.findViewById(R.id.progressBarHome);
        progressBar.setVisibility(View.VISIBLE);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshHome);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                petRVAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        mensagemSemPet = view.findViewById(R.id.mensagemSemPet);
        imagemSemPet = view.findViewById(R.id.imagemSemPet);

        getAllPets();
        getImages(view);

        return view;
    }

    @Override
    public void onPetClick(int position) {
        //passar position ou id pra usar no select dos detalhes
        Intent intent = new Intent(getActivity(),PetDetails.class);
        Pet petModel = petArrayList.get(position);
        petId = petModel.getPetId();
        intent.putExtra("petId", petId);
        startActivity(intent);
    }

    @Override
    public void onPetFilterClick(int position) {
        Log.d("Home-filterClick", "position: " + position);
        switch (position){
            case 0:
                getAllPets();
                break;
            case 1:
                getFiltered("Cachorro(a)");
                break;
            case 2:
                getFiltered("Gato(a)");
                break;
            case 3:
                getFiltered("Aves");
                break;
            case 4:
                getFiltered("Roedores");
                break;
            case 5:
                getFiltered("Outros");
                break;
            default:
                getAllPets();
                break;
        }
    }

    private void getAllPets() {
        petArrayList.clear();

        Query query = databaseReference.orderByChild("dataCadastro");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                progressBar.setVisibility(View.GONE);
                if(snapshot.getValue(Pet.class).getIsAdotado().equals("false")){
                    petArrayList.add(snapshot.getValue(Pet.class));
                    petRVAdapter.notifyDataSetChanged();
                }

                if(petArrayList.size() == 0){
                    progressBar.setVisibility(View.GONE);
                    imagemSemPet.setVisibility(View.VISIBLE);
                    mensagemSemPet.setVisibility(View.VISIBLE);
                }
                else{
                    progressBar.setVisibility(View.GONE);
                    imagemSemPet.setVisibility(View.INVISIBLE);
                    mensagemSemPet.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                progressBar.setVisibility(View.GONE);
                if(snapshot.getValue(Pet.class).getIsAdotado().equals("false")){
                    petRVAdapter.notifyDataSetChanged();
                }

                if(petArrayList.size() == 0){
                    progressBar.setVisibility(View.GONE);
                    imagemSemPet.setVisibility(View.VISIBLE);
                    mensagemSemPet.setVisibility(View.VISIBLE);
                }
                else{
                    progressBar.setVisibility(View.GONE);
                    imagemSemPet.setVisibility(View.INVISIBLE);
                    mensagemSemPet.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                if(snapshot.getValue(Pet.class).getIsAdotado().equals("false")){
                    petRVAdapter.notifyDataSetChanged();
                }
                if(petArrayList.size() == 0){
                    progressBar.setVisibility(View.GONE);
                    imagemSemPet.setVisibility(View.VISIBLE);
                    mensagemSemPet.setVisibility(View.VISIBLE);
                }
                else{
                    progressBar.setVisibility(View.GONE);
                    imagemSemPet.setVisibility(View.INVISIBLE);
                    mensagemSemPet.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                progressBar.setVisibility(View.GONE);
                if(snapshot.getValue(Pet.class).getIsAdotado().equals("false")){
                    petRVAdapter.notifyDataSetChanged();
                }
                if(petArrayList.size() == 0){
                    progressBar.setVisibility(View.GONE);
                    imagemSemPet.setVisibility(View.VISIBLE);
                    mensagemSemPet.setVisibility(View.VISIBLE);
                }
                else{
                    progressBar.setVisibility(View.GONE);
                    imagemSemPet.setVisibility(View.INVISIBLE);
                    mensagemSemPet.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getFiltered(String tipo) {
        petArrayList.clear();
        progressBar.setVisibility(View.VISIBLE);

        Query query = databaseReference.orderByChild("tipoPet").equalTo(tipo);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                progressBar.setVisibility(View.GONE);
                if(snapshot.getValue(Pet.class).getIsAdotado().equals("false")){
                    petArrayList.add(snapshot.getValue(Pet.class));
                    petRVAdapter.notifyDataSetChanged();
                }

                if(petArrayList.size() == 0){
                    progressBar.setVisibility(View.GONE);
                    imagemSemPet.setVisibility(View.VISIBLE);
                    mensagemSemPet.setVisibility(View.VISIBLE);
                }
                else{
                    progressBar.setVisibility(View.GONE);
                    imagemSemPet.setVisibility(View.INVISIBLE);
                    mensagemSemPet.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                progressBar.setVisibility(View.GONE);
                if(snapshot.getValue(Pet.class).getIsAdotado().equals("false")){
                    petRVAdapter.notifyDataSetChanged();
                }

                if(petArrayList.size() == 0){
                    progressBar.setVisibility(View.GONE);
                    imagemSemPet.setVisibility(View.VISIBLE);
                    mensagemSemPet.setVisibility(View.VISIBLE);
                }
                else{
                    progressBar.setVisibility(View.GONE);
                    imagemSemPet.setVisibility(View.INVISIBLE);
                    mensagemSemPet.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                if(snapshot.getValue(Pet.class).getIsAdotado().equals("false")){
                    petRVAdapter.notifyDataSetChanged();
                }
                if(petArrayList.size() == 0){
                    progressBar.setVisibility(View.GONE);
                    imagemSemPet.setVisibility(View.VISIBLE);
                    mensagemSemPet.setVisibility(View.VISIBLE);
                }
                else{
                    progressBar.setVisibility(View.GONE);
                    imagemSemPet.setVisibility(View.INVISIBLE);
                    mensagemSemPet.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                progressBar.setVisibility(View.GONE);
                if(snapshot.getValue(Pet.class).getIsAdotado().equals("false")){
                    petRVAdapter.notifyDataSetChanged();
                }
                if(petArrayList.size() == 0){
                    progressBar.setVisibility(View.GONE);
                    imagemSemPet.setVisibility(View.VISIBLE);
                    mensagemSemPet.setVisibility(View.VISIBLE);
                }
                else{
                    progressBar.setVisibility(View.GONE);
                    imagemSemPet.setVisibility(View.INVISIBLE);
                    mensagemSemPet.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if(petArrayList.size() == 0){
                    progressBar.setVisibility(View.GONE);
                    imagemSemPet.setVisibility(View.VISIBLE);
                    mensagemSemPet.setVisibility(View.VISIBLE);
                }
                else{
                    progressBar.setVisibility(View.GONE);
                    imagemSemPet.setVisibility(View.INVISIBLE);
                    mensagemSemPet.setVisibility(View.INVISIBLE);
                }
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if(petArrayList.size() == 0){
                    petRVAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    imagemSemPet.setVisibility(View.VISIBLE);
                    mensagemSemPet.setVisibility(View.VISIBLE);
                }
                else{
                    progressBar.setVisibility(View.GONE);
                    imagemSemPet.setVisibility(View.INVISIBLE);
                    mensagemSemPet.setVisibility(View.INVISIBLE);
                }
            }
        }, 1000);

        petRVAdapter.notifyDataSetChanged();

    }

    private void getImages(View view){
        Log.d("Home-GetImages", "initImageBitmaps: preparing bitmaps.");

        mImageUrls.add(R.drawable.ic_all);
        mNames.add("Todos");

        mImageUrls.add(R.drawable.ic_dogsvg);
        mNames.add("Cachorros");

        mImageUrls.add(R.drawable.ic_cat);
        mNames.add("Gatos");

        mImageUrls.add(R.drawable.ic_bird);
        mNames.add("Aves");

        mImageUrls.add(R.drawable.ic_hamster);
        mNames.add("Roedores");

        mImageUrls.add(R.drawable.ic_otherssvg);
        mNames.add("Outros");

        initRecyclerView(view);

    }

    private void initRecyclerView(View v){
        Log.d("HomeFragment", "initRecyclerView: init recyclerview");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        filterRv = v.findViewById(R.id.idRvFilterPets);
        filterRv.setLayoutManager(layoutManager);
        petsFilterAdapter = new PetsFilterAdapter(getContext(), mNames, mImageUrls,this);
        filterRv.setAdapter(petsFilterAdapter);
    }

}
