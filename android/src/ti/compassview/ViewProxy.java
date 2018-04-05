/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2010 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 *
 */
package ti.compassview;

import java.io.IOException;

import android.view.View;
import android.view.View.OnFocusChangeListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.io.TiBaseFile;
import org.appcelerator.titanium.io.TiFileFactory;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiSensorHelper;
import org.appcelerator.titanium.util.TiUIHelper;
import org.appcelerator.titanium.view.TiUIView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Message;
import android.view.Display;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

// This proxy can be created by calling Compassview.createExample({message: "hello world"})
@Kroll.proxy(creatableInModule = CompassviewModule.class, propertyAccessors = {
		CompassviewModule.PROP_OFFSET, CompassviewModule.PROP_BEARING })
public class ViewProxy extends TiViewProxy implements SensorEventListener {

	TiUIView view;
	private static final int MSG_FIRST_ID = TiViewProxy.MSG_LAST_ID + 1;
	private static final int MSG_START = MSG_FIRST_ID + 500;
	private static final int MSG_STOP = MSG_FIRST_ID + 501;
	private static final int MSG_GET_BEARING = MSG_FIRST_ID + 502;
	private static final int MSG_SET_OFFSET = MSG_FIRST_ID + 503;
	private float currentAzimut = 0f;
	private int currentDeviceOrientation = 0;
	private int duration = 200;
	private String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";

	private static final String LCAT = "TiCompass";
	private static Context ctx = TiApplication.getInstance()
			.getApplicationContext();
	private static SensorManager sensorManager = TiSensorHelper
			.getSensorManager();

	private float offset = 0;
	private Bitmap bitmap;
	private ImageView compassView;
	private Boolean running;

	private class CompassView extends TiUIView {
		private float currentAzimut = 0;

		public CompassView(final TiViewProxy proxy) {
			super(proxy);
			LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			LinearLayout container = new LinearLayout(proxy.getActivity());
			container.setLayoutParams(lp);
			compassView = new ImageView(proxy.getActivity());
			compassView.setImageBitmap(bitmap);
			compassView.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View view, boolean hasFocus) {
					if (hasFocus) {
						running = true;
					} else {
						running = false;
					}
					// TODO Auto-generated method stub

				}
			});
			container.addView(compassView);
			setNativeView(container);

		}
	}

	@Override
	public TiUIView createView(Activity activity) {
		view = new CompassView(this);
		view.getLayoutParams().autoFillsHeight = true;
		view.getLayoutParams().autoFillsWidth = true;
		return view;
	}

	private Bitmap loadImageFromApplication(String imageName) {
		Bitmap bitmap = null;
		String url = null;
		try {
			url = resolveUrl(null, imageName);
			TiBaseFile file = TiFileFactory.createTitaniumFile(
					new String[] { url }, false);
			bitmap = TiUIHelper.createBitmap(file.getInputStream());
		} catch (IOException e) {
			Log.e(LCAT, " WheelView only supports local image files " + url);
		}
		return bitmap;
	}

	// Constructor
	public ViewProxy() {
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

	// Handle creation options
	@Override
	public void handleCreationDict(KrollDict opts) {
		super.handleCreationDict(opts);
		if (opts.containsKey(CompassviewModule.PROP_DURATION)) {
			duration = opts.getInt(CompassviewModule.PROP_DURATION);
		}
		if (opts.containsKey(CompassviewModule.PROP_OFFSET)) {
			offset = opts.getInt(CompassviewModule.PROP_OFFSET);
		}
		if (opts.containsKeyAndNotNull(TiC.PROPERTY_IMAGE)) {
			String image = opts.getString(TiC.PROPERTY_IMAGE);

			Pattern p = Pattern.compile(URL_REGEX);
			Matcher m = p.matcher(image);// replace with string to compare
			if (m.find()) {
				Log.w(LCAT, "only images from resources are allowed");
			} else {
				bitmap = loadImageFromApplication(image);
			}
		} else
			Log.w(LCAT, "image missing");
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int event) {
	}

	// http://stackoverflow.com/questions/15155985/android-compass-bearing
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (running == true) {
			final float PIVOT = 0.5f;
			float azimut = event.values[0];
			int overhead = Math.abs(event.values[1]) > 90 ? 180 : 0;
			Display display = TiApplication.getAppRootOrCurrentActivity()
					.getWindowManager().getDefaultDisplay();
			int deviceRot = display.getRotation();
			int compassrot = 0;
			if (currentDeviceOrientation != deviceRot) {
				Log.d(LCAT, "deviceRot=" + deviceRot);
				currentDeviceOrientation = deviceRot;
			}
			azimut += deviceRot * 90;
			RotateAnimation rotAnimation = new RotateAnimation(currentAzimut,
					-azimut, Animation.RELATIVE_TO_SELF, PIVOT,
					Animation.RELATIVE_TO_SELF, PIVOT);
			rotAnimation.setDuration(duration);
			rotAnimation.setInterpolator(new LinearInterpolator());
			rotAnimation.setFillAfter(true);
			if (compassView != null) {
				compassView.setAnimation(rotAnimation);
			} else
				Log.w(LCAT, "cannot rotate, view is null");
			currentAzimut = -azimut;
		}

	}

}
