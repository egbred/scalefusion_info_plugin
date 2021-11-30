package com.example.scale_fusion_info_plugin;

import androidx.annotation.NonNull;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.content.Context;
import android.app.Activity;

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

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "scale_fusion_info_plugin");
    channel.setMethodCallHandler(this);
    context = flutterPluginBinding.getApplicationContext();
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
