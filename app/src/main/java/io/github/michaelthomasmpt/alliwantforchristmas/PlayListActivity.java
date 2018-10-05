package io.github.michaelthomasmpt.alliwantforchristmas;

import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class PlayListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //configure play list
        List<PlayListItem> dummyItems = getPlayListItems();
        RecyclerView playListView = (RecyclerView) findViewById(R.id.playListView);
        PlayListItemAdapter adapter = new PlayListItemAdapter(dummyItems);
        playListView.setAdapter(adapter);
        playListView.setLayoutManager(new LinearLayoutManager(this));

        //configure "new play" button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btnAddPlay);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("TODO...");
            }
        });
    }

    private List<PlayListItem> getPlayListItems() {
        //FIXME don't mock this data
        List<PlayListItem> dummyItems = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            dummyItems.add(new PlayListItem(new Date(), null));
        }
        return dummyItems;
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
