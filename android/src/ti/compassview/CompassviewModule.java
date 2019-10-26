/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2010 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 *
 */
package ti.compassview;

import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiSensorHelper;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Message;
import android.view.Display;
import ti.modules.titanium.ui.ScrollViewProxy;
import ti.modules.titanium.ui.widget.TiUIScrollView;
import ti.modules.titanium.ui.widget.TiUIScrollView.TiScrollViewLayout;

@Kroll.module(name = "Compassview", id = "ti.compassview")
public class CompassviewModule extends KrollModule implements SensorEventListener {

	// Standard Debugging variables
	public static final String LCAT = "TiCompass";
	public static final String PROP_BEARING = "bearing";
	public static final String PROP_TYPE = "rotationtype";
	public static final String PROP_OFFSET = "offset";
	public static final String PROP_DURATION = "duration";
	public static final int TYPE_COMPASS = -1;
	public static final int TYPE_RADAR = 1;
	private int currentDeviceOrientation = 0;
	private double offset = 0;
	private static SensorManager sensorManager = TiSensorHelper.getSensorManager();
	private Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
	private int sensorDelay = SensorManager.SENSOR_DELAY_UI;
	TiUIScrollView sv;
	private int contentWidth;
	private static final int MSG_FIRST_ID = KrollModule.MSG_LAST_ID + 1;
	private static final int MSG_SET_OFFSET = MSG_FIRST_ID + 500;

	public CompassviewModule() {
		super();
	}

	@Kroll.onAppCreate
	public static void onAppCreate(TiApplication app) {
	}

	@Override
	public boolean handleMessage(Message msg) {
		AsyncResult result = null;
		switch (msg.what) {
		case MSG_SET_OFFSET: {
			result = (AsyncResult) msg.obj;
			handleSetOffset((int) result.getArg());
			result.setResult(null);
			return true;
		}

		default: {
			return super.handleMessage(msg);
		}
		}
	}

	@Kroll.method
	public void addCompassTracker(@Kroll.argument(optional = true) Object view,
			@Kroll.argument(optional = true) Object opts) {
		if (view == null) {
			Log.e(LCAT, "first argument must be defined");
		} else if (view instanceof ScrollViewProxy) {
			sv = (TiUIScrollView) ((ScrollViewProxy) view).getOrCreateView();
			contentWidth = (int) sv.getProxy().getProperty(TiC.PROPERTY_CONTENT_WIDTH);
			sensorManager.registerListener(CompassviewModule.this, sensor, sensorDelay);
			sensorManager.registerListener(CompassviewModule.this,
					sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), sensorDelay);
		} else {
			Log.e(LCAT, "first argument must be a scrollView");
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int event) {
	}

	private int getDeviceRotation() {
		Activity activity = TiApplication.getAppRootOrCurrentActivity();
		if (activity == null)
			return 0;
		Display display = activity.getWindowManager().getDefaultDisplay();
		int deviceRot = display.getRotation();
		if (currentDeviceOrientation != deviceRot) {
			currentDeviceOrientation = deviceRot;
		}
		return deviceRot * 90;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float currentΦ = event.values[0];
		currentΦ += getDeviceRotation();
		int x = (int) (currentΦ * contentWidth / 360);
		if (TiApplication.isUIThread()) {
			handleSetOffset(x);
		} else {
			TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_SET_OFFSET));
		}
	}

	private void handleSetOffset(int x) {
		Log.d(LCAT, "scrollTo=" + x);
		sv.setContentOffset(-x, 0);
	}
}
