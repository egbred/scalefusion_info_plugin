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

  public enum APIResult {
    NOT_AUTHORIZED,
    MDM_APP_NOT_INSTALLED,
    DEVICE_CURRENTLY_UNMANAGED,
    ACTION_NOT_SUPPORTED,
    SUCCESS
  }

  public static class Response {
    APIResult apiResult;
    Cursor result;

    public Cursor result() {
      return this.result;
    }

    public APIResult apiResult() {
      return this.apiResult;
    }
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("isMdmAgentInstalled")) {
      Cursor cursor = getCursor();
      boolean isAvailable = cursor!=null;
      closeCursor(cursor);
      result.success(isAvailable);
    } else if (call.method.equals("isEnrolled")){
      Cursor cursor = getCursor();
      int isEnrolled = -1;

      if(cursor!=null && cursor.moveToFirst()){
        isEnrolled = cursor.getInt(cursor.getColumnIndex("is_enrolled"));
        cursor.close();
      }

      result.success(isEnrolled==1);
    } else if (call.method.equals("isManaged")){
      Cursor cursor = getCursor();
      int isManaged = -1;

      if(cursor!=null && cursor.moveToFirst()){
        isManaged = cursor.getInt(cursor.getColumnIndex("is_managed"));
        cursor.close();
      }

      result.success(isManaged==1);
    } else if (call.method.equals("imei")){
      Cursor cursor = getCursor();
      String imei = null;

      if(cursor!=null && cursor.moveToFirst()){
        imei = cursor.getString(cursor.getColumnIndex("imei"));
        cursor.close();
      }

      result.success(imei);
    } else if (call.method.equals("serial")){
      Cursor cursor = getCursor();
      String serial = null;

      if(cursor!=null && cursor.moveToFirst()){
        serial = cursor.getString(cursor.getColumnIndex("serial"));
        cursor.close();
      }

      result.success(serial);
    } else if (call.method.equals("gsmSerial")){
      Cursor cursor = getCursor();
      String gsmSerial = null;

      if(cursor!=null && cursor.moveToFirst()){
        gsmSerial = cursor.getString(cursor.getColumnIndex("gsm_serial"));
        cursor.close();
      }

      result.success(gsmSerial);
    } else if (call.method.equals("buildSerial")){
      Cursor cursor = getCursor();
      String buildSerial = null;

      if(cursor!=null && cursor.moveToFirst()){
        buildSerial = cursor.getString(cursor.getColumnIndex("build_serial"));
        cursor.close();
      }

      result.success(buildSerial);
    } else if(call.method.equals("reboot")) {
      Response response = rebootDevice(context);
      result.success(response.apiResult == APIResult.SUCCESS);
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  Cursor getCursor() {
    ContentResolver resolver = context.getContentResolver();
    try {
      Cursor mdminfo = resolver.query(Uri.parse("content://com.mdm.info.provider" + "/" + "mdm_info"), null, null, null, null);
      return mdminfo;
    } catch (Exception e) {
      return null;
    }
  }

  void closeCursor(Cursor cursor){
    if(cursor!=null){
      cursor.close();
    }
  }

  public static Response rebootDevice(@NonNull Context context) {
    Response response = new Response();
    try {
      Bundle actionResult = context.getContentResolver().call(Uri.parse(MDM_ACTIONS_BASE), REBOOT, (String) null, (Bundle) null);
      boolean z = actionResult.getBoolean("api_call_status");
      String errorMessage = actionResult.getString("error_message");
      if (actionResult.getBoolean("action_supported", true)) {
        if (!TextUtils.isEmpty(errorMessage)) {
          char c = 65535;
          if (errorMessage.hashCode() == -2029100113) {
            if (errorMessage.equals("Not allowed to access the API")) {
              c = 0;
            }
          }
          if (c == 0) {
            response.apiResult = APIResult.NOT_AUTHORIZED;
          } else {
            response.apiResult = APIResult.DEVICE_CURRENTLY_UNMANAGED;
          }
        } else {
          response.apiResult = APIResult.SUCCESS;
        }
      } else {
        response.apiResult = APIResult.ACTION_NOT_SUPPORTED;
      }
    } catch (Exception e) {
      response.apiResult = APIResult.MDM_APP_NOT_INSTALLED;
    }
    return response;
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
