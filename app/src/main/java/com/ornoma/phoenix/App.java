package com.ornoma.phoenix;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

public class App extends MultiDexApplication {
    // Test Update

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();

        appContext = getApplicationContext();
    }

    public static void saveToken(String token){
        getPreferences().edit().putString("token", token)
                .apply();
    }

    public static String getToken(){
        return getPreferences().getString("token", null);
    }

    public static SharedPreferences getPreferences() {
        return appContext.getSharedPreferences("name", Context.MODE_PRIVATE);
    }
}
