package com.reactnativerngwscanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.huawei.hms.hmsscankit.RemoteView;
import com.huawei.hms.ml.scan.HmsScan;

import java.io.ByteArrayOutputStream;

public class RnGwScannerView extends FrameLayout {
  private int scanType;
  private int[] additionalScanTypes;
  private boolean continuouslyScan;
  private boolean enableReturnOriginalScan;
  private float rectHeight;
  private float rectWidth;
  private boolean flashOnLightChange;

  private RemoteView remoteView;

  private final Gson gson;
  private ImageView flashButton;
  private boolean isFlashAvailable;

  private final int[] img = {R.drawable.flashlight_on, R.drawable.flashlight_off};

  public enum Event {
    RESPONSE("response"),
    ORIGINAL_SCAN_LOAD("originalScanLoad");

    private final String eventName;

    Event(String eventName) {
      this.eventName = eventName;
    }

    public String getName() {
      return eventName;
    }
  }

  public RnGwScannerView(Context context) {
    super(context);

    gson = new GsonBuilder().setPrettyPrinting().create();
    initView();
  }

  public void setScanType(int scanType) {
    this.scanType = scanType;
  }

  public void setAdditionalScanTypes(ReadableArray additionalScanTypes) {
    int[] l = new int[additionalScanTypes.size()];
    for (int idx = 0; idx < additionalScanTypes.size(); idx += 1) {
      l[idx] = additionalScanTypes.getInt(idx);
    }
    this.additionalScanTypes = l;
  }

  public void setContinuouslyScan(boolean continuouslyScan) {
    this.continuouslyScan = continuouslyScan;
  }

  public void setEnableReturnOriginalScan(boolean enableReturnOriginalScan) {
    this.enableReturnOriginalScan = enableReturnOriginalScan;
  }

  public void setRectHeight(float rectHeight) {
    this.rectHeight = rectHeight;
  }

  public void setRectWidth(float rectWidth) {
    this.rectWidth = rectWidth;
  }

  public void setFlashOnLightChange(boolean flashOnLightChange) {
    this.flashOnLightChange = flashOnLightChange;
  }

  public void setFlashAvailable(boolean flashAvailable) {
    isFlashAvailable = flashAvailable;
  }

  private void initView() {
    inflate(getContext(), R.layout.scanner_view, this);

    ImageView scanFrame = findViewById(R.id.scan_area);
    flashButton = findViewById(R.id.flush_btn);

    DisplayMetrics dm = getResources().getDisplayMetrics();
    float density = dm.density;

    int mScreenWidth = getResources().getDisplayMetrics().widthPixels;
    int mScreenHeight = getResources().getDisplayMetrics().heightPixels;

    int scanFrameSizeHeight = (int) (200 * density);
    int scanFrameSizeWidth = (int) (200 * density);

    Rect rect = new Rect();
    rect.left = mScreenWidth / 2 - scanFrameSizeWidth / 2;
    rect.right = mScreenWidth / 2 + scanFrameSizeWidth / 2;
    rect.top = mScreenHeight / 2 - scanFrameSizeHeight / 2;
    rect.bottom = mScreenHeight / 2 + scanFrameSizeHeight / 2;

    scanFrame.getLayoutParams().height = rect.height();
    scanFrame.getLayoutParams().width = rect.width();

    Toast.makeText(getContext(), "height " + rect.height() + " width " + rect.width(), Toast.LENGTH_SHORT).show();

    RemoteView.Builder builder = new RemoteView.Builder()
      .setContext(((ReactContext) getContext()).getCurrentActivity())
      .setBoundingBox(rect)
      .setFormat(scanType, additionalScanTypes)
      .setContinuouslyScan(continuouslyScan);

    if (enableReturnOriginalScan) {
      builder.enableReturnBitmap();   //Get original scan
    }

    remoteView = builder.build();
    RnGwScannerViewManager.setViews(remoteView, flashButton);

    flashButton.setVisibility(View.INVISIBLE);

    // When the light is dim, this API is called back to display the flashlight switch.
    if (flashOnLightChange) {
      setFlashOperation();
      remoteView.setOnLightVisibleCallback(visible -> {
        if (visible) {
          flashButton.setVisibility(View.VISIBLE);
        } else {
          flashButton.setVisibility(View.INVISIBLE);
        }
      });
    }

    if (isFlashAvailable) {
      flashButton.setVisibility(View.VISIBLE);
      setFlashOperation();
    }

    remoteView.setOnResultCallback(result -> {
      if (enableReturnOriginalScan) {
        sendOriginalScan(result[0]);
      }

      if (result != null && result.length > 0 && result[0] != null && !TextUtils.isEmpty(result[0].getOriginalValue())) {
        if (!continuouslyScan) {
          HmsScan hmsScan = result[0];

          if (hmsScan != null && !TextUtils.isEmpty(hmsScan.getOriginalValue())) {
            WritableMap event = Arguments.createMap();
            event.putString("type", Event.RESPONSE.getName());
            event.putString("data", gson.toJson(hmsScan));

            sendEvent("topOnNewScan", event);
          }


        } else {
          WritableMap event = Arguments.createMap();
          event.putString("type", Event.RESPONSE.getName());
          event.putString("data", gson.toJson(result[0]));

          sendEvent("topOnNewScan", event);
        }
      }
    });
  }

  private void setFlashOperation() {
    flashButton.setOnClickListener(v -> {
      if (remoteView.getLightStatus()) {
        remoteView.switchLight();
        flashButton.setImageResource(img[1]);
      } else {
        remoteView.switchLight();
        flashButton.setImageResource(img[0]);
      }
    });
  }

  private void sendOriginalScan(HmsScan scan) {
    Bitmap bitmap = scan.getOriginalBitmap();
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
    String byteArray = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

    WritableMap event = Arguments.createMap();
    event.putString("type", Event.ORIGINAL_SCAN_LOAD.getName());
    event.putString("data", byteArray);

    sendEvent("topOnNewScan", event);
  }

  private void sendEvent(String eventName, WritableMap eventArgs) {
    ReactContext reactContext = (ReactContext) getContext();
    reactContext
      .getJSModule(RCTEventEmitter.class)
      .receiveEvent(getId(), eventName, eventArgs);
  }
}
