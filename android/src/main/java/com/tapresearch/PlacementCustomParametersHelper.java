package com.tapresearch;

import android.util.Log;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.tapr.sdk.PlacementCustomParameters;

public class PlacementCustomParametersHelper {
    private static String TAG = PlacementCustomParametersHelper.class.getSimpleName();

    public static PlacementCustomParameters convertReadableMapToCustomParameters(ReadableArray readableArray) {
        if (readableArray != null) return null;

        PlacementCustomParameters placementCustomParameters = new PlacementCustomParameters();
        for (int i = 0; i < readableArray.size(); i++) {
            ReadableMap readableMap = readableArray.getMap(i);
            ReadableMapKeySetIterator readableMapKeySetIterator = readableMap.keySetIterator();
            if (readableMapKeySetIterator.hasNextKey()) {
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
        }
        return placementCustomParameters;
    }
}
