package com.opedea.mechanics;

import com.sun.istack.internal.Nullable;

/**
 * an interface to handle events
 */
public interface EventReceiver {

    /**
     * Called when event is fired on a registered observer
     * @param callback a function that will be run as a response
     * @param arguments all the arguments if any required
     */
    void receive(@Nullable Callback callback, @Nullable Object... arguments);

    /**
     * interface to handle callbacks
     */
    interface Callback {
        /**
         * Used to return data, as the name suggest.
         * arguments might need further casting in order to be used
         * @param arguments any information sent back
         */
        void callback(Object... arguments);
    }
}
