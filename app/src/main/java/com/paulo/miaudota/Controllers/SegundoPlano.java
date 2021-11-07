package com.paulo.miaudota.Controllers;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class SegundoPlano extends AsyncTask<String, Void, String> {

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(String... params) {

        if(params[0].equals("estado")){
            StringBuilder respostaEstados = new StringBuilder();
            try {
                URL urlEstados  = new URL("https://servicodados.ibge.gov.br/api/v1/localidades/estados/?orderBy=nome");
                HttpURLConnection conexao = (HttpURLConnection) urlEstados.openConnection();
                conexao.setRequestMethod("GET");
                conexao.setRequestProperty("content-type", "application/json");
                conexao.setDoOutput(true);
                conexao.setConnectTimeout(5000);
                conexao.connect();

                Scanner scanner = new Scanner(urlEstados.openStream());
                while (scanner.hasNext()){
                    respostaEstados.append(scanner.next());
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return respostaEstados.toString();
        }
        else if(params[0].equals("cidade")){

            StringBuilder respostaCidades = new StringBuilder();
            try {
                URL urlCidades  = new URL("https://servicodados.ibge.gov.br/api/v1/localidades/estados/" + params[1] + "/municipios");
                HttpURLConnection conexao = (HttpURLConnection) urlCidades.openConnection();
                conexao.setRequestMethod("GET");
                conexao.setRequestProperty("content-type", "application/json");
                conexao.setDoOutput(true);
                conexao.setConnectTimeout(5000);
                conexao.connect();

                Scanner scanner = new Scanner(urlCidades.openStream());
                while (scanner.hasNext()){
                    respostaCidades.append(scanner.next());
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return respostaCidades.toString();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String dados) {
        if(dados != null){
            Log.d("onPost", dados);
        }
        else {

        }
    }
}
