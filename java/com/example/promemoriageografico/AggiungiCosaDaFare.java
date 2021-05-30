/*
 *  Classe per aggiungere una nuova attività
 *  Autore: Ferola giovanni
 *
 *   indice:
 *       -newIstance;
 *       -onCreateView;
 *       -aggiungi attività;
 */
package com.example.promemoriageografico;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AggiungiCosaDaFare extends DialogFragment{
    LayoutInflater inflater;
    EditText nome, descrizione;
    int idLuogo;
    String email, password;
    Button aggiungi;

    public AggiungiCosaDaFare() {
        // Il costruttore è necessario
    }

    public static AggiungiCosaDaFare newInstance(int idLuogo, String email, String password) {

        AggiungiCosaDaFare frag = new AggiungiCosaDaFare();
        Bundle args = new Bundle();
        args.putInt("idLuogo", idLuogo);
        args.putString("email", email);
        args.putString("password", password);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        idLuogo = getArguments().getInt("idLuogo");
        email = getArguments().getString("email");
        password = getArguments().getString("password");
        this.inflater = inflater;
        View rootView = inflater.inflate(R.layout.aggiungi_cosa_da_fare, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        nome = rootView.findViewById(R.id.editNomeAttivita);
        descrizione = rootView.findViewById(R.id.editDescrizioneAttivita);

        aggiungi = rootView.findViewById(R.id.aggiungiAttivitaBtn);
        aggiungi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aggiungiAttivita();
            }
        });
        //bottone per chiudere il popUp
        ImageView close = rootView.findViewById(R.id.closePopUp);
        close.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return rootView;
    }

    /*
    * funzione che richiede al server di inserire l'attività nel DB
     */
    private void aggiungiAttivita(){
        if (TextUtils.isEmpty(nome.getText().toString())) {
            Toast.makeText(getContext(), "Inserire il nome per proseguire", Toast.LENGTH_SHORT).show();
        } else {
            Request inserisci = new Request();
            inserisci.execute("https://gioferola.altervista.org/promemoriageografico/insertCosaDafare.php", "insertCosaDaFare", nome.getText().toString(), descrizione.getText().toString(), ""+idLuogo, email, password);
            dismiss();
        }

    }

}