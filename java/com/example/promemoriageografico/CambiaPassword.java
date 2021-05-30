/*
 *  Classe per cambiare la password dell'utente
 *  Autore: Ferola giovanni
 *
 *   indice:
 *       -newIstance;
 *       -onCreateView;
 *       -cambia password;
 *       -md5;
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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

public class CambiaPassword extends DialogFragment{
    LayoutInflater inflater;
    EditText vecchia, nuova, reNuova;
    String email, password;
    Button cambia;
    public CambiaPassword() {
        // Il costruttore è necessario
    }

    public static CambiaPassword newInstance(String email, String password) {

        CambiaPassword frag = new CambiaPassword();
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
        View rootView = inflater.inflate(R.layout.activity_canbia_password, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        vecchia = rootView.findViewById(R.id.vecchiaPassword);
        nuova = rootView.findViewById(R.id.nuovaPassword);
        reNuova = rootView.findViewById(R.id.reNuovaPassword);

        cambia = rootView.findViewById(R.id.cambiaPasswordBtn);
        cambia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiaPassword();
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
     * funzione che invia al server la nuova password da sostituire a quella vecchia
     */
    private void cambiaPassword(){
        if (TextUtils.isEmpty(vecchia.getText().toString()) || TextUtils.isEmpty(nuova.getText().toString()) || TextUtils.isEmpty(reNuova.getText().toString())) {
            Toast.makeText(getContext(), "Inserire tutti i campi per proseguire", Toast.LENGTH_SHORT).show();
        } else if (!nuova.getText().toString().equals(reNuova.getText().toString())){
            Toast.makeText(getContext(), "Le password devono coincidere", Toast.LENGTH_SHORT).show();
        } else {
            String vecchiaPassword = md5(vecchia.getText().toString());
            String nuovaPassword = md5(nuova.getText().toString());
            Request cambia = new Request();
            String result = null;
            try {
                 result = cambia.execute("https://gioferola.altervista.org/promemoriageografico/cambiaPassword.php", "cambiaPassword", vecchiaPassword, nuovaPassword, ""+email).get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
            dismiss();
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