package com.reactnativerngwscanner;

import android.annotation.SuppressLint;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.huawei.hms.hmsscankit.RemoteView;
import com.huawei.hms.ml.scan.HmsScan;

import java.util.Map;

public class RnGwScannerViewManager extends ViewGroupManager<RnGwScannerView> {
  public static final String REACT_CLASS = "RnGwScannerView";

  private final ReactApplicationContext reactContext;
  @SuppressLint("StaticFieldLeak")
  private static RemoteView remoteView;
  @SuppressLint("StaticFieldLeak")
  private static ImageView flashButton;

  private final int[] img = {R.drawable.flashlight_on, R.drawable.flashlight_off};

  public static void setViews(RemoteView remoteView, ImageView flashButton) {
    RnGwScannerViewManager.remoteView = remoteView;
    RnGwScannerViewManager.flashButton = flashButton;
  }

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

  @ReactProp(name = "scanType")
  public void setScanType(RnGwScannerView view, int scanType) {
    view.setScanType(scanType);
  }

  @ReactProp(name = "additionalScanTypes")
  public void setAdditionalScanTypes(RnGwScannerView view, int[] additionalScanTypes) {
    view.setAdditionalScanTypes(additionalScanTypes);
  }

  @ReactProp(name = "rectWidth", defaultFloat = 200f)
  public void setRectWidth(RnGwScannerView view, float rectWidth) {
    view.setRectWidth(rectWidth);
  }

  @ReactProp(name = "rectHeight", defaultFloat = 200f)
  public void setRectHeight(RnGwScannerView view, float rectHeight) {
    view.setRectHeight(rectHeight);
  }

  @ReactProp(name = "continuouslyScan", defaultBoolean = false)
  public void setContinuouslyScan(RnGwScannerView view, boolean continuouslyScan) {
    view.setContinuouslyScan(continuouslyScan);
  }

  @ReactProp(name = "enableReturnOriginalScan", defaultBoolean = false)
  public void setEnableReturnOriginalScan(RnGwScannerView view, boolean enableReturnOriginalScan) {
    view.setEnableReturnOriginalScan(enableReturnOriginalScan);
  }

  @ReactProp(name = "flashOnLightChange", defaultBoolean = false)
  public void setFlashOnLightChange(RnGwScannerView view, boolean flashOnLightChange) {
    view.setFlashOnLightChange(flashOnLightChange);
  }

  @ReactProp(name = "flashAvailable", defaultBoolean = false)
  public void setFlashAvailable(RnGwScannerView view, boolean flashAvailable) {
    view.setFlashAvailable(flashAvailable);
  }

  @ReactMethod
  public void pauseContinuouslyScan(final Promise promise) {
    if (remoteView != null) {
      remoteView.pauseContinuouslyScan();
      promise.resolve(true);
    } else {
      promise.reject("remoteViewError", "Remote View is not initiated");
    }
  }

  @ReactMethod
  public void resumeContinuouslyScan(final Promise promise) {
    if (remoteView != null) {
      remoteView.resumeContinuouslyScan();
      promise.resolve(true);
    } else {
      promise.reject("remoteViewError", "Remote View is not initiated");
    }
  }

  @ReactMethod
  public void switchLight(final Promise promise) {
    if (remoteView != null) {
      remoteView.switchLight();
      if (remoteView.getLightStatus()) {
        flashButton.setImageResource(img[1]);
      } else {
        flashButton.setImageResource(img[0]);
      }
    } else {
      promise.reject("remoteViewError", "Remote View is not initiated");
    }
  }

  @ReactMethod
  public void getLightStatus(final Promise promise) {
    if (remoteView != null) {
      promise.resolve(remoteView.getLightStatus());
    } else {
      promise.reject("remoteViewError", "Remote View is not initiated");
    }
  }

  @Nullable
  @Override
  public Map<String, Object> getExportedCustomBubblingEventTypeConstants() {
    return MapBuilder.of(
      "topOnNewScan",
      MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onNewScan"))
    );
  }

  @Nullable
  @Override
  public Map<String, Object> getConstants() {
    Map<String, Object> constants = new ArrayMap<>();
    constants.put("SCAN_TYPES", getScanTypes());
    return constants;
  }

  public Map<String, Object> getScanTypes() {
    Map<String, Object> scanTypes = new ArrayMap<>();
    scanTypes.put("Other", HmsScan.OTHER_FORM);
    scanTypes.put("All", HmsScan.ALL_SCAN_TYPE);
    scanTypes.put("Code128", HmsScan.CODE128_SCAN_TYPE);
    scanTypes.put("Code39", HmsScan.CODE39_SCAN_TYPE);
    scanTypes.put("Code93", HmsScan.CODE93_SCAN_TYPE);
    scanTypes.put("Codabar", HmsScan.CODABAR_SCAN_TYPE);
    scanTypes.put("DataMatrix", HmsScan.DATAMATRIX_SCAN_TYPE);
    scanTypes.put("EAN13", HmsScan.EAN13_SCAN_TYPE);
    scanTypes.put("EAN8", HmsScan.EAN8_SCAN_TYPE);
    scanTypes.put("ITF14", HmsScan.ITF14_SCAN_TYPE);
    scanTypes.put("QRCode", HmsScan.QRCODE_SCAN_TYPE);
    scanTypes.put("UPCCodeA", HmsScan.UPCCODE_A_SCAN_TYPE);
    scanTypes.put("UPCCodeE", HmsScan.UPCCODE_E_SCAN_TYPE);
    scanTypes.put("Pdf417", HmsScan.PDF417_SCAN_TYPE);
    scanTypes.put("Aztec", HmsScan.AZTEC_SCAN_TYPE);
    return scanTypes;
  }
}
