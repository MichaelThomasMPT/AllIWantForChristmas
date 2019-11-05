package io.github.michaelthomasmpt.alliwantforchristmas;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static io.github.michaelthomasmpt.alliwantforchristmas.Constants.MY_APP_TAG;

public class PlayListItemAdapter extends RecyclerView.Adapter<PlayListItemAdapter.ViewHolder> {

  private List<PlayListItem> playListItems;
  private PlayListItemClickListener itemListener;

  public PlayListItemAdapter(PlayListItemClickListener itemListener, List<PlayListItem> playListItems) {
    this.playListItems = playListItems;
    this.itemListener = itemListener;
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    public TextView playDetailsTextView;

    public ViewHolder(View itemView) {
      super(itemView);
      playDetailsTextView = (TextView) itemView.findViewById(R.id.play_details);

      // on item click
      itemView.setOnLongClickListener(new View.OnLongClickListener(){
        @Override
        public boolean onLongClick(View v) {
          itemListener.playListItemViewListClicked(v, getAdapterPosition());
          return true;
        }
      });
    }
  }


  @Override
  public PlayListItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    LayoutInflater inflater = LayoutInflater.from(context);

    // Inflate the custom layout
    View playListItemView = inflater.inflate(R.layout.item_play, parent, false);

    // Return a new holder instance
    ViewHolder viewHolder = new ViewHolder(playListItemView);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(PlayListItemAdapter.ViewHolder viewHolder, int position) {
    PlayListItem playListItem = playListItems.get(position);
    TextView textView = viewHolder.playDetailsTextView;

    String locationDate = new SimpleDateFormat("EEE d MMM yyyy @ hh:mma").format(playListItem.getTimestamp());
    String playDetailsText = new StringBuilder()
        .append(locationDate)
        .append(" in ")
        .append(getSuburbFromLocation(textView.getContext(), playListItem.getLocation()))
        .toString();
    textView.setText(playDetailsText);
  }

  @Override
  public int getItemCount() {
    return playListItems.size();
  }

  private String getSuburbFromLocation(Context context, Location location) {
    String suburb = null;
    Geocoder geoCoder = new Geocoder(context, Locale.getDefault());

    if (location == null) {
      Log.e(MY_APP_TAG, "Location was null!");
      return null;
    } else {
      try {
        List<Address> addresses = geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        suburb = addresses.get(0).getLocality();
      } catch (IOException e) {
        Log.e(MY_APP_TAG, "Could not get from location.", e);
      }
    }

    return suburb;
  }
}
