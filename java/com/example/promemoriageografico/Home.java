/*
 *  Classe Home per visualizzare i luoghi dell'utente
 *  Autore: Ferola giovanni
 *
 *   indice:
 *       -onCreate;
 *       -controllo permessi;
 *       -service;
 *       -menu;
 *       -aggiugngi luogo;
 *       -controllo nuovi dati;
 *       -JSON in luoghi;
 *       -md5;
 */
package com.example.promemoriageografico;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import tyrantgit.explosionfield.ExplosionField;

public class Home extends AppCompatActivity {

    public static final String SHARED_PREFS = "shared_prefs";
    public static final String EMAIL_KEY = "email_key";
    public static final String PASSWORD_KEY = "password_key";
    SharedPreferences sharedpreferences;

    String email;
    String password;
    Dialog myDialog;

    ArrayList<Luogo> luoghi;
    GridView gridView;
    LuoghiAdapter customAdapter;
    TextView titolo;

    ExplosionField mExplosionField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.logo);
        actionBar.setTitle("");

        mExplosionField = ExplosionField.attach2Window(this);

        titolo = findViewById(R.id.textViewLuoghi);
        myDialog = new Dialog(this);

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        // prendo i dati dalla sharedPreferences
        email = sharedpreferences.getString(EMAIL_KEY, null);
        password = sharedpreferences.getString(PASSWORD_KEY,null);
        password = md5(password);
        luoghi = new ArrayList<Luogo>();
        Intent intent = getIntent();
        String json = intent.getStringExtra("json");
        try {
            luoghi = processJSON(new JSONArray(json));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        gridView = findViewById(R.id.gridView);
        customAdapter = new LuoghiAdapter(this, R.layout.luogo_view, luoghi);
        gridView.setAdapter(customAdapter);

        //apre l'activity CoseDaFare
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                Intent i = new Intent(arg1.getContext(), CoseDaFare.class);
                i.putExtra("id",luoghi.get(position).getId());
                i.putExtra("email", email);
                i.putExtra("password",password);
                startActivity(i);
            }
        });
        //elimina il luogo
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                //creo pupop per eliminare
                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                builder.setTitle("Elimina "+luoghi.get(position).getNome()+".");
                builder.setMessage("Vuoi eliminare questo luogo?");
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                mExplosionField.explode(arg1);
                                Request delete = new Request();
                                delete.execute("https://gioferola.altervista.org/promemoriageografico/deleteLuogo.php", "deleteLuogo", email, ""+luoghi.get(position).getId(),password);
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
        contrlloNuoviDati(1000, this);

        //chiede i permessi per la posizione
        if (ContextCompat.checkSelfPermission(Home.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(Home.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(Home.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }else{
                ActivityCompat.requestPermissions(Home.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
        startLocationService();
    }

    /*
    * funzione che controlla se i permessi sono stati acconsentiti
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case 1: {
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(Home.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    /*
    * funzione che fa partire il service
     */
    private void startLocationService(){
        if(!isLocationServiceRunning()){
            Intent serviceIntent = new Intent(this, LocationService.class);
            serviceIntent.putExtra("email", email);
            serviceIntent.putExtra("password", password);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                Home.this.startService(serviceIntent);
            }else{
                startService(serviceIntent);
            }
        }
    }

    /*
    * funzione che controlla se il service sta già lavorando
     */
    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("com.codingwithmitch.googledirectionstest.services.LocationService".equals(service.service.getClassName())) {
                Log.d("LOCATION", "isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Log.d("LOCATION", "isLocationServiceRunning: location service is not running.");
        return false;
    }

    /*
    * funzione che crea il menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }
    /*
    * funzione che gestisce i click sul menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.user:
                Intent intent = new Intent(this, Account.class);
                intent.putExtra("email", email);
                intent.putExtra("password", password);
                startActivity(intent);
                return true;
            case R.id.logOut:
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.apply();
                Intent i = new Intent(Home.this, LogIn.class);
                startActivity(i);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
    * funzione per aggiungere un luogo
     */
    public void ShowPopup(View v) {
        FragmentManager fm = getSupportFragmentManager();
        AggiungiLuogo dialog = AggiungiLuogo.newInstance(email, password);
        dialog.show(fm, "aggiungi_luogo");
    }

    /*
    * funzione che ogni "millisecondi" controlla se nei luoghi del DB ci sono delle differenze rispetto
    * a quelli presenti nell'ArrayList luoghi
     */
    public void contrlloNuoviDati(int millisecondi, Activity activity){
        final Handler handler = new Handler();
        final  Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ArrayList<Luogo> nuoviLuoghi = new ArrayList<>();
                try {
                    Request select = new Request();
                    String result = select.execute("https://gioferola.altervista.org/promemoriageografico/selectLuoghi.php", "selectluoghi", email, password).get();
                    try {
                        nuoviLuoghi = processJSON(new JSONArray(result));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(nuoviLuoghi.size() != luoghi.size()){
                    luoghi = nuoviLuoghi;
                    gridView.setAdapter(null);
                    customAdapter = new LuoghiAdapter(activity, R.layout.luogo_view, luoghi);
                    gridView.setAdapter(customAdapter);
                }
                handler.postDelayed(this, millisecondi);
            }
        };
        handler.post(runnable);
    }

    /*
    * funzione che trasforma la stringa JSON in un ArrayList di Luogo
     */
    private ArrayList<Luogo> processJSON(JSONArray array) throws JSONException {
        ArrayList<Luogo> luoghi = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject jobj = array.getJSONObject(i);
            Luogo l = new Luogo(
                    jobj.getString("nome"),
                    jobj.getString("descrizione"),
                    jobj.getDouble("latitudine"),
                    jobj.getDouble("longitudine"),
                    jobj.getInt("id"),
                    jobj.getDouble("raggioNotifica")
            );
            luoghi.add(l);
        }
        return luoghi;
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