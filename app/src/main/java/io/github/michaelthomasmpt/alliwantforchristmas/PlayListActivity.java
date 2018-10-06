package io.github.michaelthomasmpt.alliwantforchristmas;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.*;
import java.util.*;

import static io.github.michaelthomasmpt.alliwantforchristmas.Constants.MY_APP_TAG;

public class PlayListActivity extends AppCompatActivity {
  public static final String SAVED_PLAYS_FILE_NAME = "savedPlays.csv";
  private final List<String> REQUIRED_PERMISSIONS = Arrays.asList(
      Manifest.permission.ACCESS_COARSE_LOCATION,
      Manifest.permission.ACCESS_FINE_LOCATION,
      Manifest.permission.READ_EXTERNAL_STORAGE,
      Manifest.permission.WRITE_EXTERNAL_STORAGE
  );

  private File savedPlaysFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), SAVED_PLAYS_FILE_NAME);
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
                PlayListItem newPlay = new PlayListItem(new Date(), location);
                savePlay(newPlay);
                playListItems.add(0, newPlay);
                adapter.notifyItemInserted(0);
                RecyclerView playListView = (RecyclerView) findViewById(R.id.playListView);
                playListView.smoothScrollToPosition(0);
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

  private void savePlay(PlayListItem play) {
    try {
      Log.d(MY_APP_TAG, "Attempting to save play " + play.getId());
      FileOutputStream outputStream = new FileOutputStream(savedPlaysFile, true);
      String savedPlayString = new StringBuilder()
          .append(play.getId())
          .append(",")
          .append(play.getTimestamp().getTime())
          .append(",")
          .append(play.getLocation().getLongitude())
          .append(",")
          .append(play.getLocation().getLatitude())
          .append("\n")
          .toString();
      outputStream.write(savedPlayString.getBytes());
      outputStream.close();
      Log.d(MY_APP_TAG, "Play " + play.getId() + " was saved.");
    } catch (FileNotFoundException e) {
      Log.e(MY_APP_TAG, "Saved Plays file could not be found when attempting to save a play.", e);
    } catch (IOException e) {
      Log.e(MY_APP_TAG, "Error saving play to file.", e);
    }
  }

  private List<PlayListItem> loadPlayListItems() {
    Log.d(MY_APP_TAG, "Saved Data file: " + savedPlaysFile.getAbsolutePath());
    if (savedPlaysFile.exists()) {
      Log.i(MY_APP_TAG, "Saved Plays file exists. Will load plays from it.");
      return loadFromExistingFile();
    } else {
      Log.i(MY_APP_TAG, "Saved Plays file does not exist. Will attempt to create one.");
      createNewSavedPlaysFile();
      return new ArrayList<>();
    }
  }

  private List<PlayListItem> loadFromExistingFile() {
    List<PlayListItem> items = new ArrayList<>();
    BufferedReader br = null;
    String line = null;

    try {
      br = new BufferedReader(new FileReader(savedPlaysFile));
      br.readLine(); //throw away heading row

      //process remaining rows
      while ((line = br.readLine()) != null) {
        String[] savedPlay = line.split(",");
        if (savedPlay.length >= 4) {
          UUID id = UUID.fromString(savedPlay[0]);
          Date timestamp = new Date(Long.valueOf(savedPlay[1]));
          Location location = new Location("");
          location.setLongitude(Double.valueOf(savedPlay[2]));
          location.setLatitude(Double.valueOf(savedPlay[3]));

          PlayListItem playListItem = new PlayListItem(id, timestamp, location);
          items.add(playListItem);
          Log.d(MY_APP_TAG, "Item " + playListItem.getId() + " was loaded.");
        }
      }

    } catch (FileNotFoundException e) {
      Log.e(MY_APP_TAG, "Could not load playlist items; file not found.", e);
    } catch (IOException e) {
      Log.e(MY_APP_TAG, "Could not load playlist items; there was an error reading from file.", e);
    }

    //order list on timestamp descending
    Collections.sort(items);
    Collections.reverse(items);

    return items;
  }

  private void createNewSavedPlaysFile() {
    try {
      //write out the headings for the csv file
      FileOutputStream outputStream = new FileOutputStream(savedPlaysFile);
      outputStream.write("Play ID,Timestamp,Longitude,Latitude\n".getBytes());
      outputStream.close();
    } catch (IOException e) {
      Log.e(MY_APP_TAG, "Failed to create new savedPlays.csv file", e);
    }
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
