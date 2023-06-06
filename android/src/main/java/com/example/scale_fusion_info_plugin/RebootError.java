package com.example.scale_fusion_info_plugin;

public class RebootError extends  Exception {
    String code;
    RebootError(String message, Throwable cause, String code){
        super(message, cause);
        this.code = code;
    }
}
