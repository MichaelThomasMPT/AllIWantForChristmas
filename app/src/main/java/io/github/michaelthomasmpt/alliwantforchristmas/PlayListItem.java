package io.github.michaelthomasmpt.alliwantforchristmas;

import android.location.Location;

import java.util.Date;
import java.util.UUID;

public class PlayListItem implements Comparable<PlayListItem> {
    private UUID id;
    private Date timestamp;
    private Location location;


    public PlayListItem(Date timestamp, Location location) {
        this.id = UUID.randomUUID();
        this.timestamp = timestamp;
        this.location = location;
    }

    public PlayListItem(UUID id, Date timestamp, Location location) {
        this.id = UUID.randomUUID();
        this.timestamp = timestamp;
        this.location = location;
    }

    public UUID getId() {
        return id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public int compareTo(PlayListItem o) {
        return getTimestamp().compareTo(o.getTimestamp());
    }
}
