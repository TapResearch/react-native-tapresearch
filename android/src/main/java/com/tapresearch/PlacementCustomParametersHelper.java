package com.tapresearch;

import android.util.Log;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.tapr.sdk.PlacementCustomParameters;

public class PlacementCustomParametersHelper {
    private static final String TAG = PlacementCustomParametersHelper.class.getSimpleName();


    public static PlacementCustomParameters convertReadableMapToCustomParameters(ReadableMap readableMap) {
        if (readableMap == null) return null;

        PlacementCustomParameters placementCustomParameters = new PlacementCustomParameters();
        ReadableMapKeySetIterator readableMapKeySetIterator = readableMap.keySetIterator();
        while (readableMapKeySetIterator.hasNextKey()) {
            String key = readableMapKeySetIterator.nextKey();
            String value = readableMap.getString(key);
            try {
                placementCustomParameters.addParameter(
                        new PlacementCustomParameters.PlacementParameter.Builder()
                                .key(key)
                                .value(value)
                                .build());
            } catch (PlacementCustomParameters.PlacementCustomParametersException e) {
                Log.w(TAG, "showSurveyWall", e);
            }
        }
        Log.e(TAG, "convertReadableMapToCustomParameters: " + placementCustomParameters);
        return placementCustomParameters;
    }
}
