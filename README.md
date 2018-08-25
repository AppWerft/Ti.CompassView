# Ti.CompassView

This Titanium module realizes a compassview. This works faster and smoother then a pure JS solution, because the event managing over KrollProxy is avoided.
 
 ## Usage
 
 ```javascript
 var Compass =  require("ti.compassView"); 
 var compassView =  Compass.createView({
   offset : 0,
   type  : Compass.TYPE_RADAR,
   image : '/images/radar.png',
   duration: 200,
   image: "/asssets/arrow.png"
 });
 compassView.getBearing();
```
