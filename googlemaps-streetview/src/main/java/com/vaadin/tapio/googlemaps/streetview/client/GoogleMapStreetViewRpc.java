package com.vaadin.tapio.googlemaps.streetview.client;

import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.tapio.googlemaps.client.LatLon;

/**
 * An RPC that is used to keep the server-side up-to-date with that changes that
 * the user makes while using street view.
 */
public interface GoogleMapStreetViewRpc extends ServerRpc {

    void positionChanged(LatLon newPosition);

    void visibilityChanged(boolean visible);

    void povChanged(int heading, int pitch, int zoom);
}
