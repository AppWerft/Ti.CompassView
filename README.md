# Ti.CompassView

This Titanium module realizes a compassview. This works faster then a pure JS solution, because the event managing over KrollProxy is avoided.
 
 ## Usage
 
 ```javascript
 var view = require("ti.compassView").createCompassView({
 	offset : 0
 });
 view.add(Ti.UI.createImageView({}));
 view.start();
 view.getBearing();
 ```