package com.paulo.miaudota.Utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.paulo.miaudota.Models.Pet;
import com.paulo.miaudota.R;

import java.util.ArrayList;
import java.util.Random;

public class MyPetsRVAdapter extends RecyclerView.Adapter<MyPetsRVAdapter.ViewHolder> {

    private ArrayList<Pet> petArrayList;
    private Context context;
    int lastPos = -1;
    private PetClickInterface petClickInterface;
    private PetClickDeleteInterface petClickDeleteInterface;
    private PetClickAdoptInterface petClickAdoptInterface;
    private PetClickEditInterface petClickEditInterface;
    private Random random = new Random();

    public MyPetsRVAdapter(ArrayList<Pet> petArrayList, Context context, PetClickInterface petClickInterface, PetClickDeleteInterface petClickDeleteInterface,
                           PetClickAdoptInterface petClickAdoptInterface, PetClickEditInterface petClickEditInterface) {
        this.petArrayList = petArrayList;
        this.context = context;
        this.petClickInterface = petClickInterface;
        this.petClickDeleteInterface = petClickDeleteInterface;
        this.petClickAdoptInterface = petClickAdoptInterface;
        this.petClickEditInterface = petClickEditInterface;
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

        Drawable rdColorPlaceholder =  context.getDrawable(R.drawable.pet_color_background);
        rdColorPlaceholder.setColorFilter(Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256)), PorterDuff.Mode.SRC_IN);

        Glide.with(context)
                .load(petRvModel.getPetImg()) // image url
                .placeholder(rdColorPlaceholder) // any placeholder to load at start
                .error(rdColorPlaceholder)  // any image in case of error
                .override(200, 200) // resizing
                .centerCrop()
                .into(holder.petImage);  // imageview object

        holder.petRegistrationDate.setText(petRvModel.getDataCadastro());
        holder.petId.setText(petRvModel.getPetId());

        holder.btnEditMyPets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                petClickEditInterface.onPetClickEdit(holder.getAdapterPosition());
            }
        });

        holder.btnDeletePet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                petClickDeleteInterface.onPetClickDelete(holder.getAdapterPosition());
            }
        });


        holder.btnPetAdotado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                petClickAdoptInterface.onPetClickAdopt(holder.getAdapterPosition());
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

    public interface PetClickEditInterface{
        void onPetClickEdit(int position);
    }

    public interface PetClickAdoptInterface{
        void onPetClickAdopt(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView petNameTV, petLocalTv, petRegistrationDate, petId;
        private ImageView petImage;
        private ImageButton btnEditMyPets, btnDeletePet, btnPetAdotado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            petNameTV = itemView.findViewById(R.id.nomePetMyPets);
            petLocalTv = itemView.findViewById(R.id.cidadeUfMyPets);
            petImage = itemView.findViewById(R.id.imagemPetMyPets);
            petRegistrationDate = itemView.findViewById(R.id.dataCadastroMyPets);
            petId = itemView.findViewById(R.id.idMyPets);
            btnEditMyPets = itemView.findViewById(R.id.btnEditarMyPet);
            btnDeletePet = itemView.findViewById(R.id.btnExcluirPet);
            btnPetAdotado = itemView.findViewById(R.id.btnPetAdotadoo);
        }
    }

}
