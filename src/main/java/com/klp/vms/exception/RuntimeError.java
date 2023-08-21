package com.klp.vms.exception;

public class RuntimeError extends Exception {
    private final int code;

    public RuntimeError(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
