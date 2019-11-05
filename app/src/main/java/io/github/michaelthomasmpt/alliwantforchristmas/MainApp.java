package io.github.michaelthomasmpt.alliwantforchristmas;

import android.app.Application;
import android.content.Context;

public class MainApp extends Application {

  private static Context context;

  public void onCreate() {
    super.onCreate();
    MainApp.context = getApplicationContext();
  }

  public static Context getAppContext() {
    return MainApp.context;
  }
}
