package com.tapresearch;

import android.app.Activity;
import android.app.Application;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.tapr.helpers.JsonHelper;
import com.tapr.internal.activities.survey.SurveyActivity;
import com.tapr.sdk.PlacementCustomParameters;
import com.tapr.sdk.PlacementEventListener;
import com.tapr.sdk.PlacementListener;
import com.tapr.sdk.RewardCollectionListener;
import com.tapr.sdk.RewardListener;
import com.tapr.sdk.SurveyListener;
import com.tapr.sdk.TRPlacement;
import com.tapr.sdk.TRReward;
import com.tapr.sdk.TapResearch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RNTapResearchModule extends ReactContextBaseJavaModule
        implements LifecycleEventListener, RewardCollectionListener, RewardListener {

    private static final String TAG = "TRLogTag";
    private static final String DEVELOPMENT_PLATFORM_NAME = "react";
    private static final String DEVELOPMENT_PLATFORM_VERSION = "2.4.1";

    private final ReactApplicationContext mReactContext;
    private Map<String, TRPlacement> mPlacementMap = new HashMap();
    private boolean mInitialized = false;
    private boolean receiveRewardCollection;

    public RNTapResearchModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
        reactContext.addLifecycleEventListener(this);
        if ((getReactApplicationContext().getApplicationContext() instanceof Application)) {
            ((Application) (getReactApplicationContext().getApplicationContext())).registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                    if (activity instanceof SurveyActivity) {
                        TapResearch.getInstance().setRewardCollectionListener(null);
                        TapResearch.getInstance().setRewardListener(null);
                    }
                }

                @Override
                public void onActivityStarted(Activity activity) {

                }

                @Override
                public void onActivityResumed(Activity activity) {
                }

                @Override
                public void onActivityPaused(Activity activity) {

                }

                @Override
                public void onActivityStopped(Activity activity) {

                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

                }

                @Override
                public void onActivityDestroyed(Activity activity) {
                    if (!(activity instanceof SurveyActivity)) {
                        return;
                    }
                    receiveRewardListener();
                }
            });
        }
    }

    private void receiveRewardListener() {
        Log.d(TAG, "receiveRewardListener: " + receiveRewardCollection);
        if (receiveRewardCollection) {
            TapResearch.getInstance().setRewardCollectionListener(this);
            return;
        }
        TapResearch.getInstance().setRewardListener(this);
    }

    @Override
    public String getName() {
        return "RNTapResearch";
    }

    @ReactMethod
    public void initWithApiToken(String apiToken) {
        if (getCurrentActivity() != null) {
            TapResearch.configure(apiToken, getCurrentActivity(), DEVELOPMENT_PLATFORM_NAME, DEVELOPMENT_PLATFORM_VERSION, new PlacementEventListener() {
                @Override
                public void placementReady(TRPlacement placement) {
                    Log.d(TAG, "placementReady: " + placement.getPlacementIdentifier());
                    JSONObject jsonObject = new JsonHelper().toJson(placement);
                    WritableMap params = WritableMapHelper.convertJsonToMap(jsonObject);
                    if (placement.getPlacementCode() != TRPlacement.PLACEMENT_CODE_SDK_NOT_READY) {
                        RNTapResearchModule.this.mPlacementMap.put(placement.getPlacementIdentifier(), placement);
                    }
                    sendEvent(RNTapResearchModule.this.mReactContext, "tapResearchOnPlacementEventReady", params);

                }

                @Override
                public void placementUnavailable(String placementId) {

                    Log.d(TAG, "placementUnavailable: " + placementId);
                    WritableMap writableMap = new WritableNativeMap();
                    writableMap.putString("placementId", placementId);
                    sendEvent(RNTapResearchModule.this.mReactContext, "tapResearchOnPlacementUnavailable", writableMap);
                }
            });

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

    @ReactMethod
    public void setReceiveRewardCollection(boolean receiveRewardCollection) {
        this.receiveRewardCollection = receiveRewardCollection;
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
        showSurveyWallParams(placement, null);
    }

    @ReactMethod
    public void showSurveyWallParams(final ReadableMap placement, ReadableMap customParameters) {
        String placementIdentifier = placement.getString("placementIdentifier");

        PlacementCustomParameters placementCustomParameters =
                PlacementCustomParametersHelper.convertReadableMapToCustomParameters(customParameters);
        showSurveyWall(placementIdentifier, placementCustomParameters);
    }

    private void showSurveyWall(final String placementId, PlacementCustomParameters placementCustomParameters) {
        if (placementId == null || placementId.isEmpty()) {
            Log.e(TAG, "placementIdentifier is empty can't show survey wall");
            return;
        }

        final TRPlacement nativePlacement = mPlacementMap.get(placementId);
        if (nativePlacement == null) {
            Log.e(TAG, "Native placement is empty can't load the survey wall");
            return;
        }
        SurveyListener surveyListener = new SurveyListener() {
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
        };
        if (placementCustomParameters == null) {
            nativePlacement.showSurveyWall(surveyListener);
            return;
        }
        nativePlacement.showSurveyWall(surveyListener, placementCustomParameters);
    }

    @Override
    public void onDidReceiveReward(List<TRReward> rewards) {
        try {
            JSONArray payloadString = (JSONArray) JsonHelper.toJSON(rewards);
            WritableArray writableArray = WritableMapHelper.convertJsonToArray(payloadString);
            sendEvent(mReactContext, "tapResearchOnReceivedRewardCollection", writableArray);
            TapResearch.getInstance().setRewardCollectionListener(null);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
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

    private void sendEvent(ReactContext reactContext, String eventName, WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }


    private void sendEvent(ReactContext reactContext, String eventName, WritableArray params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    @Override
    public void onDidReceiveReward(TRReward reward) {
        JSONObject payloadString = new JsonHelper().toJson(reward);
        WritableMap writableArray = WritableMapHelper.convertJsonToMap(payloadString);
        sendEvent(mReactContext, "tapResearchOnReceivedReward", writableArray);
        TapResearch.getInstance().setRewardListener(null);
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
