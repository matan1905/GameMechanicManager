package com.opedea.mechanics;

import com.sun.istack.internal.Nullable;

public interface EventReceiver {
    //Called when event is fired on a registered observer
    void receive(@Nullable Callback callback, @Nullable Object... arguments);

    interface Callback {
        //Used to return data, as the name suggest
        void callback(Object... arguments);
    }
}
