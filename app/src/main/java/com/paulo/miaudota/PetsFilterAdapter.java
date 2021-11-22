package com.paulo.miaudota;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.paulo.miaudota.Controllers.HomeFragment;
import com.paulo.miaudota.Controllers.WelcomeScreen;

import java.util.ArrayList;
import java.util.Random;

public class PetsFilterAdapter extends RecyclerView.Adapter<PetsFilterAdapter.ViewHolder> {

    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<Integer> mImageUrls = new ArrayList<>();
    private Context mContext;
    private Random random = new Random();
    private int row_index;
    private int drawableDaVez;
    private PetFilterClickInterface petFilterClickInterface;


    public PetsFilterAdapter(Context mContext, ArrayList<String> mNames, ArrayList<Integer> mImageUrls, PetFilterClickInterface petFilterClickInterface) {
        this.mNames = mNames;
        this.mImageUrls = mImageUrls;
        this.mContext = mContext;
        this.petFilterClickInterface = petFilterClickInterface;
    }

    public interface PetFilterClickInterface{
        void onPetFilterClick(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pets_filter_rv_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.imageFilter.setImageResource(mImageUrls.get(position));
        holder.nomeFilter.setText(mNames.get(position));
        holder.imageFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                row_index = holder.getAdapterPosition();
                notifyDataSetChanged();
                petFilterClickInterface.onPetFilterClick(row_index);
            }
        });

        if(row_index == position){
            switch (position){
                case 0:
                    drawableDaVez = R.drawable.filter_bg_all;
                    break;
                case 1:
                    drawableDaVez = R.drawable.filter_bg_dog;
                    break;
                case 2:
                    drawableDaVez = R.drawable.filter_bg_cat;
                    break;
                case 3:
                    drawableDaVez = R.drawable.filter_bg_bird;
                    break;
                case 4:
                    drawableDaVez = R.drawable.filter_bg_roedores;
                    break;
                case 5:
                    drawableDaVez = R.drawable.filter_bg_outros;
                    break;
            }
            holder.constraintLayout.setBackground(AppCompatResources.getDrawable(mContext, drawableDaVez));
            holder.imageFilter.setColorFilter(Color.argb(255, 255, 255, 255));
        }
        else
        {
            switch (holder.getAdapterPosition()){
                case 0:
                    holder.imageFilter.setColorFilter(mContext.getColor(R.color.bluecachorro));
                    break;
                case 1:
                    holder.imageFilter.setColorFilter(mContext.getColor(R.color.dogRed));
                    break;
                case 2:
                    holder.imageFilter.setColorFilter(mContext.getColor(R.color.catGreen));
                    break;
                case 3:
                    holder.imageFilter.setColorFilter(mContext.getColor(R.color.birdPurple));
                    break;
                case 4:
                    holder.imageFilter.setColorFilter(mContext.getColor(R.color.hamsterPink));
                    break;
                case 5:
                    holder.imageFilter.setColorFilter(mContext.getColor(R.color.outrosYellow));
                    break;
                default:
            }
            holder.constraintLayout.setBackground(AppCompatResources.getDrawable(mContext, R.drawable.filter_bg_trans));
        }

    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView nomeFilter;
        ImageView imageFilter;
        ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nomeFilter = itemView.findViewById(R.id.nomePetFilter);
            imageFilter = itemView.findViewById(R.id.imagemPetFilter);
            constraintLayout = itemView.findViewById(R.id.ctFilter);

        }
    }
}
