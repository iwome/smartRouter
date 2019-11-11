package com.bbq.smart_router.core;


import androidx.annotation.NonNull;

public abstract class AbsHandler {
    protected abstract boolean shouldHandle(@NonNull RouterRequest request);

    protected abstract Object handleInternal(@NonNull RouterRequest request);
}
