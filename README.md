# Ti.CompassView

This Titanium module realizes a compassview. This works faster and smoother then a pure JS solution, because the event managing over KrollProxy is avoided.
 
 ## Usage
 
 ### Rotating image (compass) following compass sensor
 
 ```javascript
 var Compass =  require("ti.compassview"); 
 var compassView =  Compass.createView({
   offset : 10,
   type  : Compass.TYPE_RADAR, // or TYPE_COMPASS
   image : '/images/radar.png',
   duration: 200, //optional
   image: "/asssets/arrow.png"
 });
 compassView.getBearing();
 compassView.setOffset(45); 
```

### Shifting content of ScrollView following compass sensor

```
const Compass =  require("ti.compassview"); 
const containerView = Ti.UI.createScrollView({
	scrollType : 'horizontal';
	contentWidth : 3000,
	width: Ti.UI.FILL
});
containerView.add(Ti.UI.createImageView({
	image : 360°_PANOIMAGE
}));
for ( i = 0; i < 360; i += 30) {
		containerView.add(Ti.UI.createLabel({
			text : i + '°',
			bottom : 10,
			left : 3000 / 360*i,
			height : Ti.UI.SIZE,
			width : Ti.UI.SIZE,
			color : "yellow",
			font : {
				fontSize : 14,
				fontWeight : 'bold'
			}
		}));
}
Compass.setCompassTracker(containerView,{
	offset : 0 // offset to north
});
```