package com.example.inmia;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.libraries.places.api.Places;

import org.osmdroid.config.Configuration;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // La app usa colores de marca fijos y no tiene un diseño de modo oscuro;
        // se fuerza claro para que no dependa del tema del sistema del dispositivo.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        try {
            ApplicationInfo ai = getPackageManager()
                    .getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            String apiKey = bundle.getString("com.google.android.geo.API_KEY");

            if (apiKey != null && !Places.isInitialized()) {
                Places.initializeWithNewPlacesApiEnabled(getApplicationContext(), apiKey);
                Log.d("MyApplication", "Places inicializado correctamente");
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("MyApplication", "Error al inicializar Places", e);
        }

        Configuration.getInstance().setUserAgentValue(getPackageName());
    }
}
