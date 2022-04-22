package com.reactnativerngwscanner;

import android.graphics.Rect;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.huawei.hms.hmsscankit.RemoteView;
import com.huawei.hms.ml.scan.HmsScan;

public class RnGwScannerView extends FrameLayout {
  private final ThemedReactContext context;

  private final Gson gson;

  public enum Event {
    RESPONSE("response");

    private final String eventName;

    Event(String eventName) {
      this.eventName = eventName;
    }

    public String getName() {
      return eventName;
    }
  }

  public RnGwScannerView(ThemedReactContext context) {
    super(context);

    this.context = context;
    gson = new GsonBuilder().setPrettyPrinting().create();

    initView();
  }

  private void initView() {
    View view = LayoutInflater.from(context).inflate(R.layout.scanner_view, this, true);
    this.addView(view);

    ImageView scanFrame = view.findViewById(R.id.scan_area);

    Rect rect = new Rect(0, 0, 0, 0);

    scanFrame.getLayoutParams().height = rect.height();
    scanFrame.getLayoutParams().width = rect.width();

    Toast.makeText(context, "height " + rect.height() + " width " + rect.width(), Toast.LENGTH_SHORT).show();

    RemoteView remoteView = new RemoteView.Builder()
      .setContext(context.getCurrentActivity())
      .setBoundingBox(rect)
      .setContinuouslyScan(false)
      .setFormat(HmsScan.ALL_SCAN_TYPE)
      .build();

    remoteView.setOnResultCallback(result -> {
      if (result != null && result.length > 0 && result[0] != null && !TextUtils.isEmpty(result[0].getOriginalValue())) {

        Toast.makeText(context, result[0].getOriginalValue(), Toast.LENGTH_SHORT).show();

        WritableMap event = Arguments.createMap();
        event.putString("type", Event.RESPONSE.getName());
        event.putString("data", gson.toJson(result[0]));

        sendEvent("topOnNewScan", event);
      }
    });
  }

  private void sendEvent(String eventName, WritableMap eventArgs) {
    context.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), eventName, eventArgs);
  }
}
