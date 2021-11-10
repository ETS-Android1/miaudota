package com.paulo.miaudota.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Candidatura implements Parcelable {

    private String IdPet;
    private String IdUsuario;
    private String EmailUsuario;
    private String NomeUsuario;
    private String CelularUsuario;
    private String DataCandidatura;

    public Candidatura(){}

    public Candidatura(String idPet, String idUsuario, String emailUsuario, String nomeUsuario,String celularUsuario, String dataCandidatura){}

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
