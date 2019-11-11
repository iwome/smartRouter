package com.bbq.smart_router_compile.utils;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

public class Debugger {
    private Messager messager;

    public Debugger(Messager messager) {
        this.messager = messager;
    }

    public void i(String msg) {
        messager.printMessage(Diagnostic.Kind.NOTE, msg);
    }
}
