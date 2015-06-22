package com.vaadin.tapio.googlemaps.streetview;

import com.vaadin.tapio.googlemaps.client.LatLon;

/**
 * A listener that can be used to follow visibility and position changes of the
 * street view.
 */
public interface GoogleMapStreetViewListener {

    void visibilityChanged(boolean visible);

    void positionChanged(LatLon position);

    void povChanged(int heading, int pitch, int zoom);
}
