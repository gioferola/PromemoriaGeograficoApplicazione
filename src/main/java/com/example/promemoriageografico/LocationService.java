/*
 *  Classe per lavorare in backgroud
 *  Autore: Ferola giovanni
 *
 *   indice:
 *       -onCreate;
 *       -onStartComand;
 *       -getLocation;
 *       -calcola distanza;
 *       -crea notifica;
 *       -ocontrollo dati;
 *       -JSON in luoghi;
 */
package com.example.promemoriageografico;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class LocationService extends Service {

    private static final String TAG = "LocationService";
    private FusedLocationProviderClient mFusedLocationClient;
    private final static long UPDATE_INTERVAL = 30 * 1000;  /* 30 secs */
    private final static long FASTEST_INTERVAL = 15 * 1000; /* 15 sec */
    Context context;

    ArrayList<Luogo> luoghi;
    ArrayList<Integer> luoghiNotificati;
    String email, password;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        context = this;
        String CHANNEL_ID = "my_channel_01";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,"My Channel", NotificationManager.IMPORTANCE_DEFAULT);
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Promemoria Geografico")
                .setContentText("Promemoria Geografico sta usando la tua posizione.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");
        luoghi = new ArrayList<>();
        luoghiNotificati = new ArrayList<>();
        contrlloNuoviDati(60000);
        Log.d(TAG, "onStartCommand: called.");
        getLocation();
        return START_NOT_STICKY;
    }


    /*
    * funzione che recupera la posizione ed effetua i controllo
     */
    private void getLocation() {

        // ---------------------------------- LocationRequest ------------------------------------
        // Creo il locationRequest per recuperare la posizione del dispositivo
        LocationRequest mLocationRequestHighAccuracy = new LocationRequest();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestHighAccuracy.setInterval(UPDATE_INTERVAL);
        mLocationRequestHighAccuracy.setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getLocation: stopping the location service.");
            stopSelf();
            return;
        }
        Log.d(TAG, "getLocation: getting location information.");
        mFusedLocationClient.requestLocationUpdates(mLocationRequestHighAccuracy, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        Log.d(TAG, "onLocationResult: got location result.");
                        Location location = locationResult.getLastLocation();
                        for(int i = 0; i < luoghi.size(); i++){
                            double dist = calcolaDistanza(luoghi.get(i), location);
                            //controllo se è già stata mandata la notifica a quel luogo
                            boolean notificato = false;
                            for (int j = 0; j < luoghiNotificati.size(); j++){
                                if (luoghi.get(i).getId() == luoghiNotificati.get(j)){
                                    notificato = true;
                                    break;
                                }
                            }
                            if(dist < luoghi.get(i).getRaggioNotifica() && !notificato){
                                ShowNotification(luoghi.get(i));
                                luoghiNotificati.add(luoghi.get(i).getId());
                                break;
                            }
                        }
                    }
                },
                Looper.myLooper());
    }

    /*
    * funzione che calcola la distanza tra 3 coordinate
     */
    private double calcolaDistanza(Luogo luogo, Location location){
        double theta = luogo.getLongitudine() - location.getLongitude();
        double dist = Math.sin(Math.toRadians(luogo.getLatitudine())) * Math.sin(Math.toRadians(location.getLatitude())) + Math.cos(Math.toRadians(luogo.getLatitudine())) * Math.cos(Math.toRadians(location.getLatitude())) * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515;
        //per avere i KM
        dist = dist * 1.609344;
        return dist;
    }


    /*
    * funzione che mostra la notifica
     */
    public void ShowNotification( Luogo luogo) {
        NotificationManager mNotificationManager;

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "notify_001");
        Intent ii = new Intent(this, Home.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.setBigContentTitle(luogo.getNome());
        bigText.setSummaryText(luogo.getDescrizione());

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setNumber(2);
        mBuilder.setSmallIcon(R.drawable.logo);
        mBuilder.setColor(16362035);
        mBuilder.setContentTitle(luogo.getNome());
        mBuilder.setContentText(luogo.getDescrizione());
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(0, mBuilder.build());

    }

    /*
    * funzione che controlla i dati rispetto a quelli del DB
     */
    public void contrlloNuoviDati(int millisecondi){
        final Handler handler = new Handler();
        final  Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ArrayList<Luogo> nuoviLuoghi = new ArrayList<>();
                try {
                    Request select = new Request();
                    String result = select.execute("https://gioferola.altervista.org/promemoriageografico/selectLuoghi.php", "selectluoghi", email, password).get();
                    try {
                        luoghi = processJSON(new JSONArray(result));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.postDelayed(this, millisecondi);
            }
        };
        handler.post(runnable);
    }

    /*
    * funzione che trasforma una stringa JSON in un ArrayList di Luogo
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
}