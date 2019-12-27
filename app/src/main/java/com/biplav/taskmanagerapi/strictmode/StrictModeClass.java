package com.biplav.taskmanagerapi.strictmode;

public class StrictModeClass {
    public static void StrctMode(){
        android.os.StrictMode.ThreadPolicy policy=new android.os.StrictMode.ThreadPolicy.Builder().permitAll().build();
        android.os.StrictMode.setThreadPolicy(policy);

    }
}
