package com.paulo.miaudota;

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
import com.paulo.miaudota.Controllers.Candidaturas;
import com.paulo.miaudota.Models.Candidatura;
import com.paulo.miaudota.Models.Pet;

import java.util.ArrayList;
import java.util.Random;

public class CandidaturaRVAdapter extends RecyclerView.Adapter<CandidaturaRVAdapter.ViewHolder> {

    private ArrayList<Candidatura> candidaturasArrayList;
    private Context context;
    int lastPos = -1;
    private CandidaturaClickInterface candidaturaClickInterface;
    private Random random = new Random();

    public CandidaturaRVAdapter(ArrayList<Candidatura> candidaturasArrayList, Context context, CandidaturaClickInterface candidaturaClickInterface) {
        this.candidaturasArrayList = candidaturasArrayList;
        this.context = context;
        this.candidaturaClickInterface = candidaturaClickInterface;
    }

    @NonNull
    @Override
    public CandidaturaRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.candidaturas_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CandidaturaRVAdapter.ViewHolder holder, int position) {
        Candidatura candidaturaRvModel = candidaturasArrayList.get(holder.getAdapterPosition());

        holder.nomeCandidatoTv.setText(candidaturaRvModel.getNomeUsuario());
        holder.emailCandidatotv.setText(candidaturaRvModel.getEmailUsuario());
        holder.cidadeCandidatoTv.setText(candidaturaRvModel.getCidadeUsuario());

        Glide.with(context)
                .load(candidaturaRvModel.getProfilePicStr()) // image url
                .placeholder(R.drawable.profile_placeholder) // any placeholder to load at start
                .error(R.drawable.profile_placeholder)  // any image in case of error
                .override(200, 200) // resizing
                .centerCrop()
                .into(holder.CandidatoImage);  // imageview object

        holder.dataCandidaturaTv.setText(candidaturaRvModel.getDataCandidatura());

        holder.btnZapCandidato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                candidaturaClickInterface.onCandidaturaClick(holder.getAdapterPosition());
            }
        });

        setAnimation(holder.itemView, holder.getAdapterPosition());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                candidaturaClickInterface.onCandidaturaClick(holder.getAdapterPosition());
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
        return candidaturasArrayList.size();
    }

    public interface CandidaturaClickInterface{
        void onCandidaturaClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView nomeCandidatoTv, emailCandidatotv, cidadeCandidatoTv,dataCandidaturaTv, idCandidatura;
        private ImageView CandidatoImage;
        private ImageButton btnZapCandidato;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nomeCandidatoTv = itemView.findViewById(R.id.nomeCandidato);
            emailCandidatotv = itemView.findViewById(R.id.emailCandidato);
            cidadeCandidatoTv = itemView.findViewById(R.id.cidadeCandidato);
            dataCandidaturaTv = itemView.findViewById(R.id.dataCandidatura);
            CandidatoImage = itemView.findViewById(R.id.imagemCandidato);
            btnZapCandidato = itemView.findViewById(R.id.btnZapCandidato);
            idCandidatura = itemView.findViewById(R.id.idCandidatura);
        }
    }

}
