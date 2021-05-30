/*
 *  Classe di SingUp
 *  Autore: Ferola giovanni
 *
 *   indice:
 *       -onCreate;
 *       -to logIn activity;
 *       -registrazione;
 *       -string in chiaro a md5 string;*
 */
package com.example.promemoriageografico;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

public class SingUp extends AppCompatActivity {
    EditText nome, cognome, email, password;
    Button singUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);
        getSupportActionBar().hide();

        nome = findViewById(R.id.editNome);
        cognome = findViewById(R.id.editCognome);
        email = findViewById(R.id.editEmailSingup);
        password = findViewById(R.id.editPasswordSingup);
        singUp = findViewById(R.id.buttonSingUp);

    }

    /*
     *   Alla pressione del bottone logIn apre l'activity per accedere
     */
    public void logIn(View view) {
        Intent intent = new Intent(this, LogIn.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
    /*
     * Alla pressiione del bottone di signUp invia al server la richiesta di registrazione
     */
    public void singUp(View view) {
        if (TextUtils.isEmpty(nome.getText().toString()) || TextUtils.isEmpty(cognome.getText().toString()) || TextUtils.isEmpty(email.getText().toString()) || TextUtils.isEmpty(password.getText().toString())) {
            Toast.makeText(SingUp.this, "compila tutti i campi per proseguire", Toast.LENGTH_SHORT).show();
        } else {
            String strPassword = md5(password.getText().toString());
            Request inserisci = new Request();
            String risposta = null;
            try {
                risposta = inserisci.execute("https://gioferola.altervista.org/promemoriageografico/registration.php", "singUp", nome.getText().toString(), cognome.getText().toString(), email.getText().toString(), strPassword).get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Toast.makeText(SingUp.this, risposta, Toast.LENGTH_SHORT).show();
            if(risposta.contains("Registrazione"))
                finish();
        }
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