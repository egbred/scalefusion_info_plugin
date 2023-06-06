package com.example.scale_fusion_info_plugin;

import androidx.annotation.NonNull;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.content.Context;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;


import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** ScaleFusionInfoPlugin */
public class ScaleFusionInfoPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private Context context;
  private Activity activity;


  public static final String REBOOT = "rebootDevice";
  public static final String MDM_ACTIONS_BASE = "content://com.mdm.api";

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "scale_fusion_info_plugin");
    channel.setMethodCallHandler(this);
    context = flutterPluginBinding.getApplicationContext();
  }

  public enum APIErrorResult {
    NOT_AUTHORIZED,
    ACTION_NOT_SUPPORTED,
    UNKNOWN_ERROR,
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    switch (call.method) {
      case "isMdmAgentInstalled": {
        Cursor cursor = getCursor();
        boolean isAvailable = cursor != null;
        closeCursor(cursor);
        result.success(isAvailable);
        break;
      }
      case "isEnrolled": {
        Cursor cursor = getCursor();
        int isEnrolled = -1;

        if (cursor != null && cursor.moveToFirst()) {
          isEnrolled = cursor.getInt(cursor.getColumnIndex("is_enrolled"));
          cursor.close();
        }

        result.success(isEnrolled == 1);
        break;
      }
      case "isManaged": {
        Cursor cursor = getCursor();
        int isManaged = -1;

        if (cursor != null && cursor.moveToFirst()) {
          isManaged = cursor.getInt(cursor.getColumnIndex("is_managed"));
          cursor.close();
        }

        result.success(isManaged == 1);
        break;
      }
      case "imei": {
        Cursor cursor = getCursor();
        String imei = null;

        if (cursor != null && cursor.moveToFirst()) {
          imei = cursor.getString(cursor.getColumnIndex("imei"));
          cursor.close();
        }

        result.success(imei);
        break;
      }
      case "serial": {
        Cursor cursor = getCursor();
        String serial = null;

        if (cursor != null && cursor.moveToFirst()) {
          serial = cursor.getString(cursor.getColumnIndex("serial"));
          cursor.close();
        }

        result.success(serial);
        break;
      }
      case "gsmSerial": {
        Cursor cursor = getCursor();
        String gsmSerial = null;

        if (cursor != null && cursor.moveToFirst()) {
          gsmSerial = cursor.getString(cursor.getColumnIndex("gsm_serial"));
          cursor.close();
        }

        result.success(gsmSerial);
        break;
      }
      case "buildSerial": {
        Cursor cursor = getCursor();
        String buildSerial = null;

        if (cursor != null && cursor.moveToFirst()) {
          buildSerial = cursor.getString(cursor.getColumnIndex("build_serial"));
          cursor.close();
        }

        result.success(buildSerial);
        break;
      }
      case "reboot":
        try {
          rebootDevice(context);
          result.success(true);
        } catch (RebootError rebootError) {
          result.error(rebootError.code, rebootError.getMessage(), rebootError.getCause());
        } catch (Exception e){
          result.error("-1", e.getMessage(), e.getStackTrace());
        }

        break;
      default:
        result.notImplemented();
        break;
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  Cursor getCursor() {
    ContentResolver resolver = context.getContentResolver();
    try {
      return resolver.query(Uri.parse("content://com.mdm.info.provider" + "/" + "mdm_info"), null, null, null, null);
    } catch (Exception e) {
      return null;
    }
  }

  void closeCursor(Cursor cursor){
    if(cursor!=null){
      cursor.close();
    }
  }

  public static void rebootDevice(@NonNull Context context) throws RebootError {
    try {
      Bundle actionResult = context.getContentResolver().call(Uri.parse(MDM_ACTIONS_BASE), REBOOT, (String) null, (Bundle) null);
      boolean z = actionResult.getBoolean("api_call_status");
      String errorMessage = actionResult.getString("error_message");

      if (actionResult.getBoolean("action_supported", true)) {
        if (!TextUtils.isEmpty(errorMessage)) {
          throw new RebootError(APIErrorResult.NOT_AUTHORIZED + ", " + errorMessage, null,"undefined");
        }
        // success status lies here
      } else {
        throw new RebootError(APIErrorResult.ACTION_NOT_SUPPORTED + ", " + errorMessage, null,"undefined");
      }
    } catch (Exception e) {
      throw new RebootError(APIErrorResult.UNKNOWN_ERROR + ": " + e.getMessage(), e.getCause(),"undefined");
    }
  }

  @Override
  public void onDetachedFromActivity() {

  }

  @Override
  public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {

  }

  @Override
  public void onAttachedToActivity(ActivityPluginBinding binding) {
    activity = binding.getActivity();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {

  }
}

