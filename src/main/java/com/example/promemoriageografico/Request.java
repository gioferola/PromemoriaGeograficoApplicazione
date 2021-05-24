/*
 *  Classe per fare tutte le richieste al server
 *  Autore: Ferola giovanni
 *
 *   indice:
 *       -costruttore;
 *       -doInBackground;
 */
package com.example.promemoriageografico;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.promemoriageografico.Home;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Request extends AsyncTask<String , Void ,String> {
    private HttpURLConnection urlConnection;
    private int timeout;
    private String strurl;
    private static int DEFAULT_TIMEOUT = 6000;

    public Request(){
        this.timeout = DEFAULT_TIMEOUT;
    }

    /*
    * funzione che stabilisce una connesisone con il server e invia i parametri
    * poi attende una risposta
     */
    @Override
    protected String doInBackground(String... strings){
        try {
            strurl = strings[0];
            URL url = new URL(strurl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(timeout);
            urlConnection.setReadTimeout(timeout);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder();
            String query = null;
            //controlla che richiesta deve girare al server
            if(strings[1].equals("login")){
                builder.appendQueryParameter("email", strings[2])
                        .appendQueryParameter("password", strings[3]);
                query = builder.build().getEncodedQuery();
            } else if(strings[1].equals("singUp")) {
                builder.appendQueryParameter("nome",strings[2])
                        .appendQueryParameter("cognome",strings[3])
                        .appendQueryParameter("email", strings[4])
                        .appendQueryParameter("password", strings[5]);
                query = builder.build().getEncodedQuery();
            } else if(strings[1].equals("selectluoghi")) {
                builder.appendQueryParameter("email",strings[2])
                        .appendQueryParameter("password", strings[3]);
                query = builder.build().getEncodedQuery();
            } else if(strings[1].equals("insertLuogo")) {
                builder.appendQueryParameter("nome",strings[2])
                        .appendQueryParameter("descrizione",strings[3])
                        .appendQueryParameter("email", strings[4])
                        .appendQueryParameter("latitudine", strings[5])
                        .appendQueryParameter("longitudine", strings[6])
                        .appendQueryParameter("raggioNotifica", strings[7])
                        .appendQueryParameter("password", strings[8]);
                query = builder.build().getEncodedQuery();
            } else if(strings[1].equals("deleteLuogo")){
                builder.appendQueryParameter("email",strings[2])
                        .appendQueryParameter("id",strings[3])
                        .appendQueryParameter("password", strings[4]);
                query = builder.build().getEncodedQuery();
            } else if(strings[1].equals("deleteCosaDafare")){
                builder.appendQueryParameter("idLuogo",strings[2])
                        .appendQueryParameter("id",strings[3])
                        .appendQueryParameter("email", strings[4])
                        .appendQueryParameter("password", strings[5]);
                query = builder.build().getEncodedQuery();
            } else if(strings[1].equals("modifyCosaDafare")){
                builder.appendQueryParameter("idLuogo",strings[2])
                        .appendQueryParameter("id",strings[3])
                        .appendQueryParameter("fatto",strings[4])
                        .appendQueryParameter("email", strings[5])
                        .appendQueryParameter("password", strings[6]);
                query = builder.build().getEncodedQuery();
            } else if(strings[1].equals("insertCosaDaFare")){
                builder.appendQueryParameter("nome",strings[2])
                        .appendQueryParameter("descrizione",strings[3])
                        .appendQueryParameter("idLuogo",strings[4])
                        .appendQueryParameter("email", strings[5])
                        .appendQueryParameter("password", strings[6]);
                query = builder.build().getEncodedQuery();
            }else if(strings[1].equals("selectNominativo")) {
                builder.appendQueryParameter("email", strings[2])
                        .appendQueryParameter("password", strings[3]);
                query = builder.build().getEncodedQuery();
            } else if(strings[1].equals("cambiaInfo")) {
                builder.appendQueryParameter("nome", strings[2])
                .appendQueryParameter("cognome", strings[3])
                .appendQueryParameter("email", strings[4])
                        .appendQueryParameter("password", strings[5]);
                query = builder.build().getEncodedQuery();
            } else if(strings[1].equals("cambiaPassword")) {
                builder.appendQueryParameter("vecchia", strings[2])
                        .appendQueryParameter("nuova", strings[3])
                        .appendQueryParameter("email", strings[4]);
                query = builder.build().getEncodedQuery();
            } else {
                builder.appendQueryParameter("id",strings[2])
                    .appendQueryParameter("email", strings[3])
                    .appendQueryParameter("password", strings[4]);
                query = builder.build().getEncodedQuery();
            }

            //gira la richiesta al server
            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();

            urlConnection.connect();

            int responseCode = urlConnection.getResponseCode();
            if(responseCode!=HttpURLConnection.HTTP_OK)
                return "error";

            InputStream response = urlConnection.getInputStream();
            Scanner scanner = new Scanner(response);

            String responseBody = scanner.useDelimiter("\\A").next();
            //ritorna la risposta del server
            return responseBody;

        } catch (IOException e) {
            e.printStackTrace();
            return "exception";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        }
    }