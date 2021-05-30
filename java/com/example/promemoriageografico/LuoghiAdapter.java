/*
 *  Classe per visualizzare i luoghi nella GridView
 *  Autore: Ferola giovanni
 *
 *   indice:
 *       -costruttore;
 *       -getter;
 */
package com.example.promemoriageografico;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.promemoriageografico.Luogo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LuoghiAdapter extends ArrayAdapter<Luogo> {

    ArrayList<Luogo> luoghi = new ArrayList<>();
    int layout;
    Context context;

    public LuoghiAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Luogo> objects) {
        super(context, resource, objects);
        luoghi = objects;
        layout = resource;

        this.context = context;
    }

    @Override
    public int getCount() {
        return luoghi.size();
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

        TextView nome = v.findViewById(R.id.nomeLuogo);
        TextView indirizzo = v.findViewById(R.id.indirizzo);
        TextView descrizione = v.findViewById(R.id.descrizione);

        Luogo luogo = luoghi.get(position);

        nome.setText(luogo.getNome());
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        List<Address> addresses  = null;
        try {
            addresses = geocoder.getFromLocation(luogo.getLatitudine(),luogo.getLongitudine(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String address = addresses.get(0).getAddressLine(0);
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String zip = addresses.get(0).getPostalCode();
        String country = addresses.get(0).getCountryName();
        indirizzo.setText(address);
        descrizione.setText(luogo.getDescrizione());

        return v;
    }
}
