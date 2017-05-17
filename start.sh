#!/bin/bash

APPID=ti.compassview
VERSION=1.0.0

#cp android/assets/* iphone/

JARFILE="~/Library/Application\ Support/Titanium/modules/android/$APPID/$VERSION/chromecast.jar"

rm -rvf ~/Library/Application\ Support/Titanium/modules/android/$APPID/$VERSION/*

cd android;rm -rf build/classes;ti --platform android build --build-only --sdk 6.0.3.GA; unzip -uo  dist/$APPID-android-$VERSION.zip  -d  ~/Library/Application\ Support/Titanium/; cd ..
#cd android;rm -rf build/classes;ant; unzip -uo  dist/$APPID-android-$VERSION.zip  -d  ~/Library/Application\ Support/Titanium/; cd ..


zip -d ~/Library/Application\ Support/Titanium/modules/android/$APPID/$VERSION/compassview.jar org/appcelerator/titanium/gen/bindings.json;

#cp android/dist/$APPID-android-$VERSION.zip .
#cp iphone/$APPID-iphone-$VERSION.zip .
