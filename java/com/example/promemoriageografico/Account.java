/*
 *  Classe per mostrare i dati dell'utente
 *  Autore: Ferola giovanni
 *
 *   indice:
 *       -onCreate;
 *       -ritorno alla activity Home;
 *       -modificare nominativo;
 *       -modificare passsword;
 */
package com.example.promemoriageografico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class Account extends AppCompatActivity {

    String nome;
    String cognome;
    String email, password;

    TextView nomeTxt;
    TextView cognomeTxt;
    TextView emailTxt;

    Button infoBtn;
    Button passwordBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");

        //Recupera il nominativo dell'utente
        Request select = new Request();
        String nominativo = null;
        try {
            nominativo = select.execute("https://gioferola.altervista.org/promemoriageografico/selectNominativo.php", "selectNominativo", email, password).get();
            JSONObject nominativoJson = new JSONObject(nominativo);
            nome = nominativoJson.getString("nome");
            cognome = nominativoJson.getString("cognome");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        nomeTxt = findViewById(R.id.nomeTxt);
        cognomeTxt = findViewById(R.id.cognomeTxt);
        emailTxt = findViewById(R.id.emailCampoTxt);
        infoBtn = findViewById(R.id.btnCambiaNome);
        passwordBtn = findViewById(R.id.btnCambiaPassword);

        nomeTxt.setText(nome);
        cognomeTxt.setText(cognome);
        emailTxt.setText(email);


        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoPopup(v);
            }
        });

        passwordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPassowordPopup(v);
            }
        });
    }

    /*
    * funzione per vhiudere l'activity alla pressione del tasto back o del bottone apposito
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    * funzione che apre il popUp per modificare il nominativo
     */
    public void showInfoPopup(View v){
        FragmentManager fm = getSupportFragmentManager();
        CambiaInfo dialog = CambiaInfo.newInstance(email, nomeTxt, cognomeTxt, password);
        dialog.show(fm, "cambia_info");
    }

    /*
     * funzione che apre il popUp per modificare il nominativo
     */
    public void showPassowordPopup(View v){
        FragmentManager fm = getSupportFragmentManager();
        CambiaPassword dialog = CambiaPassword.newInstance(email, password);
        dialog.show(fm, "cambio_password");
    }

}