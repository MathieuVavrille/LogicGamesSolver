package com.mvavrill.logicGamesSolver.controller.popups;

import android.os.Bundle;

/**
 * Interface to return a value to the calling activity.
 */
public interface CallbackWithInteger {
    void callbackWithInteger(Bundle callbackBundle, int v);
}
