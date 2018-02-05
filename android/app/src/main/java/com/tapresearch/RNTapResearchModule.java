package com.tapresearch;

import android.support.annotation.Nullable;
import android.util.Log;
import android.graphics.Color;

import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import com.tapr.sdk.TapResearch;
import com.tapr.sdk.TapResearchOnRewardListener;
import com.tapr.sdk.TapResearchSurveyListener;
import com.tapr.sdk.TapResearchPlacementsListener;


public class RNTapResearchModule extends ReactContextBaseJavaModule
        implements LifecycleEventListener, TapResearchOnRewardListener, TapResearchSurveyListener,
        TapResearchPlacementsListener {

    private final ReactApplicationContext reactContext;
    private boolean initialized = false;

    public RNTapResearchModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        reactContext.addLifecycleEventListener(this);
    }

    @Override
    public String getName() {
        return "RNTapResearch";
    }

    @ReactMethod
    public void initWithApiToken(String apiToken) {
      if (getCurrentActivity() != null) {
        TapResearch.configure(apiToken, getCurrentActivity());
        TapResearch.getInstance().setSurveyListener(this);
        TapResearch.getInstance().setOnRewardListener(this);
        TapResearch.getInstance().setPlacementsListener(this);
        this.initialized = true;
      } else {
          Log.w("TRLogTag", "SDK initialization failed because getCurrentActivity == null");
      }
    }

    @ReactMethod
    public void setUniqueUserIdentifier(String uniqueIdentifier) {
        if (this.initialized) {
          TapResearch.getInstance().setUniqueUserIdentifier(uniqueIdentifier);
        } else {
          Log.w("TRLogTag", "SDK not initialized");
        }
    }

    @ReactMethod
    public void isSurveyAvailable(Callback callback) {
        boolean isSurveyAvailable = this.initialized ? TapResearch.getInstance().isSurveyAvailable() : false;
        callback.invoke(isSurveyAvailable);
    }

    @ReactMethod
    public void isSurveyAvailableForIdentifier(String surveyIdentifier, Callback callback) {
        boolean isSurveyAvailable = this.initialized ? TapResearch.getInstance().isSurveyAvailable(surveyIdentifier) : false;
        callback.invoke(isSurveyAvailable);
    }

    @ReactMethod
    public void showSurvey() {
        if (this.initialized) {
          TapResearch.getInstance().showSurvey();
        } else {
          Log.w("TRLogTag", "SDK not initialized");
        }
    }

    @ReactMethod
    public void showSurveyWithIdentifier(String surveyIdentifier) {
        if (this.initialized) {
          TapResearch.getInstance().showSurvey(surveyIdentifier);
        }
    }

    @ReactMethod
    public void setNavigationBarColor(String hexColor) {
        if (this.initialized) {
          int color = Color.parseColor(hexColor);
          TapResearch.getInstance().setActionBarColor(color);
        }
    }

    @ReactMethod
    public void setNavigationBarText(String text) {
      if(this.initialized) {
        TapResearch.getInstance().setActionBarText(text);
      }
    }

    @ReactMethod
    public void setNavigationBarTextColor(String hexColor) {
      if(this.initialized) {
        int color = Color.parseColor(hexColor);
        TapResearch.getInstance().setActionBarTextColor(color);
      }
    }

    @Override
    public void onSurveyAvailable() {
        sendEvent(this.reactContext, "tapResearchOnSurveyAvailable", null);
    }

    @Override
    public void onSurveyAvailable(String placementIdentifier) {
        WritableMap params = Arguments.createMap();
        params.putString("placementIdentifier", placementIdentifier);
        sendEvent(this.reactContext, "tapResearchOnSurveyAvailableWithPlacement", params);
    }

    @Override
    public void onSurveyNotAvailable() {
        sendEvent(this.reactContext, "tapResearchOnSurveyNotAvailable", null);
    }

    @Override
    public void onSurveyNotAvailable(String placementIdentifier) {
      WritableMap params = Arguments.createMap();
      params.putString("placementIdentifier", placementIdentifier);
      sendEvent(this.reactContext, "tapResearchOnSurveyNotAvailableWithPlacement", params);
    }

    @Override
    public void onSurveyModalOpened() {
        sendEvent(this.reactContext, "tapResearchSurveyModalOpened", null);
    }

    @Override
    public void onSurveyModalOpened(String placementIdentifier) {
        WritableMap params = Arguments.createMap();
        params.putString("placementIdentifier", placementIdentifier);
        sendEvent(this.reactContext, "tapResearchSurveyModalOpenedWithPlacement", params);
    }

    @Override
    public void onSurveyModalClosed() {
        sendEvent(this.reactContext, "tapResearchSurveyModalDismissed", null);
    }

    @Override
    public void onSurveyModalClosed(String placementIdentifier) {
      WritableMap params = Arguments.createMap();
      params.putString("placementIdentifier", placementIdentifier);
      sendEvent(this.reactContext, "tapResearchSurveyModalDismissedWithPlacement", params);
    }

    @Override
    public void onDidReceiveReward(int rewardAmount, String transactionIdentifier,
    String currencyName, int payoutEvent, String offerIdentifier) {
        WritableMap params = Arguments.createMap();
        params.putInt("quantity", rewardAmount);
        params.putString("offerIdentifier", transactionIdentifier);
        params.putString("currencyName", currencyName);
        params.putInt("payoutEvent", payoutEvent);
        params.putString("offerIdentifier", offerIdentifier);
        sendEvent(this.reactContext, "tapResearchDidReceiveReward", params);
    }

    private void sendEvent(ReactContext reactContext, String eventName, WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    @Override
    public void onHostResume() {

    }

    @Override
    public void onHostPause() {

    }

    @Override
    public void onHostDestroy() {

    }
}
