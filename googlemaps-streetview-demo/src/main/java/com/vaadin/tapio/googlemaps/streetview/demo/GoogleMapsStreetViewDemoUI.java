package com.vaadin.tapio.googlemaps.streetview.demo;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.streetview.GoogleMapStreetView;
import com.vaadin.tapio.googlemaps.streetview.GoogleMapStreetViewListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import javax.servlet.annotation.WebServlet;

@Theme("valo")
public class GoogleMapsStreetViewDemoUI extends UI {

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = GoogleMapsStreetViewDemoUI.class, widgetset = "com.vaadin.tapio.googlemaps.streetview.demo.DemoWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    private GoogleMap googleMap;

    private HorizontalLayout rootLayout;

    private CssLayout mapLayout;

    private VerticalLayout leftLayout;

    private HorizontalLayout buttonLayout;

    private CssLayout logLayout;

    private GoogleMapStreetView streetView;

    @Override
    protected void init(VaadinRequest request) {
        rootLayout = new HorizontalLayout();
        rootLayout.setMargin(true);
        rootLayout.setSpacing(true);
        rootLayout.setSizeFull();
        rootLayout.addStyleName("street-view-test-root");
        setContent(rootLayout);

        leftLayout = new VerticalLayout();
        leftLayout.setSizeFull();
        leftLayout.addStyleName("street-view-test-left");
        rootLayout.addComponent(leftLayout);
        rootLayout.setExpandRatio(leftLayout, 1.0f);

        mapLayout = new CssLayout();
        mapLayout.setSizeFull();
        mapLayout.addStyleName("street-view-test-map");
        leftLayout.addComponent(mapLayout);
        leftLayout.setExpandRatio(mapLayout, 1.0f);

        buttonLayout = new HorizontalLayout();
        buttonLayout.setMargin(true);
        buttonLayout.setSpacing(true);
        buttonLayout.setWidth("100%");
        buttonLayout.setHeight("150px");
        buttonLayout.addStyleName("street-view-test-buttons");
        leftLayout.addComponent(buttonLayout);

        logLayout = new CssLayout();
        logLayout.setHeight("100%");
        logLayout.setWidth("400px");
        logLayout.addStyleName("street-view-test-logs");
        rootLayout.addComponent(logLayout);

        initMapAndStreetView();
        createButtons();
    }

    private void initMapAndStreetView() {
        GoogleMap googleMap = new GoogleMap(null, null, null);
        googleMap.setCenter(new LatLon(60.44875000000000, 22.27702299999999));
        googleMap.setZoom(10);
        googleMap.setSizeFull();
        mapLayout.addComponent(googleMap);

        streetView = new GoogleMapStreetView(googleMap);
        streetView.addStreetViewListener(new GoogleMapStreetViewListener() {

            @Override
            public void visibilityChanged(boolean visible) {
                String logText = "StreetView visible: "
                        + String.valueOf(visible);
                logLayout.addComponent(new Label(logText), 0);

            }

            @Override
            public void positionChanged(LatLon position) {
                String logText = String.format(
                        "New position with lat: %.14f, lon: %.14f",
                        position.getLat(), position.getLon());
                logLayout.addComponent(new Label(logText), 0);

            }

            @Override
            public void povChanged(int heading, int pitch, int zoom) {
                String logText = String.format(
                        "New POV with heading: %d°, pitch: %d°, zoom %d",
                        heading, pitch, zoom);
                logLayout.addComponent(new Label(logText), 0);
            }
        });
    }

    private void createButtons() {
        buttonLayout.addComponent(new Button("Show/hide street view",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        streetView.setVisible(!streetView.isVisible());
                    }
                }));

        buttonLayout.addComponent(new Button("Zoom to 3",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        streetView.setPovZoom(3);
                    }
                }));

        buttonLayout.addComponent(new Button("Heading to 45°",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        streetView.setPovHeading(45);
                    }
                }));

        buttonLayout.addComponent(new Button("Pitch to -30°",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        streetView.setPovPitch(-30);
                    }
                }));
    }
}