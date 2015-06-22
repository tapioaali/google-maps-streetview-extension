package com.vaadin.tapio.googlemaps.streetview;

import com.vaadin.server.AbstractExtension;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.events.MapMoveListener;
import com.vaadin.tapio.googlemaps.streetview.client.GoogleMapStreetViewRpc;
import com.vaadin.tapio.googlemaps.streetview.client.GoogleMapStreetViewState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An extension that enables server-side controlling of street view functionality
 * when using GoogleMaps Add-on for Vaadin.
 */
public class GoogleMapStreetView extends AbstractExtension {

    /**
     * The map which street view is handled with this extension
     */
    protected GoogleMap map;

    /**
     * The listeners that are called when the street view overlay changes in the client-side.
     */
    protected List<GoogleMapStreetViewListener> listeners = new ArrayList<GoogleMapStreetViewListener>();

    /**
     * Default implementation for the RPC that follows client-side (i.e. the ones made by the user) changes of the
     * street view overlay. This implementation updates the state object with the new values and notifies the
     * registered listeners.
     */
    protected GoogleMapStreetViewRpc rpc = new GoogleMapStreetViewRpc() {

        @Override
        public void visibilityChanged(boolean visible) {
            getState().visible = visible;

            for (GoogleMapStreetViewListener listener : listeners) {
                listener.visibilityChanged(visible);
            }
        }

        @Override
        public void positionChanged(LatLon position) {
            map.setCenter(position);

            for (GoogleMapStreetViewListener listener : listeners) {
                listener.positionChanged(position);
            }
        }

        @Override
        public void povChanged(int heading, int pitch, int zoom) {
            for (GoogleMapStreetViewListener listener : listeners) {
                listener.povChanged(heading, pitch, zoom);
            }
        }
    };

    /**
     * Creates a new street view extension that can be used to control street view functionality
     * of the map given as a parameter.
     *
     * @param map the map which street view should be controllable
     */
    public GoogleMapStreetView(GoogleMap map) {
        this.map = map;
        extend(map);

        registerRpc(rpc);

        getState().position = map.getCenter();

        map.addMapMoveListener(new MapMoveListener() {
            @Override
            public void mapMoved(int zoomLevel, LatLon center,
                LatLon boundsNE, LatLon boundsSW) {
                getState().position = center;
            }
        });
    }

    /**
     * Sets the visibility of the street view overlay. When visible, it is
     * displayed over the map that this extension has been applied to.
     *
     * @param visible set to true to make the street view overlay visible
     */
    public void setVisible(boolean visible) {
        getState().visible = visible;
    }

    /**
     * Sets the amount of zoom of the street view overlay. Default is 1 (no zoom). The maximum
     * value depends on the used map, normally something from 5 to 20.
     *
     * @param povZoom the amount of zoom
     */
    public void setPovZoom(int povZoom) {
        getState().povZoom = povZoom;
    }

    /**
     * Sets the amount of heading (left-right position) of the  point-of-view of the street view in degrees.
     * The value is given as relative from true north. The heading is measured clockwise; 90 degrees is true east.
     *
     * @param povHeading the heading (left-right position) in degrees
     */
    public void setPovHeading(int povHeading) {
        getState().povHeading = povHeading;
    }

    /**
     * Sets the pitch (up-down position) of the point-of-view of the street view in degrees.
     * Default is 0 (straight) degrees. +90 degrees is straight up and -90 straight down.
     *
     * @param povPitch the pitch (up-down position) in degrees
     */
    public void setPovPitch(int povPitch) {
        getState().povPitch = povPitch;
    }

    public int getZoom() {
        return getState().povZoom;
    }

    public boolean isVisible() {
        return getState().visible;
    }

    public int getPovHeading() {
        return getState().povHeading;
    }

    public int getPovPitch() {
        return getState().povPitch;
    }

    public void addStreetViewListener(GoogleMapStreetViewListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void removeStreetViewListener(GoogleMapStreetViewListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    public void clearStreetViewListeners() {
        synchronized (listeners) {
            listeners.clear();
        }
    }

    public List<GoogleMapStreetViewListener> getStreetViewListeners() {
        return Collections.unmodifiableList(listeners);
    }

    @Override
    protected GoogleMapStreetViewState getState(boolean markAsDirty) {
        return (GoogleMapStreetViewState) super.getState(markAsDirty);
    }

    @Override
    protected GoogleMapStreetViewState getState() {
        return (GoogleMapStreetViewState) super.getState();
    }
}
