/*
 *  Classe di LogIn
 *  Autore: Ferola giovanni
 *
 *   indice:
 *       -onCreate;
 *       -to SingUp activity;
 *       -to Home activity;
 *       -controllo del logIn;
 *       -string in chiaro to md5 string;*
 */
package com.example.promemoriageografico;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

public class LogIn extends AppCompatActivity {

    // Variabili per il login automatico
    public static final String SHARED_PREFS = "shared_prefs";
    public static final String EMAIL_KEY = "email_key";
    public static final String PASSWORD_KEY = "password_key";
    SharedPreferences sharedpreferences;

    String strEmail, strPassword, luoghiJson;
    TextView email;
    TextView password;
    Button logIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        email = findViewById(R.id.editEmail);
        password = findViewById(R.id.editPassword);
        logIn = findViewById(R.id.buttonLogIn);

        //prendo i dati contenuti nella sharedprefernces
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        //prendo i valori contenuti nella shared preferenza
        strEmail = sharedpreferences.getString(EMAIL_KEY, null);
        strPassword = sharedpreferences.getString(PASSWORD_KEY, null);
        //se l'email è già salvata faccio il login automatico
        if(strEmail!=null){
            email.setText(strEmail);
            password.setText(strPassword);
            logIn.performClick();
        }
    }

    /*
    *   Alla pressione del bottone singUp apre l'activity per registrarsi
     */
    public void singUp(View view) {
        Intent intent = new Intent(this, SingUp.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);

    }

    /*
    * Alla pressiione del bottone di login apre l'activity Home
     */
    public void logIn(View view) {
        //controllo che siano stati completati i campi
        if (TextUtils.isEmpty(email.getText().toString()) || TextUtils.isEmpty(password.getText().toString())) {
            Toast.makeText(LogIn.this, "Inserire email e password per proseguire", Toast.LENGTH_SHORT).show();
        } else if(!logInRequest()) { // controllo che il login sia corretto
            Toast.makeText(LogIn.this, "Email o password errati", Toast.LENGTH_SHORT).show();
        } else {
            //salvo la mail e la password nelle shared preferences per fare il login automatico le prossime volte
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(EMAIL_KEY, email.getText().toString());
            editor.putString(PASSWORD_KEY, password.getText().toString());
            editor.apply();
            //apro la home activity
            Intent intent = new Intent(LogIn.this, Home.class);
            intent.putExtra("json",luoghiJson);
            startActivity(intent);
            finish();
        }

    }

    /*
    * controllo il login richiamando la pagina php che contralla se i valori sono corretti e presenti nel DB
     */
    private boolean logInRequest(){
        Request logIn = new Request();
        //cripta la password
        String strPassword = md5(password.getText().toString());
        try {
            String result = logIn.execute("https://gioferola.altervista.org/promemoriageografico/login.php", "login", email.getText().toString(), strPassword).get();
            if(result.substring(0,1).equals("[")) {
                luoghiJson = result;
                return true;
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    /*
    * cripta la stringa passata in chiaro in una stringa md5
     */
    public static String md5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }
}