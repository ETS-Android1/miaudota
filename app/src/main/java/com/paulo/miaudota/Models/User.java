package com.paulo.miaudota.Models;

import java.util.Date;

public class User {

    public String NomeCompleto, Cpf, Email,Cidade,UF,DataNascimento, Ddd, NumCelular;

    public User(){

    }

    public User(String nomeCompleto, String email){
        this.NomeCompleto = nomeCompleto;
        this.Email = email;
    }

    public User(String nomeCompleto, String cpf, String email,String cidade, String uf, String dataNascimento, String ddd, String numCelular){
        this.NomeCompleto = nomeCompleto;
        this.Cpf = cpf;
        this.Email = email;
        this.Cidade = cidade;
        this.UF = uf;
        this.DataNascimento = dataNascimento;
        this.Ddd = ddd;
        this.NumCelular = numCelular;
    }

}
