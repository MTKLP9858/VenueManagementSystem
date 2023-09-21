package com.klp.vms.exception;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;

@Getter
public class RuntimeError extends Exception {
    private final int code;

    public RuntimeError(String message, int code) {
        super(message);
        this.code = code;
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        json.put("code", this.getCode());
        json.put("success", false);
        json.put("message", this.getMessage());
        return json.toString();
    }
}
