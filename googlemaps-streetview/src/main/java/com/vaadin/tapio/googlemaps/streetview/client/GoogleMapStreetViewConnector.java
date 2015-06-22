package com.vaadin.tapio.googlemaps.streetview.client;

import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.maps.client.events.position.PositionChangeMapEvent;
import com.google.gwt.maps.client.events.position.PositionChangeMapHandler;
import com.google.gwt.maps.client.events.pov.PovChangeMapEvent;
import com.google.gwt.maps.client.events.pov.PovChangeMapHandler;
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
import com.vaadin.tapio.googlemaps.client.GoogleMapInitListener;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.streetview.GoogleMapStreetView;

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
        connector.addInitListener(new GoogleMapInitListener() {
            @Override
            public void googleMapInitiated(MapWidget map) {
                streetView = map.getStreetView();
                addChangeHandlers();
                updateFromStateObject(true);
            }
        });
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

        streetView.addPovChangeHandler(new PovChangeMapHandler() {
            @Override
            public void onEvent(PovChangeMapEvent event) {
                StreetViewPov pov = streetView.getPov();
                rpc.povChanged(pov.getHeading(), pov.getPitch(), pov.getZoom());
            }
        });
    }

    protected void updateFromStateObject(boolean force) {
        // Have to check for every value that it really has changed before
        // setting it. Otherwise we may end up with infinite
        // server-client-server loop.
        if (streetView == null) {
            //map not yet initiated
            return;
        }

        if (streetView.getVisible() != getState().visible || force) {
            streetView.setVisible(getState().visible);
        }

        LatLng pos = LatLng.newInstance(getState().position.getLat(),
            getState().position.getLon());
        if (!pos.equals(streetView.getPosition())) {
            streetView.setPosition(pos);
        }

        StreetViewPov pov = streetView.getPov();
        if ((pov.getHeading() != getState().povHeading
            || pov.getPitch() != getState().povPitch
            || pov.getZoom() != getState().povZoom) || force) {
            pov = StreetViewPov.newInstance();
            pov.setHeading(getState().povHeading);
            pov.setPitch(getState().povPitch);
            pov.setZoom(getState().povZoom);
            streetView.setPov(pov);
        }
    }

    @Override
    public GoogleMapStreetViewState getState() {
        return (GoogleMapStreetViewState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        updateFromStateObject(false);
    }
}
