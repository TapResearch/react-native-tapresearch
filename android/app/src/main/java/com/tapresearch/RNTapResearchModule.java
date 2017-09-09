package com.tapresearch;

import android.support.annotation.Nullable;
import android.util.Log;

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


public class RNTapResearchModule extends ReactContextBaseJavaModule
        implements LifecycleEventListener, TapResearchOnRewardListener, TapResearchSurveyListener {

    private final ReactApplicationContext reactContext;


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
      TapResearch.configure(apiToken, getCurrentActivity());
      TapResearch.getInstance().setSurveyListener(this);
      TapResearch.getInstance().setOnRewardListener(this);
    }

    @ReactMethod
    public void setUniqueUserIdentifier(String uniqueIdentifier) {
        Log.i("tap", "identifier - " + uniqueIdentifier);
        TapResearch.getInstance().setUniqueUserIdentifier(uniqueIdentifier);
    }

    @ReactMethod
    public void isSurveyAvailable(Callback callback) {
        callback.invoke(TapResearch.getInstance().isSurveyAvailable());
    }

    @ReactMethod
    public void showSurvey() {
        TapResearch.getInstance().showSurvey();
    }

    @ReactMethod
    public void showSurveyWithIdentifier(String surveyIdentifier) {
        TapResearch.getInstance().showSurvey(surveyIdentifier);
    }

    private void sendEvent(ReactContext reactContext,
                           String eventName,
                           WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    public void onSurveyAvailable() {
        sendEvent(this.reactContext, "tapResearchOnSurveyAvailable", null);
    }

    public void onSurveyNotAvailable() {
        sendEvent(this.reactContext, "tapResearchOnSurveyNotAvailable", null);
    }

    public void onSurveyModalOpened() {
        sendEvent(this.reactContext, "tapResearchSurveyModalOpened", null);
    }

    public void onSurveyModalClosed() {
        sendEvent(this.reactContext, "tapResearchSurveyModalDismissed", null);
    }

    public void onDidReceiveReward(int rewardAmount, String transactionIdentifier,
    String currencyName, int payoutEvent, String offerIdentifier) {
        WritableMap params = Arguments.createMap();
        params.putInt("quantity", rewardAmount);
        params.putString("offerIdentifier", transactionIdentifier);
        params.putString("currencyName", currencyName);
        params.putInt("payoutEvent", payoutEvent);
        sendEvent(this.reactContext, "tapResearchDidReceiveReward", params);
    }

    @Override
    public void onHostResume() {

    }

    @Override
    public void onHostPause() {

    }

    @Override
    public void onHostDestroy() {
        // Actvity `onDestroy`
    }
}
