package com.paulo.miaudota;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.paulo.miaudota.Controllers.EditProfile;
import com.paulo.miaudota.Models.Pet;

import java.util.ArrayList;

public class PetRVAdapter extends RecyclerView.Adapter<PetRVAdapter.ViewHolder> {

    private ArrayList<Pet> petArrayList;
    private Context context;
    int lastPos = -1;
    private PetClickInterface petClickInterface;

    public PetRVAdapter(ArrayList<Pet> petArrayList, Context context, PetClickInterface petClickInterface) {
        this.petArrayList = petArrayList;
        this.context = context;
        this.petClickInterface = petClickInterface;
    }

    @NonNull
    @Override
    public PetRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pet_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetRVAdapter.ViewHolder holder, int position) {
        Pet petRvModel = petArrayList.get(holder.getAdapterPosition());
        holder.petNameTV.setText(petRvModel.getPetName());
        holder.petLocalTv.setText(petRvModel.getCidadePet() + "/" + petRvModel.getUfPet());

        if(petRvModel.getGeneroPet().equals("Masculino")){
            holder.petGender.setImageResource(R.drawable.male);
        }
        else{
            holder.petGender.setImageResource(R.drawable.female);
        }

        Glide.with(context)
                .load(petRvModel.getPetImg()) // image url
                .placeholder(R.drawable.profile_placeholder) // any placeholder to load at start
                .error(R.drawable.profile_placeholder)  // any image in case of error
                .override(200, 200) // resizing
                .centerCrop()
                .into(holder.petImage);  // imageview object
        if(petRvModel.getIdadeAnosPet().equals("1")){
            holder.petAge.setText(petRvModel.getIdadeAnosPet() + " ano");
        }
        else{
            holder.petAge.setText(petRvModel.getIdadeAnosPet() + " anos");
        }

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

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView petNameTV, petLocalTv, petAge;
        private ImageView petImage, petGender;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            petNameTV = itemView.findViewById(R.id.nomePetHome);
            petLocalTv = itemView.findViewById(R.id.cidadeUfHome);
            petImage = itemView.findViewById(R.id.imagemPetHome);
            petGender = itemView.findViewById(R.id.generoHome);
            petAge = itemView.findViewById(R.id.idadeAnosHome);

        }
    }

}
