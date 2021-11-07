package com.paulo.miaudota;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.paulo.miaudota.Models.Pet;

import java.util.ArrayList;

public class MyPetsRVAdapter extends RecyclerView.Adapter<MyPetsRVAdapter.ViewHolder> {

    private ArrayList<Pet> petArrayList;
    private Context context;
    int lastPos = -1;
    private PetClickInterface petClickInterface;
    private PetClickDeleteInterface petClickDeleteInterface;

    public MyPetsRVAdapter(ArrayList<Pet> petArrayList, Context context, PetClickInterface petClickInterface, PetClickDeleteInterface petClickDeleteInterface) {
        this.petArrayList = petArrayList;
        this.context = context;
        this.petClickInterface = petClickInterface;
        this.petClickDeleteInterface = petClickDeleteInterface;
    }

    @NonNull
    @Override
    public MyPetsRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_pets_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPetsRVAdapter.ViewHolder holder, int position) {
        Pet petRvModel = petArrayList.get(holder.getAdapterPosition());
        holder.petNameTV.setText(petRvModel.getPetName());
        holder.petLocalTv.setText(petRvModel.getCidadePet() + "/" + petRvModel.getUfPet());
        Glide.with(context)
                .load(petRvModel.getPetImg()) // image url
                .placeholder(R.drawable.profile_placeholder) // any placeholder to load at start
                .error(R.drawable.profile_placeholder)  // any image in case of error
                .override(200, 200) // resizing
                .centerCrop()
                .into(holder.petImage);  // imageview object

        holder.petRegistrationDate.setText(petRvModel.getDataCadastro());
        holder.petId.setText(petRvModel.getPetId());

        holder.btnEditMyPets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                petClickInterface.onPetClick(holder.getAdapterPosition());
            }
        });

        holder.btnDeletePet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                petClickDeleteInterface.onPetClickDelete(holder.getAdapterPosition());
            }
        });


        setAnimation(holder.itemView, holder.getAdapterPosition());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                petClickInterface.onPetClick(holder.getAdapterPosition());
            }
        });

    }

    private void setAnimation(View itemView, int postion){
        if(postion > lastPos){
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            itemView.setAnimation(animation);
            lastPos = postion;
        }
    }

    @Override
    public int getItemCount() {
        return petArrayList.size();
    }

    public interface PetClickInterface{
        void onPetClick(int position);
    }

    public interface PetClickDeleteInterface{
        void onPetClickDelete(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView petNameTV, petLocalTv, petRegistrationDate, petId, petPostion;
        private ImageView petImage;
        private Button btnEditMyPets, btnDeletePet;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            petNameTV = itemView.findViewById(R.id.nomePetMyPets);
            petLocalTv = itemView.findViewById(R.id.cidadeUfMyPets);
            petImage = itemView.findViewById(R.id.imagemPetMyPets);
            petRegistrationDate = itemView.findViewById(R.id.dataCadastroMyPets);
            petId = itemView.findViewById(R.id.idMyPets);
            btnEditMyPets = itemView.findViewById(R.id.btnEditarMyPet);
            btnDeletePet = itemView.findViewById(R.id.btnExcluirPet);
        }
    }

}
