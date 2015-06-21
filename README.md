# Street View Extension for GoogleMaps Add-on for Vaadin 7

The extension adds server-side controls for street view functionality of Google Maps.

Download the extension from Vaadin Directory.

Check online demo @ [tapio.app.fi](http://tapio.app.fi/googlemaps-streetview-demo)

## Release notes ##

**0.1**

An initial release of the add-on. While it has all the basic functionality to control the street view overlay from the server-side, there’s one crucial feature missing: the user actions on the client-side aren’t updated to the server-side.

Street View API has a built-in listener for changes in point-of-view but for some reason it seems to always return default values for all of the three parameters: zoom, heading and pitch.

I'll investigate this further and hopefully in 0.2 there'll be a working client-server-communication.
