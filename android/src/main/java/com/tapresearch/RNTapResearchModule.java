package com.tapresearch;

import android.graphics.Color;
import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.tapr.helpers.JsonHelper;
import com.tapr.sdk.PlacementListener;
import com.tapr.sdk.RewardListener;
import com.tapr.sdk.SurveyListener;
import com.tapr.sdk.TRPlacement;
import com.tapr.sdk.TRReward;
import com.tapr.sdk.TapResearch;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RNTapResearchModule extends ReactContextBaseJavaModule
        implements LifecycleEventListener, RewardListener {

    private static final String TAG = "TRLogTag";
    private static final String DEVELOPMENT_PLATFORM_NAME = "react";
    private static final String DEVELOPMENT_PLATFORM_VERSION = "2.0.3";

    private final ReactApplicationContext mReactContext;
    private Map<String, TRPlacement> mPlacementMap = new HashMap();
    private boolean mInitialized = false;

    public RNTapResearchModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
        reactContext.addLifecycleEventListener(this);
    }

    @Override
    public String getName() {
        return "RNTapResearch";
    }

    @ReactMethod
    public void initWithApiToken(String apiToken) {
        if (getCurrentActivity() != null) {
            TapResearch.configure(apiToken, getCurrentActivity(), DEVELOPMENT_PLATFORM_NAME, DEVELOPMENT_PLATFORM_VERSION);
            TapResearch.getInstance().setRewardListener(this);
            mInitialized = true;
        } else {
            Log.w(TAG, "SDK initialization failed because getCurrentActivity == null");
        }
    }

    @ReactMethod
    public void setUniqueUserIdentifier(String uniqueIdentifier) {
        if (mInitialized) {
            TapResearch.getInstance().setUniqueUserIdentifier(uniqueIdentifier);
        } else {
            Log.w(TAG, "SDK not mInitialized");
        }
    }

    @Deprecated
    @ReactMethod
    public void initPlacement(String placementIdentifier, final Callback placementCallback) {
        if (mInitialized) {
            TapResearch.getInstance().initPlacement(placementIdentifier, new PlacementListener() {
                @Override
                public void onPlacementReady(TRPlacement placement) {
                    if (placement.getPlacementCode() != TRPlacement.PLACEMENT_CODE_SDK_NOT_READY) {
                        RNTapResearchModule.this.mPlacementMap.put(placement.getPlacementIdentifier(), placement);
                        JSONObject jsonObject = new JsonHelper().toJson(placement);
                        WritableMap params = WritableMapHelper.convertJsonToMap(jsonObject);
                        placementCallback.invoke(params);
                      }
                }
            });
        }
    }

    @ReactMethod
    public void initPlacementEvent(String placementIdentifier) {
        if (mInitialized) {
            TapResearch.getInstance().initPlacement(placementIdentifier, new PlacementListener() {
                @Override
                public void onPlacementReady(TRPlacement placement) {
                    JSONObject jsonObject = new JsonHelper().toJson(placement);
                    WritableMap params = WritableMapHelper.convertJsonToMap(jsonObject);
                    if (placement.getPlacementCode() != TRPlacement.PLACEMENT_CODE_SDK_NOT_READY) {
                        RNTapResearchModule.this.mPlacementMap.put(placement.getPlacementIdentifier(), placement);
                    }
                    sendEvent(RNTapResearchModule.this.mReactContext, "tapResearchOnPlacementReady", params);
                }
            });
        }
    }

    @ReactMethod
    public void showSurveyWall(final ReadableMap placement) {
        String placementIdentifier = placement.getString("placementIdentifier");
        if (placementIdentifier == null || placementIdentifier.isEmpty()) {
            Log.e(TAG, "placementIdentifier is empty can't show survey wall");
            return;
        }

        final TRPlacement nativePlacement = mPlacementMap.get(placementIdentifier);
        if (nativePlacement == null) {
            Log.e(TAG, "Native placement is empty can't load the survey wall");
            return;
        }

        nativePlacement.showSurveyWall(new SurveyListener() {
            @Override
            public void onSurveyWallOpened() {
                JSONObject jsonObject = new JsonHelper().toJson(nativePlacement);
                WritableMap params = WritableMapHelper.convertJsonToMap(jsonObject);
                sendEvent(RNTapResearchModule.this.mReactContext, "tapResearchOnSurveyWallOpened", params);
            }


            @Override
            public void onSurveyWallDismissed() {
                JSONObject jsonObject = new JsonHelper().toJson(nativePlacement);
                WritableMap params = WritableMapHelper.convertJsonToMap(jsonObject);
                sendEvent(RNTapResearchModule.this.mReactContext, "tapResearchOnSurveyWallDismissed", params);
            }
        });
    }

    @ReactMethod
    public void setNavigationBarColor(String hexColor) {
        if (mInitialized) {
            int color = Color.parseColor(hexColor);
            TapResearch.getInstance().setActionBarColor(color);
        }
    }

    @ReactMethod
    public void setNavigationBarText(String text) {
        if (mInitialized) {
            TapResearch.getInstance().setActionBarText(text);
        }
    }

    @ReactMethod
    public void setNavigationBarTextColor(String hexColor) {
        if (mInitialized) {
            int color = Color.parseColor(hexColor);
            TapResearch.getInstance().setActionBarTextColor(color);
        }
    }

    @Override
    public void onDidReceiveReward(TRReward reward) {
        JSONObject jsonObject = new JsonHelper().toJson(reward);
        WritableMap params = WritableMapHelper.convertJsonToMap(jsonObject);
        sendEvent(mReactContext, "tapResearchOnReceivedReward", params);
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
