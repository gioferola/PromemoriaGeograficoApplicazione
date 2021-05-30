/*
 *  Classe per visualizzare e eliminare le attività
 *  Autore: Ferola giovanni
 *
 *   indice:
 *       -onCreate;
 *       -controlla nuovo dati;
 *       -converti JSON in ArrayList di Attivita;
 *       -torna all' activity Home;
 *       -aggiungi attività;
 */
package com.example.promemoriageografico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import tyrantgit.explosionfield.ExplosionField;

public class CoseDaFare extends AppCompatActivity {

    ListView listView;
    CoseDaFareAdapter customAdapter;
    Dialog myDialog;
    ArrayList<Attivita> attivita;
    int idLuogo;
    String email, password;
    ExplosionField mExplosionField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cose_da_fare);
        attivita = new ArrayList<>();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //recupero i dati passati dall'altra activiity
        Intent intent = getIntent();
        idLuogo = intent.getIntExtra("id",0);
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");
        mExplosionField = ExplosionField.attach2Window(this);

        myDialog = new Dialog(this);

        contrlloNuoviDati(1000, this);
        listView = findViewById(R.id.listView);
        customAdapter = new CoseDaFareAdapter(this, R.layout.cose_da_fare_view, attivita);
        listView.setAdapter(customAdapter);

        View footer = View.inflate(this, R.layout.add_cose_da_fare, null);
        listView.addFooterView(footer);

        footer.findViewById(R.id.addCosaDaFare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPopup(v);
            }
        });
        //se non lo metto da errore
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //per spuntare l'attività
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                Request modify = new Request();
                ImageView image = arg1.findViewById(R.id.checkBoxCosaDaFare);
                TextView nome = arg1.findViewById(R.id.nomeCosaDaFare);
                TextView descrizione = arg1.findViewById(R.id.descrizioneCosaDaFare);
                if (attivita.get(position).isFatto()){
                    attivita.get(position).setFatto(false);
                    image.setBackgroundResource(R.drawable.icon_checkbox_unchecked);
                    nome.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
                    descrizione.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
                    modify.execute("https://gioferola.altervista.org/promemoriageografico/modifyCosaDaFare.php", "modifyCosaDafare", ""+idLuogo, ""+attivita.get(position).getId(), ""+false, email, password);

                } else{
                    attivita.get(position).setFatto(true);
                    image.setBackgroundResource(R.drawable.icon_checkbox_checked);
                    modify.execute("https://gioferola.altervista.org/promemoriageografico/modifyCosaDaFare.php", "modifyCosaDafare", ""+idLuogo, ""+attivita.get(position).getId(), ""+true, email, password);

                }

                customAdapter.notifyDataSetChanged();

            }
        });
        //per eliminare l'attività
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                //creo pupop per eliminare
                AlertDialog.Builder builder = new AlertDialog.Builder(arg1.getContext());
                builder.setTitle("Elimina "+attivita.get(position).getNome()+".");
                builder.setMessage("Vuoi eliminare quessta attività?");
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                mExplosionField.explode(arg1);
                                Request delete = new Request();
                                delete.execute("https://gioferola.altervista.org/promemoriageografico/deleteCosaDaFare.php", "deleteCosaDafare", ""+idLuogo, ""+attivita.get(position).getId(), email, password);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                builder.setPositiveButton("Si", dialogClickListener);
                builder.setNegativeButton("No", dialogClickListener);
                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            }
        });
    }


    /*
    * funzione che ogni "millisecondi" controlla se nel DB ci sono delle modifiche
    * rispetto ai dati già presenti nell'ArrayList attività
     */
    public void contrlloNuoviDati(int millisecondi, Activity activity){

        final Handler handler = new Handler();
        final  Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ArrayList<Attivita> nuoveAttivita = new ArrayList<>();
                try {
                    Request select = new Request();
                    String result = select.execute("https://gioferola.altervista.org/promemoriageografico/selectCoseDaFare.php", "selectCoseDaFare", ""+idLuogo, email, password).get();
                    try {
                        nuoveAttivita = processJSON(new JSONArray(result));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(nuoveAttivita.size() != attivita.size()){
                    attivita = nuoveAttivita;
                    listView.setAdapter(null);
                    customAdapter = new CoseDaFareAdapter(activity, R.layout.cose_da_fare_view, attivita);
                    listView.setAdapter(customAdapter);
                }
                handler.postDelayed(this, millisecondi);
            }
        };
        handler.post(runnable);
    }

    /*
    * funzione che trasforma la stringa Json in un ArrayList di Attività
     */
    private ArrayList<Attivita> processJSON(JSONArray array) throws JSONException {
        ArrayList<Attivita> attivita = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject jobj = array.getJSONObject(i);
            boolean b = false;
            if (jobj.getInt("fatto") == 1){
                b = true;
            }
            Attivita a = new Attivita(
                    jobj.getString("nome"),
                    jobj.getString("descrizione"),
                    jobj.getInt("id"),
                    b
            );
            attivita.add(a);
        }
        return attivita;
    }

    /*
    * funzione che permette di tornare all'activity precedente
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
    * funzione per aggiungere una nuova attività
     */
    public void ShowPopup(View v) {
        FragmentManager fm = getSupportFragmentManager();
        AggiungiCosaDaFare dialog = AggiungiCosaDaFare.newInstance(idLuogo, email, password);
        dialog.show(fm, "aggiungi_cosa_da_fare");
    }
}