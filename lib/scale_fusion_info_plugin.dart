import 'dart:async';

import 'package:flutter/services.dart';

class ScaleFusionInfoPlugin {
  static const MethodChannel _channel = MethodChannel('scale_fusion_info_plugin');

  static Future<bool?> get isMdmAgentInstalled => _channel.invokeMethod('isMdmAgentInstalled');

  static Future<bool?> get isEnrolled => _channel.invokeMethod('isEnrolled');

  static Future<bool?> get isManaged => _channel.invokeMethod('isManaged');

  static Future<String?> get imei => _channel.invokeMethod('imei');

  static Future<String?> get serial => _channel.invokeMethod('serial');

  static Future<String?> get gsmSerial => _channel.invokeMethod('gsmSerial');

  static Future<String?> get buildSerial => _channel.invokeMethod('buildSerial');
}
