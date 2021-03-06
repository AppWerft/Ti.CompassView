/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2010 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 *
 */
package ti.compassview;

import java.util.HashMap;
import java.util.Map.Entry;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiSensorHelper;
import org.appcelerator.titanium.util.TiUIHelper;
import org.appcelerator.titanium.view.TiCompositeLayout;
import org.appcelerator.titanium.view.TiCompositeLayout.LayoutArrangement;
import org.appcelerator.titanium.view.TiUIView;
import org.appcelerator.titanium.view.TiCompositeLayout;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.*;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
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
	public static final String PROP_SMOOTHSCROLL = "smoothScroll";
	public static final String PROP_SENSORDELAY = "sensorDelay";

	public static final int TYPE_COMPASS = -1;
	public static final int TYPE_RADAR = 1;
	private int currentDeviceOrientation = 0;
	private double offset = 0;
	private static SensorManager sensorManager = TiSensorHelper.getSensorManager();
	private Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
	private float scale = 1.0f;
	ScrollViewProxy scrollViewProxy;
	TiUIScrollView tiview;

	private int contentWidth;
	private boolean smoothScroll = false;
	private static final int MSG_FIRST_ID = KrollModule.MSG_LAST_ID + 1;
	private static final int MSG_SET_OFFSET = MSG_FIRST_ID + 500;
	private static Context ctx = TiApplication.getInstance().getApplicationContext();
	final float density = ctx.getResources().getDisplayMetrics().density;
	@Kroll.constant
	final public static int SENSOR_DELAY_UI = SensorManager.SENSOR_DELAY_UI;
	@Kroll.constant
	final public static int SENSOR_DELAY_FASTEST = SensorManager.SENSOR_DELAY_FASTEST;
	@Kroll.constant
	final public static int SENSOR_DELAY_NORMAL = SensorManager.SENSOR_DELAY_NORMAL;
	@Kroll.constant
	final public static int SENSOR_DELAY_GAME = SensorManager.SENSOR_DELAY_GAME;

	private int sensorDelay = SENSOR_DELAY_NORMAL;

	public CompassviewModule() {
		super();
	}

	@Kroll.onAppCreate
	public static void onAppCreate(TiApplication app) {
		DisplayMetrics dm = new DisplayMetrics();
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
	public void addCompassTracker(@Kroll.argument(optional = true) Object viewproxy,
			@Kroll.argument(optional = true) KrollDict _opts) {
		if (_opts != null) {
			KrollDict opts = _opts;
			if (opts.containsKeyAndNotNull(PROP_SMOOTHSCROLL)) {
				smoothScroll = opts.getBoolean(PROP_SMOOTHSCROLL);
			}
			if (opts.containsKeyAndNotNull(PROP_OFFSET)) {
				offset = opts.getInt(PROP_OFFSET);
			}
			if (opts.containsKeyAndNotNull(PROP_SENSORDELAY)) {
				sensorDelay = opts.getInt(PROP_SENSORDELAY);
			}
			Log.d(LCAT, opts.toString());

		} else
			Log.w(LCAT, "second param was missing, use defaults");

		if (viewproxy == null) {
			Log.e(LCAT, "first argument must be defined");
		} else if (viewproxy instanceof ScrollViewProxy) {
			scrollViewProxy = (ScrollViewProxy) viewproxy;
			tiview = (TiUIScrollView) scrollViewProxy.getOrCreateView();
			// tiview.getNativeView().addOnAttachStateChangeListener(this);
			// getting original contentWidth (must be numeric, Ti.UI.SIZE doesn't work):
			contentWidth = (int) scrollViewProxy.getProperty(TiC.PROPERTY_CONTENT_WIDTH);
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					addImageViewAtRightEdgeOfScrollView();
				}
			});

			// starting tracking:
			sensorManager.registerListener(CompassviewModule.this, sensor, sensorDelay);
			sensorManager.registerListener(CompassviewModule.this,
					sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), sensorDelay);
		} else {
			Log.e(LCAT, "first argument must be a scrollView");
		}
		scale = contentWidth / 360 / density;
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
		int x = (int) (currentΦ * scale);
		if (TiApplication.isUIThread()) {
			handleSetOffset(x);
		} else {
			TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_SET_OFFSET));
		}
	}

	private void handleSetOffset(int x) {

		tiview.scrollTo(x, 0, smoothScroll);
	}

	private class DummyTiView extends TiUIView {
		public DummyTiView(final TiViewProxy proxy) {
			super(proxy);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);

			lp.width = 500;

			RelativeLayout container = new RelativeLayout(ctx);
			lp.addRule(RelativeLayout.ALIGN_LEFT);
			container.setMinimumHeight(6);
			container.setPadding(contentWidth, 0, 0, 0);
			container.setLayoutParams(lp);
			// making „screenshot“
			TiBlob blob = (TiBlob) (tiview.toImage().get("media"));

			Log.d(LCAT, "bloblength=" + blob.getBytes().length + " width=" + tiview.toImage().get("width"));
			ImageView iv = new ImageView(ctx);
			iv.setImageBitmap(BitmapFactory.decodeByteArray(blob.getBytes(), 0, blob.getBytes().length));
			Log.d(LCAT, iv.toString());
			container.setBackground(new BitmapDrawable(ctx.getResources(),
					BitmapFactory.decodeByteArray(blob.getBytes(), 0, blob.getBytes().length)));
			container.addView(iv);
			// container.setBackgroundColor(Color.CYAN);
			setNativeView(container);
		}

	}

	private void addImageViewAtRightEdgeOfScrollView() {
		// extending the width:
		scrollViewProxy.setProperty(TiC.PROPERTY_CONTENT_WIDTH, 2 * contentWidth);
		// tiview.add(new DummyTiView(scrollViewProxy));
	}
}
