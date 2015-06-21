package com.vaadin.tapio.googlemaps.streetview.client;

import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.maps.client.events.position.PositionChangeMapEvent;
import com.google.gwt.maps.client.events.position.PositionChangeMapHandler;
import com.google.gwt.maps.client.events.visible.VisibleChangeMapEvent;
import com.google.gwt.maps.client.events.visible.VisibleChangeMapHandler;
import com.google.gwt.maps.client.streetview.StreetViewPanoramaImpl;
import com.google.gwt.maps.client.streetview.StreetViewPov;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.communication.StateChangeEvent.StateChangeHandler;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tapio.googlemaps.client.GoogleMapConnector;
import com.vaadin.tapio.googlemaps.client.GoogleMapWidget;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.streetview.GoogleMapStreetView;

import java.io.Serializable;

/**
 * Client-side implementation of the Street View Extension for GoogleMaps
 * Add-on for Vaadin. Basically, it grabs the street view instance from the extended map
 * instance and operates with it.
 */
@Connect(GoogleMapStreetView.class)
public class GoogleMapStreetViewConnector extends AbstractExtensionConnector
        implements StateChangeHandler {

    protected StreetViewPanoramaImpl streetView;

    protected GoogleMapStreetViewRpc rpc = RpcProxy.create(
            GoogleMapStreetViewRpc.class, this);

    @Override
    protected void extend(ServerConnector target) {
        GoogleMapConnector connector = (GoogleMapConnector) target;
        GoogleMapWidget widget = connector.getWidget();

        streetView = widget.getMap().getStreetView();

        addChangeHandlers();

        updateFromStateObject();
    }

    protected void addChangeHandlers() {
        streetView.addVisibleChangeHandler(new VisibleChangeMapHandler() {

            @Override
            public void onEvent(VisibleChangeMapEvent event) {
                Boolean visible = streetView.getVisible();
                if (getState().visible != visible) {
                    rpc.visibilityChanged(visible);
                }
            }
        });

        streetView.addPositionChangeHandler(new PositionChangeMapHandler() {

            @Override
            public void onEvent(PositionChangeMapEvent event) {
                LatLng position = streetView.getPosition();
                LatLon newPos = new LatLon(position.getLatitude(), position
                        .getLongitude());
                rpc.positionChanged(newPos);
            }
        });
    }

    protected void updateFromStateObject() {
        // Have to check for every value that it really has changed before
        // setting it. Otherwise we may end up with infinite
        // server-client-server loop.

        if (streetView.getVisible() != getState().visible) {
            streetView.setVisible(getState().visible);
        }

        StreetViewPov pov = streetView.getPov();
        if (pov == null) {
            // If the street view is not visible, the point-of-view may not be
            // available. Skipping setting at thus point should not be
            // problematic since they'll be updated when the street view is set
            // visible.
            return;
        }
        if (pov.getHeading() != getState().povHeading) {
            pov.setHeading(getState().povHeading);
        }
        if (pov.getPitch() != getState().povPitch) {
            pov.setPitch(getState().povPitch);
        }
        if (pov.getZoom() != getState().povZoom) {
            pov.setZoom(getState().povZoom);
        }
    }

    @Override
    public GoogleMapStreetViewState getState() {
        return (GoogleMapStreetViewState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        updateFromStateObject();
    }
}
