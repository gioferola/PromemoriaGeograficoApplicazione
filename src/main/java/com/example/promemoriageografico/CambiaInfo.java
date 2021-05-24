/*
 *  Classe per modificare il nominativo dell'utente
 *  Autore: Ferola giovanni
 *
 *   indice:
 *       -costruttore;
 *       -newIstance;
 *       -onCreateView;
 *       -cambia nominativo;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

//TODO creare il fragment
public class CambiaInfo extends DialogFragment{
    LayoutInflater inflater;
    EditText nome, cognome;
    String email, password;
    Button aggiungi;
    TextView nomeTxt, cognomeTxt;

    public CambiaInfo( TextView nomeTxt, TextView cognomeTxt) {
        this.nomeTxt = nomeTxt;
        this.cognomeTxt = cognomeTxt;
    }

    public static CambiaInfo newInstance(String email, TextView nomeTxt, TextView cognomeTxt, String password) {

        CambiaInfo frag = new CambiaInfo(nomeTxt, cognomeTxt);
        Bundle args = new Bundle();
        args.putString("email", email);
        args.putString("password", password);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        email = getArguments().getString("email");
        password = getArguments().getString("password");
        this.inflater = inflater;
        View rootView = inflater.inflate(R.layout.activity_cambia_info, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        nome = rootView.findViewById(R.id.cambiaNome);
        cognome = rootView.findViewById(R.id.cambiaCognome);

        aggiungi = rootView.findViewById(R.id.cambiaInfoBtn);
        aggiungi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiaInfo();
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
    * funzione che invia al server il nuovo nominativo e cambia i valori del nominativo nella classe Account
     */
    private void cambiaInfo(){
        if (TextUtils.isEmpty(nome.getText().toString()) || TextUtils.isEmpty(cognome.getText().toString())) {
            Toast.makeText(getContext(), "Inserire nome e cognome per proseguire", Toast.LENGTH_SHORT).show();
        } else {
            Request cambia = new Request(getActivity());
            cambia.execute("https://gioferola.altervista.org/promemoriageografico/cambiaInfo.php", "cambiaInfo", nome.getText().toString(), cognome.getText().toString(), email, password);

            nomeTxt.setText(nome.getText());
            cognomeTxt.setText(cognome.getText());

            dismiss();
        }

    }

}