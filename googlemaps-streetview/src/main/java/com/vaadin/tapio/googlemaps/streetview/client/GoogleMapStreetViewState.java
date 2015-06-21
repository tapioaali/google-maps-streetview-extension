package com.vaadin.tapio.googlemaps.streetview.client;

import com.vaadin.shared.communication.SharedState;

/**
 * The state object for Google Maps Add-On for Vaadin Street View Extension. The
 * default values are the ones used by Google Maps.
 */
public class GoogleMapStreetViewState extends SharedState {
    /**
     * The Visibility of the street view overlay.
     */
    public boolean visible = false;
    /**
     * The amount of zoom used with the street view overlay.
     */
    public int povZoom = 1;
    /**
     * The heading (left-right) of the street view overlay in degrees.
     */
    public int povHeading = 0;
    /**
     * The pitch (up-down) of the street view overlay in degrees.
     */
    public int povPitch = 0;
}
