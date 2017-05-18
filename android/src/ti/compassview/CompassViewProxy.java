/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2010 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 *
 */
package ti.compassview;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiConfig;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiSensorHelper;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiCompositeLayout;
import org.appcelerator.titanium.view.TiCompositeLayout.LayoutArrangement;
import org.appcelerator.titanium.view.TiUIView;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

// This proxy can be created by calling Compassview.createExample({message: "hello world"})
@Kroll.proxy(creatableInModule = CompassviewModule.class, propertyAccessors = {
		CompassviewModule.PROP_OFFSET, CompassviewModule.PROP_BEARING })
public class CompassViewProxy extends TiViewProxy implements
		SensorEventListener {

	TiUIView view;
	private static final int MSG_FIRST_ID = TiViewProxy.MSG_LAST_ID + 1;
	private static final int MSG_START = MSG_FIRST_ID + 500;
	private static final int MSG_STOP = MSG_FIRST_ID + 501;
	private static final int MSG_GET_BEARING = MSG_FIRST_ID + 502;
	private static final int MSG_SET_OFFSET = MSG_FIRST_ID + 503;
	private float currentAzimut = 0f;

	private static final String LCAT = "TiCompass";
	private static Context ctx = TiApplication.getInstance()
			.getApplicationContext();
	private static SensorManager sensorManager = TiSensorHelper
			.getSensorManager();

	private float offset = 0;

	private class CompassView extends TiUIView {
		private float currentAzimut = 0;

		public CompassView(TiViewProxy proxy) {
			super(proxy);
			LayoutArrangement arrangement = LayoutArrangement.DEFAULT;
			if (proxy.hasProperty(TiC.PROPERTY_LAYOUT)) {
				String layoutProperty = TiConvert.toString(proxy
						.getProperty(TiC.PROPERTY_LAYOUT));
				if (layoutProperty.equals(TiC.LAYOUT_HORIZONTAL)) {
					arrangement = LayoutArrangement.HORIZONTAL;
				} else if (layoutProperty.equals(TiC.LAYOUT_VERTICAL)) {
					arrangement = LayoutArrangement.VERTICAL;
				}
			}
			setNativeView(new TiCompositeLayout(proxy.getActivity(),
					arrangement));
		}
	}

	// Constructor
	public CompassViewProxy() {
		super();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean handleMessage(Message msg) {
		AsyncResult result = null;
		switch (msg.what) {
		case MSG_SET_OFFSET: {
			result = (AsyncResult) msg.obj;
			handleSetOffset((Float) result.getArg());
			result.setResult(null);
			return true;
		}
		case MSG_GET_BEARING: {
			result = (AsyncResult) msg.obj;
			result.setResult(handleGetBearing());
			return true;
		}
		case MSG_START: {
			result = (AsyncResult) msg.obj;
			handleStart();
			result.setResult(null);
			return true;
		}
		case MSG_STOP: {
			result = (AsyncResult) msg.obj;
			handleStop();
			result.setResult(null);
			return true;
		}
		default: {
			return super.handleMessage(msg);
		}
		}
	}

	@Kroll.method
	public void start() {
		if (TiApplication.isUIThread()) {
			Log.d(LCAT, "direct handleStart()");
			handleStart();
		} else {
			Log.d(LCAT, "indirect handleStart() by TiMessenger");
			TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(
					MSG_START));

		}
	}

	private void handleStart() {
		Log.d(LCAT, "handleStart()");
		@SuppressWarnings("deprecation")
		Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		sensorManager.registerListener(this, sensor,
				SensorManager.SENSOR_DELAY_GAME);
	}

	@Kroll.method
	public void stop() {
		if (TiApplication.isUIThread()) {
			handleStart();
		} else {
			TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(
					MSG_STOP));

		}
	}

	private void handleStop() {
		sensorManager.unregisterListener(this);
	}

	@Kroll.method
	@Kroll.setProperty
	public void setOffset(float offset) {
		if (TiApplication.isUIThread()) {
			handleSetOffset(offset);
		} else {
			TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(
					MSG_SET_OFFSET, offset));

		}
	}

	private void handleSetOffset(float offset) {

	}

	@Kroll.method
	@Kroll.getProperty
	public float getBearing() {
		if (TiApplication.isUIThread()) {
			return handleGetBearing();
		} else {
			return (Float) TiMessenger.sendBlockingMainMessage(getMainHandler()
					.obtainMessage(MSG_SET_OFFSET));

		}
	}

	private float handleGetBearing() {
		return 0;
	}

	@Override
	public TiUIView createView(Activity activity) {
		Log.d(LCAT, "TiUIView createView");
		view = new CompassView(this);
		view.getLayoutParams().autoFillsHeight = true;
		view.getLayoutParams().autoFillsWidth = true;
		return view;
	}

	// Handle creation options
	@Override
	public void handleCreationDict(KrollDict opts) {
		super.handleCreationDict(opts);
		if (opts.containsKey(CompassviewModule.PROP_OFFSET)) {
			offset = opts.getInt(CompassviewModule.PROP_OFFSET);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int event) {
	}

	// http://stackoverflow.com/questions/15155985/android-compass-bearing
	@Override
	public void onSensorChanged(SensorEvent event) {
		float azimut = Math.round(event.values[0]);
		RotateAnimation ra = new RotateAnimation(currentAzimut, -azimut,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		// how long the animation will take place
		ra.setDuration(210);
		ra.setFillAfter(true);
		if (view != null) {
			// Log.d(LCAT, "rotate=" + azimut);
			view.setAnimatedRotationDegrees(-azimut);
		} else
			Log.w(LCAT, "cannot rotate, view is null");
		// startAnimation(ra);
		currentAzimut = -azimut;

	}
}