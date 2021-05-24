/*
 *  Classe per visualizzare le attività nella ListView
 *  Autore: Ferola giovanni
 *
 *   indice:
 *       -costruttore;
 *       -getter;
 */
package com.example.promemoriageografico;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.promemoriageografico.Luogo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CoseDaFareAdapter extends ArrayAdapter<Attivita> {

    ArrayList<Attivita> cosedaFare = new ArrayList<>();
    int layout;
    Context context;

    public CoseDaFareAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Attivita> objects) {
        super(context, resource, objects);
        cosedaFare = objects;
        layout = resource;
        this.context = context;
    }

    @Override
    public int getCount() {
        return cosedaFare.size();
    }

    /*
    * crea il layout da visualizzare per ogni elemento della ListView
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(layout, null);
        }

        TextView nome = v.findViewById(R.id.nomeCosaDaFare);
        ImageView check = v.findViewById(R.id.checkBoxCosaDaFare);
        TextView descrizione = v.findViewById(R.id.descrizioneCosaDaFare);

        Attivita a = cosedaFare.get(position);

        if(a.isFatto()){
            nome.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            descrizione.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            check.setBackgroundResource(R.drawable.icon_checkbox_checked);
        }
        nome.setText(a.getNome());
        descrizione.setText(a.getDescrizione());
        return v;
    }
}
