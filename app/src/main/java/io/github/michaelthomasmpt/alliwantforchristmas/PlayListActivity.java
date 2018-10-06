package io.github.michaelthomasmpt.alliwantforchristmas;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class PlayListActivity extends AppCompatActivity {
  private final List<String> REQUIRED_PERMISSIONS = Arrays.asList(
      Manifest.permission.ACCESS_COARSE_LOCATION,
      Manifest.permission.ACCESS_FINE_LOCATION
  );

  private final String MY_APP_TAG = "AllIWantForChristmas";
  private final List<PlayListItem> playListItems = loadPlayListItems();
  private final PlayListItemAdapter adapter = new PlayListItemAdapter(playListItems);
  private FusedLocationProviderClient locationProvider;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Log.d(MY_APP_TAG, "Starting the app!");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_play_list);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    //set up the required permissions
    locationProvider = LocationServices.getFusedLocationProviderClient(this);
    requestRequiredPermissions();

    //configure play list
    RecyclerView playListView = (RecyclerView) findViewById(R.id.playListView);
    playListView.setAdapter(adapter);
    playListView.setLayoutManager(new LinearLayoutManager(this));

    //configure "new play" button
    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btnAddPlay);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        addNewPlay();
      }
    });
  }

  /**
   * request all of the permissions required from the user at app startup
   */
  private void requestRequiredPermissions() {
    for (String permission : REQUIRED_PERMISSIONS) {
      if (ContextCompat.checkSelfPermission(this, permission)
          != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this,
            new String[]{permission},
            REQUIRED_PERMISSIONS.indexOf(permission));
      }
    }
  }

  private void addNewPlay() {
    try {
      Log.d(MY_APP_TAG, "Adding a new play to the list.");
      requestRequiredPermissions(); //just in case they haven't been granted already!

      locationProvider.getLastLocation()
          .addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
              if (location != null) {

                Log.d(MY_APP_TAG, "A location was found: " + location.toString());
                playListItems.add(new PlayListItem(new Date(), location));
                adapter.notifyItemInserted(playListItems.size() - 1);

              } else {
                Log.e(MY_APP_TAG, "Retrieved location was null.");
              }
            }
          })
          .addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
              Log.e(MY_APP_TAG, "Location retrieval failed.", e);
            }
          });

    } catch (SecurityException e) {
      Log.e(MY_APP_TAG, "Location services were not available.", e);
    }
  }

  private List<PlayListItem> loadPlayListItems() {
    List<PlayListItem> items = new ArrayList<>();
    //TODO load from disk here
    return items;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_play_list, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
