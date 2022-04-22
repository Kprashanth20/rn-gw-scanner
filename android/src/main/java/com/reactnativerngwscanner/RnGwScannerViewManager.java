package com.reactnativerngwscanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;

import java.util.Map;

public class RnGwScannerViewManager extends ViewGroupManager<RnGwScannerView> {
  public static final String REACT_CLASS = "RnGwScannerView";

  private final ReactApplicationContext reactContext;

  public RnGwScannerViewManager(ReactApplicationContext reactContext) {
    this.reactContext = reactContext;
  }

  @Override
  @NonNull
  public String getName() {
    return REACT_CLASS;
  }

  @Override
  @NonNull
  public RnGwScannerView createViewInstance(@NonNull ThemedReactContext reactContext) {
    return new RnGwScannerView(reactContext);
  }

  @Nullable
  @Override
  public Map<String, Object> getExportedCustomBubblingEventTypeConstants() {
    return MapBuilder.of(
      "topOnNewScan",
      MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onNewScan"))
    );
  }
}
