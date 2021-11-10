package com.paulo.miaudota.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Candidatura implements Parcelable {

    private String IdPet;
    private String IdUsuario;
    private String EmailUsuario;
    private String NomeUsuario;
    private String CelularUsuario;
    private String CidadeUsuario;
    private String DataCandidatura;
    private String ProfilePicStr;

    public Candidatura(){}

    public Candidatura(String idPet, String idUsuario, String emailUsuario, String nomeUsuario,String celularUsuario, String cidadeUsuario ,String dataCandidatura, String profilePicStr){
        this.IdPet = idPet;
        this.IdUsuario = idUsuario;
        this.EmailUsuario = emailUsuario;
        this.NomeUsuario = nomeUsuario;
        this.CelularUsuario = celularUsuario;
        this.CidadeUsuario = cidadeUsuario;
        this.DataCandidatura = dataCandidatura;
        this.ProfilePicStr = profilePicStr;
    }

    protected Candidatura(Parcel in){
        IdPet = in.readString();
        IdUsuario = in.readString();
        EmailUsuario = in.readString();
        NomeUsuario = in.readString();
        CelularUsuario = in.readString();
        CidadeUsuario = in.readString();
        DataCandidatura = in.readString();
        ProfilePicStr = in.readString();
    }

    public static final Creator<Candidatura> CREATOR = new Creator<Candidatura>() {
        @Override
        public Candidatura createFromParcel(Parcel in) {
            return new Candidatura(in);
        }

        @Override
        public Candidatura[] newArray(int size) {
            return new Candidatura[size];
        }
    };

    public String getIdPet() {
        return IdPet;
    }

    public void setIdPet(String idPet) {
        IdPet = idPet;
    }

    public String getIdUsuario() {
        return IdUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        IdUsuario = idUsuario;
    }

    public String getEmailUsuario() {
        return EmailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        EmailUsuario = emailUsuario;
    }

    public String getNomeUsuario() {
        return NomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        NomeUsuario = nomeUsuario;
    }

    public String getCelularUsuario() {
        return CelularUsuario;
    }

    public void setCelularUsuario(String celularUsuario) {
        CelularUsuario = celularUsuario;
    }

    public String getDataCandidatura() {
        return DataCandidatura;
    }

    public void setDataCandidatura(String dataCandidatura) {
        DataCandidatura = dataCandidatura;
    }

    public String getCidadeUsuario() {
        return CidadeUsuario;
    }

    public void setCidadeUsuario(String cidadeUsuario) {
        CidadeUsuario = cidadeUsuario;
    }

    public String getProfilePicStr() {
        return ProfilePicStr;
    }

    public void setProfilePicStr(String profilePicStr) {
        ProfilePicStr = profilePicStr;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(IdPet);
        parcel.writeString(IdUsuario);
        parcel.writeString(EmailUsuario);
        parcel.writeString(NomeUsuario);
        parcel.writeString(CelularUsuario);
        parcel.writeString(CidadeUsuario);
        parcel.writeString(DataCandidatura);
        parcel.writeString(ProfilePicStr);
    }
}
