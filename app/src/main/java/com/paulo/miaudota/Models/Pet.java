package com.paulo.miaudota.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Pet implements Parcelable {

    private String petImg;
    private String petName;
    private String tipoPet;
    private String idadeAnosPet;
    private String idadeMesesPet;
    private String generoPet;
    private String tamanhoPet;
    private String ufPet;
    private String cidadePet;
    private String descricaoPet;
    private String petId;
    private String dataCadastro;
    private String userId;
    private String ddd;
    private String numCelular;

    public Pet(){}

    public Pet(String petImg,String petName, String tipoPet, String idadeAnosPet, String idadeMesesPet, String generoPet, String tamanhoPet, String ufPet, String cidadePet,
               String descricaoPet, String petId, String dataCadastro, String userId, String ddd, String numCelular) {
        this.petImg = petImg;
        this.petName = petName;
        this.tipoPet = tipoPet;
        this.idadeAnosPet = idadeAnosPet;
        this.idadeMesesPet = idadeMesesPet;
        this.generoPet = generoPet;
        this.tamanhoPet = tamanhoPet;
        this.ufPet = ufPet;
        this.cidadePet = cidadePet;
        this.descricaoPet = descricaoPet;
        this.petId = petId;
        this.dataCadastro = dataCadastro;
        this.userId = userId;
        this.ddd = ddd;
        this.numCelular = numCelular;
    }

    protected Pet(Parcel in) {
        petImg = in.readString();
        petName = in.readString();
        tipoPet = in.readString();
        idadeAnosPet = in.readString();
        idadeMesesPet = in.readString();
        generoPet = in.readString();
        tamanhoPet = in.readString();
        ufPet = in.readString();
        cidadePet = in.readString();
        descricaoPet = in.readString();
        petId = in.readString();
        dataCadastro = in.readString();
        userId = in.readString();
        ddd = in.readString();
        numCelular = in.readString();
    }

    public static final Creator<Pet> CREATOR = new Creator<Pet>() {
        @Override
        public Pet createFromParcel(Parcel in) {
            return new Pet(in);
        }

        @Override
        public Pet[] newArray(int size) {
            return new Pet[size];
        }
    };

    public String getPetImg() {
        return petImg;
    }

    public void setPetImg(String petImg) {
        this.petImg = petImg;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getTipoPet() {
        return tipoPet;
    }

    public void setTipoPet(String tipoPet) {
        this.tipoPet = tipoPet;
    }

    public String getIdadeAnosPet() {
        return idadeAnosPet;
    }

    public void setIdadeAnosPet(String idadeAnosPet) {
        this.idadeAnosPet = idadeAnosPet;
    }

    public String getIdadeMesesPet() {
        return idadeMesesPet;
    }

    public void setIdadeMesesPet(String idadeMesesPet) {
        this.idadeMesesPet = idadeMesesPet;
    }

    public String getGeneroPet() {
        return generoPet;
    }

    public void setGeneroPet(String generoPet) {
        this.generoPet = generoPet;
    }

    public String getTamanhoPet() {
        return tamanhoPet;
    }

    public void setTamanhoPet(String tamanhoPet) {
        this.tamanhoPet = tamanhoPet;
    }

    public String getUfPet() {
        return ufPet;
    }

    public void setUfPet(String ufPet) {
        this.ufPet = ufPet;
    }

    public String getCidadePet() {
        return cidadePet;
    }

    public void setCidadePet(String cidadePet) {
        this.cidadePet = cidadePet;
    }

    public String getDescricaoPet() {
        return descricaoPet;
    }

    public void setDescricaoPet(String descricaoPet) {
        this.descricaoPet = descricaoPet;
    }

    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    public String getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(String dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDdd() {
        return ddd;
    }

    public void setDdd(String ddd) {
        this.ddd = ddd;
    }

    public String getNumCelular() {
        return numCelular;
    }

    public void setNumCelular(String numCelular) {
        this.numCelular = numCelular;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(petImg);
        parcel.writeString(petName);
        parcel.writeString(tipoPet);
        parcel.writeString(idadeAnosPet);
        parcel.writeString(idadeMesesPet);
        parcel.writeString(generoPet);
        parcel.writeString(tamanhoPet);
        parcel.writeString(ufPet);
        parcel.writeString(cidadePet);
        parcel.writeString(descricaoPet);
        parcel.writeString(petId);
        parcel.writeString(ddd);
        parcel.writeString(numCelular);
    }
}
