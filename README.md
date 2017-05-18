# Ti.CompassView

This Titanium module realizes a compassview. This works faster and smother then a pure JS solution, because the event managing over KrollProxy is avoided.
 
 ## Usage
 
 ```javascript
 var view = require("ti.compassView").createCompassView({
 	offset : 0,
 	duration: 200,
 	image: "/asssets/arrow.png"
 });
 view.getBearing();
 view.setOffset(32);
 window.addEventListener("focus",function(){view.start();});
 window.addEventListener("blur",function(){view.stop();});
  ```