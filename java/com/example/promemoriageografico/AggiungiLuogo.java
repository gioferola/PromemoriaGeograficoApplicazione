/*
 *  Classe per aggiungere un nuovo luogo
 *  Autore: Ferola giovanni
 *
 *   indice:
 *       -newIstance;
 *       -onCreateView;
 *       -creaMappa;
 *       -aggiungi attività;
 */
package com.example.promemoriageografico;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.appcompat.widget.SearchView;

import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.slider.Slider;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class AggiungiLuogo extends DialogFragment implements OnMapReadyCallback{
    LayoutInflater inflater;
    GoogleMap mMap;
    EditText nome, descrizione;
    String email, password;
    LatLng posizione;
    Button aggiungi;
    SearchView searchView;
    Slider raggioNotifica;
    boolean fullSizeMap = false;
    boolean marker = false;
    int height,width;

    public AggiungiLuogo() {
        // Il costruttore è necessario
    }

    public static AggiungiLuogo newInstance(String email, String password) {

        AggiungiLuogo frag = new AggiungiLuogo();
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
        View rootView = inflater.inflate(R.layout.aggiungi_luogo, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        searchView = rootView.findViewById(R.id.sv_location);
        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment supportMapFragment =  SupportMapFragment.newInstance();
        fm.beginTransaction().replace(R.id.map, supportMapFragment).commit();

        raggioNotifica = rootView.findViewById(R.id.raggioNotifica);
        supportMapFragment.getMapAsync(this);
        nome = rootView.findViewById(R.id.editNomeLuogo);
        descrizione = rootView.findViewById(R.id.editDescrizione);
        aggiungi = rootView.findViewById(R.id.aggiungiLuogoBtn);

        FrameLayout frame = rootView.findViewById(R.id.map);
        height = frame.getLayoutParams().height;
        width = frame.getLayoutParams().width;

        ImageView image = rootView.findViewById(R.id.imageView);
        //per portare la mappa a tutto schermo e rimpicciolirla
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(fullSizeMap){
                    frame.getLayoutParams().height =height;
                    frame.getLayoutParams().width = width;
                    frame.requestLayout();
                    image.setImageResource(R.drawable.icon_normal_screen);
                    fullSizeMap = false;
                } else{
                    DisplayMetrics metrics = new DisplayMetrics();
                    getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) frame.getLayoutParams();
                    params.width =  metrics.widthPixels-20;
                    params.height = (int) (metrics.heightPixels*0.65);
                    //params.leftMargin = 0;
                    frame.setLayoutParams(params);
                    image.setImageResource(R.drawable.icons_full_sreen);
                    fullSizeMap = true;
                }
            }
        });

        aggiungi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aggiungiLuogo();
            }
        });

        //bottone per chiudere il popUp
        ImageView close = rootView.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dismiss();
        }
        });

        //barra per la ricerca dei luoghi sulla mappa
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if(location != null || !location.equals("")){
                    Geocoder geocoder = new Geocoder(rootView.getContext());
                    try {
                        addressList = geocoder.getFromLocationName(location,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(addressList.size() > 0){
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                        posizione = latLng;
                        marker = true;
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return rootView;
    }

    /*
    * crea la mappa e gesticse i click su di essa
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        posizione = new LatLng(46.14605395368557, 9.52816697430139);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posizione,15));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(point));
                posizione = point;
                marker = true;
            }
        });
    }

    /*
     * funzione che richiede al server di inserire l'attività nel DB
     */
    private void aggiungiLuogo(){
        if (TextUtils.isEmpty(nome.getText().toString()) || !marker) {
            Toast.makeText(getContext(), "compila tutti i campi per proseguire", Toast.LENGTH_SHORT).show();
        } else {
            Request inserisci = new Request();
            inserisci.execute("https://gioferola.altervista.org/promemoriageografico/insertLuogo.php", "insertLuogo", nome.getText().toString(), descrizione.getText().toString(), email, ""+posizione.latitude, ""+posizione.longitude, ""+raggioNotifica.getValue(), password);
            dismiss();
        }
    }
}