package com.klp.vms.exception;

import lombok.Getter;

@Getter
public class RuntimeError extends Exception {
    private final int code;

    public RuntimeError(String message, int code) {
        super(message);
        this.code = code;
    }
}
