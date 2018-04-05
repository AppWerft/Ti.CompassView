# Ti.CompassView

This Titanium module realizes a compassview. This works faster and smother then a pure JS solution, because the event managing over KrollProxy is avoided.
 
 ## Usage
 
 ```javascript
 var compassView = require("ti.compassView").createView({
 	offset : 0,
 	duration: 200,
 	image: "/asssets/arrow.png"
 });
 compassView.getBearing();
```